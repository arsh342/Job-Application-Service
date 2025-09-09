# Job Portal Microservices System

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [System Architecture](#system-architecture)
- [Services Overview](#services-overview)
- [Authentication Service (Port 8083)](#authentication-service-port-8083)
- [Job Service (Port 8081)](#job-service-port-8081)
- [Application Service (Port 8082)](#application-service-port-8082)
- [Inter-Service Communication Flow](#inter-service-communication-flow)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Setup & Installation](#setup--installation)
- [User Workflows](#user-workflows)
- [Security Implementation](#security-implementation)
- [Troubleshooting](#troubleshooting)
- [Development Guidelines](#development-guidelines)

## 🎯 Project Overview

The **Job Portal Microservices System** is a comprehensive, enterprise-grade job application platform built using Spring Boot microservices architecture. This system demonstrates modern software engineering practices with JWT-based authentication, RESTful APIs, and independent service deployment.

### Key Features

- **Multi-role Support**: Separate interfaces for Job Seekers and Employers
- **Advanced Authentication**:
  - JWT token-based authentication across all services
  - **HTTP Cookie Persistence**: Automatic cookie setting for page reload authentication
  - **Cross-Service Token Validation**: Real-time token verification
  - **Seamless Navigation**: Token-based routing with URL parameter support
- **Real-time Communication**: Seamless inter-service communication via REST APIs
- **Scalable Architecture**: Independent services that can be scaled individually
- **Modern UI**: Responsive web interface with professional design
- **Enhanced Security**: Role-based access control with comprehensive endpoint protection

### Technology Stack

| Component          | Technology             | Version       |
| ------------------ | ---------------------- | ------------- |
| **Framework**      | Spring Boot            | 3.5.5         |
| **Language**       | Java                   | 21            |
| **Security**       | Spring Security + JWT  | JJWT 0.11.5   |
| **Database**       | MySQL                  | 8.0+          |
| **ORM**            | Hibernate/JPA          | -             |
| **Build Tool**     | Maven                  | 3.6+          |
| **Frontend**       | Thymeleaf + HTML5/CSS3 | -             |
| **Authentication** | HTTP Cookies + JWT     | Enhanced v1.1 |

## 🏗️ System Architecture

### Microservices Design Pattern

```
┌─────────────────────┐    ┌─────────────────────┐    ┌─────────────────────┐
│   AUTHENTICATION    │    │    JOB SERVICE      │    │  APPLICATION        │
│      SERVICE        │    │                     │    │    SERVICE          │
│   (Port 8083)       │    │   (Port 8081)       │    │  (Port 8082)        │
│                     │    │                     │    │                     │
│ • User Management   │    │ • Job Posting       │    │ • Job Applications  │
│ • JWT Token Issue   │    │ • Job Management    │    │ • Application       │
│ • Authentication    │    │ • Employer Portal   │    │   Tracking          │
│ • User Validation   │    │ • Job Search        │    │ • Job Seeker Portal │
└─────────────────────┘    └─────────────────────┘    └─────────────────────┘
         │                           │                           │
         │◄────── Token Validation ──┤                           │
         │                           │                           │
         │◄────── Token Validation ──┼───────────────────────────┤
         │                           │                           │
         │                           │◄───── Job Data Fetch ─────┤
```

### Service Independence Benefits

- **Scalability**: Each service can be scaled based on demand
- **Technology Flexibility**: Services can use different tech stacks
- **Fault Isolation**: Service failures don't cascade to others
- **Independent Deployment**: Update services without affecting others

## 📊 Services Overview

### Service Distribution

| Service            | Port | Database                    | Primary Function         | User Role   |
| ------------------ | ---- | --------------------------- | ------------------------ | ----------- |
| **Authentication** | 8083 | `job_portal_auth_db`        | User management & JWT    | All Users   |
| **Job**            | 8081 | `job_portal_job_db`         | Job posting & management | Employers   |
| **Application**    | 8082 | `job_portal_application_db` | Application tracking     | Job Seekers |

### Communication Matrix

- **Authentication Service**: Provides token validation to all services
- **Job Service**: Supplies job data to Application Service
- **Application Service**: Manages applications and fetches job details

## 🔐 Authentication Service (Port 8083)

### Core Functions

The Authentication Service serves as the central identity management system, handling user registration, login, and token validation across the entire platform.

### Key Components & Files

#### **Main Application Class**

- **File**: `AuthenticationApplication.java`
- **Function**: Spring Boot application entry point with `@SpringBootApplication`
- **Purpose**: Initializes the authentication service with all configurations

#### **Security Configuration**

- **File**: `SecurityConfig.java`
- **Function**: Configures Spring Security with JWT authentication
- **Features**:
  - JWT token validation filter
  - CORS configuration for cross-origin requests
  - Public endpoint access (login, register)
  - Protected route security

#### **JWT Utility**

- **File**: `JwtUtil.java`
- **Function**: Handles JWT token generation, validation, and parsing
- **Operations**:
  - Generate tokens with user information
  - Extract claims from tokens
  - Validate token expiration and signature
  - Parse user details from tokens

#### **User Entity**

- **File**: `User.java`
- **Structure**:
  ```java
  @Entity
  public class User implements UserDetails {
      @Id @GeneratedValue private Long id;
      @Column(unique = true) private String email;
      private String password; // BCrypt encrypted
      private String name;
      @Enumerated private UserType userType; // EMPLOYER/JOB_SEEKER
      private Long externalUserId; // Reference to other services
      private String companyName; // For employers
  }
  ```

#### **Authentication Controller**

- **File**: `AuthController.java`
- **Endpoints**:
  - `POST /api/auth/register` - User registration
  - `POST /api/auth/login` - User authentication
  - `POST /api/auth/validate` - Token validation
- **Features**: Input validation, error handling, response formatting

#### **Authentication Service**

- **File**: `AuthService.java`
- **Functions**:
  - User registration with password encryption
  - Login authentication with JWT generation
  - Token validation with user information retrieval
  - Password encoding using BCrypt

#### **User Repository**

- **File**: `UserRepository.java`
- **Interface**: Extends `JpaRepository<User, Long>`
- **Queries**: Custom finder methods for user lookup

#### **Web Controller**

- **File**: `WebController.java`
- **Purpose**: Handles web page routing for authentication UI
- **Templates**: login.html, register.html, dashboard.html

### Service Workflow

1. **Registration**: User submits details → Password encrypted → User saved → Success response
2. **Login**: Credentials validated → JWT generated → Token returned
3. **Validation**: Token received → Signature verified → User details extracted → Validation response

## 💼 Job Service (Port 8081)

### Core Functions

The Job Service manages all job-related operations, providing employers with tools to post, update, and manage job listings while offering public access to job browsing.

### Key Components & Files

#### **Main Application Class**

- **File**: `JobApplication.java`
- **Function**: Spring Boot application with `@EnableFeignClients`
- **Purpose**: Enables communication with other services via Feign

#### **Job Entity**

- **File**: `Job.java`
- **Structure**:
  ```java
  @Entity
  public class Job {
      @Id @GeneratedValue private Long jobId;
      private String title, description, location, company;
      private Double salaryMin, salaryMax;
      @Enumerated private JobStatus status; // OPEN/CLOSED
      private Long employerId;
      private LocalDate postedDate;
  }
  ```

#### **Job Controller**

- **File**: `JobController.java`
- **Endpoints**:
  - `GET /api/jobs` - Public job browsing
  - `POST /api/jobs` - Create job (Employer)
  - `PUT /api/jobs/{id}` - Update job (Employer)
  - `DELETE /api/jobs/{id}` - Delete job (Employer)
- **Features**: Authentication validation, employer authorization

#### **Job Service**

- **File**: `JobService.java`
- **Functions**:
  - Job creation with employer validation
  - Job updates with ownership verification
  - Job search and filtering
  - Application count retrieval

#### **Authentication Client**

- **File**: `AuthServiceClient.java`
- **Purpose**: Communicates with Authentication Service for token validation
- **Methods**: Validate tokens, retrieve user information

#### **Web Controller**

- **File**: `WebController.java`
- **Purpose**: Handles employer dashboard and job management UI
- **Templates**: dashboard.html, create-job.html, job-listings.html

### Service Workflow

1. **Job Creation**: Employer authenticated → Job data validated → Job saved → Success response
2. **Job Browsing**: Public access → Jobs retrieved → Formatted response
3. **Job Management**: Employer authenticated → Ownership verified → Operation performed

## 📋 Application Service (Port 8082)

### Core Functions

The Application Service handles job applications, tracking application status, and providing job seekers with application management tools.

### Key Components & Files

#### **Main Application Class**

- **File**: `Application.java`
- **Function**: Spring Boot application with `@EnableFeignClients`
- **Purpose**: Enables inter-service communication

#### **Application Entity**

- **File**: `JobApplication.java`
- **Structure**:
  ```java
  @Entity
  public class JobApplication {
      @Id @GeneratedValue private Long applicationId;
      private Long jobId, applicantId;
      private String coverLetter;
      @Enumerated private ApplicationStatus status; // APPLIED/SHORTLISTED/etc.
      private LocalDate appliedDate;
  }
  ```

#### **Application Controller**

- **File**: `ApplicationController.java`
- **Endpoints**:
  - `POST /api/applications` - Apply to job
  - `GET /api/applications/my-applications` - Get user's applications
  - `PUT /api/applications/{id}/status` - Update status (Employer)
- **Features**: Authentication, authorization, data validation

#### **Application Service**

- **File**: `JobApplicationService.java`
- **Functions**:
  - Application creation with duplicate prevention
  - Status updates with permission checks
  - Application retrieval with job details

#### **Service Clients**

- **Files**: `AuthServiceClient.java`, `JobServiceClient.java`
- **Purpose**:
  - Auth client: Token validation and user info
  - Job client: Fetch job details for applications

#### **Web Controller**

- **File**: Handles job seeker UI routing
- **Templates**: dashboard.html, browse-jobs.html, my-applications.html

### Service Workflow

1. **Job Application**: User authenticated → Application created → Job details fetched → Success response
2. **Application Tracking**: User authenticated → Applications retrieved → Job info enriched → Response formatted
3. **Status Updates**: Employer authenticated → Permissions verified → Status updated

## 🔄 Inter-Service Communication Flow

### Authentication Flow

```
User Login Request → Authentication Service
    ↓
JWT Token Generated → User Details Retrieved
    ↓
Token Returned to Client → Stored in Browser
    ↓
Subsequent Requests → Token Attached in Header
    ↓
Service Receives Request → Validates Token with Auth Service
    ↓
User Context Established → Request Processed
```

### Job Application Flow

```
Job Seeker Browses Jobs → Job Service (Public)
    ↓
Selects Job → Applies via Application Service
    ↓
Application Service → Validates Token with Auth Service
    ↓
Fetches Job Details → From Job Service
    ↓
Creates Application → Saves to Database
    ↓
Success Response → Job Seeker Dashboard Updated
```

### Employer Management Flow

```
Employer Logs In → Authentication Service
    ↓
Accesses Dashboard → Job Service
    ↓
Creates Job Posting → Job Service Database
    ↓
Views Applications → Application Service Queries
    ↓
Updates Status → Application Service Database
```

## 📚 API Documentation

All services support JWT-based authentication with HTTP cookies for seamless page reloads and navigation persistence.

### 🔐 Authentication Service (Port 8083)

Base URL: `http://localhost:8083`

#### **Web Pages (UI Routes)**

```http
GET  /                          # Home (redirects to login)
GET  /login                     # Login page
GET  /register                  # Registration page
GET  /dashboard                 # User dashboard
GET  /profile                   # User profile page
```

#### **REST API Endpoints**

```http
POST /api/auth/register         # Register new user
POST /api/auth/login            # Authenticate user
POST /api/auth/validate         # Validate JWT token
```

#### **Request/Response Examples**

**User Registration:**

```json
POST /api/auth/register
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "name": "John Doe",
  "userType": "JOB_SEEKER", // or "EMPLOYER"
  "companyName": "Company Inc" // Required for EMPLOYER
}
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "user@example.com",
  "name": "John Doe",
  "userType": "JOB_SEEKER",
  "userId": 1
}
```

**User Login:**

```json
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "user@example.com",
  "name": "John Doe",
  "userType": "JOB_SEEKER",
  "userId": 1
}
```

### 💼 Job Service (Port 8081)

Base URL: `http://localhost:8081`

#### **Web Pages (UI Routes)**

```http
GET  /                          # Home (redirects to dashboard)
GET  /login-redirect            # Login redirect page
GET  /dashboard                 # Employer dashboard
GET  /job-listings              # Employer's job listings
GET  /jobs                      # Public job browsing (alias)
GET  /create-job                # Create/edit job form
GET  /job-details               # Job details page
GET  /profile                   # Employer profile
GET  /debug                     # Debug information page
```

#### **Public API Endpoints (No Authentication Required)**

```http
GET  /api/jobs                  # Browse all public jobs
GET  /api/jobs/all              # Get all jobs (alias)
GET  /api/jobs/{jobId}          # Get specific job details
```

#### **Protected API Endpoints (Authentication Required)**

```http
# Job Management (Employers)
POST   /api/jobs                # Create new job posting
PUT    /api/jobs/{jobId}        # Update existing job
DELETE /api/jobs/{jobId}        # Delete job posting
GET    /api/jobs/my-jobs        # Get employer's jobs

# Employer-specific
GET    /api/employers/{employerId}/jobs    # Get jobs by employer

# Application Management (Cross-service)
GET    /api/jobs/{jobId}/applications      # Get job applications
PUT    /api/applications/{applicationId}/status  # Update application status
```

#### **Request/Response Examples**

**Create Job:**

```json
POST /api/jobs
Headers: Authorization: Bearer {token}
{
  "title": "Software Developer",
  "description": "Full-stack developer position...",
  "location": "New York, NY",
  "company": "Tech Corp",
  "salaryMin": 70000.00,
  "salaryMax": 90000.00,
  "status": "OPEN"
}

Response:
{
  "jobId": 1,
  "title": "Software Developer",
  "description": "Full-stack developer position...",
  "location": "New York, NY",
  "company": "Tech Corp",
  "salaryMin": 70000.00,
  "salaryMax": 90000.00,
  "postedDate": "2025-09-10",
  "status": "OPEN",
  "employerId": 1
}
```

### 📋 Application Service (Port 8082)

Base URL: `http://localhost:8082`

#### **Web Pages (UI Routes)**

```http
GET  /                          # Home (redirects to dashboard)
GET  /dashboard                 # Job seeker dashboard
GET  /browse-jobs               # Browse available jobs
GET  /my-applications           # View user's applications
GET  /profile                   # Job seeker profile
GET  /job-details               # Job details and application
GET  /status-demo               # Application status demo
```

#### **Protected API Endpoints (Authentication Required)**

```http
# Application Management
POST   /api/applications        # Submit job application
POST   /api/applications/apply  # Alternative apply endpoint
PUT    /api/applications/{applicationId}         # Update application
DELETE /api/applications/{applicationId}         # Withdraw application
GET    /api/applications/{applicationId}         # Get specific application

# Application Retrieval
GET    /api/applications/my-applications         # Get user's applications
GET    /api/applicants/{applicantId}/applications  # Get applicant's applications
GET    /api/jobs/{jobId}/applications            # Get applications for job

# Application Status (Employers)
PUT    /api/applications/{applicationId}/status  # Update application status

# Profile Management
GET    /api/profile                             # Get user profile
PUT    /api/profile                             # Update user profile

# Debug Endpoints
GET    /api/debug/applications                  # Debug applications
GET    /api/debug/auth                         # Debug authentication
```

#### **Request/Response Examples**

**Submit Application:**

```json
POST /api/applications
Headers: Authorization: Bearer {token}
{
  "jobId": 1,
  "coverLetter": "I am very interested in this position...",
  "resumeUrl": "https://example.com/resume.pdf"
}

Response:
{
  "applicationId": 1,
  "jobId": 1,
  "applicantId": 2,
  "coverLetter": "I am very interested in this position...",
  "resumeUrl": "https://example.com/resume.pdf",
  "status": "APPLIED",
  "appliedDate": "2025-09-10"
}
```

**Get User Applications:**

```json
GET /api/applications/my-applications
Headers: Authorization: Bearer {token}

Response:
[
  {
    "applicationId": 1,
    "jobId": 1,
    "jobTitle": "Software Developer",
    "company": "Tech Corp",
    "status": "APPLIED",
    "appliedDate": "2025-09-10",
    "coverLetter": "I am very interested...",
    "resumeUrl": "https://example.com/resume.pdf"
  }
]
```

### 🔄 Inter-Service Communication

The services communicate via REST APIs with the following patterns:

#### **Authentication Flow**

1. User logs in through Authentication Service → Receives JWT token
2. Token included in subsequent requests to Job/Application services
3. Services validate token with Authentication Service via `/api/auth/validate`

#### **Job Application Flow**

1. Job Seeker browses jobs on Application Service
2. Application Service fetches job data from Job Service
3. Application submitted through Application Service
4. Job Service queries Application Service for application data

#### **Cross-Service Endpoints**

**Job Service ↔ Application Service:**

```http
# Job Service calls Application Service
GET  {APPLICATION_SERVICE}/api/jobs/{jobId}/applications

# Application Service calls Job Service
GET  {JOB_SERVICE}/api/jobs/{jobId}
GET  {JOB_SERVICE}/api/jobs
```

**All Services ↔ Authentication Service:**

```http
POST {AUTH_SERVICE}/api/auth/validate
```

### 🔐 Authentication & Security

#### **JWT Token Format**

- **Header**: `Authorization: Bearer {token}`
- **Cookie**: `authToken={token}` (for page reload persistence)
- **Query Parameter**: `?token={token}` (for navigation links)

#### **Token Claims**

```json
{
  "sub": "user@example.com",
  "userId": 1,
  "userType": "JOB_SEEKER",
  "iat": 1757444864,
  "exp": 1757531264
}
```

#### **Security Features**

- ✅ **JWT Authentication**: Stateless token-based authentication
- ✅ **HTTP Cookies**: Automatic cookie setting for page reload persistence
- ✅ **CORS Support**: Cross-origin request handling
- ✅ **Role-based Access**: Separate endpoints for Employers/Job Seekers
- ✅ **Token Validation**: Real-time token verification across services
- ✅ **Secure Headers**: Proper security headers configuration

### 📊 HTTP Status Codes

| Code | Meaning               | Usage                             |
| ---- | --------------------- | --------------------------------- |
| 200  | OK                    | Successful requests               |
| 201  | Created               | Resource created successfully     |
| 400  | Bad Request           | Invalid request data              |
| 401  | Unauthorized          | Missing or invalid authentication |
| 403  | Forbidden             | Access denied for resource        |
| 404  | Not Found             | Resource not found                |
| 409  | Conflict              | Resource already exists           |
| 500  | Internal Server Error | Server processing error           |

## 🗄️ Database Schema

### Authentication Database (`job_portal_auth_db`)

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    user_type ENUM('EMPLOYER', 'JOB_SEEKER') NOT NULL,
    external_user_id BIGINT,
    company_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Job Database (`job_portal_job_db`)

```sql
CREATE TABLE jobs (
    job_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255) NOT NULL,
    company VARCHAR(255) NOT NULL,
    salary_min DECIMAL(10,2),
    salary_max DECIMAL(10,2),
    posted_date DATE DEFAULT CURRENT_DATE,
    status ENUM('OPEN', 'CLOSED') DEFAULT 'OPEN',
    employer_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Application Database (`job_portal_application_db`)

```sql
CREATE TABLE job_applications (
    application_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_id BIGINT NOT NULL,
    applicant_id BIGINT NOT NULL,
    cover_letter TEXT,
    status ENUM('APPLIED', 'SHORTLISTED', 'REJECTED', 'HIRED') DEFAULT 'APPLIED',
    applied_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_job_applicant (job_id, applicant_id)
);
```

## 🚀 Setup & Installation

### Prerequisites

- **Java**: OpenJDK 21 or higher
- **Maven**: 3.6+ for dependency management
- **MySQL**: 8.0+ database server
- **Git**: For version control

### Database Setup

```sql
-- Create databases
CREATE DATABASE job_portal_auth_db;
CREATE DATABASE job_portal_job_db;
CREATE DATABASE job_portal_application_db;

-- Create user (optional)
CREATE USER 'jobportal'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON job_portal_*.* TO 'jobportal'@'localhost';
```

### Service Configuration

Update `application.properties` in each service:

```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.url=jdbc:mysql://localhost:3306/database_name
```

### Startup Sequence

```bash
# Terminal 1: Authentication Service (Required First)
cd Authentication && mvn spring-boot:run

# Terminal 2: Job Service
cd Job && mvn spring-boot:run

# Terminal 3: Application Service
cd Application && mvn spring-boot:run
```

### Access URLs

- **Authentication Portal**: http://localhost:8083
- **Employer Dashboard**: http://localhost:8081/dashboard
- **Job Seeker Dashboard**: http://localhost:8082/dashboard

## 👥 User Workflows

### Job Seeker Journey

1. **Registration**: Create account as JOB_SEEKER
2. **Authentication**: Login to receive JWT token
3. **Job Discovery**: Browse available jobs
4. **Application**: Apply with cover letter
5. **Tracking**: Monitor application status
6. **Management**: View and manage applications

### Employer Journey

1. **Registration**: Create account as EMPLOYER
2. **Authentication**: Login to receive JWT token
3. **Job Posting**: Create job listings
4. **Management**: Update and manage jobs
5. **Review**: View applications for jobs
6. **Hiring**: Update application status

## 🔒 Security Implementation

### JWT Authentication Flow

1. **Token Generation**: User credentials validated → JWT created with user info
2. **Token Storage**: Client stores token in localStorage/sessionStorage + HTTP cookies
3. **Request Authorization**: Token attached to Authorization header or cookies
4. **Token Validation**: Service validates token with Authentication Service
5. **User Context**: User information extracted and made available

### 🔄 Enhanced Authentication Features (v1.1)

#### **HTTP Cookie Persistence**

- **Server-side Cookie Setting**: When tokens are found in URL parameters, services automatically set HttpOnly cookies
- **Page Reload Support**: Cookies persist across browser refreshes and navigation
- **Cross-Service Compatibility**: Cookies work across all microservices (Application, Job, Authentication)

#### **Multi-Source Token Detection**

Services check for authentication tokens in the following order:

1. **Authorization Header**: `Bearer {token}` (for API calls)
2. **URL Query Parameter**: `?token={token}` (for navigation links)
3. **HTTP Cookies**: `authToken={token}` (for page reloads)

#### **Authentication Filter Enhancement**

```java
// Enhanced AuthenticationFilter checks multiple sources
if (authHeader != null && authHeader.startsWith("Bearer ")) {
    token = authHeader.substring(7);
} else if ((token = request.getParameter("token")) != null) {
    // Set cookie for future requests
    Cookie authCookie = new Cookie("authToken", token);
    authCookie.setHttpOnly(true);
    response.addCookie(authCookie);
} else if (cookies != null) {
    // Check for existing auth cookie
    for (Cookie cookie : cookies) {
        if ("authToken".equals(cookie.getName())) {
            token = cookie.getValue();
            break;
        }
    }
}
```

#### **Client-Side Pre-Authentication**

- **Pre-auth Scripts**: Execute before page rendering to handle tokens immediately
- **URL Parameter Processing**: Extract tokens from navigation links and store locally
- **Seamless Navigation**: Token-based routing prevents authentication interruptions

#### **Benefits**

- ✅ **No Page Reload Redirects**: Users stay on the same page after refresh
- ✅ **Seamless Navigation**: Clicking between pages maintains authentication
- ✅ **Enhanced UX**: No authentication pop-ups or login interruptions
- ✅ **Cross-Browser Support**: Works with all modern browsers
- ✅ **Security Maintained**: HttpOnly cookies protect against XSS attacks

### Security Features

- **Password Encryption**: BCrypt hashing for secure storage
- **Strong Password Requirements**: Minimum 8 characters with uppercase, lowercase, number and special character
- **Token Expiration**: 24-hour token validity
- **Role-based Access**: Different permissions for user types
- **CORS Configuration**: Cross-origin request handling
- **Input Validation**: Comprehensive request validation

## 🐛 Troubleshooting

### Common Issues

#### Service Startup Problems

```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Verify MySQL connection
mysql -u root -p -e "SHOW DATABASES;"
```

#### Authentication Issues

- Verify Authentication Service is running on port 8083
- Check JWT token format: `Bearer <token>`
- Use debug endpoints for token validation

#### Database Connection Issues

- Ensure MySQL is running
- Verify database credentials
- Check database existence and permissions

### Debug Tools

- **Authentication Debug**: http://localhost:8081/debug
- **Browser Console**: Check for JavaScript errors
- **Application Logs**: Review service console output

## 📝 Development Guidelines

### Project Structure

```
Job-Application-Service/
├── Authentication/          # Port 8083
│   ├── src/main/java/com/service/authentication/
│   │   ├── controller/     # REST controllers
│   │   ├── entity/         # JPA entities
│   │   ├── repository/     # Data access
│   │   ├── service/        # Business logic
│   │   ├── config/         # Security config
│   │   └── util/           # JWT utilities
│   └── src/main/resources/
│       ├── templates/      # Thymeleaf views
│       └── application.properties
├── Job/                    # Port 8081
├── Application/            # Port 8082
└── README.md
```

### Code Standards

- **Java 21**: Modern Java features and syntax
- **RESTful APIs**: Consistent endpoint design
- **Error Handling**: Comprehensive exception management
- **Logging**: Structured logging with SLF4J
- **Testing**: Unit and integration tests

### API Design Principles

- **Consistent URLs**: RESTful resource naming
- **HTTP Methods**: Proper use of GET, POST, PUT, DELETE
- **Status Codes**: Appropriate HTTP response codes
- **Request/Response**: Standardized JSON formats
- **Documentation**: Comprehensive API documentation
