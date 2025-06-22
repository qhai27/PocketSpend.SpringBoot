package com.pocketspend.service;

import com.pocketspend.model.Expense;
import com.pocketspend.model.User;
import com.pocketspend.repository.ExpenseRepository;
import com.pocketspend.repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private BudgetService budgetService; // Injected BudgetService

    /**
     * Adds a new expense for a given user and updates the budget.
     */
    @Transactional
    public Expense addExpense(Long userId, Expense expense) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        expense.setUser(user); // Set the User relationship
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
