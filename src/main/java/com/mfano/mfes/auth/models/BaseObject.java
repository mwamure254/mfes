package com.mfano.mfes.auth.models;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import com.mfano.mfes.config.CustomUserDetails;

@Data
@MappedSuperclass
public abstract class BaseObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    @Column(unique = true)
    private String reference; // e.g. LN-20261016-0001

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (reference == null) {
            this.reference = String.valueOf(ThreadLocalRandom.current().nextLong(1_000_000_000L, 9_000_000_000L));
        }
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            this.createdBy = userDetails.getEmail();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }
}