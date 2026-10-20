package com.mfano.mfes.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mfes.models.MemberCollection;

public interface MemberCollectionRepository extends JpaRepository<MemberCollection, Long>{

    Optional<MemberCollection> findByMemberId(Long id);
    
}
