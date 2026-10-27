package com.mfano.mfes.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data 
public class ParDto {
    BigDecimal totalPortfolio;
    BigDecimal amountInArrears;
    BigDecimal parPercent;
    int loansInArrearsCount;
    BigDecimal current;
    BigDecimal par1_30, par31_60, par61_90, parOver90;
    //List<ParLoanDto> parLoans;
}