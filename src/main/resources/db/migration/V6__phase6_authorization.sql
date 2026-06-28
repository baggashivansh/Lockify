-- Phase 6: Advanced Authorization (Modules 20-22)

CREATE TABLE user_attributes (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    department  VARCHAR(100),
    location    VARCHAR(100),
    job_title   VARCHAR(100),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE user_resources (
    id          BIGSERIAL PRIMARY KEY,
    owner_id    BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title       VARCHAR(255) NOT NULL,
    content     TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE abac_policies (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    resource    VARCHAR(100) NOT NULL,
    action      VARCHAR(50)  NOT NULL,
    condition_json TEXT      NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

INSERT INTO abac_policies (name, resource, action, condition_json) VALUES
    ('DEPT_READ', 'DOCUMENT', 'READ', '{"departmentMatch": true}'),
    ('LOCATION_UPDATE', 'DOCUMENT', 'UPDATE', '{"locationMatch": true, "ownerOnly": true}');

CREATE INDEX idx_user_resources_owner ON user_resources(owner_id);
