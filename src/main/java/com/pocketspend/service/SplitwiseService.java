package com.pocketspend.service;

import com.pocketspend.model.*;
import com.pocketspend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class SplitwiseService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private SplitExpenseRepository splitExpenseRepository;
    @Autowired
    private ExpenseSplitRepository expenseSplitRepository;

    // Group CRUD
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group createGroup(Group group) {
        if (group.getName() == null || group.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be empty");
        }
        group.setCreatedAt(java.time.LocalDateTime.now());
        return groupRepository.save(group);
    }

    public void deleteGroup(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new IllegalArgumentException("Group not found with id: " + groupId);
        }
        groupRepository.deleteById(groupId);
    }

    public Optional<Group> getGroup(Long groupId) {
        return groupRepository.findById(groupId);
    }

    // Member CRUD
    public List<GroupMember> getMembersByGroup(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new IllegalArgumentException("Group not found with id: " + groupId);
        }
        return groupMemberRepository.findByGroupId(groupId);
    }

    public GroupMember createMember(Long groupId, GroupMember member) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + groupId));

        if (member.getName() == null || member.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be empty");
        }

        member.setGroup(group);
        member.setCreatedAt(java.time.LocalDateTime.now());
        return groupMemberRepository.save(member);
    }

    public void deleteMember(Long memberId) {
        if (!groupMemberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("Member not found with id: " + memberId);
        }
        groupMemberRepository.deleteById(memberId);
    }

    // Expense CRUD
    public List<SplitExpense> getExpensesByGroup(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new IllegalArgumentException("Group not found with id: " + groupId);
        }
        return splitExpenseRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
    }

    public SplitExpense createExpense(Long groupId, SplitExpense expense, List<ExpenseSplit> splits) {
        System.out.println("Service: Creating expense for group: " + groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + groupId));

        if (expense.getDescription() == null || expense.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Expense description cannot be empty");
        }

        BigDecimal totalAmount = expense.getAmount();
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero");
        }

        if (expense.getPaidBy() == null || expense.getPaidBy().getId() == null) {
            throw new IllegalArgumentException("Paid by member must be specified");
        }

        GroupMember paidByMember = groupMemberRepository.findById(expense.getPaidBy().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Paid by member not found with ID: " + expense.getPaidBy().getId()));

        if (!paidByMember.getGroup().getId().equals(groupId)) {
            throw new IllegalArgumentException("Paid by member does not belong to this group");
        }

        expense.setGroup(group);
        expense.setPaidBy(paidByMember);
        expense.setCreatedAt(java.time.LocalDateTime.now());
        SplitExpense savedExpense = splitExpenseRepository.save(expense);

        // --- Core Splitting Logic ---
        List<ExpenseSplit> calculatedSplits = calculateSplits(savedExpense, splits);

        // Save the calculated splits
        for (ExpenseSplit split : calculatedSplits) {
            GroupMember splitMember = groupMemberRepository.findById(split.getMember().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Split member not found with ID: " + split.getMember().getId()));
            if (!splitMember.getGroup().getId().equals(groupId)) {
                throw new IllegalArgumentException(
                        "Split member " + splitMember.getName() + " does not belong to this group");
            }
            split.setExpense(savedExpense);
            split.setMember(splitMember);
            expenseSplitRepository.save(split);
        }

        return savedExpense;
    }

    private List<ExpenseSplit> calculateSplits(SplitExpense expense, List<ExpenseSplit> providedSplits) {
        BigDecimal totalAmount = expense.getAmount();
        List<ExpenseSplit> finalSplits = new ArrayList<>();
        List<Long> memberIds = providedSplits.stream().map(s -> s.getMember().getId()).toList();

        switch (expense.getSplitType()) {
            case EQUAL:
                if (memberIds.isEmpty()) {
                    throw new IllegalArgumentException("At least one member must be selected for an equal split.");
                }
                BigDecimal splitCount = new BigDecimal(memberIds.size());
                BigDecimal individualAmount = totalAmount.divide(splitCount, 2, java.math.RoundingMode.HALF_UP);
                BigDecimal remainder = totalAmount.subtract(individualAmount.multiply(splitCount));

                for (int i = 0; i < memberIds.size(); i++) {
                    ExpenseSplit split = new ExpenseSplit();
                    split.setMember(new GroupMember(memberIds.get(i)));
                    BigDecimal amount = individualAmount;
                    if (i == 0) { // Add remainder to the first person
                        amount = amount.add(remainder);
                    }
                    split.setAmount(amount);
                    finalSplits.add(split);
                }
                break;

            case PERCENTAGE:
                BigDecimal totalPercentage = providedSplits.stream()
                        .map(ExpenseSplit::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalPercentage.compareTo(new BigDecimal("100")) != 0) {
                    throw new IllegalArgumentException("Percentages must add up to exactly 100.");
                }

                BigDecimal runningTotal = BigDecimal.ZERO;
                for (int i = 0; i < providedSplits.size(); i++) {
                    ExpenseSplit providedSplit = providedSplits.get(i);
                    ExpenseSplit finalSplit = new ExpenseSplit();
                    finalSplit.setMember(providedSplit.getMember());

                    BigDecimal percentage = providedSplit.getAmount();
                    BigDecimal calculatedAmount = totalAmount.multiply(percentage).divide(new BigDecimal("100"), 2,
                            java.math.RoundingMode.HALF_UP);

                    runningTotal = runningTotal.add(calculatedAmount);
                    finalSplit.setAmount(calculatedAmount);
                    finalSplits.add(finalSplit);
                }

                // Adjust for rounding
                BigDecimal remainderAfterPercentage = totalAmount.subtract(runningTotal);
                if (remainderAfterPercentage.compareTo(BigDecimal.ZERO) != 0 && !finalSplits.isEmpty()) {
                    finalSplits.get(0).setAmount(finalSplits.get(0).getAmount().add(remainderAfterPercentage));
                }
                break;

            case CUSTOM:
                BigDecimal customTotal = providedSplits.stream()
                        .map(ExpenseSplit::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (customTotal.compareTo(totalAmount) != 0) {
                    throw new IllegalArgumentException("Custom split amounts must add up to the total expense amount.");
                }

                return providedSplits; // Use as is after validation

            default:
                throw new IllegalArgumentException("Invalid split type specified.");
        }

        return finalSplits;
    }

    public void deleteExpense(Long expenseId) {
        if (!splitExpenseRepository.existsById(expenseId)) {
            throw new IllegalArgumentException("Expense not found with id: " + expenseId);
        }
        splitExpenseRepository.deleteById(expenseId);
    }

    // Settle all balances for a group by deleting all its expenses
    public void settleUp(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new IllegalArgumentException("Group not found with id: " + groupId);
        }
        List<SplitExpense> expenses = splitExpenseRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
        if (!expenses.isEmpty()) {
            splitExpenseRepository.deleteAll(expenses);
        }
    }

    // Balances for a group
    public Map<Long, BigDecimal> calculateBalances(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new IllegalArgumentException("Group not found with id: " + groupId);
        }

        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        Map<Long, BigDecimal> balances = new HashMap<>();

        for (GroupMember member : members) {
            BigDecimal paid = BigDecimal.ZERO;
            BigDecimal owed = BigDecimal.ZERO;

            // Calculate total paid by this member
            List<SplitExpense> expensesPaidByMember = splitExpenseRepository
                    .findByPaidByOrderByCreatedAtDesc(member.getId());
            for (SplitExpense expense : expensesPaidByMember) {
                if (expense.getGroup().getId().equals(groupId)) {
                    paid = paid.add(expense.getAmount());
                }
            }

            // Calculate total owed by this member
            List<ExpenseSplit> memberSplits = expenseSplitRepository.findByMemberId(member.getId());
            for (ExpenseSplit split : memberSplits) {
                if (split.getExpense().getGroup().getId().equals(groupId)) {
                    owed = owed.add(split.getAmount());
                }
            }

            balances.put(member.getId(), paid.subtract(owed));
        }

        return balances;
    }
}