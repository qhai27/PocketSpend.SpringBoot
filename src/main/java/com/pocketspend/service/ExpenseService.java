package com.pocketspend.service;


import java.util.List;

import com.pocketspend.model.Budget;
import com.pocketspend.model.Expense;
import com.pocketspend.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetService budgetService;

    @Transactional
    public Expense addExpense(Long userId, Expense expense) {
        Budget budget = budgetService.getBudget(userId);
        if (budget == null) {
            throw new RuntimeException("No budget set for user");
        }
        expense.setBudgetId(budget.getId());
        Expense savedExpense = expenseRepository.save(expense);
        // Update budget
        budgetService.getBudget(userId); // Recalculates totalExpenses and budgetLeft
        return savedExpense;
    }

    public List<Expense> getExpensesByUserId(Long userId) {
        Budget budget = budgetService.getBudget(userId);
        if (budget == null) {
            return List.of();
        }
        return expenseRepository.findByBudgetId(budget.getId());
    }

    @Transactional
    public void removeExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        expenseRepository.deleteById(id);
        // Update budget
        budgetService.getBudget(expense.getBudgetId());
    }
}