package com.pocketspend.repository;

import com.pocketspend.model.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {

    @Query("SELECT es FROM ExpenseSplit es WHERE es.expense.id = :expenseId")
    List<ExpenseSplit> findByExpenseId(@Param("expenseId") Long expenseId);

    @Query("SELECT es FROM ExpenseSplit es WHERE es.member.id = :memberId")
    List<ExpenseSplit> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT COALESCE(SUM(es.amount), 0) FROM ExpenseSplit es WHERE es.member.id = :memberId")
    BigDecimal getTotalAmountOwedByMember(@Param("memberId") Long memberId);

    @Query("SELECT COALESCE(SUM(se.amount), 0) FROM SplitExpense se WHERE se.paidBy.id = :memberId")
    BigDecimal getTotalAmountPaidByMember(@Param("memberId") Long memberId);
}