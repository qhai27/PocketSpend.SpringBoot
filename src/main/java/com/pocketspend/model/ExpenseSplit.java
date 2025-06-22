package com.pocketspend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "expense_splits")
public class ExpenseSplit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id")
    @JsonIgnore
    private SplitExpense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private GroupMember member;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;

    // Constructors
    public ExpenseSplit() {
    }

    public ExpenseSplit(SplitExpense expense, GroupMember member, BigDecimal amount) {
        this.expense = expense;
        this.member = member;
        this.amount = amount;
    }

    public ExpenseSplit(SplitExpense expense, GroupMember member, BigDecimal amount, BigDecimal percentage) {
        this.expense = expense;
        this.member = member;
        this.amount = amount;
        this.percentage = percentage;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SplitExpense getExpense() {
        return expense;
    }

    public void setExpense(SplitExpense expense) {
        this.expense = expense;
    }

    public GroupMember getMember() {
        return member;
    }

    public void setMember(GroupMember member) {
        this.member = member;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}