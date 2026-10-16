package com.mfano.mfes.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mfano.mfes.models.Loan;
import com.mfano.mfes.models.Repayments;
import com.mfano.mfes.repositories.RepaymentRepository;

@Service
public class RepaymentService {
    private final RepaymentRepository repaymentRepository;

    public RepaymentService(RepaymentRepository repaymentRepository) {
        this.repaymentRepository = repaymentRepository;
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
     *     if(r.getDueDate().getDayOfWeek() == DayOfWeek.SUNDAY){
        r.setDueDate(r.getDueDate().plusDays(1));
    }
     */
}
