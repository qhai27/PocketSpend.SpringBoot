package com.pocketspend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private double totalBudget;

    private double totalExpenses;

    private double budgetLeft;

    public Budget() {
    }

    public Budget(Long userId, double totalBudget) {
        this.userId = userId;
        this.totalBudget = totalBudget;
        this.totalExpenses = 0.0;
        this.budgetLeft = totalBudget;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
        this.budgetLeft = totalBudget - this.totalExpenses;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
        this.budgetLeft = this.totalBudget - totalExpenses;
    }

    public double getBudgetLeft() {
        return budgetLeft;
    }

    public void setBudgetLeft(double budgetLeft) {
        this.budgetLeft = budgetLeft;
    }
}