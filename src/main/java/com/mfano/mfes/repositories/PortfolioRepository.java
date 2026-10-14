package com.mfano.mfes.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mfes.auth.models.User;
import com.mfano.mfes.models.Portfolio;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long>{

    List<Portfolio> findByCreatedBy(String user);

    List<Portfolio> findByUser(User user);

    Portfolio findByName(String name);

    List<Portfolio> findAllByOrderByCreatedAtDesc();
    
}
