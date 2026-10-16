package com.mfano.mfes.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mfano.mfes.auth.models.Branch;
import com.mfano.mfes.models.Client;
import com.mfano.mfes.models.Loan;
import com.mfano.mfes.models.Loan.LoanStatus;
import com.mfano.mfes.models.Repayments;
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

    @Transactional
    public void disburseLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow();
        loan.setStatus(Loan.LoanStatus.DISBURSED);
        loan.setDisbursementDate(LocalDate.now());
        loanRepository.save(loan);

        List<Repayments> schedule = repaymentService.generateSchedule(loan);
    }
}
