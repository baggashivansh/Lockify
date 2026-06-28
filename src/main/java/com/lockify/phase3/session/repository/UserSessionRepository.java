package com.lockify.phase3.session.repository;

import com.lockify.phase3.session.entity.UserSession;
import com.lockify.shared.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    List<UserSession> findByUserAndActiveTrueOrderByLastActivityDesc(User user);

    Optional<UserSession> findBySessionIdAndUser(String sessionId, User user);

    Optional<UserSession> findBySessionId(String sessionId);

    /** Logout everywhere - saari active sessions deactivate karo */
    @Modifying
    @Query("UPDATE UserSession s SET s.active = false, s.logoutAt = CURRENT_TIMESTAMP WHERE s.user = :user AND s.active = true")
    void deactivateAllByUser(User user);
}
