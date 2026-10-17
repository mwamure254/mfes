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

        if (l.getStatus() == LoanStatus.APPROVED) {
            loan.setApprovalDate(LocalDate.now());
        } else if (l.getStatus() == LoanStatus.DISBURSED) {
            loan.setDisbursementDate(LocalDate.now());
            // Call repayments calculator
            repaymentService.generateSchedule(loan);
        } else if (l.getStatus() == LoanStatus.CLOSED) {
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

}
