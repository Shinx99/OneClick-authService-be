-- Migration: auth - seed recruiter accounts with fixed UUIDs for testing
-- Created: Wed Apr 16 2026
-- Purpose: Tạo các tài khoản recruiter với UUID cố định, khớp với employer bên recruitment service

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 1) Tạo accounts với UUID cố định
INSERT INTO auth_accounts (account_id, email, phone, status, email_verified_at, created_at, updated_at) VALUES
    ('e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', 'fpt@example.com',   '+84901000001', 'active', now(), now(), now()),
    ('e2e2e2e2-e2e2-e2e2-e2e2-e2e2e2e2e2e2', 'vng@example.com',   '+84901000002', 'active', now(), now(), now()),
    ('e3e3e3e3-e3e3-e3e3-e3e3-e3e3e3e3e3e3', 'tiki@example.com',  '+84901000003', 'active', now(), now(), now()),
    ('e4e4e4e4-e4e4-e4e4-e4e4-e4e4e4e4e4e4', 'vinai@example.com', '+84901000004', 'active', now(), now(), now()),
    ('e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5', 'cmc@example.com',   '+84901000005', 'active', now(), now(), now()),
    ('e6e6e6e6-e6e6-e6e6-e6e6-e6e6e6e6e6e6', 'kms@example.com',   '+84901000006', 'active', now(), now(), now())
ON CONFLICT (email) DO NOTHING;

-- 2) Password: tất cả dùng password "Recruiter@123"
INSERT INTO auth_password_credentials (account_id, password_hash, password_algo, password_updated_at)
SELECT a.account_id, crypt('Recruiter@123', gen_salt('bf', 10)), 'bcrypt', now()
FROM auth_accounts a
WHERE a.email IN (
    'fpt@example.com',
    'vng@example.com',
    'tiki@example.com',
    'vinai@example.com',
    'cmc@example.com',
    'kms@example.com'
)
ON CONFLICT (account_id) DO UPDATE SET
    password_hash = EXCLUDED.password_hash,
    password_algo = EXCLUDED.password_algo,
    password_updated_at = now();

-- 3) Gán role "recruiter" cho tất cả
INSERT INTO auth_accounts_roles (account_id, role_id)
SELECT a.account_id, r.role_id
FROM auth_accounts a
JOIN auth_roles r ON r.role_name = 'recruiter'
WHERE a.email IN (
    'fpt@example.com',
    'vng@example.com',
    'tiki@example.com',
    'vinai@example.com',
    'cmc@example.com',
    'kms@example.com'
)
ON CONFLICT (account_id, role_id) DO NOTHING;
