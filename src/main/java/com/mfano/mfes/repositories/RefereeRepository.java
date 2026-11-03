package com.mfano.mfes.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mfes.models.Referee;

public interface RefereeRepository extends JpaRepository<Referee, Long>{

 public List<Referee> findByClient(Long id);

} 
