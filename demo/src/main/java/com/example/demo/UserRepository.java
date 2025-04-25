package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Checks if an email already exists (used in registration)
    boolean existsByEmail(String email);

    // Fetches the user by email (used in login)
    Optional<UserEntity> findByEmail(String email);
}
