package com.pocketspend.repository;

import com.pocketspend.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Find all expenses for a specific user
    List<Expense> findByUserId(Long userId);

    // Optional: Add more query methods as needed, for example:
    // List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    // List<Expense> findByUserIdAndTitleContainingIgnoreCase(Long userId, String keyword);
}
