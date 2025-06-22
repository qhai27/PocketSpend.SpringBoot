package com.pocketspend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pocketspend.model.Expense;
import com.pocketspend.model.User;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

   // Find all expenses for a specific user
   List<Expense> findByUser(User user);

   // Find all expenses for a specific user by user ID
   @Query("SELECT e FROM Expense e WHERE e.user.id = :userId")
   List<Expense> findByUserId(@Param("userId") Long userId);

   @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId")
   double sumExpensesByUserId(@Param("userId") Long userId);

   // Optional: Add more query methods as needed, for example:
   // List<Expense> findByUserAndDateBetween(User user, LocalDate startDate,
   // LocalDate endDate);
   // List<Expense> findByUserAndTitleContainingIgnoreCase(User user, String
   // keyword);
}
