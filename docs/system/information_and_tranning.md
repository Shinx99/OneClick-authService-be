# 📝 Project Documentation - Auth Service Backend

Welcome to Auth Service Backend!

## Operating System
- **Recommended**: Ubuntu 22.04 LTS or 24.04 LTS
- **Supported**: Windows 10/11, macOS 12+

## IDE
- **Recommended**: IntelliJ IDEA (Community or Ultimate)
- **Alternative**: VS Code, Eclipse, or any Java IDE

### Required Tools
- Java Development Kit (JDK) 17+
- Maven 3.8+
- Docker 20.10+
- Docker Compose 2.0+
- Git 2.30+
- PostgreSQL 14+ (for local development without Docker)

---

## 📚 Tech Stack

**Backend Framework**
- Java 17
- Spring Boot 3.5.9
- Spring Web (RESTful APIs)
- Spring Data JPA (ORM)
- Spring Data Redis (Caching)
- Spring Validation (Request Validation)
- Spring Actuator (Monitoring)

**Database & Migration**
- PostgreSQL (Primary Database)
- Redis (Caching Layer)
- Flyway (Database Migration)

**Development Tools**
- Lombok 1.18.30 (Reduce Boilerplate)
- MapStruct 1.5.5.Final (DTO Mapping)
- Apache Commons Lang3 (Utilities)
- Spring Boot DevTools (Hot Reload)

**Build & Deployment**
- Maven 3.8+
- Docker & Docker Compose
- Maven Compiler Plugin 3.13.0

---

## 📁 Folder Structure

```bash
authService-be/
├── .idea/                            # IntelliJ IDEA configuration
├── .mvn/                             # Maven wrapper files
├── docs/                             # Project documentation
│   ├── flyway/
│   │   └── migration.md              # Database migration guide
│   └── api/
│       └── endpoints.md              # API documentation
│
├── scripts/                          # Automation scripts
│   ├── create-migration.sh           # Create Flyway migration
│   ├── setup.sh                      # Initial project setup
│   └── utils/
│       └── db-backup.sh              # Database backup script
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.oneClick.authService_be/
│   │   │       ├── AuthServiceBeApplication.java  # Main application class
│   │   │       │
│   │   │       ├── config/           # Configuration classes
│   │   │       │   ├── SecurityConfig.java        # Spring Security config
│   │   │       │   ├── JwtConfig.java             # JWT configuration
│   │   │       │   ├── CorsConfig.java            # CORS configuration
│   │   │       │   ├── RedisConfig.java           # Redis configuration
│   │   │       │   └── SwaggerConfig.java         # API documentation config
│   │   │       │
│   │   │       ├── controller/       # REST API Controllers
│   │   │       │   ├── AuthController.java        # Auth endpoints (/api/auth/*)
│   │   │       │   ├── UserController.java        # User endpoints (/api/users/*)
│   │   │       │   ├── CandidateController.java   # Candidate endpoints
│   │   │       │   └── RecruiterController.java   # Recruiter endpoints
│   │   │       │
│   │   │       ├── dto/              # Data Transfer Objects
│   │   │       │   ├── request/      # Request DTOs
│   │   │       │   │   ├── LoginRequest.java
│   │   │       │   │   ├── RegisterRequest.java
│   │   │       │   │   └── UpdateProfileRequest.java
│   │   │       │   │
│   │   │       │   └── response/     # Response DTOs
│   │   │       │       ├── AuthResponse.java
│   │   │       │       ├── UserResponse.java
│   │   │       │       └── ApiResponse.java
│   │   │       │
│   │   │       ├── domain/           # JPA Entities (Database models)
│   │   │       │   ├── User.java                  # User entity
│   │   │       │   ├── Role.java                  # Role entity
│   │   │       │   ├── Candidate.java             # Candidate profile
│   │   │       │   ├── Recruiter.java             # Recruiter profile
│   │   │       │   └── BaseEntity.java            # Base entity with common fields
│   │   │       │
│   │   │       ├── repository/       # Spring Data JPA Repositories
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── RoleRepository.java
│   │   │       │   ├── CandidateRepository.java
│   │   │       │   └── RecruiterRepository.java
│   │   │       │
│   │   │       ├── service/          # Business Logic Layer
│   │   │       │   ├── AuthService.java           # Authentication service
│   │   │       │   ├── UserService.java           # User management service
│   │   │       │   ├── EmailService.java          # Email service
│   │   │       │   ├── JwtService.java            # JWT token service
│   │   │       │   └── impl/         # Service implementations
│   │   │       │       ├── AuthServiceImpl.java
│   │   │       │       ├── UserServiceImpl.java
│   │   │       │       └── EmailServiceImpl.java
│   │   │       │
│   │   │       ├── security/         # Security & Authentication
│   │   │       │   ├── JwtAuthenticationFilter.java  # JWT filter
│   │   │       │   ├── JwtTokenProvider.java         # JWT utilities
│   │   │       │   ├── UserDetailsServiceImpl.java   # User details service
│   │   │       │   └── CustomAccessDeniedHandler.java
│   │   │       │
│   │   │       ├── exception/        # Exception Handling
│   │   │       │   ├── GlobalExceptionHandler.java   # Global handler
│   │   │       │   ├── ResourceNotFoundException.java
│   │   │       │   ├── BadRequestException.java
│   │   │       │   └── UnauthorizedException.java
│   │   │       │
│   │   │       ├── mapper/           # MapStruct Mappers
│   │   │       │   ├── UserMapper.java
│   │   │       │   ├── CandidateMapper.java
│   │   │       │   └── RecruiterMapper.java
│   │   │       │
│   │   │       ├── validation/       # Custom Validators
│   │   │       │   ├── UniqueEmail.java
│   │   │       │   └── StrongPassword.java
│   │   │       │
│   │   │       └── util/             # Utility Classes
│   │   │           ├── DateUtils.java
│   │   │           ├── StringUtils.java
│   │   │           └── Constants.java
│   │   │
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/        # Flyway migration files
│   │       │       ├── V20260120_140000__create_users_table.sql
│   │       │       ├── V20260120_140100__create_candidates_table.sql
│   │       │       └── V20260120_140200__create_recruiters_table.sql
│   │       │
│   │       ├── templates/            # Email templates (Thymeleaf)
│   │       │   ├── welcome-email.html
│   │       │   └── password-reset.html
│   │       │
│   │       ├── application.yml       # Main configuration
│   │       ├── application-dev.yml   # Development profile
│   │       ├── application-prod.yml  # Production profile
│   │       └── banner.txt            # Custom Spring Boot banner
│   │
│   └── test/                         # Test files
│       └── java/
│           └── com.oneClick.authService_be/
│               ├── controller/       # Controller tests
│               │   └── AuthControllerTest.java
│               │
│               ├── service/          # Service tests
│               │   └── AuthServiceTest.java
│               │
│               ├── repository/       # Repository tests
│               │   └── UserRepositoryTest.java
│               │
│               └── integration/      # Integration tests
│                   └── AuthIntegrationTest.java
│
├── .dockerignore                     # Docker ignore patterns
├── .env                              # Environment variables (git-ignored)
├── .env.example                      # Environment template for team
├── .gitattributes                    # Git attributes
├── .gitignore                        # Git ignore rules
├── compose.yml                       # Docker Compose configuration
├── Dockerfile                        # Docker image definition
├── HELP.md                           # Spring Boot help
├── mvnw                              # Maven wrapper (Linux/macOS)
├── mvnw.cmd                          # Maven wrapper (Windows)
├── pom.xml                           # Maven dependencies & build config
└── README.md                         # Project documentation
```

📊 Folder Roles Summary

| Folder/File | Main Purpose | Usage Notes |
|-------------|--------------|-------------|
| `authService-be/` | Root directory of backend project | Contains source code, config, documentation |
| `docs/` | Project documentation | Migration guides, API specs |
| `scripts/` | Automation shell scripts | Database setup, migration creation |
| `src/main/java/` | Java source code | All application code |
| `config/` | Configuration classes | Security, JWT, CORS, Redis, Swagger |
| `controller/` | REST API endpoints | Handle HTTP requests/responses |
| `dto/` | Data Transfer Objects | Request/Response objects for APIs |
| `entity/` | JPA database entities | Database table mappings |
| `repository/` | Database access layer | Spring Data JPA interfaces |
| `service/` | Business logic layer | Core application logic |
| `security/` | Authentication & authorization | JWT filters, security config |
| `exception/` | Exception handling | Global error handling |
| `mapper/` | DTO ↔ Entity conversion | MapStruct mappers |
| `validation/` | Custom validators | Email, password validation |
| `util/` | Utility classes | Helper functions, constants |
| `resources/db/migration/` | Database migrations | Flyway SQL scripts |
| `resources/templates/` | Email templates | HTML templates for emails |
| `resources/application.yml` | App configuration | Database, server, JWT settings |
| `src/test/` | Unit & integration tests | All test files |
| `.env` | Environment variables | Local config (not in Git) |
| `compose.yml` | Docker services definition | Database, Redis, App container |
| `Dockerfile` | Docker image build | Container image specification |
| `pom.xml` | Maven dependencies | Project dependencies & plugins |

---
🧩 Development Guidelines for Team
### Controllers (`src/main/java/.../controller/`)

- **Only handle HTTP**: Receive requests, call services, return responses
- **No business logic**: Delegate all logic to service layer
- **Use DTOs**: Never expose entities directly in responses
- **Validation**: Use `@Valid` on request objects

**Example:**
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
```
### Services (`src/main/java/.../service/`)

- **Business logic only**: All core application logic
- **Transaction management**: Use `@Transactional` when needed
- **Call repositories**: Database access through repositories
- **Never return entities**: Use mappers to convert to DTOs

### Repositories (`src/main/java/.../repository/`)

- **Database queries only**: Spring Data JPA interfaces
- **Custom queries**: Use `@Query` for complex queries
- **Naming convention**: Follow Spring Data naming rules

### DTOs (`src/main/java/.../dto/`)

- **Separate request/response**: Different DTOs for input/output
- **Use validation annotations**: `@NotNull`, `@Email`, `@Size`
- **Use Lombok**: Reduce boilerplate with `@Data`, `@Builder`

### Entities (`src/main/java/.../entity/`)

- **Map database tables**: One entity = one table
- **Relationships**: Define with `@OneToMany`, `@ManyToOne`, etc.
- **Use Lombok**: `@Entity`, `@Table`, `@Data`
- **Extend BaseEntity**: Common fields (id, createdAt, updatedAt)

### Mappers (`src/main/java/.../mapper/`)

- **MapStruct interfaces**: Automatic DTO ↔ Entity conversion
- **No manual mapping**: Let MapStruct generate code

### Exception Handling (`src/main/java/.../exception/`)

- **Custom exceptions**: Create specific exceptions
- **Global handler**: `@ControllerAdvice` handles all exceptions
- **Return ApiResponse**: Consistent error format

### Database Migrations (`src/main/resources/db/migration/`)

- **Never modify existing**: Create new migration for changes
- **Naming convention**: `V{timestamp}__{feature}__{description}.sql`
- **Use script**: Run `./scripts/create-migration.sh` to generate

---

## 🔧 Configuration Files

### `application.yml`
- Main configuration for all profiles
- Database connection, server port, JWT settings

### `application-dev.yml`
- Development-specific settings
- Debug logging, dev database

### `application-prod.yml`
- Production settings
- Optimized logging, production database

### `.env`
- Local environment variables
- **Never commit to Git**
- Copy from `.env.example` to start

---

## 🐛 Troubleshooting

### Database Connection Failed

```bash
# Check if PostgreSQL is running
docker-compose ps

# View database logs
docker-compose logs database

# Restart database
docker-compose restart database
```

### Port 8080 Already in Use
```bash
# Find process using port 8080
lsof -ti:8080 | xargs kill

# Or change port in application.yml:
server:
  port: 8081
```

### Flyway Migration Failed
```bash
# Check migration files
ls -la src/main/resources/db/migration/

# Clean and rebuild
./mvnw clean install

# Reset database (⚠️ deletes all data)
docker-compose down -v
docker-compose up -d
```

### Maven Build Failed
```bash
# Clean build
./mvnw clean install

# Skip tests
./mvnw clean install -DskipTests

# Update dependencies
./mvnw dependency:resolve
```

## 🧪 Useful Scripts
```bash
# Development
./mvnw spring-boot:run              # Run application
./mvnw test                         # Run tests
./mvnw clean install                # Build project

# Docker
docker-compose up -d                # Start all services
docker-compose down                 # Stop services
docker-compose logs -f auth-service # View logs
docker-compose restart auth-service # Restart app

# Database
./scripts/create-migration.sh       # Create new migration
docker-compose exec database psql -U postgres  # Access DB
```

## 🤝 Development Conventions
### Code Style
- Follow Google Java Style Guide
- Use meaningful variable names
- Add JavaDoc for public methods
- Keep methods short (< 20 lines)

### Git Workflow
- Create feature branch: git checkout -b feature/user-registration
- Commit convention: feat: add user registration endpoint
- Push and create Pull Request
- Code review required before merge

### API Design
- RESTful conventions: GET, POST, PUT, DELETE
- Versioning: /api/v1/auth/login
- Response format: Always use ApiResponse<T>
- HTTP status codes: 200 (OK), 201 (Created), 400 (Bad Request), 401 (Unauthorized), 404 (Not Found)

### Testing
- Write unit tests for services
- Integration tests for controllers
- Minimum 80% code coverage
- Use @MockBean for mocking dependencies

### Security
- Never log sensitive data (passwords, tokens)
- Always validate user input
- Use parameterized queries (prevent SQL injection)
- Implement rate limiting for auth endpoints

## 📝 Additional Resources
>- Spring Boot Documentation
>- Spring Security Reference
>- Flyway Documentation
>- MapStruct Guide
>- Docker Best Practices

---
## Built with ❤️ by the Auth Service Team

