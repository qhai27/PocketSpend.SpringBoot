package com.pocketspend.repository;


import com.pocketspend.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Budget findByUserId(Long userId);
}