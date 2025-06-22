package com.pocketspend.repository;

import com.pocketspend.model.Budget;
import com.pocketspend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Budget findByUser(User user);

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId")
    Budget findByUserId(@Param("userId") Long userId);
}