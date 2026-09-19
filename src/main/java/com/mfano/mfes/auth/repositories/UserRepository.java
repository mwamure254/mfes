package com.mfano.mfes.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mfes.auth.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByEmailAndPassword(String email, String password);

    User findByBranch_Id(Long storeId);

}
