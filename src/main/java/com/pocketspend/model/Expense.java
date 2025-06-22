package com.pocketspend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 1000)
    private String description;

    @Lob
    @Column(name = "receipt_image_data", columnDefinition = "LONGBLOB")
    private byte[] receiptImageData;

    @Column(name = "receipt_image_type")
    private String receiptImageType;

    public Expense() {
        // Default constructor
    }

    public Expense(User user, String title, double amount, LocalDate date, String description) {
        this.user = user;
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    // --- Getters and Setters ---
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getReceiptImageData() {
        return receiptImageData;
    }

    public void setReceiptImageData(byte[] receiptImageData) {
        this.receiptImageData = receiptImageData;
    }

    public String getReceiptImageType() {
        return receiptImageType;
    }

    public void setReceiptImageType(String receiptImageType) {
        this.receiptImageType = receiptImageType;
    }
}
