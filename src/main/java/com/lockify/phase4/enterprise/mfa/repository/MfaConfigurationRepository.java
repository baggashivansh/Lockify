package com.lockify.phase4.enterprise.mfa.repository;

import com.lockify.phase4.enterprise.mfa.entity.MfaConfiguration;
import com.lockify.shared.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MfaConfigurationRepository extends JpaRepository<MfaConfiguration, Long> {

    Optional<MfaConfiguration> findByUser(User user);

    Optional<MfaConfiguration> findByUserId(Long userId);
}
