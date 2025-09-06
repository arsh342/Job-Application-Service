# Job Portal Microservices

A comprehensive Job Portal application built with Spring Boot microservices architecture, featuring three independent services for authentication, job management, and application management with JWT-based security.

## Architecture Overview

This project follows a microservices architecture with three core services:

- **Authentication Service** (Port 8083): Centralized user authentication and authorization
- **Job Service** (Port 8081): Job posting management and employer operations
- **Application Service** (Port 8082): Job application management and applicant operations

### Service Communication

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Authentication │    │   Job Service   │    │ Application     │
│   Service       │    │   (Port 8081)   │    │   Service       │
│  (Port 8083)    │    │                 │    │  (Port 8082)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │◄──── Token Validation ─┤                       │
         │                       │                       │
         │◄──── Token Validation ─┼───────────────────────┤
         │                       │                       │
         │                       │◄──── Job Data ────────┤
         │                       │                       │
```

## Features

### Authentication Service (Port 8083)

- **Centralized Authentication**: Single sign-on for all services
- **User Registration**: Support for both Employers and Job Seekers
- **JWT Token Management**: Secure token generation and validation
- **User Profile Management**: Update personal information and preferences
- **Role-based Access Control**: Different permissions for Employers and Job Seekers
- **Password Security**: BCrypt encryption for secure password storage
- **Session Management**: Secure login/logout functionality

### Job Service (Port 8081) - Employer Operations

- **Job Management**: Complete CRUD operations for job postings
- **Advanced Search**: Filter jobs by title, location, company, salary range
- **Application Tracking**: View and manage applications for posted jobs
- **Job Status Control**: Open/Close job postings
- **Dashboard**: Modern UI for managing all job-related activities
- **Company Branding**: Associate jobs with company information
- **Salary Management**: Support for salary ranges and compensation details

### Application Service (Port 8082) - Job Seeker Operations

- **Job Application**: Apply to jobs with cover letters and additional notes
- **Application Tracking**: Monitor application status and progress
- **Browse Jobs**: Search and filter available job opportunities
- **Profile Management**: Maintain skills, education, and resume information
- **Application Statistics**: View comprehensive application analytics
- **Status Updates**: Real-time tracking of application progress
- **Withdraw Applications**: Remove applications when needed

## Technologies Used

- **Backend Framework**: Spring Boot 3.5.5
- **Security**: Spring Security with JWT authentication
- **Database**: MySQL 8.0+ with Spring Data JPA
- **ORM**: Hibernate for object-relational mapping
- **Inter-service Communication**: RESTful APIs with HTTP client
- **Frontend**: Modern HTML5, CSS3, JavaScript (ES6+)
- **UI Framework**: Custom CSS with gradient designs and responsive layouts
- **Authentication**: JWT (JSON Web Token) with JJWT 0.11.5
- **Build Tool**: Maven for dependency management and builds
- **API Documentation**: RESTful API design with comprehensive endpoints
- **Password Encryption**: BCrypt for secure password hashing

## Authentication & Security

This application implements a comprehensive JWT-based authentication system:

### Authentication Flow

1. **User Registration**: Users register through the Authentication Service
2. **Login Process**: Credentials are validated and JWT token is generated
3. **Token Distribution**: Token is shared across all services for authentication
4. **Service Access**: Each service validates tokens with the Authentication Service
5. **Session Management**: Tokens expire after 24 hours for security

### Security Features

- **JWT Token Authentication**: Stateless authentication across all services
- **Password Encryption**: BCrypt hashing with salt for password security
- **Role-based Access Control**: Different permissions for Employers and Job Seekers
- **Token Validation**: Real-time token verification with Authentication Service
- **CORS Support**: Configured cross-origin resource sharing for frontend
- **API Security**: Protected endpoints with Bearer token authentication
- **Session Security**: Secure token storage and management

### Token Structure

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 123,
  "email": "user@example.com",
  "name": "John Doe",
  "userType": "EMPLOYER" | "JOB_SEEKER",
  "externalUserId": 456,
  "companyName": "TechCorp" // For employers only
}
```

## Database Schema

### Authentication Service Database (`job_portal_auth_db`)

#### Users Table

```sql
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    user_type ENUM('EMPLOYER', 'JOB_SEEKER') NOT NULL,
    external_user_id BIGINT,
    company_name VARCHAR(255), -- For employers only
    created_date DATE DEFAULT CURRENT_DATE,
    updated_date DATE DEFAULT CURRENT_DATE
);
```

### Job Service Database (`job_portal_job_db`)

#### Jobs Table

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
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Application Service Database (`job_portal_application_db`)

#### Applications Table

```sql
CREATE TABLE applications (
    application_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_id BIGINT NOT NULL,
    applicant_id BIGINT NOT NULL,
    cover_letter TEXT NOT NULL,
    additional_notes TEXT,
    status ENUM('APPLIED', 'SHORTLISTED', 'REJECTED', 'HIRED') DEFAULT 'APPLIED',
    applied_date DATE DEFAULT CURRENT_DATE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_job_applicant (job_id, applicant_id)
);
```

## Setup Instructions

### Prerequisites

- **Java**: OpenJDK 21 or higher
- **Maven**: 3.6+ for dependency management
- **MySQL**: 8.0+ database server
- **Git**: For version control
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code (recommended)

### Database Setup

1. **Install and start MySQL server**
2. **Create databases** (auto-created if they don't exist):
   ```sql
   CREATE DATABASE job_portal_auth_db;
   CREATE DATABASE job_portal_job_db;
   CREATE DATABASE job_portal_application_db;
   ```
3. **Update database credentials** in each service's `application.properties`:
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.datasource.url=jdbc:mysql://localhost:3306/database_name
   ```

### Service Startup Sequence

**IMPORTANT**: Services must be started in the correct order for proper functionality.

#### Step 1: Start Authentication Service (Required First)

```bash
cd Authentication
mvn clean compile
mvn spring-boot:run
```

- Service will be available at: http://localhost:8083
- Verify startup: http://localhost:8083/health

#### Step 2: Start Job Service

```bash
cd Job
mvn clean compile
mvn spring-boot:run
```

- Service will be available at: http://localhost:8081
- Employer Dashboard: http://localhost:8081/dashboard

#### Step 3: Start Application Service

```bash
cd Application
mvn clean compile
mvn spring-boot:run
```

- Service will be available at: http://localhost:8082
- Job Seeker Dashboard: http://localhost:8082/dashboard

### Quick Start Guide

1. **Clone the repository**:

   ```bash
   git clone https://github.com/arsh342/Job-Application-Service.git
   cd Job-Application-Service
   ```

2. **Start all services** (in separate terminals):

   ```bash
   # Terminal 1 - Authentication Service
   cd Authentication && mvn spring-boot:run

   # Terminal 2 - Job Service
   cd Job && mvn spring-boot:run

   # Terminal 3 - Application Service
   cd Application && mvn spring-boot:run
   ```

3. **Access the application**:
   - Main Portal: http://localhost:8083
   - Register as Employer or Job Seeker
   - Start using the platform!

### Accessing the Application

- **Main Authentication Portal**: http://localhost:8083
- **Job Seeker Dashboard**: http://localhost:8082/dashboard
- **Employer Dashboard**: http://localhost:8081/dashboard
- **Browse Jobs**: http://localhost:8082/browse-jobs
- **My Applications**: http://localhost:8082/my-applications

## API Endpoints

### Authentication Service APIs (`http://localhost:8083`)

#### Public Endpoints

- `GET /` - Main landing page
- `GET /login` - Login page
- `GET /register` - Registration page
- `GET /health` - Service health check

#### Authentication APIs

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login (returns JWT token)
- `POST /api/auth/validate-token` - Validate JWT token
- `GET /api/auth/profile` - Get user profile (requires token)
- `PUT /api/auth/profile` - Update user profile (requires token)

**Registration Request:**

```json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "name": "John Doe",
  "userType": "EMPLOYER", // or "JOB_SEEKER"
  "companyName": "TechCorp" // Required for employers only
}
```

**Login Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 123,
  "email": "user@example.com",
  "name": "John Doe",
  "userType": "EMPLOYER",
  "externalUserId": 456,
  "companyName": "TechCorp"
}
```

### Job Service APIs (`http://localhost:8081`)

#### Public Endpoints

- `GET /` - Employer landing page
- `GET /dashboard` - Employer dashboard (requires authentication)
- `GET /profile` - Employer profile page
- `GET /job-details` - Job details page
- `GET /api/jobs/all` - Get all jobs (public access)

#### Job Management APIs (Protected)

- `POST /api/jobs` - Create new job (Employer only)
- `PUT /api/jobs/{jobId}` - Update job (Employer only)
- `DELETE /api/jobs/{jobId}` - Delete job (Employer only)
- `GET /api/jobs/{jobId}` - Get specific job details
- `GET /api/jobs/employer/{employerId}` - Get jobs by employer
- `GET /api/jobs/{jobId}/applications` - Get applications for job

**Create Job Request:**

```json
{
  "title": "Senior Software Engineer",
  "description": "We are looking for an experienced software engineer...",
  "location": "New York, NY",
  "company": "TechCorp",
  "salaryMin": 80000.0,
  "salaryMax": 120000.0
}
```

### Application Service APIs (`http://localhost:8082`)

#### Public Endpoints

- `GET /` - Job seeker landing page
- `GET /dashboard` - Job seeker dashboard (requires authentication)
- `GET /browse-jobs` - Browse available jobs
- `GET /my-applications` - View my applications
- `GET /profile` - Job seeker profile page

#### Application Management APIs (Protected)

- `POST /api/applications/apply` - Apply to a job
- `GET /api/applications/my-applications` - Get my applications
- `PUT /api/applications/{applicationId}` - Update application
- `DELETE /api/applications/{applicationId}` - Withdraw application
- `PUT /api/applications/{applicationId}/status` - Update application status (Employer only)

**Apply to Job Request:**

```json
{
  "jobId": 123,
  "coverLetter": "I am excited to apply for this position...",
  "additionalNotes": "I have 5 years of experience in Java development."
}
```

## Authentication Usage Examples

### 1. Register a new user:

```bash
curl -X POST http://localhost:8083/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "employer@techcorp.com",
    "password": "securePassword123",
    "name": "John Doe",
    "userType": "EMPLOYER",
    "companyName": "TechCorp"
  }'
```

### 2. Login to get JWT token:

```bash
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "employer@techcorp.com",
    "password": "securePassword123"
  }'
```

### 3. Use token for authenticated requests:

```bash
curl -X POST http://localhost:8081/api/jobs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Software Engineer",
    "description": "Java Developer position...",
    "location": "New York, NY",
    "company": "TechCorp",
    "salaryMin": 80000,
    "salaryMax": 120000
  }'
```

### 4. Apply to a job:

```bash
curl -X POST http://localhost:8082/api/applications/apply \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "jobId": 1,
    "coverLetter": "I am excited to apply for this position...",
    "additionalNotes": "I have relevant experience..."
  }'
```

## Data Models

### User (Authentication Service)

```java
{
    "userId": Long,
    "email": String,
    "password": String, // BCrypt encrypted
    "name": String,
    "userType": "EMPLOYER" | "JOB_SEEKER",
    "externalUserId": Long, // Reference to Job/Application service
    "companyName": String, // For employers only
    "createdDate": LocalDate,
    "updatedDate": LocalDate
}
```

### Job (Job Service)

```java
{
    "jobId": Long,
    "title": String,
    "description": String,
    "location": String,
    "company": String,
    "salaryMin": Double,
    "salaryMax": Double,
    "postedDate": LocalDate,
    "status": "OPEN" | "CLOSED",
    "employerId": Long,
    "createdDate": Timestamp,
    "updatedDate": Timestamp
}
```

### Application (Application Service)

```java
{
    "applicationId": Long,
    "jobId": Long,
    "applicantId": Long,
    "coverLetter": String,
    "additionalNotes": String,
    "status": "APPLIED" | "SHORTLISTED" | "REJECTED" | "HIRED",
    "appliedDate": LocalDate,
    "createdDate": Timestamp,
    "updatedDate": Timestamp
}
```

## Inter-Service Communication

The microservices communicate through well-defined REST APIs:

```
Authentication Service (8083)
    ↓ (Token Validation)
Job Service (8081) ←→ Application Service (8082)
    ↑                        ↑
    └── Job Data Exchange ───┘
```

### Communication Patterns:

1. **Authentication Flow**:

   - All services validate tokens with Authentication Service
   - Token contains user information and permissions

2. **Job Data Sharing**:

   - Application Service fetches job details from Job Service
   - Job Service provides public job listings

3. **Application Management**:
   - Application Service manages all job applications
   - Job Service can query applications for specific jobs

## Security Implementation

### Authentication Filter Chain

```java
@Configuration
public class SecurityConfig {
    // Custom authentication filter for JWT validation
    // CORS configuration for cross-origin requests
    // Public endpoints configuration
    // Protected routes with role-based access
}
```

### Token Validation Process

1. **Request Interception**: Authentication filter intercepts all requests
2. **Token Extraction**: Bearer token extracted from Authorization header
3. **Service Validation**: Token validated with Authentication Service
4. **User Context**: User information added to request context
5. **Route Access**: Access granted based on user role and permissions

## User Workflows

### 1. Employer Workflow (Complete Journey)

1. **Registration & Setup**:

   - Visit http://localhost:8083/register
   - Register as "EMPLOYER" with company details
   - Login to receive JWT token
   - Redirect to Job Service dashboard

2. **Job Management**:

   - Access dashboard at http://localhost:8081/dashboard
   - Create job postings with detailed information
   - Set salary ranges and job requirements
   - Manage job status (Open/Closed)

3. **Application Review**:
   - View applications for each job posting
   - Review candidate cover letters and notes
   - Update application status (Shortlisted/Rejected/Hired)
   - Track hiring progress

### 2. Job Seeker Workflow (Complete Journey)

1. **Registration & Profile**:

   - Visit http://localhost:8083/register
   - Register as "JOB_SEEKER" with personal details
   - Login to receive JWT token
   - Redirect to Application Service dashboard

2. **Job Discovery**:

   - Browse jobs at http://localhost:8082/browse-jobs
   - Use advanced filters (location, company, salary)
   - View detailed job descriptions
   - Research company information

3. **Application Process**:

   - Apply to jobs with personalized cover letters
   - Add additional notes and qualifications
   - Track application status and progress
   - Manage multiple applications

4. **Application Management**:
   - View all applications at http://localhost:8082/my-applications
   - Monitor application statistics
   - Withdraw applications when needed
   - Update profile and qualifications

## User Interface Features

### Modern Design System

- **Gradient Backgrounds**: Professional purple-blue gradient theme
- **Card-based Layout**: Clean, modern card designs for content
- **Responsive Design**: Mobile-friendly responsive layouts
- **Interactive Elements**: Hover effects and smooth transitions
- **Consistent Typography**: Professional font choices and hierarchy

### Dashboard Features

- **Real-time Statistics**: Application counts and success rates
- **Quick Actions**: Fast access to common operations
- **Search and Filters**: Advanced filtering capabilities
- **Status Indicators**: Visual status badges and progress indicators

## Troubleshooting

### Common Issues and Solutions

#### 1. Service Startup Issues

**Problem**: Service fails to start
**Solutions**:

- Check if MySQL is running
- Verify database credentials in `application.properties`
- Ensure ports 8081, 8082, 8083 are available
- Check Java version (requires Java 21+)

#### 2. Authentication Problems

**Problem**: "Authentication required" errors
**Solutions**:

- Verify Authentication Service is running on port 8083
- Check JWT token validity and expiration
- Ensure proper token format: `Bearer <token>`
- Verify user exists in Authentication Service

#### 3. Database Connection Issues

**Problem**: Cannot connect to database
**Solutions**:

- Start MySQL service
- Create required databases manually
- Check MySQL credentials and permissions
- Verify database URLs in configuration

#### 4. Cross-Service Communication

**Problem**: Services cannot communicate
**Solutions**:

- Ensure all services are running
- Check service URLs in configuration
- Verify network connectivity between services
- Check firewall settings

### Service Health Checks

```bash
# Check Authentication Service
curl http://localhost:8083/health

# Check Job Service
curl http://localhost:8081/health

# Check Application Service
curl http://localhost:8082/health
```

## Development Guidelines

### Project Structure

```
Job-Application-Service/
├── Authentication/          # Authentication Service (Port 8083)
│   ├── src/main/java/
│   │   └── com/service/auth/
│   │       ├── controller/   # REST API controllers
│   │       ├── model/       # JPA entities
│   │       ├── repository/  # Data access layer
│   │       ├── service/     # Business logic
│   │       └── config/      # Security & configuration
│   └── src/main/resources/
│       ├── templates/       # Thymeleaf HTML templates
│       └── application.properties
│
├── Job/                     # Job Service (Port 8081)
│   ├── src/main/java/
│   │   └── com/service/job/
│   │       ├── controller/   # Job management APIs
│   │       ├── model/       # Job entities
│   │       ├── repository/  # Job data access
│   │       ├── service/     # Job business logic
│   │       └── config/      # Authentication filter
│   └── src/main/resources/
│       ├── templates/       # Employer UI templates
│       └── application.properties
│
├── Application/             # Application Service (Port 8082)
│   ├── src/main/java/
│   │   └── com/service/application/
│   │       ├── controller/   # Application management APIs
│   │       ├── model/       # Application entities
│   │       ├── repository/  # Application data access
│   │       ├── service/     # Application business logic
│   │       └── config/      # Authentication filter
│   └── src/main/resources/
│       ├── templates/       # Job seeker UI templates
│       └── application.properties
│
└── README.md               # This comprehensive documentation
```

### Code Standards

- **Java 21**: Modern Java features and syntax
- **Spring Boot 3.5.5**: Latest stable Spring Boot version
- **RESTful APIs**: Follow REST principles for all endpoints
- **Exception Handling**: Comprehensive error handling and validation
- **Logging**: Structured logging for debugging and monitoring
- **Security**: JWT-based authentication with role-based access

### Database Design Principles

- **Normalization**: Properly normalized database schema
- **Indexes**: Optimized indexes for performance
- **Constraints**: Foreign key relationships and data integrity
- **Migrations**: Automatic schema creation with JPA/Hibernate

### API Design Guidelines

- **Consistent URLs**: RESTful URL patterns
- **HTTP Methods**: Proper use of GET, POST, PUT, DELETE
- **Status Codes**: Appropriate HTTP status codes
- **Request/Response**: Consistent JSON request/response formats
- **Authentication**: Bearer token authentication for protected endpoints

## Monitoring and Maintenance

### Application Monitoring

- **Health Endpoints**: Each service provides `/health` endpoint
- **Logging**: Comprehensive application logging
- **Error Tracking**: Structured error handling and reporting
- **Performance**: Monitor response times and database queries

### Security Monitoring

- **Token Management**: Monitor JWT token usage and expiration
- **Authentication Logs**: Track login attempts and failures
- **Access Control**: Monitor unauthorized access attempts
- **Password Security**: BCrypt encryption monitoring

### Deployment Considerations

- **Environment Variables**: Use environment-specific configuration
- **Database Connections**: Connection pooling and optimization
- **Service Discovery**: Consider service registry for production
- **Load Balancing**: Multiple instance deployment support
- **Container Support**: Docker-ready application structure

## Contributing

### Development Setup

1. **Fork the repository**
2. **Create feature branch**: `git checkout -b feature/new-feature`
3. **Make changes**: Follow coding standards and conventions
4. **Test thoroughly**: Ensure all services work correctly
5. **Submit pull request**: With detailed description of changes

### Code Review Guidelines

- **Functionality**: Does the code work as expected?
- **Security**: Are there any security vulnerabilities?
- **Performance**: Is the code optimized for performance?
- **Maintainability**: Is the code readable and well-documented?
- **Testing**: Are appropriate tests included?

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:

- **Issues**: Create GitHub issues for bugs and feature requests
- **Documentation**: Refer to this comprehensive README
- **Contact**: Reach out to the development team

---

**Version**: 1.0.0  
**Last Updated**: September 2025  
**Maintainers**: Development Team
