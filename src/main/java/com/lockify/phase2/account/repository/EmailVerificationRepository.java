package com.lockify.phase2.account.repository;

import com.lockify.phase2.account.entity.EmailVerification;
import com.lockify.shared.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByTokenHash(String tokenHash);

    Optional<EmailVerification> findFirstByUserAndUsedFalseOrderByCreatedAtDesc(User user);
}
