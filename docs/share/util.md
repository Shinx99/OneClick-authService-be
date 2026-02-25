📝 Usage Examples

DateTimeUtil Usage
```bash
// In RegisterHandler
Instant expiryTime = DateTimeUtil.createEmailVerificationExpiry(); // +24 hours

// Check if token expired
if (DateTimeUtil.isExpired(token.getExpiresAt())) {
throw new TokenExpiredException("Verification token has expired");
}

// Human-readable time
String timeAgo = DateTimeUtil.timeAgo(account.getCreatedAt());
// Output: "2 hours ago"
```
PasswordUtil Usage
```bash
// In RegisterHandler
PasswordUtil.validatePasswordStrength(request.getPassword()); // Throws if weak

// Check strength score
int score = PasswordUtil.calculatePasswordStrength("MyP@ssw0rd123");
String label = PasswordUtil.getPasswordStrengthLabel("MyP@ssw0rd123");
// Output: "Strong" (score: 85)

// Generate temporary password
String tempPassword = PasswordUtil.generateRandomPassword(12);
```
ValidationUtil Usage
```bash
// In RegisterHandler
ValidationUtil.validateEmail(request.getEmail());
ValidationUtil.validateNotBlank(request.getEmail(), "email");

// In RefreshTokenHandler
UUID tokenId = ValidationUtil.parseUUID(tokenIdString, "tokenId");

// Sanitize user input
String safeName = ValidationUtil.sanitizeInput(userInput);
```