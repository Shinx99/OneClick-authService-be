📁 Cấu trúc Exception Layer
```text
shared/
└── exception/
├── GlobalExceptionHandler.java    # @RestControllerAdvice
├── BusinessException.java         # Base exception
├── ResourceNotFoundException.java # 404 errors
├── ValidationException.java       # 400 validation errors
├── UnauthorizedException.java     # 401 auth errors
└── ForbiddenException.java        # 403 permission errors
```

📝 Usage Examples

Trong Service/Handler
```bash
// Throw validation exception
if (accountRepository.existsByEmail(email)) {
throw new ValidationException("email", "Email already exists");
}

// Throw resource not found
Account account = accountRepository.findById(accountId)
.orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

// Throw unauthorized
if (!passwordEncoder.matches(password, credential.getPasswordHash())) {
throw new UnauthorizedException("Invalid credentials");
}

// Throw forbidden
if (!currentUser.hasRole("ADMIN")) {
throw new ForbiddenException("admin operations", "perform");
}
```
Response Format
```bash
{
  "timestamp": "2026-02-11T01:07:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "path": "/api/auth/register",
  "validationErrors": {
    "email": "Email already exists",
    "password": "Password must be at least 8 characters"
  }
}
```