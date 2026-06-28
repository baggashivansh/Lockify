package com.lockify.phase3.session.repository;

import com.lockify.phase3.session.entity.TrustedDevice;
import com.lockify.shared.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, Long> {

    List<TrustedDevice> findByUserOrderByLastUsedAtDesc(User user);

    Optional<TrustedDevice> findByUserAndDeviceId(User user, String deviceId);
}
