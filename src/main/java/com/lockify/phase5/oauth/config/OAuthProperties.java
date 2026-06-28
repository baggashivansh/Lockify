package com.lockify.phase5.oauth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * OAuth client credentials - env se aate hain (GOOGLE_CLIENT_ID etc).
 * Jab client IDs set nahi hain tab OAuth2 config disabled rehta hai.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "lockify.oauth")
public class OAuthProperties {

    private ProviderConfig google = new ProviderConfig();
    private ProviderConfig github = new ProviderConfig();

    @Getter
    @Setter
    public static class ProviderConfig {
        private String clientId;
        private String clientSecret;
    }

    public boolean isGoogleConfigured() {
        return StringUtils.hasText(google.getClientId()) && StringUtils.hasText(google.getClientSecret());
    }

    public boolean isGithubConfigured() {
        return StringUtils.hasText(github.getClientId()) && StringUtils.hasText(github.getClientSecret());
    }

    public boolean isAnyProviderConfigured() {
        return isGoogleConfigured() || isGithubConfigured();
    }
}
