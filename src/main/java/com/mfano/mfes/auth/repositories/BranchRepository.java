package com.mfano.mfes.auth.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mfes.auth.models.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    Branch findByName(String name);
}
