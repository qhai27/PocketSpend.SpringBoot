package com.pocketspend.service;

import com.pocketspend.model.Expense;
import com.pocketspend.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetService budgetService; // Injected BudgetService

    /**
     * Adds a new expense for a given user and updates the budget.
     */
    @Transactional
    public Expense addExpense(Long userId, Expense expense) {
        expense.setUserId(userId); // Ensure expense is associated with the correct user
        Expense savedExpense = expenseRepository.save(expense);
        budgetService.updateBudgetExpenses(userId); // Update budget
        return savedExpense;
    }

    /**
     * Retrieves all expenses for a given user.
     */
    public List<Expense> getExpensesByUserId(Long userId) {
        return expenseRepository.findByUserId(userId);
    }

    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    /**
     * Deletes an expense by its ID and updates the budget.
     */
    @Transactional
    public boolean removeExpense(Long id) {
        Optional<Expense> expenseOptional = expenseRepository.findById(id);
        if (expenseOptional.isPresent()) {
            Expense expense = expenseOptional.get();
            Long userId = expense.getUserId();
            expenseRepository.deleteById(id);
            budgetService.updateBudgetExpenses(userId); // Update budget
            return true;
        }
        return false;
    }
}
