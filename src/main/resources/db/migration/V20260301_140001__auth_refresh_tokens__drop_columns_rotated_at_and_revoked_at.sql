-- Migration: auth_refresh_tokens - drop_columns_rotated_at_and_revoked_at
-- Created: Sun Mar  1 02:00:01 PM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:
ALTER TABLE auth_refresh_tokens
DROP COLUMN rotated_at,
DROP COLUMN revoked_at;
