# Stop và xóa container
docker-compose down

# Rebuild với clean cache
docker-compose build --no-cache

# Start lại
docker-compose up -d

# Xem logs để confirm Security config loaded
docker-compose logs -f app | grep -i "security\|filter"
