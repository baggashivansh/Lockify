package com.lockify.phase7.hardening.repository;

import com.lockify.phase7.hardening.entity.DeviceFingerprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceFingerprintRepository extends JpaRepository<DeviceFingerprint, Long> {

    Optional<DeviceFingerprint> findByUserIdAndFingerprintHash(Long userId, String fingerprintHash);
}
