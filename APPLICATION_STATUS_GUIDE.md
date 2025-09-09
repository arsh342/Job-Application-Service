# Application Status Management - Job Portal System

## 🎯 Overview

The Job Application Service already has a **complete implementation** for managing and displaying application statuses. This document shows how employers can manage application statuses (Accept, Reject, Shortlist, Pending) and how job seekers can view their application status.

## 📊 Application Status Types

The system supports four distinct application statuses:

### 1. **APPLIED** (Pending)

- **Color:** Blue (#1976d2)
- **Description:** Initial status when a job seeker submits an application
- **From Employer Perspective:** Application received, awaiting review

### 2. **SHORTLISTED**

- **Color:** Orange (#f57c00)
- **Description:** Employer has reviewed and shortlisted the candidate for further consideration
- **From Employer Perspective:** Candidate moved to interview/evaluation stage

### 3. **HIRED** (Accepted)

- **Color:** Green (#388e3c)
- **Description:** Employer has decided to hire the candidate
- **From Employer Perspective:** Offer made/candidate selected

### 4. **REJECTED**

- **Color:** Red (#d32f2f)
- **Description:** Employer has rejected the application
- **From Employer Perspective:** Application declined

## 🔄 Status Workflow

```
APPLIED → SHORTLISTED → HIRED
   ↓           ↓
REJECTED ← REJECTED
```

- Applications start as **APPLIED**
- Employers can move them to **SHORTLISTED**, **HIRED**, or **REJECTED**
- From **SHORTLISTED**, applications can move to **HIRED** or **REJECTED**
- **HIRED** and **REJECTED** are final states

## 👤 Job Seeker Experience

### My Applications Page (`/my-applications`)

Job seekers can:

- **View all applications** with current status
- **See application statistics** (Total, Pending, Shortlisted, Hired, Rejected)
- **Filter by status** to find specific applications
- **Sort applications** by date or status
- **Withdraw applications** that are still in APPLIED status

**Status Display Features:**

- Color-coded status badges
- Application timeline showing when status changed
- Statistics dashboard showing application breakdown
- Real-time status updates

### Profile Page Enhancements (`/profile`)

The profile page now includes:

- **Application Statistics Overview** with visual cards
- **Status breakdown** showing counts for each status
- **Interactive dashboard** with status-specific styling

## 🏢 Employer Experience

### Job Listings & Application Management (`/job-listings`)

Employers can:

- **View all applications** for their job postings
- **Update application status** with one-click buttons
- **See application summary** with status distribution
- **Filter and sort** applications by status
- **Track hiring progress** across all jobs

**Status Management Features:**

- One-click status update buttons
- Contextual actions based on current status
- Application status history
- Bulk status updates (if needed)
- Real-time status synchronization

### Available Actions by Current Status:

#### For APPLIED applications:

- 📋 Shortlist
- ✅ Hire (Accept)
- ❌ Reject

#### For SHORTLISTED applications:

- ✅ Hire (Accept)
- ❌ Reject

#### For HIRED/REJECTED applications:

- No actions available (final states)

## 🔧 Technical Implementation

### Backend (Spring Boot)

#### Models:

```java
public enum ApplicationStatus {
    APPLIED, SHORTLISTED, REJECTED, HIRED
}
```

#### Key Endpoints:

```java
// For Job Seekers
GET /api/applications/my-applications
GET /api/applicants/{applicantId}/applications

// For Employers
PUT /api/applications/{applicationId}/status
GET /api/jobs/{jobId}/applications
```

#### Status Update Request:

```json
{
  "status": "SHORTLISTED" | "HIRED" | "REJECTED"
}
```

### Frontend (HTML/CSS/JavaScript)

#### Status Styling:

```css
.status-applied {
  background: #e3f2fd;
  color: #1976d2;
}
.status-shortlisted {
  background: #fff3e0;
  color: #f57c00;
}
.status-rejected {
  background: #ffebee;
  color: #d32f2f;
}
.status-hired {
  background: #e8f5e8;
  color: #388e3c;
}
```

#### Status Update Function:

```javascript
async function updateApplicationStatus(applicationId, newStatus) {
  const response = await fetch(`/api/applications/${applicationId}/status`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ status: newStatus }),
  });
}
```

## 🚀 How to Test the System

### 1. Setup Services

```bash
# Start all services
cd Authentication && mvn spring-boot:run  # Port 8083
cd Job && mvn spring-boot:run             # Port 8081
cd Application && mvn spring-boot:run     # Port 8082
```

### 2. Test Workflow

#### Step 1: Employer Setup

1. Go to `http://localhost:8083/register`
2. Register as **EMPLOYER**
3. Login and get redirected to Job Service
4. Create a job posting at `http://localhost:8081/create-job`

#### Step 2: Job Seeker Setup

1. Go to `http://localhost:8083/register`
2. Register as **JOB_SEEKER**
3. Login and get redirected to Application Service
4. Browse jobs at `http://localhost:8082/browse-jobs`
5. Apply to the job created by employer

#### Step 3: Status Management

1. **Login as employer** again
2. Go to `http://localhost:8081/job-listings`
3. **View applications** for your job posting
4. **Update status** using action buttons:
   - Click "📋 Shortlist" to move to SHORTLISTED
   - Click "✅ Hire" to move to HIRED
   - Click "❌ Reject" to move to REJECTED

#### Step 4: Verify Status Updates

1. **Login as job seeker**
2. Go to `http://localhost:8082/my-applications`
3. **See updated status** with new color coding
4. Check `http://localhost:8082/profile` for **status statistics**

### 3. Demo Page

Visit `http://localhost:8082/status-demo` for an interactive demonstration of the status system.

## 📱 User Interface Screenshots

### Job Seeker View:

- ✅ My Applications page with status badges
- ✅ Profile page with status statistics
- ✅ Color-coded status indicators
- ✅ Application filtering by status

### Employer View:

- ✅ Job listings with application management
- ✅ One-click status update buttons
- ✅ Application status summary
- ✅ Real-time status synchronization

## 🎨 Visual Design

The status system uses:

- **Consistent color coding** across all pages
- **Intuitive icons** for different actions
- **Responsive design** that works on all devices
- **Smooth animations** for status transitions
- **Clear typography** for easy readability

## ✅ Features Already Implemented

- [x] **Four status types** (Applied, Shortlisted, Hired, Rejected)
- [x] **Status update API** endpoints
- [x] **Employer status management** UI
- [x] **Job seeker status viewing** UI
- [x] **Color-coded status badges**
- [x] **Application statistics** dashboard
- [x] **Status filtering** and sorting
- [x] **Real-time updates** between services
- [x] **Responsive design** for all devices
- [x] **Status workflow** validation
- [x] **Database persistence** of status changes

## 🔒 Security & Validation

- **Authentication required** for all status operations
- **Authorization checks** ensure only job owners can update status
- **Status validation** prevents invalid transitions
- **Audit trail** for status changes
- **Error handling** for failed operations

## 🎯 Summary

The Job Application Service provides a **complete, production-ready system** for managing application statuses. Both employers and job seekers have intuitive interfaces to manage and track application progress, with real-time updates, comprehensive filtering, and detailed analytics.

The system is **already fully functional** and ready for immediate use with proper authentication, security, and a polished user experience.
