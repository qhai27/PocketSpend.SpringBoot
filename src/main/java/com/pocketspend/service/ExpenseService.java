package com.pocketspend.service;

import com.pocketspend.model.Expense;
import com.pocketspend.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    /**
     * Adds a new expense for a given user.
     */
    @Transactional
    public Expense addExpense(Long userId, Expense expense) {
        expense.setUserId(userId); // Ensure expense is associated with the correct user
        return expenseRepository.save(expense);
    }

    /**
     * Retrieves all expenses for a given user.
     */
    public List<Expense> getExpensesByUserId(Long userId) {
        return expenseRepository.findByUserId(userId);
    }

    /**
     * Deletes an expense by its ID.
     *
     * @return
     */
    @Transactional
    public boolean removeExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Expense with ID " + id + " not found");
        }
        expenseRepository.deleteById(id);
        return false;
    }
}
