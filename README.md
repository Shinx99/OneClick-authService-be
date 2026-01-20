# Auth Service - Job Recruitment Platform

>Microservice handling authentication and authorization for the job recruitment system, with account management for candidates and recruiters.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📋 Table of Contents

- [About the Project](#about-the-project)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Running with Docker](#running-with-docker)
- [Database Migrations](#database-migrations)
- [API Documentation](#api-documentation)
- [Development](#development)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

---

## 📖 About the Project

>Auth Service is a microservice responsible for authentication and authorization in the online recruitment platform. The service manages two main user categories: Candidates and Recruiters, implementing security through JWT tokens and role-based access control (RBAC).
- User authentication for candidates and recruiters
- Role-based authorization (Candidate, Recruiter, Admin)
- User registration with email verification
- Profile management and password recovery
- Session and token management
- OAuth2 integration (Google)

---

## ✨ Features

- ✅ Multi-role authentication (Candidate/Recruiter/Admin)
- ✅ JWT-based secure authentication
- ✅ Email verification system
- ✅ Password reset functionality
- ✅ Social login integration (Google, LinkedIn)
- ✅ Profile management endpoints
- ✅ Database migration with Flyway
- ✅ Dockerized deployment
- ✅ RESTful API design
- ✅ Comprehensive error handling
- ✅ API documentation with Swagger/OpenAPI

---

## 🛠 Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming Language |
| Spring Boot | 3.5.9 | Backend Framework |
| PostgreSQL | Latest | Database |
| Redis | Latest | Caching |
| Flyway | Latest | Database Migration |
| Docker | Latest | Containerization |
| Maven | 3.8+ | Build Tool |



---

## 📁 Project Structure

```bash
authService-be/
├── .idea/ # IntelliJ IDEA configuration
├── .mvn/ # Maven wrapper
├── docs/ # Documentation files
│ └── flyway/
│ └── migration.md # Flyway migration guide
├── scripts/ # Shell scripts for automation
│ └── create-migration.sh # Script to create new migrations
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com.oneClick.authService_be/
│ │ │ ├── AuthServiceBeApplication.java
│ │ │ ├── config/ # Configuration classes
│ │ │ ├── controller/ # REST controllers
│ │ │ ├── dto/ # Data Transfer Objects
│ │ │ ├── entity/ # JPA entities (User, Role, etc.)
│ │ │ ├── repository/ # Database repositories
│ │ │ ├── service/ # Business logic
│ │ │ ├── security/ # Security & JWT configs
│ │ │ └── exception/ # Custom exceptions
│ │ └── resources/
│ │ ├── db/
│ │ │ └── migration/ # Flyway migration files
│ │ └── application.yaml # Application configuration
│ └── test/ # Unit and integration tests
├── .dockerignore # Docker ignore patterns
├── .env # Environment variables (git-ignored)
├── .env.example # Environment template
├── .gitattributes # Git attributes
├── .gitignore # Git ignore patterns
├── compose.yml # Docker Compose configuration
├── Dockerfile # Docker image definition
├── HELP.md # Spring Boot help
├── mvnw # Maven wrapper (Linux/Mac)
├── mvnw.cmd # Maven wrapper (Windows)
├── pom.xml # Maven dependencies
└── README.md # This file
```

---

## 🚀 Getting Started

### Prerequisites

Choose **one** of the following setups:

#### Option 1: Local Development
- Java Development Kit (JDK) 17 or higher
- Maven 3.8+
- PostgreSQL/MySQL database
- IDE (IntelliJ IDEA recommended)

#### Option 2: Docker (Recommended)
- Docker 20.10+
- Docker Compose 2.0+

---

### Installation

#### 1. Clone the Repository

```bash
git clone git@github.com:Shinx99/OneClick-authService-be.git
```
```bash
cd OneClick-authService-be
```

2. Configure Environment Variables

```bash
# Copy environment template
cp .env.example .env
```

```text
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=recruitment_auth
DB_USERNAME=postgres
DB_PASSWORD=your_password

# JWT Configuration
JWT_SECRET=your-secret-key-change-this-in-production
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Email Configuration (for verification)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# OAuth2 (Google)
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# OAuth2 (LinkedIn)
LINKEDIN_CLIENT_ID=your-linkedin-client-id
LINKEDIN_CLIENT_SECRET=your-linkedin-client-secret

# Application Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev
```
---
## Quick Start
```bash
# Start all services
docker compose up --build
```
**The application will be available at:** 
```text
# Health check endpoint
http://localhost:8080/actuator/health

# View all endpoints
http://localhost:8080/actuator

# Info endpoint
http://localhost:8080/actuator/info
```

```bash
# Check logs
docker compose logs -f

# Stop services
docker compose down
```


**Docker Compose Services:**
- auth-service: Spring Boot application (port 8080)
- database: PostgreSQL (port 5432)
- redis (optional): Session cache (port 6379)






















