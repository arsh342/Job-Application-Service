# 🔧 Troubleshooting "Failed to load applications: 400"

## Quick Fixes to Try First

### 1. **Check Authentication Status**

Open your browser's Developer Console (F12) and visit:

```
http://localhost:8082/api/debug/auth
```

This will show you if authentication is working properly.

### 2. **Verify Services are Running**

Make sure all three services are running:

```bash
# Terminal 1 - Authentication Service
cd Authentication
mvn spring-boot:run
# Should show: Started AuthenticationApplication on port 8083

# Terminal 2 - Job Service
cd Job
mvn spring-boot:run
# Should show: Started JobApplication on port 8081

# Terminal 3 - Application Service
cd Application
mvn spring-boot:run
# Should show: Started Application on port 8082
```

### 3. **Check if You're Logged In**

- Go to `http://localhost:8083/login`
- Log in with your credentials
- You should be redirected to the Application Service

### 4. **Clear Browser Data**

- Clear localStorage: `localStorage.clear()` in console
- Clear cookies for localhost
- Refresh the page

## 🕵️ Detailed Debugging Steps

### Step 1: Check Authentication Token

1. Open Developer Console (F12)
2. Go to Application/Storage tab → Local Storage
3. Look for `authToken` key
4. If missing, you need to log in again

### Step 2: Test Authentication Endpoint

Visit this URL to check auth status:

```
http://localhost:8082/api/debug/auth
```

**Expected Response (if authenticated):**

```json
{
  "applicantId": 123,
  "userEmail": "user@example.com",
  "userName": "John Doe",
  "userType": "APPLICANT",
  "authHeader": "Bearer eyJ...",
  "timestamp": "2024-..."
}
```

**If you get 401 Unauthorized:**

- Your session expired → Go to login page
- No token found → Log in again

### Step 3: Test Direct API Call

Try calling the applications endpoint directly:

```
http://localhost:8082/api/applications/my-applications
```

**Expected Responses:**

- **200 OK**: Returns list of applications
- **401 Unauthorized**: Authentication failed
- **400 Bad Request**: User authenticated but request invalid

### Step 4: Check Server Logs

Look at the Application Service console output for error messages like:

```
ERROR: Authentication required
ERROR: Unable to validate token
ERROR: JobApplicationService error
```

## 🔍 Common Causes & Solutions

### Cause 1: Not Logged In

**Symptoms:** 401 error, no authToken in localStorage
**Solution:**

1. Go to `http://localhost:8083/login`
2. Log in with valid credentials
3. You'll be redirected to the Application Service

### Cause 2: Wrong User Type

**Symptoms:** 400 error, logged in as EMPLOYER instead of APPLICANT
**Solution:**

1. Register a new account as "JOB_SEEKER" / "APPLICANT"
2. Or log in with an existing applicant account

### Cause 3: Service Communication Error

**Symptoms:** 500 error, authentication works but app fails
**Solution:**

1. Restart all services
2. Check if ports 8081, 8082, 8083 are not occupied
3. Verify network connectivity between services

### Cause 4: Database Issues

**Symptoms:** 500 error, authentication works but database queries fail
**Solution:**

1. Check if MySQL/H2 database is running
2. Verify database connection in `application.properties`
3. Check if job_applications table exists

### Cause 5: CORS Issues

**Symptoms:** Network errors in browser console
**Solution:**
All services are configured with `@CrossOrigin(origins = "*")` so this should not be an issue.

## 🚀 Step-by-Step Test Workflow

### Complete Fresh Start:

1. **Stop all services** (Ctrl+C in all terminals)

2. **Restart services in order:**

   ```bash
   # Terminal 1
   cd Authentication && mvn spring-boot:run

   # Terminal 2 (wait for Terminal 1 to fully start)
   cd Job && mvn spring-boot:run

   # Terminal 3 (wait for Terminal 2 to fully start)
   cd Application && mvn spring-boot:run
   ```

3. **Register & Test:**

   ```bash
   # Step 1: Register as Job Seeker
   http://localhost:8083/register
   # Choose "JOB_SEEKER", fill form, submit

   # Step 2: Login
   http://localhost:8083/login
   # Enter credentials, should redirect to Application Service

   # Step 3: Test Applications Page
   http://localhost:8082/my-applications
   # Should show applications list (empty if no applications yet)
   ```

### Create Test Data:

1. **Register as Employer:**
   - Go to `http://localhost:8083/register`
   - Choose "EMPLOYER", add company name
2. **Create a Job:**
   - Login as employer → redirected to Job Service
   - Go to "Create Job" and post a job
3. **Apply to Job:**
   - Login as job seeker → redirected to Application Service
   - Go to "Browse Jobs" and apply to the job
4. **Check Applications:**
   - Go to "My Applications" → should show your application

## 🛠️ Advanced Debugging

### Check Database Directly

If using H2 database, access the H2 console:

```
http://localhost:8082/h2-console
```

Query: `SELECT * FROM job_applications;`

### Manual API Testing

Use curl or Postman to test the API directly:

```bash
# Get auth token first
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'

# Use token to call applications API
curl -X GET http://localhost:8082/api/applications/my-applications \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Check Service Health

```bash
curl http://localhost:8083/health  # Authentication Service
curl http://localhost:8081/health  # Job Service (if health endpoint exists)
curl http://localhost:8082/health  # Application Service (if health endpoint exists)
```

## 📞 Quick Support Commands

**Check what's in localStorage:**

```javascript
// Open browser console and run:
console.log("Auth Token:", localStorage.getItem("authToken"));
console.log("User Name:", localStorage.getItem("userName"));
console.log("User Type:", localStorage.getItem("userType"));
```

**Test authentication:**

```javascript
// In browser console:
fetch("/api/debug/auth", {
  headers: {
    Authorization: "Bearer " + localStorage.getItem("authToken"),
  },
})
  .then((r) => r.json())
  .then(console.log);
```

**Clear and restart:**

```javascript
// Clear everything and start over:
localStorage.clear();
sessionStorage.clear();
window.location.href = "http://localhost:8083/login";
```

The most likely cause of your 400 error is that you're either not properly authenticated or you're logged in as an EMPLOYER instead of an APPLICANT/JOB_SEEKER. Try the authentication debug endpoint first to see what's happening!
