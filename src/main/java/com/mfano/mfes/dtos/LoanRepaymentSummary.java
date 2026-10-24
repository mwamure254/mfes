package com.mfano.mfes.dtos;

import java.math.BigDecimal;

public record LoanRepaymentSummary(
        Long loanId,
        Long installmentCount,
        Integer lastInstallmentNo,
        BigDecimal principalAmount,
        BigDecimal interestAmount,
        BigDecimal totalAmount,
        BigDecimal amountPaid,
        BigDecimal penalty,
        BigDecimal outstandingBalance
) {}