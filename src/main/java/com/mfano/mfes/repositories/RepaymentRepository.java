package com.mfano.mfes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mfes.models.Repayments;

public interface RepaymentRepository extends JpaRepository<Repayments, Long>{
    
}
