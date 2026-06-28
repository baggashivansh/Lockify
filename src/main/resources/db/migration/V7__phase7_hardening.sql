-- Phase 7: Security Beast Mode (Modules 23-27)

ALTER TABLE refresh_tokens ADD COLUMN IF NOT EXISTS family_id VARCHAR(64);
ALTER TABLE refresh_tokens ADD COLUMN IF NOT EXISTS replaced_by_hash VARCHAR(255);

CREATE TABLE security_events (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT REFERENCES users(id) ON DELETE SET NULL,
    event_type  VARCHAR(100) NOT NULL,
    severity    VARCHAR(20)  NOT NULL,
    ip_address  VARCHAR(45),
    location    VARCHAR(100),
    details     TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE device_fingerprints (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    fingerprint_hash VARCHAR(255) NOT NULL,
    browser         VARCHAR(100),
    os              VARCHAR(100),
    screen_res      VARCHAR(50),
    timezone        VARCHAR(50),
    first_seen      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    last_seen       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, fingerprint_hash)
);

CREATE INDEX idx_security_events_user ON security_events(user_id);
CREATE INDEX idx_security_events_type ON security_events(event_type, created_at);
CREATE INDEX idx_refresh_tokens_family ON refresh_tokens(family_id);
