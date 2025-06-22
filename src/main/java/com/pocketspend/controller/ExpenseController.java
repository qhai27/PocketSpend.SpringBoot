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

        try {
            System.out.println("Received expense request for user: " + userId);
            System.out.println("Title: " + title + ", Amount: " + amount + ", Date: " + expenseDate);

            if (receiptImage != null) {
                System.out.println("Image received: " + receiptImage.getOriginalFilename() +
                        ", Size: " + receiptImage.getSize() +
                        ", Content Type: " + receiptImage.getContentType());
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

            LocalDate parsedDate = LocalDate.parse(expenseDate);
            Expense expense = new Expense(user, title, amount, parsedDate, description);

            if (receiptImage != null && !receiptImage.isEmpty()) {
                try {
                    // Validate file size (max 10MB)
                    if (receiptImage.getSize() > 10 * 1024 * 1024) {
                        System.err.println("Image file too large: " + receiptImage.getSize() + " bytes");
                        return ResponseEntity.badRequest().build();
                    }

                    // Validate content type
                    if (receiptImage.getContentType() == null || !receiptImage.getContentType().startsWith("image/")) {
                        System.err.println("Invalid content type: " + receiptImage.getContentType());
                        return ResponseEntity.badRequest().build();
                    }

                    expense.setReceiptImageData(receiptImage.getBytes());
                    expense.setReceiptImageType(receiptImage.getContentType());
                    System.out.println("Image processed successfully");
                } catch (IOException e) {
                    System.err.println("Error reading image file: " + e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.internalServerError().build();
                }
            }

            Expense savedExpense = expenseService.addExpense(userId, expense);
            System.out.println("Expense saved successfully with ID: " + savedExpense.getId());
            return ResponseEntity.ok(savedExpense);

        } catch (Exception e) {
            System.err.println("Error creating expense: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
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
