package com.mfano.mfes.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mfes.models.GroupCollection;

public interface GroupCollectionRepository extends JpaRepository<GroupCollection, Long>{

    List<GroupCollection> findAllByOrderByCreatedAtDesc();

}
