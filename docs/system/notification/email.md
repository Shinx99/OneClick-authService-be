# 📧 Email Infrastructure - Cấu trúc và Luồng hoạt động
## 📁 Cấu trúc thư mục
```bash
src/main/java/com/oneClick/authService/
│
├── domain/                                        # ═══ DOMAIN LAYER ═══
│   └── verification/
│       └── service/
│           └── TokenGenerationService.java        # Generate tokens/OTP
│
├── application/                                   # ═══ APPLICATION LAYER ═══
│   ├── usecase/
│   │   └── verification/
│   │       ├── RequestEmailVerificationUseCase.java
│   │       └── SendOtpUseCase.java
│   │
│   └── port/
│       └── output/
│           └── EmailPort.java                     # Output Port (interface)
│
└── infrastructure/                                # ═══ INFRASTRUCTURE LAYER ═══
│
├── notification/
│   └── email/
│       ├── EmailService.java                  # 🔹 Interface - Email operations contract
│       ├── EmailServiceImpl.java              # 🔸 SMTP implementation
│       ├── EmailAdapter.java                  # 🔸 Hexagonal adapter (EmailPort → EmailService)
│       ├── EmailTemplateService.java          # 🔹 Interface - Template processing contract
│       ├── EmailTemplateServiceImpl.java      # 🔸 Thymeleaf template processor
│       ├── EmailConfig.java                   # 🔸 JavaMailSender configuration
│       │
│       └── dto/
│           ├── EmailRequest.java              # 🔸 Input DTO
│           └── EmailResponse.java             # 🔸 Output DTO
│
└── web/
└── devtools/
└── DevEmailController.java            # 🛠️ Dev testing endpoint

src/main/resources/
└── templates/
└── email/                                     # 📄 Thymeleaf HTML templates
├── email-verification.html
├── password-reset.html
├── otp-email.html
├── password-changed.html
├── welcome.html
└── suspicious-login.html
```

## 🔄 Luồng hoạt động (Flow)
Flow 1: Production Flow (Use Case → Email)
```text
┌─────────────────────────────────────────────────────────────────────────┐
│                         APPLICATION LAYER                                │
└─────────────────────────────────────────────────────────────────────────┘
│
│ 1. Use Case calls EmailPort
↓
Use Case (SendOtpUseCase)
│
│ emailPort.sendOtpEmail(email, name, otp, expiry)
↓
┌─────────────────────────────────────────────────────────────────────────┐
│                      INFRASTRUCTURE LAYER                                │
└─────────────────────────────────────────────────────────────────────────┘
│
│ 2. EmailAdapter implements EmailPort
↓
EmailAdapter (implements EmailPort)
│
│ 3. Delegates to EmailService
↓
EmailServiceImpl
│
│ 4. Build URLs and parameters
↓
EmailTemplateService
│
│ 5. Process Thymeleaf template
↓
TemplateEngine
│
│ 6. Load HTML template + inject variables
↓
otp-email.html (Thymeleaf template)
│
│ 7. Return processed HTML
↓
EmailServiceImpl
│
│ 8. Send email via JavaMailSender
↓
JavaMailSender → SMTP Server → 📧 Email delivered
Flow 2: Development Testing Flow (DevTools)
text
┌─────────────────────────────────────────────────────────────────────────┐
│                      DEVELOPMENT TESTING                                 │
└─────────────────────────────────────────────────────────────────────────┘
│
│ 1. Developer calls DevTools endpoint
↓
DevEmailController (Swagger/Postman)
│
│ GET /api/devtools/email/otp?email=test@example.com
↓
EmailServiceImpl
│
│ 2. Skip Use Case layer (testing only)
↓
EmailTemplateService → TemplateEngine → HTML
│
│ 3. Send test email
↓
JavaMailSender → SMTP → 📧 Test email delivered
```

## 📝 Chi tiết từng Component
### 1️⃣ EmailPort.java (Application Layer)
Vai trò:

✅ Định nghĩa trong Application Layer

✅ Use Cases gọi port này

✅ Không biết implementation details (SMTP, SendGrid, AWS SES)

### 2️⃣ EmailAdapter.java (Infrastructure Layer)
Vai trò:

✅ Bridge pattern

✅ Application Layer không biết EmailService existence

✅ Dễ swap provider (SMTP → SendGrid → AWS SES)

### 3️⃣ EmailService.java (Interface)
Vai trò:

✅ Contract cho email sending

✅ Infrastructure layer interface

### 4️⃣ EmailServiceImpl.java (SMTP Implementation)
Vai trò:

✅ Orchestrate email sending

✅ Build URLs

✅ Delegate template processing

✅ Handle SMTP communication

### 5️⃣ EmailTemplateService.java (Interface)
Vai trò:

✅ Contract cho template processing

✅ Tách biệt template logic khỏi email sending

### 6️⃣ EmailTemplateServiceImpl.java (Thymeleaf Processor)
Vai trò:

✅ Single Responsibility: Chỉ xử lý templates

✅ Inject variables vào Thymeleaf templates

✅ Return HTML string

### 7️⃣ EmailConfig.java (Spring Configuration)
Vai trò:

✅ Configure SMTP connection

✅ Load credentials từ environment variables

✅ Setup timeouts

### 8️⃣ EmailRequest.java & EmailResponse.java (DTOs)
Vai trò:

✅ Data transfer between layers

✅ Validation

✅ Response tracking

### 9️⃣ DevEmailController.java (Development Testing)
Vai trò:

✅ Quick testing via Swagger UI

✅ Verify SMTP configuration works

✅ Preview email templates

⚠️ Chỉ dùng trong dev environment

 ## Test via DevTools Controller (Swagger)

>### http://localhost:8080/swagger-ui.html



## 🎯 Kết luận
Cấu trúc email infrastructure này tuân theo:

✅ DDD + Clean Architecture

✅ Hexagonal Architecture (Ports & Adapters)

✅ Single Responsibility Principle

✅ Separation of Concerns

✅ Testability (Unit + Integration)

✅ Maintainability (Easy to change provider)