package com.pocketspend.service;

import com.pocketspend.model.Expense;
import com.pocketspend.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> getExpenses() {
        return expenseRepository.findAll();
    }

    public void removeExpense(Long id) {
        expenseRepository.deleteById(id);
    }
}