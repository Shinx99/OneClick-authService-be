-- Migration: auth_account - seed_datas
-- Created: Wed May  6 06:00:23 PM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:
-- ========================================================
-- 1) Tạo accounts với UUID cố định
-- ========================================================
INSERT INTO auth_accounts (account_id, email, phone, status, email_verified_at, created_at, updated_at) VALUES
    ('e7e7e7e7-e7e7-e7e7-e7e7-e7e7e7e7e7e7', 'saigontech@example.com',  '+84901000007', 'active', now(), now(), now()),
    ('e8e8e8e8-e8e8-e8e8-e8e8-e8e8e8e8e8e8', 'tma@example.com',         '+84901000008', 'active', now(), now(), now()),
    ('e9e9e9e9-e9e9-e9e9-e9e9-e9e9e9e9e9e9', 'axonactive@example.com',  '+84901000009', 'active', now(), now(), now()),
    ('eaeaeaea-eaea-eaea-eaea-eaeaeaeaeaea', 'ghtk@example.com',         '+84901000010', 'active', now(), now(), now()),
    ('ebebebeb-ebeb-ebeb-ebeb-ebebebebebeb', 'elsa@example.com',          '+84901000011', 'active', now(), now(), now()),
    ('ecececec-ecec-ecec-ecec-ecececececec', 'rikkeisoft@example.com',    '+84901000012', 'active', now(), now(), now()),
    ('edededed-eded-eded-eded-edededededed', 'viettel@example.com',      '+84901000013', 'active', now(), now(), now()),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'momo@example.com',         '+84901000014', 'active', now(), now(), now()),
    ('efefefef-efef-efef-efef-efefefefefef', 'shopee@example.com',        '+84901000015', 'active', now(), now(), now()),
    ('f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0', 'vnpay@example.com',       '+84901000016', 'active', now(), now(), now()),
    ('f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1', 'amanotes@example.com',    '+84901000017', 'active', now(), now(), now()),
    ('f2f2f2f2-f2f2-f2f2-f2f2-f2f2f2f2f2f2', 'nashtech@example.com',    '+84901000018', 'active', now(), now(), now()),
    ('f3f3f3f3-f3f3-f3f3-f3f3-f3f3f3f3f3f3', 'basevn@example.com',      '+84901000019', 'active', now(), now(), now()),
    ('f4f4f4f4-f4f4-f4f4-f4f4-f4f4f4f4f4f4', 'coccoc@example.com',      '+84901000020', 'active', now(), now(), now()),
    ('f5f5f5f5-f5f5-f5f5-f5f5-f5f5f5f5f5f5', 'begroup@example.com',     '+84901000021', 'active', now(), now(), now()),
    ('f6f6f6f6-f6f6-f6f6-f6f6-f6f6f6f6f6f6', 'sendo@example.com',       '+84901000022', 'active', now(), now(), now()),
    ('f7f7f7f7-f7f7-f7f7-f7f7-f7f7f7f7f7f7', 'gotit@example.com',       '+84901000023', 'active', now(), now(), now()),
    ('f8f8f8f8-f8f8-f8f8-f8f8-f8f8f8f8f8f8', 'orient@example.com',      '+84901000024', 'active', now(), now(), now())
ON CONFLICT (email) DO NOTHING;

-- ========================================================
-- 2) Password: tất cả dùng "Recruiter@123"
-- ========================================================
INSERT INTO auth_password_credentials (account_id, password_hash, password_algo, password_updated_at)
SELECT a.account_id, crypt('Recruiter@123', gen_salt('bf', 10)), 'bcrypt', now()
FROM auth_accounts a
WHERE a.email IN (
    'saigontech@example.com',
    'tma@example.com',
    'axonactive@example.com',
    'ghtk@example.com',
    'elsa@example.com',
    'rikkeisoft@example.com',
    'viettel@example.com',
    'momo@example.com',
    'shopee@example.com',
    'vnpay@example.com',
    'amanotes@example.com',
    'nashtech@example.com',
    'basevn@example.com',
    'coccoc@example.com',
    'begroup@example.com',
    'sendo@example.com',
    'gotit@example.com',
    'orient@example.com'
)
ON CONFLICT (account_id) DO UPDATE SET
    password_hash        = EXCLUDED.password_hash,
    password_algo        = EXCLUDED.password_algo,
    password_updated_at  = now();

-- ========================================================
-- 3) Gán role "recruiter"
-- ========================================================
INSERT INTO auth_accounts_roles (account_id, role_id)
SELECT a.account_id, r.role_id
FROM auth_accounts a
JOIN auth_roles r ON r.role_name = 'recruiter'
WHERE a.email IN (
    'saigontech@example.com',
    'tma@example.com',
    'axonactive@example.com',
    'ghtk@example.com',
    'elsa@example.com',
    'rikkeisoft@example.com',
    'viettel@example.com',
    'momo@example.com',
    'shopee@example.com',
    'vnpay@example.com',
    'amanotes@example.com',
    'nashtech@example.com',
    'basevn@example.com',
    'coccoc@example.com',
    'begroup@example.com',
    'sendo@example.com',
    'gotit@example.com',
    'orient@example.com'
)
ON CONFLICT (account_id, role_id) DO NOTHING;
