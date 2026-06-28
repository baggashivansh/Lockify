package com.lockify.phase4.enterprise.mfa.repository;

import com.lockify.phase4.enterprise.mfa.entity.OtpCode;
import com.lockify.shared.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    Optional<OtpCode> findTopByUserAndTypeAndUsedFalseOrderByCreatedAtDesc(User user, String type);
}
