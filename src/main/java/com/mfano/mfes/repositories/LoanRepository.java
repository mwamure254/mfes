package com.mfano.mfes.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mfes.models.Client;
import com.mfano.mfes.models.Loan;
import com.mfano.mfes.models.Loan.LoanStatus;

public interface LoanRepository extends JpaRepository<Loan, Long>{

    List<Loan> findAllByOrderByCreatedAtDesc();

    Loan findByReference(String ref);
    List<Loan> findByStatus(LoanStatus s);

    List<Loan> findByClient(Client c);
    
}
