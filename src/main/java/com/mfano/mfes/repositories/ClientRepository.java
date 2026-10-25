package com.mfano.mfes.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mfes.auth.models.Branch;
import com.mfano.mfes.models.Client;
import com.mfano.mfes.models.Group;

public interface ClientRepository extends JpaRepository<Client,Long>{

    List<Client> findAllByOrderByCreatedAtDesc();

    List<Client> findByGroup(Group g);

	List<Client> findByBranch(Branch b);

    Client findByIdNumber(String n);

    Client findByPhone(String phone);
    
}
