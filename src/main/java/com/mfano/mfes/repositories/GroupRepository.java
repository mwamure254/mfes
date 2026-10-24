package com.mfano.mfes.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mfes.models.Group;
import com.mfano.mfes.models.Portfolio;

public interface GroupRepository extends JpaRepository<Group,Long>{

    List<Group> findByCreatedBy(String user);

    List<Group> findByFolio(Portfolio p);

    List<Group> findAllByOrderByCreatedAtDesc();

    Group findByName(String name);

}
