package com.mfano.mfes.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mfano.mfes.models.Client;
import com.mfano.mfes.models.Loan;
import com.mfano.mfes.models.Loan.LoanStatus;
import com.mfano.mfes.repositories.LoanRepository;

import jakarta.transaction.Transactional;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final RepaymentService repaymentService;

    public LoanService(LoanRepository loanRepository, RepaymentService repaymentService) {
        this.loanRepository = loanRepository;
        this.repaymentService = repaymentService;
    }

    public List<Loan> getAll() {
        return loanRepository.findAllByOrderByCreatedAtDesc();
    }

    public Loan save(Loan l) {
        return loanRepository.save(l);
    }

    public void approve(Long id, Loan l) {
        Loan loan = getById(id).orElse(l);

        loan.setStatus("APPROVED");
        loan.setApprovalDate(LocalDate.now());
        loanRepository.save(l);
    }

    public Loan disburse(Long id, Loan l) {
        Loan loan = getById(id).orElse(l);

        loan.setDisbursementDate(LocalDate.now());
        loan.setTotalAmount(loan.getTotalAmount());
        loan.setMonthlyRepayment(loan.getMonthlyRepayment());
        loan.setTotalInterest(loan.getTotalInterest());
        loan.setStatus("DISBURSED");
        // Call repayments calculator
        repaymentService.generateSchedule(loan);
        return loanRepository.save(l);
    }

    @Transactional
    public Loan update(Long id, Loan l) {
        Loan loan = getById(id).orElse(l);
        loan.setAmount(l.getAmount());
        loan.setClient(l.getClient());
        loan.setDocument(l.getDocument());
        loan.setInterestRate(l.getInterestRate());
        loan.setLandMark(l.getLandMark());
        loan.setStatus(l.getStatus());
        loan.setTermMonths(l.getTermMonths());
        loan.setProduct(l.getProduct());
        loan.setLatitude(l.getLatitude());
        loan.setLongitude(l.getLongitude());

        if (l.getStatus() == "APPROVED") {
            approve(id, loan);
        } else if (l.getStatus() == "DISBURSED") {
            disburse(id, loan);
        } else if (l.getStatus() == "CLOSED") {
            loan.setCloseDate(LocalDate.now());
        }

        return save(loan);
    }

    public Optional<Loan> getById(Long id) {
        return loanRepository.findById(id);
    }

    public void delete(Long id) {
        Loan l = getById(id).orElse(null);
        loanRepository.delete(l);
    }

    public Loan getByReference(String ref) {
        return loanRepository.findByReference(ref);
    }

    public List<Loan> getByStatus(LoanStatus s) {
        return loanRepository.findByStatus(s);
    }

    public List<Loan> getByClient(Client c) {
        return loanRepository.findByClient(c);
    }

    public List<Loan> getByOverdue(LocalDate date) {
        return loanRepository.findLoansWithOverdueInstallments(date);
    }

    public List<Loan> getByOverdueBetween(LocalDate start, LocalDate end) {
        return loanRepository.findOverdueBetween(start, end);
    }

    public long countPendingLoansCreatedToday(String status) {
        LocalDate today = LocalDate.now(ZoneId.of("Africa/Nairobi"));

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfTomorrow = today.plusDays(1).atStartOfDay();

        return loanRepository.countLoansCreatedTodayByStatus(
                status,
                startOfDay,
                startOfTomorrow);
    }
}
