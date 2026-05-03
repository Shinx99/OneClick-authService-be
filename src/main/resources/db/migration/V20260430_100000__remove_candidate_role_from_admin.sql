-- Migration: Remove candidate role from admin account
-- Author: [your name]
-- Date: 2026-04-30
-- Description: Xóa vai trò candidate khỏi tài khoản admin@example.com

DELETE FROM auth_accounts_roles
WHERE account_id = (SELECT account_id FROM auth_accounts WHERE email = 'admin@example.com')
  AND role_id   = (SELECT role_id FROM auth_roles WHERE role_name = 'candidate');