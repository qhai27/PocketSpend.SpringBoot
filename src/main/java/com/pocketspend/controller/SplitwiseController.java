package com.pocketspend.controller;

import com.pocketspend.model.*;
import com.pocketspend.service.SplitwiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/splitwise")
@CrossOrigin(origins = "*")
public class SplitwiseController {
    @Autowired
    private SplitwiseService splitwiseService;

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Splitwise API is running");
        return ResponseEntity.ok(response);
    }

    // GROUPS
    @GetMapping("/groups")
    public ResponseEntity<List<Group>> getAllGroups() {
        try {
            List<Group> groups = splitwiseService.getAllGroups();
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/groups", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createGroup(@RequestBody Group group) {
        try {
            // Add debugging
            System.out.println("Received group creation request");
            System.out.println("Group name: " + (group != null ? group.getName() : "null"));
            System.out.println("Group object: " + group);

            if (group == null || group.getName() == null || group.getName().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Group name is required");
                return ResponseEntity.badRequest().body(error);
            }

            Group createdGroup = splitwiseService.createGroup(group);
            return ResponseEntity.ok(createdGroup);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            System.out.println("Error creating group: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId) {
        try {
            splitwiseService.deleteGroup(groupId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // MEMBERS
    @GetMapping("/groups/{groupId}/members")
    public ResponseEntity<List<GroupMember>> getMembersByGroup(@PathVariable Long groupId) {
        try {
            List<GroupMember> members = splitwiseService.getMembersByGroup(groupId);
            return ResponseEntity.ok(members);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/groups/{groupId}/members", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createMember(@PathVariable Long groupId, @RequestBody GroupMember member) {
        try {
            GroupMember createdMember = splitwiseService.createMember(groupId, member);
            return ResponseEntity.ok(createdMember);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable Long memberId) {
        try {
            splitwiseService.deleteMember(memberId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // EXPENSES
    @GetMapping("/groups/{groupId}/expenses")
    public ResponseEntity<List<SplitExpense>> getExpensesByGroup(@PathVariable Long groupId) {
        try {
            List<SplitExpense> expenses = splitwiseService.getExpensesByGroup(groupId);
            return ResponseEntity.ok(expenses);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/groups/{groupId}/expenses", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createExpense(@PathVariable Long groupId, @RequestBody SplitExpenseRequest request) {
        try {
            // Add logging to debug the request
            System.out.println("Received expense request for group: " + groupId);
            System.out.println("Expense data: " + request.getExpense());
            System.out.println("Splits data: " + request.getSplits());

            if (request.getExpense() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Expense data is required");
                return ResponseEntity.badRequest().body(error);
            }

            if (request.getSplits() == null || request.getSplits().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "At least one split is required");
                return ResponseEntity.badRequest().body(error);
            }

            // Convert DTO to service objects
            SplitExpense expense = new SplitExpense();
            expense.setDescription(request.getExpense().getDescription());
            expense.setAmount(new java.math.BigDecimal(request.getExpense().getAmount()));
            expense.setSplitType(SplitExpense.SplitType.valueOf(request.getExpense().getSplitType()));

            // Debug paidBy information
            System.out.println("PaidById from request: " + request.getExpense().getPaidById());

            // Create a temporary GroupMember for paidBy (will be replaced in service)
            GroupMember paidBy = new GroupMember();
            paidBy.setId(request.getExpense().getPaidById());
            expense.setPaidBy(paidBy);

            System.out.println("PaidBy member ID set: " + expense.getPaidBy().getId());

            // Convert splits
            List<ExpenseSplit> splits = new ArrayList<>();
            for (SplitData splitData : request.getSplits()) {
                ExpenseSplit split = new ExpenseSplit();
                split.setAmount(new java.math.BigDecimal(splitData.getAmount()));

                // Create a temporary GroupMember (will be replaced in service)
                GroupMember member = new GroupMember();
                member.setId(splitData.getMemberId());
                split.setMember(member);

                splits.add(split);
            }

            System.out.println("About to call service with expense: " + expense);
            System.out.println("PaidBy member in expense: " + expense.getPaidBy());

            SplitExpense createdExpense = splitwiseService.createExpense(groupId, expense, splits);

            System.out.println("Expense created successfully. ID: " + createdExpense.getId());
            System.out.println("PaidBy in created expense: " + createdExpense.getPaidBy());

            return ResponseEntity.ok(createdExpense);
        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/expenses/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long expenseId) {
        try {
            splitwiseService.deleteExpense(expenseId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // BALANCES
    @GetMapping("/groups/{groupId}/balances")
    public ResponseEntity<Map<Long, java.math.BigDecimal>> getBalances(@PathVariable Long groupId) {
        try {
            Map<Long, java.math.BigDecimal> balances = splitwiseService.calculateBalances(groupId);
            return ResponseEntity.ok(balances);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/groups/{groupId}/settle")
    public ResponseEntity<?> settleUp(@PathVariable Long groupId) {
        try {
            splitwiseService.settleUp(groupId);
            return ResponseEntity.ok().body(Map.of("message", "All balances have been settled successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    // DTO for expense creation
    public static class SplitExpenseRequest {
        private ExpenseData expense;
        private List<SplitData> splits;

        public ExpenseData getExpense() {
            return expense;
        }

        public void setExpense(ExpenseData expense) {
            this.expense = expense;
        }

        public List<SplitData> getSplits() {
            return splits;
        }

        public void setSplits(List<SplitData> splits) {
            this.splits = splits;
        }
    }

    public static class ExpenseData {
        private String description;
        private String amount;
        private Long paidById;
        private String splitType;

        // Getters and Setters
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public Long getPaidById() {
            return paidById;
        }

        public void setPaidById(Long paidById) {
            this.paidById = paidById;
        }

        public String getSplitType() {
            return splitType;
        }

        public void setSplitType(String splitType) {
            this.splitType = splitType;
        }
    }

    public static class SplitData {
        private Long memberId;
        private String amount;

        // Getters and Setters
        public Long getMemberId() {
            return memberId;
        }

        public void setMemberId(Long memberId) {
            this.memberId = memberId;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }
}