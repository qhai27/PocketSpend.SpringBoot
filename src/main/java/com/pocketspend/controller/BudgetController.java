package com.pocketspend.controller;

import com.pocketspend.model.Budget;
import com.pocketspend.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping("/{userId}")
    public ResponseEntity<Budget> setBudget(@PathVariable Long userId, @RequestBody Budget budget) {
        Budget createdBudget = budgetService.setBudget(userId, budget.getTotalBudget());
        return ResponseEntity.ok(createdBudget);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Budget> getBudget(@PathVariable Long userId) {
        Budget budget = budgetService.getBudget(userId);
        return budget != null ? ResponseEntity.ok(budget) : ResponseEntity.notFound().build();
    }
}