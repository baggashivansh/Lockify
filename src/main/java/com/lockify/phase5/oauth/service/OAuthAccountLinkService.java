package com.lockify.phase5.oauth.service;

import com.lockify.phase5.oauth.dto.OAuthLinkedAccountResponse;
import com.lockify.phase5.oauth.entity.OAuthAccount;
import com.lockify.phase5.oauth.repository.OAuthAccountRepository;
import com.lockify.shared.domain.entity.User;
import com.lockify.shared.domain.repository.UserRepository;
import com.lockify.shared.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * OAuth accounts ko local users se link/unlink karta hai.
 */
@Service
@RequiredArgsConstructor
public class OAuthAccountLinkService {

    private final OAuthAccountRepository oauthAccountRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<OAuthLinkedAccountResponse> getLinkedAccounts(Long userId) {
        return oauthAccountRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public OAuthAccount linkAccount(Long userId, String provider, OAuth2User oauthUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("User nahi mila"));

        String providerUserId = oauthUser.getName();
        String email = oauthUser.getAttribute("email");

        return oauthAccountRepository.findByProviderAndProviderUserId(provider, providerUserId)
                .orElseGet(() -> oauthAccountRepository.save(OAuthAccount.builder()
                        .user(user)
                        .provider(provider)
                        .providerUserId(providerUserId)
                        .email(email)
                        .profileJson(oauthUser.getAttributes().toString())
                        .build()));
    }

    @Transactional
    public void unlinkAccount(Long userId, Long accountId) {
        OAuthAccount account = oauthAccountRepository.findById(accountId)
                .orElseThrow(() -> new AuthenticationException("OAuth account nahi mila"));

        if (!account.getUser().getId().equals(userId)) {
            throw new AuthenticationException("Yeh account aapka nahi hai");
        }

        oauthAccountRepository.delete(account);
    }

    private OAuthLinkedAccountResponse toResponse(OAuthAccount account) {
        return OAuthLinkedAccountResponse.builder()
                .id(account.getId())
                .provider(account.getProvider())
                .email(account.getEmail())
                .linkedAt(account.getLinkedAt())
                .build();
    }
}
