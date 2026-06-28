-- Phase 3: Session Management (Modules 11-13)

CREATE TABLE user_sessions (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_id      VARCHAR(64)  NOT NULL UNIQUE,
    device_name     VARCHAR(100),
    browser         VARCHAR(100),
    os              VARCHAR(100),
    ip_address      VARCHAR(45),
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    login_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    last_activity   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    logout_at       TIMESTAMPTZ
);

CREATE TABLE trusted_devices (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_id       VARCHAR(64)  NOT NULL,
    device_name     VARCHAR(100),
    fingerprint     VARCHAR(255),
    trusted         BOOLEAN      NOT NULL DEFAULT TRUE,
    last_used_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, device_id)
);

CREATE INDEX idx_user_sessions_user ON user_sessions(user_id);
CREATE INDEX idx_trusted_devices_user ON trusted_devices(user_id);
