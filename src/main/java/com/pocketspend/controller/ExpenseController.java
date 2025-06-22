package com.pocketspend.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pocketspend.model.Expense;
import com.pocketspend.model.User;
import com.pocketspend.repository.UserRepository;
import com.pocketspend.service.ExpenseService;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*") // Tambah kalau frontend hosted pada port lain (seperti 5500 / 3000)
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserRepository userRepository;

    // ✅ Add Expense with Image
    @PostMapping(value = "/{userId}", consumes = { "multipart/form-data" })
    public ResponseEntity<Expense> createExpense(
            @PathVariable Long userId,
            @RequestParam("title") String title,
            @RequestParam("amount") double amount,
            @RequestParam("expenseDate") String expenseDate,
            @RequestParam("description") String description,
            @RequestParam(value = "receiptImage", required = false) MultipartFile receiptImage) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        LocalDate parsedDate = LocalDate.parse(expenseDate);
        Expense expense = new Expense(user, title, amount, parsedDate, description);

        if (receiptImage != null && !receiptImage.isEmpty()) {
            try {
                expense.setReceiptImageData(receiptImage.getBytes());
                expense.setReceiptImageType(receiptImage.getContentType());
            } catch (IOException e) {
                // For simplicity, we'll just print the error. In a real app, you'd log this.
                System.err.println("Error reading file: " + e.getMessage());
                // Optionally return an error response
                return ResponseEntity.internalServerError().build();
            }
        }

        Expense savedExpense = expenseService.addExpense(userId, expense);
        return ResponseEntity.ok(savedExpense);
    }

    // ✅ Get All Expenses by User
    @GetMapping("/{userId}")
    public ResponseEntity<List<Expense>> getAllExpenses(@PathVariable Long userId) {
        List<Expense> expenses = expenseService.getExpensesByUserId(userId);
        return ResponseEntity.ok(expenses);
    }

    // ✅ Get Expense Image
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getExpenseImage(@PathVariable Long id) {
        Optional<Expense> expenseOptional = expenseService.getExpenseById(id);
        if (expenseOptional.isPresent()) {
            Expense expense = expenseOptional.get();
            if (expense.getReceiptImageData() != null && expense.getReceiptImageType() != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(expense.getReceiptImageType()))
                        .body(expense.getReceiptImageData());
            }
        }
        return ResponseEntity.notFound().build();
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
