# 1. Health check (public endpoint)
curl http://localhost:8080/actuator/health
# ✅ Expect: 200 OK

# 2. Protected endpoint without token
curl -v http://localhost:8080/api/test/protected
# ✅ Expect: 401 Unauthorized

# 3. Generate test token
curl http://localhost:8080/api/test/generate-token
# ✅ Expect: {"token":"eyJ..."}

# 4. Protected endpoint with valid token
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/test/protected
# ✅ Expect: 200 OK with user data

# 5. Run unit tests
mvn test -Dtest=JwtServiceTest
# ✅ Expect: All tests pass

# 6. Run integration tests
mvn test -Dtest=JwtAuthenticationFilterTest
# ✅ Expect: All tests pass


> ## Flow Test

# Test by Swagger
http://localhost:8080/swagger-ui/index.html

# 1. Test public endpoint - PHẢI 200
curl http://localhost:8080/actuator/health
# ✅ {"status":"UP"}

# 2. Test protected without token - PHẢI 401
curl -v http://localhost:8080/api/protected
# ✅ HTTP/1.1 401 Unauthorized

# 3. Generate token
curl http://localhost:8080/api/test/generate-token
# ✅ {"token":"eyJhbGc..."}

# 4. Test with valid token - PHẢI 200
TOKEN="<copy token từ bước 3>"
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/test/protected
# ✅ {"message":"You accessed protected resource!"}

# 5. Test with invalid token - PHẢI 401
curl -v -H "Authorization: Bearer invalid-token" http://localhost:8080/api/protected
# ✅ HTTP/1.1 401 Unauthorized

