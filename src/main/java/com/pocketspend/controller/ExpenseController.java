package com.pocketspend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pocketspend.model.Expense;
import com.pocketspend.service.ExpenseService;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*") // ✅ Tambah kalau frontend hosted pada port lain (seperti 5500 / 3000)
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // ✅ Add Expense
    @PostMapping(value = "/{userId}", consumes = {"multipart/form-data"})
    public ResponseEntity<Expense> createExpense(
            @PathVariable Long userId,
            @RequestParam("title") String title,
            @RequestParam("amount") double amount,
            @RequestParam("expenseDate") String expenseDate,
            @RequestParam("description") String description) {

        LocalDate parsedDate = LocalDate.parse(expenseDate);
        Expense expense = new Expense(userId, title, amount, parsedDate, description);
        Expense savedExpense = expenseService.addExpense(userId, expense);

        return ResponseEntity.ok(savedExpense);
    }

    // ✅ Get All Expenses by User
    @GetMapping("/{userId}")
    public ResponseEntity<List<Expense>> getAllExpenses(@PathVariable Long userId) {
        List<Expense> expenses = expenseService.getExpensesByUserId(userId);
        return ResponseEntity.ok(expenses);
    }

    // ✅ Delete Expense by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id) {
        boolean deleted = expenseService.removeExpense(id);
        if (deleted) {
            return ResponseEntity.ok("Expense deleted successfully.");
        } else {
            return ResponseEntity.notFound().build(); // ID not found
        }
    }
}
