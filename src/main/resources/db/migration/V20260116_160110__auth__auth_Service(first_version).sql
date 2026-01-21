-- Migration: auth - auth_Service(first_version)
-- Created: Fri Jan 16 04:01:10 PM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 1) Accounts
CREATE TABLE auth_accounts (
    account_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(32),
    status VARCHAR(20) NOT NULL DEFAULT 'active', -- active/disabled/deleted
    email_verified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 2) Password credentials (shared PK = 1-1)
CREATE TABLE auth_password_credentials (
    account_id BIGINT PRIMARY KEY REFERENCES auth_accounts(account_id) ON DELETE CASCADE,
    password_hash TEXT NOT NULL,
    password_algo VARCHAR(20) NOT NULL DEFAULT 'bcrypt',
    password_updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 3) Roles
CREATE TABLE auth_roles (
    role_id SMALLSERIAL PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE auth_accounts_roles (
    account_id BIGINT NOT NULL REFERENCES auth_accounts(account_id) ON DELETE CASCADE,
    role_id SMALLINT NOT NULL REFERENCES auth_roles(role_id) ON DELETE RESTRICT,
    PRIMARY KEY (account_id, role_id)
);

-- 4) Sessions
CREATE TABLE auth_sessions (
    session_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id BIGINT NOT NULL REFERENCES auth_accounts(account_id) ON DELETE CASCADE,
    ip INET,
    user_agent TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_seen_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ
);

CREATE INDEX idx_auth_sessions_account ON auth_sessions(account_id);

-- 5) Refresh tokens
CREATE TABLE auth_refresh_tokens (
    token_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES auth_sessions(session_id) ON DELETE CASCADE,
    account_id BIGINT NOT NULL REFERENCES auth_accounts(account_id) ON DELETE CASCADE,

    token_prefix VARCHAR(24) NOT NULL,
    token_hash TEXT NOT NULL,

    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    rotated_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ,

    UNIQUE (token_prefix)
);

CREATE INDEX idx_refresh_tokens_account ON auth_refresh_tokens(account_id);
CREATE INDEX idx_refresh_tokens_session ON auth_refresh_tokens(session_id);
CREATE INDEX idx_refresh_tokens_expires ON auth_refresh_tokens(expires_at);

-- 6) Email verification tokens
CREATE TABLE auth_email_verify_tokens (
    verify_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id BIGINT NOT NULL REFERENCES auth_accounts(account_id) ON DELETE CASCADE,
    token_hash TEXT NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_email_verify_account ON auth_email_verify_tokens(account_id);

-- 7) Password reset tokens
CREATE TABLE auth_password_reset_tokens (
    reset_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id BIGINT NOT NULL REFERENCES auth_accounts(account_id) ON DELETE CASCADE,
    token_hash TEXT NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_pw_reset_account ON auth_password_reset_tokens(account_id);

-- 8) OAuth identities
CREATE TABLE auth_oauth_identities (
    oauth_id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES auth_accounts(account_id) ON DELETE CASCADE,
    provider VARCHAR(30) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (provider, provider_user_id)
);

CREATE INDEX idx_oauth_account ON auth_oauth_identities(account_id);

-- 9) Audit log
CREATE TABLE auth_audit_logs (
    audit_id BIGSERIAL PRIMARY KEY,
    account_id BIGINT REFERENCES auth_accounts(account_id) ON DELETE SET NULL,
    event_type VARCHAR(50) NOT NULL,
    ip INET,
    user_agent TEXT,
    meta JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_account_created ON auth_audit_logs(account_id, created_at DESC);