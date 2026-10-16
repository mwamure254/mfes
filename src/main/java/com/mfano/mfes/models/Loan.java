package com.mfano.mfes.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.mfano.mfes.auth.models.BaseObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "loans", indexes = {
        @Index(name = "idx_loan_status", columnList = "status"),
        @Index(name = "idx_loan_client", columnList = "client_id")
})
public class Loan extends BaseObject {
    public enum LoanStatus {
        PENDING, APPROVED, REJECTED, DISBURSED, CLOSED
    }

    // ===== RELATIONSHIPS =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // ===== LOAN DETAILS =====
    @DecimalMin(value = "1000.0")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Min(1)
    @Max(60)
    @Column(name = "term_months", nullable = false)
    private Integer termMonths = 12;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Digits(integer = 3, fraction = 2)
    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate = new BigDecimal("10.00"); // 10%

    @Size(max = 500)
    @Column(columnDefinition = "TEXT")
    private String product;
    private String document;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status = LoanStatus.PENDING;

    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate = LocalDate.now();

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "disbursement_date")
    private LocalDate disbursementDate;

    // ===== LOCATION FOR FIELD VERIFICATION =====
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Digits(integer = 3, fraction = 6)
    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Digits(integer = 4, fraction = 6)
    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    @Size(max = 255)
    @Column(name = "land_mark")
    private String landMark; // "Next to Malindi Market"

    private BigDecimal monthlyRepayment;
    private BigDecimal totalAmount;
    private BigDecimal totalInterest;

    @PrePersist
    protected void onCreate() {
        if (amount == null || termMonths == null || interestRate == null) {
            return;
        } else {
            if (monthlyRepayment != null) {
                this.monthlyRepayment = getMonthlyRepayment();
            }
            if (totalAmount != null) {
                this.totalAmount = getTotalAmount();
            }
            if (totalInterest != null) {
                this.totalInterest = getTotalInterest();
            }
        }
    }

    // Calculate monthly repayment: P*r/(1-(1+r)^-n)
    @Transient
    public BigDecimal getMonthlyRepayment() {
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal one = BigDecimal.ONE;
        BigDecimal power = one.add(monthlyRate).pow(termMonths);

        monthlyRepayment = amount.multiply(monthlyRate)
                .multiply(power)
                .divide(power.subtract(one), 2, RoundingMode.HALF_UP);
        return monthlyRepayment;
    }

    @Transient
    private BigDecimal getTotalAmount() {
        totalAmount = getMonthlyRepayment().multiply(BigDecimal.valueOf(termMonths));
        return totalAmount;
    }

    @Transient
    private BigDecimal getTotalInterest() {
        totalInterest = getTotalAmount().subtract(amount);
        return totalInterest;
    }

}