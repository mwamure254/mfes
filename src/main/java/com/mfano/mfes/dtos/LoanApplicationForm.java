package com.mfano.mfes.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mfano.mfes.models.Collaterals;
import com.mfano.mfes.models.Guarantors;

import lombok.Data;

@Data
public class LoanApplicationForm {
    private Long clientId;
    private Long loanProductId;
    private BigDecimal amount;
    private Integer termMonths;
    private BigDecimal interestRate;
    private LocalDate applicationDate;
    private String loanPurpose;
    private String repaymentFrequency;
    private LocalDate expectedDisbursementDate;
    private String remarks;

    private List<Collaterals> collaterals = new ArrayList<>();
    private List<Guarantors> guarantors = new ArrayList<>();
}
