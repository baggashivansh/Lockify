package com.lockify.phase5.oauth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

/**
 * OAuth2 login config - sirf tab load hota hai jab GOOGLE_CLIENT_ID / GITHUB_CLIENT_ID set hon.
 * Bina credentials ke yeh bean skip ho jata hai - app normal JWT auth se chalegi.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@Conditional(OAuthProvidersConfiguredCondition.class)
public class OAuth2ClientConfig {

    private final OAuthProperties oauthProperties;

    @Bean
    @Order(0)
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/oauth2/**", "/login/oauth2/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .oauth2Login(oauth -> oauth.defaultSuccessUrl("/api/oauth2/success", true));

        log.info("OAuth2 login enabled - configured providers: {}", listConfiguredProviders());
        return http.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>();

        if (oauthProperties.isGoogleConfigured()) {
            registrations.add(ClientRegistration.withRegistrationId("google")
                    .clientId(oauthProperties.getGoogle().getClientId())
                    .clientSecret(oauthProperties.getGoogle().getClientSecret())
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .scope("openid", "profile", "email")
                    .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                    .tokenUri("https://oauth2.googleapis.com/token")
                    .userInfoUri("https://openidconnect.googleapis.com/v1/userinfo")
                    .userNameAttributeName("sub")
                    .clientName("Google")
                    .build());
        }

        if (oauthProperties.isGithubConfigured()) {
            registrations.add(ClientRegistration.withRegistrationId("github")
                    .clientId(oauthProperties.getGithub().getClientId())
                    .clientSecret(oauthProperties.getGithub().getClientSecret())
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .scope("read:user", "user:email")
                    .authorizationUri("https://github.com/login/oauth/authorize")
                    .tokenUri("https://github.com/login/oauth/access_token")
                    .userInfoUri("https://api.github.com/user")
                    .userNameAttributeName("id")
                    .clientName("GitHub")
                    .build());
        }

        if (registrations.isEmpty()) {
            throw new IllegalStateException("lockify.oauth.enabled=true but koi client ID set nahi hai");
        }

        return new InMemoryClientRegistrationRepository(registrations);
    }

    private List<String> listConfiguredProviders() {
        List<String> providers = new ArrayList<>();
        if (oauthProperties.isGoogleConfigured()) {
            providers.add("google");
        }
        if (oauthProperties.isGithubConfigured()) {
            providers.add("github");
        }
        return providers;
    }
}
