package com.dxc.iotbackend.repository;




import com.dxc.iotbackend.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Checks if an email already exists (used in registration)
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // Fetches the user by email (used in login)
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByResetTokenAndResetTokenExpiryAfter(String resetToken, LocalDateTime now);


}