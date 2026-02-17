#!/bin/bash
cd ~/java/One-Click/authService/authService-be

echo "=== Fixing file names and class names ==="

# Rename files
[ -f "src/main/java/com/oneClick/authService/shared/security/CustomAuthenticationEntryPoint.java" ] && \
  mv src/main/java/com/oneClick/authService/shared/security/CustomAuthenticationEntryPoint.java \
     src/main/java/com/oneClick/authService/shared/security/JwtAuthenticationEntryPoint.java && \
  echo "✓ Renamed CustomAuthenticationEntryPoint"

[ -f "src/main/java/com/oneClick/authService/shared/security/JwtService.java" ] && \
  mv src/main/java/com/oneClick/authService/shared/security/JwtService.java \
     src/main/java/com/oneClick/authService/shared/security/JwtTokenProvider.java && \
  echo "✓ Renamed JwtService"

# Update class names inside files
sed -i 's/public class CustomAuthenticationEntryPoint/public class JwtAuthenticationEntryPoint/g' \
  src/main/java/com/oneClick/authService/shared/security/JwtAuthenticationEntryPoint.java 2>/dev/null

sed -i 's/public class JwtService/public class JwtTokenProvider/g' \
  src/main/java/com/oneClick/authService/shared/security/JwtTokenProvider.java 2>/dev/null

sed -i 's/public interface JwtService/public interface JwtTokenProvider/g' \
  src/main/java/com/oneClick/authService/shared/security/JwtTokenProvider.java 2>/dev/null

echo "✓ Updated class names"

# Update all imports
find src/main/java/com/oneClick/authService/shared -name "*.java" -type f \
  -exec sed -i 's/import com\.oneClick\.authService_be\./import com.oneClick.authService.shared./g' {} \;

find src/main/java/com/oneClick/authService/shared -name "*.java" -type f \
  -exec sed -i 's/CustomAuthenticationEntryPoint/JwtAuthenticationEntryPoint/g' {} \;

find src/main/java/com/oneClick/authService/shared -name "*.java" -type f \
  -exec sed -i 's/\bJwtService\b/JwtTokenProvider/g' {} \;

echo "✓ Updated imports"

# Fix package declarations
find src/main/java/com/oneClick/authService/shared -name "*.java" -type f \
  -exec sed -i 's/package com\.oneClick\.authService_be\.infrastructure\.config/package com.oneClick.authService.shared.config/g' {} \;

find src/main/java/com/oneClick/authService/shared -name "*.java" -type f \
  -exec sed -i 's/package com\.oneClick\.authService_be\.infrastructure\.security/package com.oneClick.authService.shared.security/g' {} \;

find src/main/java/com/oneClick/authService/shared -name "*.java" -type f \
  -exec sed -i 's/package com\.oneClick\.authService_be\.infrastructure\.notification/package com.oneClick.authService.shared.notification/g' {} \;

find src/main/java/com/oneClick/authService/shared -name "*.java" -type f \
  -exec sed -i 's/package com\.oneClick\.authService_be\./package com.oneClick.authService.shared./g' {} \;

echo "✓ Fixed package declarations"

# Verification
echo -e "\n=== Verification ==="
remaining=$(grep -r "authService_be" src/main/java/com/oneClick/authService/shared/ | wc -l)
if [ "$remaining" -eq 0 ]; then
  echo "✅ No references to authService_be found"
else
  echo "⚠️  Found $remaining references to authService_be:"
  grep -r "authService_be" src/main/java/com/oneClick/authService/shared/
fi

echo -e "\n=== Files in shared/ ==="
tree src/main/java/com/oneClick/authService/shared -I 'target' -L 2

echo -e "\n✓ Fix complete!"
