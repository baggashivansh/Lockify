package com.lockify.phase2.account.repository;

import com.lockify.phase2.account.entity.PasswordHistory;
import com.lockify.shared.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {

    List<PasswordHistory> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
