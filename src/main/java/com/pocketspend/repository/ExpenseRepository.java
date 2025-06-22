package com.pocketspend.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pocketspend.model.Expense;


@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {


   // Find all expenses for a specific user
   List<Expense> findByUserId(Long userId);


   @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.userId = :userId")
   double sumExpensesByUserId(@Param("userId") Long userId);


   // Optional: Add more query methods as needed, for example:
   // List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
   // List<Expense> findByUserIdAndTitleContainingIgnoreCase(Long userId, String keyword);
}

