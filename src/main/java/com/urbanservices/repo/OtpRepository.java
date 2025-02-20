package com.urbanservices.repo;

import com.urbanservices.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmailAndExpiresAtAfter(String email, LocalDateTime currentTime);

    void deleteByEmail(String email); // Cleanup after verification
}