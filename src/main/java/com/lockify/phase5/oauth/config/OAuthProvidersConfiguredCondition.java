package com.lockify.phase5.oauth.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * OAuth2 tabhi enable jab Google ya GitHub client credentials env me hon.
 */
public class OAuthProvidersConfiguredCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        var env = context.getEnvironment();
        boolean google = StringUtils.hasText(env.getProperty("lockify.oauth.google.client-id"))
                && StringUtils.hasText(env.getProperty("lockify.oauth.google.client-secret"));
        boolean github = StringUtils.hasText(env.getProperty("lockify.oauth.github.client-id"))
                && StringUtils.hasText(env.getProperty("lockify.oauth.github.client-secret"));
        return google || github;
    }
}
