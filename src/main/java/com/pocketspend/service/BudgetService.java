package com.pocketspend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pocketspend.model.Budget;
import com.pocketspend.repository.BudgetRepository;

@Service
public class BudgetService {


    private BudgetRepository budgetRepository;

    private ExpenseService expenseService;

    @Transactional
    public Budget setBudget(Long userId, double totalBudget) {
        Budget budget = budgetRepository.findByUserId(userId);
        if (budget == null) {
            budget = new Budget(userId, totalBudget);
        } else {
            budget.setTotalBudget(totalBudget);
        }
        return budgetRepository.save(budget);
    }

    public Budget getBudget(Long userId) {
        Budget budget = budgetRepository.findByUserId(userId);
        if (budget != null) {
            // Update total expenses and budget left
            double totalExpenses = expenseService.getExpensesByUserId(userId)
                    .stream()
                    .mapToDouble(expense -> expense.getAmount())
                    .sum();
            budget.setTotalExpenses(totalExpenses);
            budget.setBudgetLeft(budget.getTotalBudget() - totalExpenses);
            budgetRepository.save(budget);
        }
        return budget;
    }
}