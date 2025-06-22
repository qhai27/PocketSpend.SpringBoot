package com.pocketspend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pocketspend.model.Budget;
import com.pocketspend.service.BudgetService;


@RestController
@RequestMapping("/api/budgets")
public class BudgetController {


   @Autowired
   private BudgetService budgetService;


   // Set or update a user's budget
   @PostMapping("/{userId}")
   public ResponseEntity<Budget> setOrUpdateBudget(
           @PathVariable Long userId,
           @RequestBody Budget budgetRequest
   ) {
       if (budgetRequest.getTotalBudget() <= 0) {
           return ResponseEntity.badRequest().body(null);
       }


       Budget savedBudget = budgetService.setBudget(userId, budgetRequest.getTotalBudget());
       return ResponseEntity.ok(savedBudget);
   }


   // Get budget by user ID
   @GetMapping("/{userId}")
   public ResponseEntity<Budget> getBudget(@PathVariable Long userId) {
       Budget budget = budgetService.getBudget(userId);
       return budget != null ? ResponseEntity.ok(budget) : ResponseEntity.notFound().build();
   }
}
