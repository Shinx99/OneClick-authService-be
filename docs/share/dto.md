📝 USAGE EXAMPLES - DTO LAYER
Đây là các ví dụ sử dụng 3 DTO classes trong authService:

1️⃣ ApiResponse<T> Usage
```bash
✅ Success Responses
Example 1: Register Success (201 Created)
java
// In RegisterController
@PostMapping("/register")
public ResponseEntity<ApiResponse<RegisterResponse>> register(
@Valid @RequestBody RegisterRequest request) {

    RegisterResponse response = registerHandler.handle(request);
    
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Registration successful. Please check your email to verify your account.", response));
}

// Response JSON
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 201,
"success": true,
"message": "Registration successful. Please check your email to verify your account.",
"data": {
"accountId": 1,
"email": "user@example.com",
"status": "PENDING"
}
}
```

Example 2: Login Success (200 OK)
```bash
// In LoginController
@PostMapping("/login")
public ResponseEntity<ApiResponse<LoginResponse>> login(
@Valid @RequestBody LoginRequest request) {

    LoginResponse response = loginHandler.handle(request);
    
    return ResponseEntity.ok(
        ApiResponse.success("Login successful", response)
    );
}

// Response JSON
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 200,
"success": true,
"message": "Login successful",
"data": {
"accessToken": "eyJhbGciOiJIUzUxMiJ9...",
"refreshToken": "550e8400-e29b-41d4-a716-446655440000",
"tokenType": "Bearer",
"expiresIn": 900,
"user": {
"accountId": 1,
"email": "user@example.com",
"roles": ["USER"]
}
}
}
```
Example 3: Get Profile (200 OK)
```bash
// In ProfileController
@GetMapping("/profile")
public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(@CurrentUser Long accountId) {

    ProfileResponse profile = getProfileHandler.handle(accountId);
    
    return ResponseEntity.ok(
        ApiResponse.success(profile)
    );
}

// Response JSON (using default "Success" message)
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 200,
"success": true,
"message": "Success",
"data": {
"accountId": 1,
"email": "user@example.com",
"phone": "+84901234567",
"status": "ACTIVE",
"emailVerified": true,
"roles": ["USER"],
"createdAt": "2026-01-15T10:30:00Z"
}
}
```
Example 4: Logout Success (200 OK - No Data)
```bash
// In LogoutController
@PostMapping("/logout")
public ResponseEntity<ApiResponse<Void>> logout(@CurrentUser Long accountId) {

    logoutHandler.handle(accountId);
    
    return ResponseEntity.ok(
        ApiResponse.success("Logout successful", null)
    );
}

// Response JSON
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 200,
"success": true,
"message": "Logout successful",
"data": null
}
```
Example 5: Email Verification Success
```bash

// In VerifyEmailController
@PostMapping("/verify-email")
public ResponseEntity<ApiResponse<VerifyEmailResponse>> verifyEmail(
@Valid @RequestBody VerifyEmailRequest request) {

    VerifyEmailResponse response = verifyEmailHandler.handle(request);
    
    return ResponseEntity.ok(
        ApiResponse.success("Email verified successfully. You can now login.", response)
    );
}

// Response JSON
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 200,
"success": true,
"message": "Email verified successfully. You can now login.",
"data": {
"accountId": 1,
"email": "user@example.com",
"verified": true
}
}
❌ Error Responses (Using GlobalExceptionHandler)
```
Example 6: Validation Error (400 Bad Request)
```bash

// GlobalExceptionHandler automatically catches ValidationException
// When: registerHandler.handle() throws ValidationException

// Response JSON
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 400,
"success": false,
"message": "Validation failed",
"path": "/api/auth/register"
}
```
Example 7: Unauthorized (401)
```bash

// When accessing protected endpoint without token
// Response JSON
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 401,
"success": false,
"message": "Authentication required. Please provide valid credentials.",
"path": "/api/profile"
}
```
Example 8: Forbidden (403)
```bash

// When user tries to access admin endpoint
// Response JSON
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 403,
"success": false,
"message": "You don't have permission to access this resource",
"path": "/api/admin/roles/assign"
}
```
2️⃣ PageResponse<T> Usage

Example 9: Get Active Sessions (Paginated)
```bash

// In SessionController
@GetMapping("/sessions")
public ResponseEntity<ApiResponse<PageResponse<SessionResponse>>> getSessions(
@CurrentUser Long accountId,
@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    Page<SessionResponse> page = getSessionsHandler.handle(accountId, pageable);
    PageResponse<SessionResponse> pageResponse = PageResponse.from(page);
    
    return ResponseEntity.ok(
        ApiResponse.success(pageResponse)
    );
}

// Response JSON
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 200,
"success": true,
"message": "Success",
"data": {
"content": [
{
"sessionId": "550e8400-e29b-41d4-a716-446655440000",
"ip": "192.168.1.100",
"userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)...",
"createdAt": "2026-02-11T00:00:00Z",
"lastSeenAt": "2026-02-11T01:28:00Z",
"isCurrent": true
},
{
"sessionId": "660e8400-e29b-41d4-a716-446655440111",
"ip": "192.168.1.101",
"userAgent": "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0)...",
"createdAt": "2026-02-10T10:00:00Z",
"lastSeenAt": "2026-02-10T23:00:00Z",
"isCurrent": false
}
],
"pageNumber": 0,
"pageSize": 20,
"totalElements": 2,
"totalPages": 1,
"last": true
}
}
```
Example 10: Empty Page (No Sessions)
```bash

// When user has no active sessions
Page<SessionResponse> emptyPage = Page.empty();
PageResponse<SessionResponse> pageResponse = PageResponse.from(emptyPage);

// Response JSON
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 200,
"success": true,
"message": "Success",
"data": {
"content": [],
"pageNumber": 0,
"pageSize": 20,
"totalElements": 0,
"totalPages": 0,
"last": true
}
}
```
3️⃣ ErrorResponse Usage (via GlobalExceptionHandler)
Example 11: Validation Error with Field Details
```bash

// In GlobalExceptionHandler
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
MethodArgumentNotValidException ex,
HttpServletRequest request) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
        String fieldName = ((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        errors.put(fieldName, errorMessage);
    });
    
    ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message("Validation failed for one or more fields")
            .path(request.getRequestURI())
            .validationErrors(errors)
            .build();
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
}

// Response JSON
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 400,
"error": "Bad Request",
"message": "Validation failed for one or more fields",
"path": "/api/auth/register",
"validationErrors": {
"email": "must be a well-formed email address",
"password": "Password must be at least 8 characters"
}
}
```
Example 12: Custom Business Exception
```bash

// In feature exception (e.g., EmailAlreadyExistsException)
if (accountRepository.existsByEmail(email)) {
throw new ValidationException("email", "Email already exists");
}

// GlobalExceptionHandler catches ValidationException
// Response JSON
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 400,
"error": "Bad Request",
"message": "Validation failed",
"path": "/api/auth/register",
"validationErrors": {
"email": "Email already exists"
}
}
```
Example 13: Resource Not Found (404)
```bash

// In GetProfileHandler
Account account = accountRepository.findById(accountId)
.orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

// GlobalExceptionHandler catches ResourceNotFoundException
// Response JSON
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 404,
"error": "Not Found",
"message": "Account not found with id: 123",
"path": "/api/profile"
}
```
Example 14: Token Expired (401)
```bash

// In VerifyEmailHandler
if (DateTimeUtil.isExpired(token.getExpiresAt())) {
throw new TokenExpiredException("Verification token has expired");
}

// Response JSON
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 401,
"error": "Unauthorized",
"message": "Verification token has expired",
"path": "/api/auth/verify-email"
}
```
Example 15: Internal Server Error (500)
```bash

// When unexpected exception occurs
// GlobalExceptionHandler catches all Exception.class

// Response JSON
{
"timestamp": "2026-02-11T01:28:00Z",
"status": 500,
"error": "Internal Server Error",
"message": "An unexpected error occurred. Please contact support.",
"path": "/api/auth/register"
}
```
🎯 Best Practices
1. Consistent Response Format
```bash
   // ✅ GOOD: Always wrap in ApiResponse
   return ResponseEntity.ok(ApiResponse.success(data));

// ❌ BAD: Return raw data
return ResponseEntity.ok(data);
```
2. Meaningful Messages
```bash

   // ✅ GOOD: Clear, actionable message
   ApiResponse.created("Registration successful. Please check your email to verify your account.", response)

// ❌ BAD: Vague message
ApiResponse.created("Done", response)
```
3. Use Appropriate HTTP Status
```bash

   // ✅ GOOD: Match status with operation
   return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(message, data));

// ❌ BAD: Always 200 OK
return ResponseEntity.ok(ApiResponse.created(message, data));
```
4. Set Path in Errors
```bash
   // ✅ GOOD: Include request path for debugging
   ErrorResponse error = ErrorResponse.builder()
   .path(request.getRequestURI())
   .build();

// ❌ BAD: Missing context
ErrorResponse error = ErrorResponse.builder()
.message("Error")
.build();
```