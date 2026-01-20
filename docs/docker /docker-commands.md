# Docker & Docker Compose Commands - Team Development Guide

## 🚀 Quick Start Commands

### Start Project
```bash
# Start all services in background
docker-compose up -d

# Start with rebuild (when Dockerfile changes)
docker-compose up -d --build

# Start and view logs
docker-compose up
```
### Stop Project
```bash
# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ deletes data)
docker-compose down -v

# Stop without removing containers
docker-compose stop
```
## 📋 Docker Compose - Daily Development
### View Services Status
```bash
# List running services
docker-compose ps

# View all services (including stopped)
docker-compose ps -a
View Logs
```
```bash
# View all logs
docker-compose logs

# Follow logs (real-time)
docker-compose logs -f

# Logs for specific service
docker-compose logs -f auth-service

# Last 100 lines
docker-compose logs --tail=100
```

### Restart Services
```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart auth-service
```

### Execute Commands in Container
```bash
# Open bash shell in container
docker-compose exec auth-service bash

# Run command in container
docker-compose exec auth-service ls -la

# Run migration script
docker-compose exec auth-service ./scripts/create-migration.sh

# Access database
docker-compose exec database psql -U postgres -d recruitment_auth
```

### Build & Rebuild
```bash
# Build images
docker-compose build

# Build specific service
docker-compose build auth-service

# Build with no cache (clean build)
docker-compose build --no-cache

# Pull latest images
docker-compose pull
```

## 🐳 Docker - Container Management
### List Containers
```bash
# List running containers
docker ps

# List all containers (including stopped)
docker ps -a

# List with specific format
docker ps --format "table {{.ID}}\t{{.Names}}\t{{.Status}}"
```

### Container Lifecycle
```bash
# Start container
docker start <container-name>

# Stop container
docker stop <container-name>

# Restart container
docker restart <container-name>

# Remove container
docker rm <container-name>

# Force remove running container
docker rm -f <container-name>
```

### Execute in Container
```bash
# Interactive bash
docker exec -it <container-name> bash

# Run single command
docker exec <container-name> ls /app

# Run as root
docker exec -u root -it <container-name> bash
```

### View Container Info
```bash
# View container logs
docker logs <container-name>

# Follow logs
docker logs -f <container-name>

# View resource usage
docker stats

# Inspect container
docker inspect <container-name>

# View running processes
docker top <container-name>
```

## 🖼 Docker - Image Management
### List & Remove Images
```bash
# List images
docker images

# Remove image
docker rmi <image-name>

# Remove dangling images
docker image prune

# Remove all unused images
docker image prune -a
```

### Build Images
```bash
# Build from Dockerfile
docker build -t auth-service:latest .

# Build with custom Dockerfile
docker build -f Dockerfile.dev -t auth-service:dev .

# Build with no cache
docker build --no-cache -t auth-service:latest .
```

## 💾 Docker - Volume Management
### Volume Commands
```bash
# List volumes
docker volume ls

# Inspect volume
docker volume inspect <volume-name>

# Remove volume
docker volume rm <volume-name>

# Remove all unused volumes (⚠️ deletes data)
docker volume prune
```

## 🌐 Docker - Network Management
### Network Commands
```bash
# List networks
docker network ls

# Inspect network
docker network inspect <network-name>

# Create network
docker network create <network-name>

# Remove network
docker network rm <network-name>
```

## 🧹 Clean Up Commands
### Remove Stopped Containers
```bash
# Remove all stopped containers
docker container prune

# Remove with force
docker container prune -f
```

### Clean Up Everything
```bash
# Remove all unused resources (⚠️ careful!)
docker system prune

# Remove everything including volumes
docker system prune -a --volumes

# See disk usage
docker system df
```

### Kill & Remove All
```bash
# Stop all running containers
docker stop $(docker ps -q)

# Remove all containers
docker rm $(docker ps -aq)

# Remove all images
docker rmi $(docker images -q)
```

## 🔧 Debugging Commands
### Check Container Health
```bash
# View health status
docker inspect --format='{{.State.Health.Status}}' <container-name>

# View container environment variables
docker exec <container-name> env

# Check container IP
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <container-name>
```

### Copy Files
```bash
# Copy from container to host
docker cp <container-name>:/path/in/container /host/path

# Copy from host to container
docker cp /host/path <container-name>:/path/in/container
```

### Database Access
```bash
# PostgreSQL
docker-compose exec database psql -U postgres

# Run SQL file
docker-compose exec -T database psql -U postgres -d dbname < backup.sql

# Backup database
docker-compose exec database pg_dump -U postgres dbname > backup.sql
```

## 📊 Monitoring Commands
### Resource Usage
```bash
# View real-time stats
docker stats

# Stats for specific container
docker stats <container-name>

# Disk usage
docker system df

# Detailed disk usage
docker system df -v
```
---
## 🎯 Project-Specific Commands
### Development Workflow
```bash
# 1. Start fresh environment
docker-compose down -v
docker-compose up -d --build

# 2. View logs
docker-compose logs -f auth-service

# 3. Access container
docker-compose exec auth-service bash

# 4. Run migrations
docker-compose exec auth-service ./mvnw flyway:migrate

# 5. Restart after code changes
docker-compose restart auth-service
```

### Testing in Container
```bash
# Run tests
docker-compose exec auth-service ./mvnw test

# Run specific test
docker-compose exec auth-service ./mvnw test -Dtest=AuthServiceTest
```

### Database Operations
```bash
# Access database CLI
docker-compose exec database psql -U postgres -d recruitment_auth

# Create database backup
docker-compose exec database pg_dump -U postgres recruitment_auth > backup_$(date +%Y%m%d).sql

# Restore database
docker-compose exec -T database psql -U postgres recruitment_auth < backup.sql

# View database size
docker-compose exec database psql -U postgres -c "SELECT pg_size_pretty(pg_database_size('recruitment_auth'));"
```

## ⚠️ Important Notes
### Do NOT Run in Production
```bash
# These commands can cause data loss:
docker-compose down -v          # Deletes all volumes
docker system prune -a --volumes # Removes everything
docker volume prune             # Removes all volumes
```

### Best Practices

- ✅ Always use docker-compose for project development
- ✅ Use -d flag to run in background
- ✅ Check logs with docker-compose logs -f when debugging
- ✅ Use docker-compose down (without -v) to preserve data
- ⚠️ Be careful with prune commands
- ⚠️ Always backup database before running destructive commands

## 🆘 Troubleshooting
### Port Already in Use
```bash
# Find process using port
sudo lsof -i :8080

# Kill process
sudo kill -9 <PID>

# Or change port in docker-compose.yml
```

### Container Won't Start
```bash
# Check logs
docker-compose logs <service-name>

# Rebuild container
docker-compose up -d --build <service-name>

# Remove and recreate
docker-compose rm -f <service-name>
docker-compose up -d <service-name>
```

### Out of Disk Space
```bash
# Check disk usage
docker system df

# Clean up
docker system prune -a
docker volume prune
```
---
>## Đây là tổng hợp đầy đủ các lệnh thường dùng cho team dev !