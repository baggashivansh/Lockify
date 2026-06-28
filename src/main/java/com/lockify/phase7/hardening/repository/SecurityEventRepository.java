package com.lockify.phase7.hardening.repository;

import com.lockify.phase7.hardening.entity.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long> {

    List<SecurityEvent> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<SecurityEvent> findAllByOrderByCreatedAtDesc();

    long countByUserIdAndEventTypeAndCreatedAtAfter(Long userId, String eventType, Instant since);
}
