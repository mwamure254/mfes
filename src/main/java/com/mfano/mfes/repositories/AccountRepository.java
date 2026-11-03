package com.mfano.mfes.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mfes.models.Accounts;

public interface AccountRepository extends JpaRepository<Accounts, Long>{

    List<Accounts> findByClient(Long id);
    
}
