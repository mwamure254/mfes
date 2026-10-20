package com.mfano.mfes.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mfano.mfes.models.Loan;
import com.mfano.mfes.models.Repayments;
import com.mfano.mfes.repositories.LoanRepository;
import com.mfano.mfes.repositories.RepaymentRepository;

import jakarta.transaction.Transactional;

@Service
public class RepaymentService {
    private final RepaymentRepository repaymentRepository;
    private final LoanRepository loanRepository;

    public RepaymentService(RepaymentRepository repaymentRepository, LoanRepository loanRepository) {
        this.repaymentRepository = repaymentRepository;
        this.loanRepository = loanRepository;
    }

    public List<Repayments> generateSchedule(Loan loan) {
        List<Repayments> schedule = new ArrayList<>();

        BigDecimal monthlyRepayment = loan.getMonthlyRepayment();
        BigDecimal principalPerMonth = loan.getAmount().divide(BigDecimal.valueOf(loan.getTermMonths()), 2,
                RoundingMode.HALF_UP);

        LocalDate startDate = loan.getDisbursementDate() != null ? loan.getDisbursementDate()
                : loan.getApplicationDate();

        for (int i = 1; i <= loan.getTermMonths(); i++) {
            Repayments r = new Repayments();
            r.setLoan(loan);
            r.setInstallmentNo(i);

            // Add months. If weekly: startDate.plusWeeks(i)
            r.setDueDate(startDate.plusMonths(i));

            r.setPrincipalAmount(principalPerMonth);
            r.setInterestAmount(monthlyRepayment.subtract(principalPerMonth));
            r.setTotalAmount(monthlyRepayment);

            schedule.add(r);
        }
        return repaymentRepository.saveAll(schedule);
    }

    /*
     * // Get the group's meeting day
     * DayOfWeek meetingDay = DayOfWeek.MONDAY; // from loan.client.group.meetingDay
     * 
     * LocalDate firstDueDate =
     * startDate.with(TemporalAdjusters.nextOrSame(meetingDay));
     * 
     * for(
     * int i = 1;i<=loan.getTermMonths()*4;i++)
     * { // 12 months * 4 weeks
     * r.setDueDate(firstDueDate.plusWeeks(i));
     * // ...
     * }
     * if(r.getDueDate().getDayOfWeek() == DayOfWeek.SUNDAY){
     * r.setDueDate(r.getDueDate().plusDays(1));
     * }
     */

    @Transactional
    public void repayLoan(
            Long loanAccountId,
            BigDecimal payment) {

        if (payment == null || payment.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Repayment must be greater than zero.");
        }

        // Replace this with your actual repository method.
        Loan loan = loanRepository
                .findById(loanAccountId).orElse(null);

        BigDecimal outstanding = loan.getOlb()
                .setScale(2, RoundingMode.HALF_UP);

        if (outstanding.signum() <= 0) {
            throw new IllegalStateException(
                    "This loan has no outstanding balance.");
        }

        if (payment.compareTo(outstanding) > 0) {
            throw new IllegalArgumentException(
                    "Repayment exceeds the outstanding loan balance.");
        }

        BigDecimal newBalance = outstanding.subtract(payment);

        loan.setOlb(newBalance);

        if (newBalance.signum() == 0) {
            loan.setStatus("CLEARED");
        }

        loanRepository.save(loan);

        // Persist a LoanRepayment ledger entry here, including:
        // loan account, amount, reference, payment date,
        // principal, interest, fees, and posting user.
    }

    public List<Repayments> findAllUnpaid() {
        return repaymentRepository.findAllUnpaid();
    }

    public List<Repayments> findUnpaidBetween(LocalDate startDate, LocalDate endDate) {
        return repaymentRepository.findUnpaidBetween(startDate, endDate);
    }

    public List<Repayments> findOverdue(LocalDate today) {
        return repaymentRepository.findOverdue(today);
    }

    // Calculations of penalties
    private static final BigDecimal WEEKLY_RATE = new BigDecimal("0.02");

    @Transactional
    public void calculatePenalty(Long installmentId) {

        Repayments installment = repaymentRepository
                .findById(installmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Installment not found"));

        LocalDate today = LocalDate.now();
        LocalDate dueDate = installment.getDueDate();

        if (!today.isAfter(dueDate)) {
            return;
        }

        BigDecimal unpaid = installment.getAmountDue()
                .subtract(installment.getAmountPaid())
                .max(BigDecimal.ZERO);

        // No new penalty after the installment is cleared.
        if (unpaid.signum() == 0) {
            return;
        }

        long overdueWeeks = ChronoUnit.DAYS.between(
                dueDate, today) / 7;

        int newWeeks = (int) overdueWeeks
                - installment.getPenaltyWeeksCharged();

        if (newWeeks <= 0) {
            return;
        }

        installment.setPenaltyWeeksCharged(
                installment.getPenaltyWeeksCharged()
                        + newWeeks);

        BigDecimal weeklyPenalty = unpaid
                .multiply(WEEKLY_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal additionalPenalty = weeklyPenalty
                .multiply(BigDecimal.valueOf(newWeeks));

        installment.setPenalty(
                installment.getPenalty()
                        .add(additionalPenalty));

        repaymentRepository.save(installment);
    }
}
