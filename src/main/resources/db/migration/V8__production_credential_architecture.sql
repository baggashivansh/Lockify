-- V8: Production Credential Architecture
-- Credentials alag table me - identity (users) aur secrets (user_credentials) separated
-- Agar profile data leak ho bhi jaye, password hash alag table me protected rehta hai

-- ============================================================
-- USER PROFILES - Non-sensitive user details (PII, display info)
-- ============================================================
CREATE TABLE user_profiles (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    first_name      VARCHAR(100),
    last_name       VARCHAR(100),
    display_name    VARCHAR(150),
    phone           VARCHAR(20),
    phone_verified  BOOLEAN      NOT NULL DEFAULT FALSE,
    avatar_url      VARCHAR(500),
    timezone        VARCHAR(50)  DEFAULT 'UTC',
    locale          VARCHAR(10)  DEFAULT 'en',
    country_code    VARCHAR(3),
    bio             TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ============================================================
-- USER CREDENTIALS - Sensitive auth data (ISOLATED from profile)
-- ============================================================
CREATE TABLE user_credentials (
    id                      BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT       NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    password_hash           VARCHAR(255) NOT NULL,
  -- Algorithm metadata - future me Argon2 migrate karne ke liye track karte hain
    hash_algorithm          VARCHAR(20)  NOT NULL DEFAULT 'BCRYPT',
    hash_strength           INT          NOT NULL DEFAULT 12,
    pepper_version          INT          NOT NULL DEFAULT 1,
  -- Account lock state (credential-level security)
    failed_login_attempts   INT          NOT NULL DEFAULT 0,
    locked_until            TIMESTAMPTZ,
    password_changed_at     TIMESTAMPTZ,
    password_expires_at     TIMESTAMPTZ,
  -- MFA enforcement flag (adaptive auth se set hota hai)
    mfa_required            BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_credentials_user ON user_credentials(user_id);
CREATE INDEX idx_user_credentials_locked ON user_credentials(locked_until) WHERE locked_until IS NOT NULL;

-- ============================================================
-- MIGRATE existing data from users table
-- ============================================================
INSERT INTO user_credentials (
    user_id, password_hash, hash_algorithm, hash_strength,
    failed_login_attempts, locked_until, password_changed_at, password_expires_at
)
SELECT
    id, password_hash, 'BCRYPT', 12,
    failed_login_attempts, locked_until, password_changed_at, password_expires_at
FROM users;

INSERT INTO user_profiles (user_id, display_name, timezone, locale)
SELECT id, username, 'UTC', 'en' FROM users;

-- ============================================================
-- CLEAN UP users table - credential columns hatao (separation of concerns)
-- ============================================================
ALTER TABLE users DROP COLUMN IF EXISTS password_hash;
ALTER TABLE users DROP COLUMN IF EXISTS failed_login_attempts;
ALTER TABLE users DROP COLUMN IF EXISTS locked_until;
ALTER TABLE users DROP COLUMN IF EXISTS password_changed_at;
ALTER TABLE users DROP COLUMN IF EXISTS password_expires_at;

-- ============================================================
-- API KEYS - Service-to-service auth (future microservices ke liye)
-- ============================================================
CREATE TABLE api_keys (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name            VARCHAR(100) NOT NULL,
    key_prefix      VARCHAR(10)  NOT NULL,
    key_hash        VARCHAR(255) NOT NULL UNIQUE,
    scopes          VARCHAR(500),
    expires_at      TIMESTAMPTZ,
    last_used_at    TIMESTAMPTZ,
    revoked         BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_api_keys_user ON api_keys(user_id);
CREATE INDEX idx_api_keys_prefix ON api_keys(key_prefix);

-- ============================================================
-- COMMENTS - documentation in DB itself (production teams ke liye)
-- ============================================================
COMMENT ON TABLE users IS 'Identity only - NO passwords here. Auth secrets in user_credentials.';
COMMENT ON TABLE user_credentials IS 'Password hashes and lock state. Never log or expose this table.';
COMMENT ON TABLE user_profiles IS 'Display/PII data. Separate from credentials for breach containment.';
COMMENT ON TABLE refresh_tokens IS 'Opaque refresh tokens as SHA-256 hashes only.';
COMMENT ON TABLE api_keys IS 'Hashed API keys for machine-to-machine auth. Raw key shown once at creation.';
