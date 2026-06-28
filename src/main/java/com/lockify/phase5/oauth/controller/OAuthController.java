package com.lockify.phase5.oauth.controller;

import com.lockify.phase5.oauth.dto.OAuthLinkedAccountResponse;
import com.lockify.phase5.oauth.service.OAuthAccountLinkService;
import com.lockify.phase1.coreauth.security.LockifyUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * OAuth linked accounts API - protected endpoint.
 * Note: OAuth2 login tab kaam karta hai jab GOOGLE_CLIENT_ID / GITHUB_CLIENT_ID env set hon;
 * warna OAuth2ClientConfig load nahi hota aur sirf JWT auth active rehti hai.
 */
@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthAccountLinkService oauthAccountLinkService;

    @GetMapping("/linked-accounts")
    public ResponseEntity<List<OAuthLinkedAccountResponse>> linkedAccounts(
            @AuthenticationPrincipal LockifyUserDetails user
    ) {
        return ResponseEntity.ok(oauthAccountLinkService.getLinkedAccounts(user.getId()));
    }
}
