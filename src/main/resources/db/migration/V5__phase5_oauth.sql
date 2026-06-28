-- Phase 5: OAuth2 & SSO (Modules 17-19)

CREATE TABLE oauth_accounts (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider        VARCHAR(50)  NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    email           VARCHAR(255),
    profile_json    TEXT,
    linked_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (provider, provider_user_id)
);

CREATE INDEX idx_oauth_accounts_user ON oauth_accounts(user_id);
