package com.mfano.mfes.auth.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mfes.auth.models.AuditEntry;

public interface AuditRepository extends JpaRepository<AuditEntry, Long> {

    List<AuditEntry> findByAction(String action);
    
}
