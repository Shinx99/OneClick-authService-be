-- Migration: example_data - insert_example_data
-- Created: Sat Jan 17 02:21:07 PM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 1) Roles
INSERT INTO auth_roles (role_name) VALUES
  ('admin'),
  ('candidate'),
  ('recruiter')
ON CONFLICT (role_name) DO NOTHING;

-- 2) Accounts (UUID tự generate bởi gen_random_uuid())
INSERT INTO auth_accounts (email, phone, status, email_verified_at, created_at, updated_at) VALUES
  ('admin@example.com',       '+84901311111', 'active', now() - interval '30 days', now() - interval '30 days', now() - interval '1 day'),
  ('candidate0@example.com',  '+84902222022', 'active', now() - interval '5 days',  now() - interval '5 days',  now() - interval '2 hours'),
  ('candidate1@example.com',  '+84911911111', 'active', now() - interval '4 days',  now() - interval '4 days',  now() - interval '2 hours'),
  ('candidate2@example.com',  '+84902522222', 'active', now() - interval '4 days',  now() - interval '4 days',  now() - interval '2 hours'),
  ('candidate3@example.com',  '+84903373333', 'active', now() - interval '4 days',  now() - interval '4 days',  now() - interval '2 hours'),
  ('recruiter0@example.com',  '+84913333933', 'active', now() - interval '5 days',  now() - interval '1 day',   now() - interval '1 day')
ON CONFLICT (email) DO UPDATE SET
  phone             = EXCLUDED.phone,
  status            = EXCLUDED.status,
  email_verified_at = EXCLUDED.email_verified_at,
  updated_at        = EXCLUDED.updated_at;

-- 3) Password credentials
INSERT INTO auth_password_credentials (account_id, password_hash, password_algo, password_updated_at)
SELECT a.account_id, v.password_hash, v.password_algo, now()
FROM auth_accounts a
JOIN (VALUES
    ('admin@example.com',      crypt('Admin@123',     gen_salt('bf', 10)), 'bcrypt'),
    ('candidate0@example.com', crypt('Candidate@123', gen_salt('bf', 10)), 'bcrypt'),
    ('candidate1@example.com', crypt('Candidate@123', gen_salt('bf', 10)), 'bcrypt'),
    ('candidate2@example.com', crypt('Candidate@123', gen_salt('bf', 10)), 'bcrypt'),
    ('candidate3@example.com', crypt('Candidate@123', gen_salt('bf', 10)), 'bcrypt'),
    ('recruiter0@example.com', crypt('Recruiter@123', gen_salt('bf', 10)), 'bcrypt')
) AS v(email, password_hash, password_algo)
    ON v.email = a.email
ON CONFLICT (account_id) DO UPDATE SET
    password_hash       = EXCLUDED.password_hash,
    password_algo       = EXCLUDED.password_algo,
    password_updated_at = now();

-- 4) Accounts <-> Roles
INSERT INTO auth_accounts_roles (account_id, role_id)
SELECT a.account_id, r.role_id
FROM auth_accounts a
JOIN auth_roles r ON r.role_name = 'admin'
WHERE a.email = 'admin@example.com'

UNION ALL
SELECT a.account_id, r.role_id
FROM auth_accounts a
JOIN auth_roles r ON r.role_name = 'candidate'
WHERE a.email IN ('admin@example.com', 'candidate0@example.com', 'candidate1@example.com', 'candidate2@example.com', 'candidate3@example.com')

UNION ALL
SELECT a.account_id, r.role_id
FROM auth_accounts a
JOIN auth_roles r ON r.role_name = 'recruiter'
WHERE a.email = 'recruiter0@example.com'
ON CONFLICT (account_id, role_id) DO NOTHING;

-- 5) Sessions
INSERT INTO auth_sessions (session_id, account_id, ip, user_agent, created_at, last_seen_at, revoked_at)
SELECT v.session_id, a.account_id, v.ip, v.user_agent, v.created_at, v.last_seen_at, v.revoked_at
FROM (VALUES
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'::uuid, 'admin@example.com',      '203.0.113.10',  'Mozilla/5.0 (Windows NT 10.0; Win64; x64)',      now() - interval '2 days',  now() - interval '10 minutes', NULL::timestamptz),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'::uuid, 'candidate0@example.com', '198.51.100.20', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 14_0)',   now() - interval '1 day',   now() - interval '1 hour',     NULL::timestamptz),
  ('dddddddd-dddd-dddd-dddd-dddddddddddd'::uuid, 'candidate1@example.com', '198.51.100.21', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 14_0)',   now() - interval '1 day',   now() - interval '2 hours',    NULL::timestamptz),
  ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee'::uuid, 'candidate2@example.com', '198.51.100.22', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 14_0)',   now() - interval '1 day',   now() - interval '3 hours',    NULL::timestamptz),
  ('cccccccc-cccc-cccc-cccc-cccccccccccc'::uuid, 'candidate3@example.com', '198.51.100.23', 'curl/8.5.0',                                     now() - interval '10 days', now() - interval '9 days',     now() - interval '9 days'),
  ('ffffffff-ffff-ffff-ffff-ffffffffffff'::uuid, 'recruiter0@example.com', '198.51.100.30', 'Mozilla/5.0 (X11; Linux x86_64)',                now() - interval '3 days',  now() - interval '2 hours',    NULL::timestamptz)
) AS v(session_id, email, ip, user_agent, created_at, last_seen_at, revoked_at)
JOIN auth_accounts a ON a.email = v.email
ON CONFLICT (session_id) DO NOTHING;

-- 6) Refresh tokens
INSERT INTO auth_refresh_tokens
  (token_id, session_id, account_id, token_prefix, token_hash, expires_at, created_at, rotated_at, revoked_at)
SELECT v.token_id, v.session_id, a.account_id, v.token_prefix, v.token_hash,
       v.expires_at, v.created_at, v.rotated_at, v.revoked_at
FROM (VALUES
  ('11111111-1111-1111-1111-111111111111'::uuid, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'::uuid, 'admin@example.com',
   'rt_aaaaaaaaaaaaaaaaaaaaa', 'sha256$admin_rt_01',  now() + interval '30 days', now() - interval '2 days',  NULL::timestamptz, NULL::timestamptz),

  ('22222222-2222-2222-2222-222222222222'::uuid, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'::uuid, 'candidate0@example.com',
   'rt_bbbbbbbbbbbbbbbbbbbbb', 'sha256$cand0_rt_01',  now() + interval '30 days', now() - interval '1 day',   now() - interval '12 hours', NULL::timestamptz),

  ('33333333-3333-3333-3333-333333333333'::uuid, 'dddddddd-dddd-dddd-dddd-dddddddddddd'::uuid, 'candidate1@example.com',
   'rt_ddddddddddddddddddddd', 'sha256$cand1_rt_01',  now() + interval '30 days', now() - interval '1 day',   NULL::timestamptz, NULL::timestamptz),

  ('44444444-4444-4444-4444-444444444444'::uuid, 'cccccccc-cccc-cccc-cccc-cccccccccccc'::uuid, 'candidate3@example.com',
   'rt_ccccccccccccccccccccc', 'sha256$cand3_rt_01',  now() - interval '1 day',   now() - interval '10 days', NULL::timestamptz, now() - interval '9 days'),

  ('55555555-5555-5555-5555-555555555555'::uuid, 'ffffffff-ffff-ffff-ffff-ffffffffffff'::uuid, 'recruiter0@example.com',
   'rt_fffffffffffffffffffff', 'sha256$rec0_rt_01',   now() + interval '30 days', now() - interval '3 days',  NULL::timestamptz, NULL::timestamptz)
) AS v(token_id, session_id, email, token_prefix, token_hash, expires_at, created_at, rotated_at, revoked_at)
JOIN auth_accounts a ON a.email = v.email
ON CONFLICT (token_id) DO NOTHING;

-- 7) Email verification tokens
INSERT INTO auth_email_verify_tokens (verify_id, account_id, token_hash, expires_at, used_at, created_at)
SELECT v.verify_id, a.account_id, v.token_hash, v.expires_at, v.used_at, v.created_at
FROM (VALUES
  ('66666666-6666-6666-6666-666666666666'::uuid, 'candidate2@example.com', 'sha256$ev_cand2_01', now() + interval '2 days', NULL::timestamptz,          now() - interval '1 hour'),
  ('77777777-7777-7777-7777-777777777777'::uuid, 'recruiter0@example.com', 'sha256$ev_rec0_01',  now() + interval '2 days', now() - interval '2 hours', now() - interval '1 day')
) AS v(verify_id, email, token_hash, expires_at, used_at, created_at)
JOIN auth_accounts a ON a.email = v.email
ON CONFLICT (verify_id) DO NOTHING;

-- 8) Password reset tokens
INSERT INTO auth_password_reset_tokens (reset_id, account_id, token_hash, expires_at, used_at, created_at)
SELECT v.reset_id, a.account_id, v.token_hash, v.expires_at, v.used_at, v.created_at
FROM (VALUES
  ('88888888-8888-8888-8888-888888888888'::uuid, 'candidate1@example.com', 'sha256$pwreset_cand1_01', now() + interval '30 minutes', NULL::timestamptz,           now()),
  ('99999999-9999-9999-9999-999999999999'::uuid, 'admin@example.com',      'sha256$pwreset_admin_01', now() - interval '1 hour',     now() - interval '2 hours', now() - interval '3 hours')
) AS v(reset_id, email, token_hash, expires_at, used_at, created_at)
JOIN auth_accounts a ON a.email = v.email
ON CONFLICT (reset_id) DO NOTHING;

-- 9) OAuth identities (oauth_id tự generate UUID)
INSERT INTO auth_oauth_identities (account_id, provider, provider_user_id, created_at)
SELECT a.account_id, v.provider, v.provider_user_id, v.created_at
FROM auth_accounts a
JOIN (VALUES
  ('candidate0@example.com', 'google',   'google-oauth2|100000000000000000001', now() - interval '20 days'),
  ('recruiter0@example.com', 'linkedin', '12345678',                            now() - interval '5 days')
) AS v(email, provider, provider_user_id, created_at)
  ON v.email = a.email
ON CONFLICT (provider, provider_user_id) DO NOTHING;

-- 10) Audit logs
INSERT INTO auth_audit_logs (account_id, event_type, ip, user_agent, meta, created_at)
SELECT a.account_id, v.event_type, v.ip, v.user_agent, v.meta, v.created_at
FROM auth_accounts a
JOIN (VALUES
  ('admin@example.com',      'login_success',         '203.0.113.10'::inet,  'Mozilla/5.0 (Windows NT 10.0; Win64; x64)',    '{"method":"password"}'::jsonb,                                         now() - interval '10 minutes'),
  ('candidate0@example.com', 'login_failed',          '198.51.100.20'::inet, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 14_0)', '{"reason":"wrong_password"}'::jsonb,                                    now() - interval '2 hours'),
  ('candidate0@example.com', 'refresh_token_rotated', '198.51.100.20'::inet, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 14_0)', '{"session_id":"bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"}'::jsonb,          now() - interval '1 hour'),
  ('recruiter0@example.com', 'logout',                '198.51.100.30'::inet, 'Mozilla/5.0 (X11; Linux x86_64)',              '{"via":"ui"}'::jsonb,                                                   now() - interval '30 minutes')
) AS v(email, event_type, ip, user_agent, meta, created_at)
  ON v.email = a.email;

-- Log hệ thống không gắn account
INSERT INTO auth_audit_logs (account_id, event_type, ip, user_agent, meta, created_at) VALUES
  (NULL, 'rate_limit_block', '198.51.100.99'::inet, 'bot', '{"reason":"too_many_requests"}'::jsonb, now() - interval '5 minutes');

