package com.mfano.mfes.utils;

import org.springframework.stereotype.Service;

@Service
public class ParService {
    /*
     * public ParDto calculatePar(Long branchId, Long portfolioId, Long groupId,
     * LocalDate asAtDate){
     * 
     * List<Loan> loans;
     * if(groupId != null) loans = loanRepo.findByGroupAndActive(groupId, asAtDate);
     * else if(portfolioId != null) loans =
     * loanRepo.findByPortfolioAndActive(portfolioId, asAtDate);
     * else if(branchId != null) loans = loanRepo.findByBranchAndActive(branchId,
     * asAtDate);
     * else loans = loanRepo.findAllActive(asAtDate);
     * 
     * BigDecimal totalPortfolio = BigDecimal.ZERO;
     * BigDecimal amountInArrears = BigDecimal.ZERO;
     * BigDecimal par1_30 = BigDecimal.ZERO, par31_60 = BigDecimal.ZERO, par61_90 =
     * BigDecimal.ZERO, parOver90 = BigDecimal.ZERO;
     * int loansInArrears = 0;
     * List<ParLoanDto> parLoans = new ArrayList<>();
     * 
     * for(Loan loan : loans){
     * BigDecimal balance = loan.getOutstandingBalance(asAtDate);
     * totalPortfolio = totalPortfolio.add(balance);
     * 
     * int daysInArrears = loan.getDaysInArrears(asAtDate);
     * if(daysInArrears > 0){
     * loansInArrears++;
     * amountInArrears = amountInArrears.add(balance);
     * 
     * if(daysInArrears <= 30) par1_30 = par1_30.add(balance);
     * else if(daysInArrears <= 60) par31_60 = par31_60.add(balance);
     * else if(daysInArrears <= 90) par61_90 = par61_90.add(balance);
     * else parOver90 = parOver90.add(balance);
     * 
     * parLoans.add(new ParLoanDto(loan, daysInArrears));
     * }
     * }
     * 
     * BigDecimal parPercent = totalPortfolio.compareTo(BigDecimal.ZERO) > 0
     * ? amountInArrears.multiply(BigDecimal.valueOf(100)).divide(totalPortfolio, 2,
     * RoundingMode.HALF_UP)
     * : BigDecimal.ZERO;
     * 
     * return new ParDto(totalPortfolio, amountInArrears, parPercent,
     * loansInArrears,
     * par1_30, par31_60, par61_90, parOver90, parLoans);
     * }
     */
}
