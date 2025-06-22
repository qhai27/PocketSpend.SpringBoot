package com.pocketspend.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pocketspend.model.Budget;
import com.pocketspend.repository.BudgetRepository;
import com.pocketspend.repository.ExpenseRepository;


@Service
public class BudgetService {


   @Autowired
   private BudgetRepository budgetRepository;


   @Autowired
   private ExpenseRepository expenseRepository;


   public Budget setBudget(Long userId, double totalBudget) {
       double totalExpenses = expenseRepository.sumExpensesByUserId(userId);
       Budget budget = budgetRepository.findByUserId(userId);


       if (budget == null) {
           budget = new Budget(userId, totalBudget);
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
}
