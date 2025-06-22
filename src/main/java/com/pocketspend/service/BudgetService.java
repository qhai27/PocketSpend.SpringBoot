package com.pocketspend.service;

import com.pocketspend.model.Budget;
import com.pocketspend.model.User;
import com.pocketspend.repository.BudgetRepository;
import com.pocketspend.repository.ExpenseRepository;
import com.pocketspend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    public Budget setBudget(Long userId, double totalBudget) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        double totalExpenses = expenseRepository.sumExpensesByUserId(userId);
        Budget budget = budgetRepository.findByUserId(userId);

        if (budget == null) {
            budget = new Budget(user, totalBudget);
        } else {
            budget.setTotalBudget(totalBudget);
        }

        budget.setTotalExpenses(totalExpenses);
        budget.setBudgetLeft(totalBudget - totalExpenses);

        return budgetRepository.save(budget);
    }

    public Budget getBudget(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    public void updateBudgetExpenses(Long userId) {
        Budget budget = budgetRepository.findByUserId(userId);
        if (budget != null) {
            double totalExpenses = expenseRepository.sumExpensesByUserId(userId);
            budget.setTotalExpenses(totalExpenses); // this will also update budgetLeft
            budgetRepository.save(budget);
        }
    }
}
