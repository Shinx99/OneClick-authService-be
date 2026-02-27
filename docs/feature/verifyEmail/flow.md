```bash
🔄 Full Flow hoạt động (8 bước)
Giai đoạn 1: REGISTER (Tạo token)
text
1. User POST /register {email: "user@example.com", password: "..."}
2. Backend:
   - Tạo Account: status="pending" 
   - Tạo plainToken = UUID.randomUUID()
   - tokenHash = SHA256(plainToken)
   - Lưu EmailVerificationToken: account_id, token_hash, expires_at=24h
3. Gửi email: "Click verify: https://app.com/verify-email?token=<plainToken>"
Giai đoạn 2: VERIFY (Xác thực)
text
4. User click link → Frontend POST /api/auth/verify-email {token: "plainToken"}
5. Backend VerifyEmailHandler:
   a. hash = SHA256(plainToken) → DB lookup ✓
   b. Check: !expired && !used ✓
   c. UPDATE account: status="active", email_verified_at=NOW()
   d. UPDATE token: used_at=NOW()
   e. Gửi welcome email
   f. Audit log: "EMAIL_VERIFIED"
6. Response: {accountId: "uuid", message: "success"}
Giai đoạn 3: LOGIN (Chỉ active user)
text
7. POST /login → Check status="active" → JWT token
8. "pending" user → 401 "Please verify email"
📊 Database changes:
text
auth_accounts:
account_id | email              | status  | email_verified_at
uuid1      | user@example.com   | active  | 2026-02-27 11:00

email_verification_token:
id | account_id | token_hash           | expires_at       | used_at
1  | uuid1      | 5e884898da280471... | 2026-02-28 11:00 | 2026-02-27 11:00

auth_audit_logs:
account_id | action         | ip        | meta
uuid1      | EMAIL_VERIFIED | 127.0.0.1 | {"verified_at": "..."}
🛡️ Security Features:
Tính năng	Mô tả
SHA256	Token hash DB-safe
Single-use	used_at prevent replay
Expiry	24h auto expire
Audit trail	Full log compliance
IP tracking	Suspicious detection
🚀 API Endpoints:
text
POST /api/auth/register      → Tạo account + gửi verification email
POST /api/auth/verify-email  → Xác thực token → kích hoạt account
POST /api/auth/login         → JWT (chỉ active accounts)

```