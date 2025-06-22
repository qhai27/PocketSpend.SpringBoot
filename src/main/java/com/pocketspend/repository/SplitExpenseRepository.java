package com.pocketspend.repository;

import com.pocketspend.model.SplitExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SplitExpenseRepository extends JpaRepository<SplitExpense, Long> {

    @Query("SELECT se FROM SplitExpense se WHERE se.group.id = :groupId ORDER BY se.createdAt DESC")
    List<SplitExpense> findByGroupIdOrderByCreatedAtDesc(@Param("groupId") Long groupId);

    @Query("SELECT se FROM SplitExpense se WHERE se.paidBy.id = :memberId ORDER BY se.createdAt DESC")
    List<SplitExpense> findByPaidByOrderByCreatedAtDesc(@Param("memberId") Long memberId);

    @Query("SELECT se FROM SplitExpense se JOIN se.splits es WHERE es.member.id = :memberId ORDER BY se.createdAt DESC")
    List<SplitExpense> findByMemberInvolvedOrderByCreatedAtDesc(@Param("memberId") Long memberId);
}