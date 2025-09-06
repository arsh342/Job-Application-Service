# Job Portal Microservices

A comprehensive Job Portal application built with Spring Boot microservices architecture, featuring separate services for job management and application management with JWT-based authentication.

## Architecture

- **Job Service** (Port 8081): Manages job postings and employer operations
- **Application Service** (Port 8082): Manages job applications and applicant operations

## Features

### Job Service (Employers)

- Employer registration and JWT-based authentication
- Job CRUD operations (Create, Read, Update, Delete)
- Job search with filters (title, location, company, salary range)
- View applications for posted jobs
- JWT token-based security

### Application Service (Applicants)

- Applicant registration and JWT-based authentication
- Apply to jobs
- View and manage applications
- Application status tracking (Applied, Shortlisted, Rejected, Hired)
- Withdraw applications

## Technologies Used

- **Backend**: Spring Boot 3.5.5, Spring Security, Spring Data JPA, Hibernate
- **Database**: MySQL
- **Inter-service Communication**: OpenFeign
- **Frontend**: HTML, Bootstrap 5, JavaScript
- **Security**: JWT (JSON Web Token) authentication
- **JWT Library**: JJWT 0.11.5
- **Build Tool**: Maven

## Authentication

This application uses JWT (JSON Web Token) for stateless authentication:

- **Token Generation**: Upon successful login, a JWT token is generated and returned
- **Token Validation**: All protected endpoints require a valid JWT token in the Authorization header
- **Token Format**: `Authorization: Bearer <jwt-token>`
- **Token Expiration**: 24 hours (configurable)
- **Claims**: Contains user ID (employerId/applicantId) and username

## Database Schema

### Job Service Database (`job_portal_job_db`)

- `employers` table
- `jobs` table

### Application Service Database (`job_portal_application_db`)

- `applicants` table
- `job_applications` table
- `applicant_skills` table
- `applicant_educations` table

## Setup Instructions

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- MySQL 8.0+

### Database Setup

1. Install and start MySQL
2. Create databases (they will be created automatically if they don't exist):
   ```sql
   CREATE DATABASE job_portal_job_db;
   CREATE DATABASE job_portal_application_db;
   ```
3. Update database credentials in both services' `application.properties` if needed

### Running the Services

1. **Start Job Service:**

   ```bash
   cd Job
   mvn spring-boot:run
   ```

   Service will be available at: http://localhost:8081

2. **Start Application Service:**
   ```bash
   cd Application
   mvn spring-boot:run
   ```
   Service will be available at: http://localhost:8082

### Accessing the Application

- **Employer Dashboard**: http://localhost:8081
- **Job Seeker Dashboard**: http://localhost:8082

## API Endpoints

### Job Service APIs (`http://localhost:8081`)

#### Authentication (JWT-based)

- `POST /api/auth/employer/register` - Register employer
- `POST /api/auth/employer/login` - Login employer (returns JWT token)
- `POST /api/auth/logout` - Logout (client-side token removal)
- `GET /api/auth/profile` - Get profile (requires JWT token)

**Login Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "employerId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "companyName": "TechCorp"
}
```

#### Jobs (Protected - Requires JWT Token)

- `POST /api/jobs` - Create job (Employer only)
- `PUT /api/jobs/{jobId}` - Update job (Employer only)
- `DELETE /api/jobs/{jobId}` - Delete job (Employer only)
- `GET /api/jobs` - Get all jobs with filters (Public)
- `GET /api/jobs/{jobId}` - Get job by ID (Public)
- `GET /api/employers/{employerId}/jobs` - Get jobs by employer
- `GET /api/jobs/{jobId}/applications` - Get applications for job (Employer only)

### Application Service APIs (`http://localhost:8082`)

#### Authentication (JWT-based)

- `POST /api/auth/applicant/register` - Register applicant
- `POST /api/auth/applicant/login` - Login applicant (returns JWT token)
- `POST /api/auth/logout` - Logout (client-side token removal)
- `GET /api/auth/profile` - Get profile (requires JWT token)

**Login Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "applicantId": 1,
  "name": "Jane Smith",
  "email": "jane@example.com"
}
```

#### Applications (Protected - Requires JWT Token)

- `POST /api/applications` - Apply to job (Applicant only)
- `PUT /api/applications/{applicationId}` - Update application (Applicant only)
- `DELETE /api/applications/{applicationId}` - Withdraw application (Applicant only)
- `GET /api/applications/{applicationId}` - Get application details
- `GET /api/jobs/{jobId}/applications` - Get applications for job
- `GET /api/applicants/{applicantId}/applications` - Get applications by applicant
- `PUT /api/applications/{applicationId}/status` - Update application status (Employer only)

## JWT Authentication Usage

### 1. Login to get JWT token:

```bash
curl -X POST http://localhost:8081/api/auth/employer/login \
  -H "Content-Type: application/json" \
  -d '{"email": "john@example.com", "password": "password123"}'
```

### 2. Use token in subsequent requests:

```bash
curl -X POST http://localhost:8081/api/jobs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"title": "Software Engineer", "description": "Java Developer", "location": "NYC", "salary": 80000}'
```

### 3. Token expires in 24 hours (configurable)

## Data Models

### Employer

```java
{
    "employerId": Long,
    "name": String,
    "email": String,
    "companyName": String,
    "password": String (encrypted),
    "role": "EMPLOYER",
    "createdDate": LocalDate
}
```

### Job

```java
{
    "jobId": Long,
    "title": String,
    "description": String,
    "location": String,
    "salary": Double,
    "postedDate": LocalDate,
    "status": "OPEN" | "CLOSED",
    "employerId": Long
}
```

### Applicant

```java
{
    "applicantId": Long,
    "name": String,
    "email": String,
    "resumeUrl": String,
    "skills": List<String>,
    "password": String (encrypted),
    "role": "APPLICANT",
    "createdDate": LocalDate
}
```

### Application

```java
{
    "applicationId": Long,
    "jobId": Long,
    "applicantId": Long,
    "status": "APPLIED" | "SHORTLISTED" | "REJECTED" | "HIRED",
    "appliedDate": LocalDate
}
```

## Inter-Service Communication

The services communicate via REST APIs using OpenFeign:

- **Job Service** calls **Application Service** to fetch applications for jobs
- **Application Service** calls **Job Service** to verify job existence and status

## Security Features

- Password encryption using BCrypt
- Session-based authentication
- Role-based access control
- CSRF protection disabled for API endpoints
- Cross-origin requests enabled

## Usage Flow

1. **Employer Workflow:**

   - Register/Login at http://localhost:8081
   - Create job postings
   - View and manage posted jobs
   - Review applications for jobs

2. **Job Seeker Workflow:**
   - Register/Login at http://localhost:8082
   - Browse available jobs
   - Apply to interesting positions
   - Track application status
   - Withdraw applications if needed

## Development Notes

- Both services use session-based authentication
- Database tables are auto-created using JPA
- Services are designed to be independently deployable
- Comprehensive error handling and validation
- Responsive web interface using Bootstrap
