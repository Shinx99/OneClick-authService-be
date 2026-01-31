#!/bin/bash
#scripts/generate-jwt-secret.sh

# Generate random 256-bit (32 bytes) secret in Base64
SECRET=$(openssl rand -base64 32)

echo "==========================="
echo "Generated JWT Secret:"
echo "==========================="
echo "$SECRET"
echo ""
echo "Add to your .env file:"
echo "JWT_SECRET=$SECRET"
echo "==========================="
