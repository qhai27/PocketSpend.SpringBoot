package com.pocketspend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pocketspend.model.Expense;
import com.pocketspend.service.ExpenseService;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping(value = "/{userId}", consumes = {"multipart/form-data"})
    public ResponseEntity<Expense> createExpense(
            @PathVariable Long userId,
            @RequestParam("title") String title,
            @RequestParam("amount") double amount,
            @RequestParam("expenseDate") String expenseDate,
            @RequestParam("description") String description,
            @RequestPart(value = "receiptImage", required = false) MultipartFile receiptImage) {
        // Handle file storage and save expense
        // Example: String imageUrl = fileStorageService.save(receiptImage);
        // Expense expense = new Expense(..., imageUrl, ...);
        // return ResponseEntity.ok(expenseService.addExpense(userId, expense));
        // Implement your logic here
        return null;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Expense>> getAllExpenses(@PathVariable Long userId) {
        List<Expense> expenses = expenseService.getExpensesByUserId(userId);
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.removeExpense(id);
        return ResponseEntity.noContent().build();
    }
}