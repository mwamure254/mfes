package com.mfano.mfes.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mfes.auth.models.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long>{ 
        public Profile findByUser(Long userId);
}
