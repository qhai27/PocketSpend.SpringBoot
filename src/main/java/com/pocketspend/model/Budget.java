package com.pocketspend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "total_budget", nullable = false)
    private double totalBudget;

    @Column(name = "total_expenses", nullable = false)
    private double totalExpenses;

    @Column(name = "budget_left", nullable = false)
    private double budgetLeft;

    public Budget() {
        // Default constructor
    }

    public Budget(User user, double totalBudget) {
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
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
