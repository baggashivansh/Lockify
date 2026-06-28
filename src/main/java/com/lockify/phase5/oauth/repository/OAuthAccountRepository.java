package com.lockify.phase5.oauth.repository;

import com.lockify.phase5.oauth.entity.OAuthAccount;
import com.lockify.shared.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

    List<OAuthAccount> findByUser(User user);

    List<OAuthAccount> findByUserId(Long userId);

    Optional<OAuthAccount> findByProviderAndProviderUserId(String provider, String providerUserId);
}
