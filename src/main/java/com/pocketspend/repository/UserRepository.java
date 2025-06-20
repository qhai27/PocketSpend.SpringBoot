package com.pocketspend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pocketspend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
