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
        System.out.println(
                "Service: Expense paidBy ID: " + (expense.getPaidBy() != null ? expense.getPaidBy().getId() : "null"));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + groupId));

        if (expense.getDescription() == null || expense.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Expense description cannot be empty");
        }

        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero");
        }

        if (expense.getPaidBy() == null || expense.getPaidBy().getId() == null) {
            throw new IllegalArgumentException("Paid by member must be specified");
        }

        if (splits == null || splits.isEmpty()) {
            throw new IllegalArgumentException("At least one split must be specified");
        }

        // Verify paidBy member exists and belongs to the group
        System.out.println("Service: Looking for paidBy member with ID: " + expense.getPaidBy().getId());
        GroupMember paidByMember = groupMemberRepository.findById(expense.getPaidBy().getId())
                .orElseThrow(() -> new IllegalArgumentException("Paid by member not found"));

        System.out.println(
                "Service: Found paidBy member: " + paidByMember.getName() + " (ID: " + paidByMember.getId() + ")");
        System.out.println("Service: PaidBy member belongs to group: " + paidByMember.getGroup().getId());

        if (!paidByMember.getGroup().getId().equals(groupId)) {
            throw new IllegalArgumentException("Paid by member does not belong to this group");
        }

        expense.setGroup(group);
        expense.setPaidBy(paidByMember);
        expense.setCreatedAt(java.time.LocalDateTime.now());

        System.out.println("Service: About to save expense with paidBy: " + expense.getPaidBy().getName());
        SplitExpense savedExpense = splitExpenseRepository.save(expense);
        System.out.println("Service: Expense saved with ID: " + savedExpense.getId());
        System.out.println("Service: Saved expense paidBy: " + savedExpense.getPaidBy().getName());

        for (ExpenseSplit split : splits) {
            // Verify split member exists and belongs to the group
            GroupMember splitMember = groupMemberRepository.findById(split.getMember().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Split member not found"));

            if (!splitMember.getGroup().getId().equals(groupId)) {
                throw new IllegalArgumentException("Split member does not belong to this group");
            }

            split.setExpense(savedExpense);
            split.setMember(splitMember);
            expenseSplitRepository.save(split);
        }

        return savedExpense;
    }

    public void deleteExpense(Long expenseId) {
        if (!splitExpenseRepository.existsById(expenseId)) {
            throw new IllegalArgumentException("Expense not found with id: " + expenseId);
        }
        splitExpenseRepository.deleteById(expenseId);
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