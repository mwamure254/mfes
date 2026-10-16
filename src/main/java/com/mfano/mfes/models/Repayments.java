package com.mfano.mfes.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter 
@Getter
@Table(name = "repayments")
public class Repayments {
    public enum RepaymentStatus { PENDING, PAID, LATE, WAIVED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "inum")
    private Integer installmentNo; // 1,2,3...12

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(precision = 12, scale = 2)
    private BigDecimal principalAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal interestAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalAmount; // principal + interest

    @Column(precision = 12, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private RepaymentStatus status = RepaymentStatus.PENDING;

    @Column(name = "paid_date")
    private LocalDate paidDate;
}