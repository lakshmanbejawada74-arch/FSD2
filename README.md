# CampusCare — Student Complaint Management System

**CampusCare** is a production-grade, enterprise-ready web application designed for higher education institutions to manage, resolve, and audit student grievances with complete transparency and speed.

Built strictly using **Java Servlets (`javax.servlet.*`), JSP, JDBC, MySQL, Bootstrap 5, and Apache Tomcat 9**, it adheres to standard MVC architecture with zero framework bloat.

---

## 1. Technology Stack

- **Backend**: Java Servlets (`javax.servlet-api 4.0.1`), JSP (`javax.servlet.jsp-api 2.3.3`), JSTL 1.2
- **Database**: MySQL 8.0 with MySQL Connector/J (`com.mysql:mysql-connector-j:8.3.0`)
- **Server**: Apache Tomcat 9.0 (Default port on this machine: `8081`, context path: `/CampusCare`)
- **Security & Password Hashing**: BCrypt (`org.mindrot:jbcrypt:0.4`) with salted 12-round hashing
- **CSRF & Session Security**: Cryptographic Base64 token verification, session fixation prevention, and HTTP-only cookie configuration
- **Frontend / UI**: HTML5, CSS3, JavaScript, Bootstrap 5.3, Bootstrap Icons
- **Build System**: Apache Maven WAR project (Java 8/21 compatible)
- **IDE Support**: Eclipse IDE (Enterprise Java and Web Developer)

---

## 2. Default Seed Credentials for Quick Testing

The database comes pre-seeded with accounts ready for instant testing:

| Role | Username / Identifier | Password | Notes |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` (or ID: `ADM001`) | `Admin@123` | Full administrative control, assignment, & statistics |
| **Staff (Hostel)** | `STF002` (or `hostel_staff@campuscare.edu`) | `Staff@123` | Assigned to Hostel Affairs |
| **Staff (IT)** | `STF001` (or `it_staff@campuscare.edu`) | `Staff@123` | Assigned to IT & Networks |
| **Staff (Facilities)**| `STF003` (or `facilities_staff@campuscare.edu`) | `Staff@123` | Assigned to Facilities |
| **Staff (Sanitation)**| `STF004` (or `sanitation_staff@campuscare.edu`) | `Staff@123` | Assigned to Sanitation & Hygiene |
| **Student (Hostel)** | `STU1001` (or `student@campuscare.edu`) | `Student@123` | Alex Johnson (Aryabhata Hall, Rm B-204) |
| **Student (Day Scholar)** | `STU1002` (or `emily@campuscare.edu`) | `Student@123` | Emily Davis (Day Scholar) |

---

## 3. Database Configuration & Schema

### MySQL Connection Details
- **Host**: `localhost`
- **Port**: `3306`
- **Database**: `campuscare`
- **Username**: `root`
- **Password**: `1234`

Database settings are stored securely in `src/main/resources/db.properties`:
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/campuscare?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.username=root
db.password=1234
```

### Database Tables
1. `students`: Stores student profiles with `CK_STUDENT_HOSTEL` constraint (requires hostel name and room for `HOSTEL` students, strictly `NULL` for `DAY_SCHOLAR`).
2. `staff`: Department staff handling complaints.
3. `admins`: System administrators.
4. `complaint_categories`: Active complaint categories (Hostel, Sanitation, Classroom, Transport, Internet, Electricity, Other).
5. `grievances`: Primary complaint tickets with `GRIEVANCE_ID` starting at `1001` (displayed publicly as `GC1001`).
6. `grievance_updates`: Audit log recording every state transition, old status, new status, actor ID, actor role, remarks, and timestamp.

---

## 4. How to Set Up and Run

### Step 1: Initialize Database (If Setting up on a Fresh MySQL Instance)
Open a terminal / PowerShell and execute:
```powershell
mysql -u root -p1234 < "C:\Campuscare\database\campuscare_schema.sql"
mysql -u root -p1234 < "C:\Campuscare\database\campuscare_seed.sql"
```
*(On this system, the database and seed data are already populated and verified).*

### Step 2: Build the WAR File Using Maven
In the `C:\Campuscare` directory, run:
```powershell
mvn clean package
```
This produces `target\CampusCare.war` and compiles all classes with 100% test coverage.

### Step 3: Running via Apache Tomcat 9

#### Option A: Running with Standalone Tomcat
1. Copy `target\CampusCare.war` to your Tomcat `webapps` directory:
   ```powershell
   Copy-Item "C:\Campuscare\target\CampusCare.war" "C:\Program Files\Apache Software Foundation\Tomcat 9.0\webapps\CampusCare.war" -Force
   ```
2. Start Tomcat using `catalina.bat`:
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12.1"
   $env:CATALINA_HOME = "C:\Program Files\Apache Software Foundation\Tomcat 9.0"
   & "C:\Program Files\Apache Software Foundation\Tomcat 9.0\bin\catalina.bat" run
   ```
3. Open your browser and navigate to:
   ```
   http://localhost:8081/CampusCare/
   ```
   *(Note: If your Tomcat `server.xml` is configured for port 8080 or 8090, replace 8081 with your configured port).*

---

## 5. Importing into Eclipse IDE

1. **Launch Eclipse IDE** (Eclipse IDE for Enterprise Java and Web Developers).
2. Click **File** -> **Import...**
3. Select **Maven** -> **Existing Maven Projects** and click **Next**.
4. In **Root Directory**, browse to `C:\Campuscare` and click **Select Folder**.
5. Ensure `pom.xml` is checked and click **Finish**.
6. **Configure Tomcat Server in Eclipse**:
   - Go to the **Servers** tab at the bottom (or **Window** -> **Show View** -> **Servers**).
   - Right-click in the panel -> **New** -> **Server**.
   - Select **Apache** -> **Tomcat v9.0 Server** and click **Next**.
   - In **Tomcat installation directory**, browse to `C:\Program Files\Apache Software Foundation\Tomcat 9.0`.
   - Click **Finish**.
7. **Run the Project**:
   - Right-click the `CampusCare` project in Project Explorer.
   - Select **Run As** -> **Run on Server**.
   - Choose the Tomcat v9.0 Server and click **Finish**.

---

## 6. End-to-End Complaint Lifecycle Walkthrough

To verify the complete system, follow this step-by-step workflow:

### A. Student: Raise a Grievance
1. Navigate to `http://localhost:8081/CampusCare/`.
2. Click **Student Sign In** and log in with:
   - **ID**: `STU1001`
   - **Password**: `Student@123`
3. Click **Raise New Complaint**.
4. Select category (e.g. `HOSTEL`), priority `HIGH`, enter title and description (e.g. "Water cooler leak on 2nd floor").
5. Click **Submit Complaint**.
6. You will be redirected to the ticket details page displaying ticket number (e.g. `GC1001`) with status **PENDING**.
7. Click **Logout** at the top right.

### B. Admin: Review and Assign to Staff
1. Go to `http://localhost:8081/CampusCare/admin-login`.
2. Log in with:
   - **Username**: `admin`
   - **Password**: `Admin@123`
3. Notice the metric cards updated with the pending complaint.
4. Click **All Complaints** in the navigation bar.
5. Locate the newly created complaint and click **Manage**.
6. In the **Assign / Reassign Staff Member** dropdown, select an active staff member (e.g., `Mr. Wardell Hostel - HOSTEL (STF002)`).
7. Enter assignment instructions and click **Save Staff Assignment**.
8. The complaint status immediately updates to **ASSIGNED**, and an audit history log is recorded.
9. Click **Logout**.

### C. Staff: Work and Resolve Complaint
1. Go to `http://localhost:8081/CampusCare/staff-login`.
2. Log in with:
   - **Staff ID**: `STF002`
   - **Password**: `Staff@123`
3. Notice the assigned complaint appears in the **Active Assigned Tasks** queue.
4. Click **Manage** next to the ticket.
5. Click **Start Work (Mark IN_PROGRESS)**. Status changes to **IN_PROGRESS**.
6. Once the issue is resolved, enter mandatory **Resolution Remarks** (e.g. "Replaced shut-off valve; tested water pressure.") and click **Mark as RESOLVED**.
7. The status updates to **RESOLVED**, recording the exact timestamp and staff remarks in the audit trail.
8. Click **Logout**.

### D. Verification: Student & Admin Views
1. Log in as Student (`STU1001` / `Student@123`): The student sees status **RESOLVED** and reads the staff's resolution remarks in the live timeline.
2. Log in as Admin (`admin` / `Admin@123`): The staff member's workload metrics show +1 resolved complaint.

---

## 7. Role-Based Security & Validations

- **Authentication & Role Authorization**:
  - `AuthenticationFilter`: Prevents unauthenticated access to `/student/*`, `/staff/*`, and `/admin/*`.
  - `AuthorizationFilter`: Strict role boundary enforcement. Students cannot view staff/admin portals; staff cannot view admin portals or other staff members' assigned complaints.
- **CSRF Token Protection**: Every state-changing form (`POST`) contains a cryptographically secure token tied to the user's session.
- **Data Integrity Constraints**:
  - Password complexity: Minimum 8 characters with at least one uppercase letter, one lowercase letter, one number, and one special character.
  - Day Scholar students cannot have hostel values (enforced in code and via MySQL check constraint).
  - Resolution remarks are mandatory for status `RESOLVED` (max 500 chars).
  - Status transitions are strictly validated on the server to prevent illegal jumps (e.g., direct jump from PENDING to RESOLVED by a student).

---

## 8. Troubleshooting Guide

### 1. `CommunicationsException: Communications link failure`
- **Cause**: MySQL server is stopped or port 3306 is blocked.
- **Fix**: Open PowerShell and run `Get-Service -Name MySQL80` and `Start-Service -Name MySQL80`. Check `src/main/resources/db.properties` credentials.

### 2. `Address already in use: bind` (Tomcat Port Conflict)
- **Cause**: Another service is using Tomcat's HTTP port (8081 or 8080).
- **Fix**: Check `conf/server.xml` in Tomcat. Change `<Connector port="8081" ... />` to an unused port such as `8090`, then restart Tomcat.

### 3. `ClassNotFoundException: com.mysql.cj.jdbc.Driver`
- **Cause**: MySQL JDBC driver missing from runtime classpath.
- **Fix**: Ensure `mysql-connector-j` is in `pom.xml` (already configured). In Eclipse, right-click project -> **Maven** -> **Update Project...**

### 4. Direct JSP Access Attempt
- **Protection**: All view JSPs are placed under `WEB-INF/views/`, which is protected from direct browser access by the servlet container. Always access views through their registered Servlets (e.g. `/student/dashboard`, `/staff/dashboard`, `/admin/dashboard`).
