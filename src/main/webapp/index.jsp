<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Welcome to CampusCare - Student Complaint Management System" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="hero-section">
    <div class="container">
        <span class="badge bg-light text-primary px-3 py-2 rounded-pill fw-semibold mb-3">Campus Grievance Redressal Portal</span>
        <h1 class="hero-title">Transparent, Fast &amp; Accountable Redressal</h1>
        <p class="hero-subtitle">
            CampusCare empowers students to voice concerns, enables dedicated staff to resolve issues efficiently, and provides administrators complete operational visibility.
        </p>
        <div class="d-flex justify-content-center gap-3 flex-wrap">
            <a href="${pageContext.request.contextPath}/student-login" class="btn btn-teal btn-lg shadow-sm">
                <i class="bi bi-box-arrow-in-right me-2"></i> Student Sign In
            </a>
            <a href="${pageContext.request.contextPath}/student-register" class="btn btn-outline-light btn-lg">
                <i class="bi bi-person-plus me-2"></i> New Student Registration
            </a>
        </div>
    </div>
</div>

<div class="container my-5">
    <div class="text-center mb-5">
        <h2 class="fw-bold text-navy">Select Your Access Portal</h2>
        <p class="text-muted">Choose your role below to access your dedicated dashboard and tools.</p>
    </div>

    <div class="row g-4 justify-content-center">
        <!-- Student Card -->
        <div class="col-md-4">
            <div class="card card-custom h-100 p-4 text-center">
                <div class="mb-3">
                    <span class="d-inline-flex p-3 rounded-circle bg-teal-subtle text-teal fs-2">
                        <i class="bi bi-mortarboard-fill text-success"></i>
                    </span>
                </div>
                <h4 class="fw-bold text-navy mb-2">Student Portal</h4>
                <p class="text-muted small mb-4">
                    Register with student credentials, lodge academic or hostel complaints, track live progress with ticket GC numbers, and view resolution remarks.
                </p>
                <div class="mt-auto d-grid gap-2">
                    <a href="${pageContext.request.contextPath}/student-login" class="btn btn-navy">
                        <i class="bi bi-box-arrow-in-right me-1"></i> Student Login
                    </a>
                    <a href="${pageContext.request.contextPath}/student-register" class="btn btn-outline-secondary btn-sm">
                        Create Account
                    </a>
                </div>
            </div>
        </div>

        <!-- Staff Card -->
        <div class="col-md-4">
            <div class="card card-custom h-100 p-4 text-center">
                <div class="mb-3">
                    <span class="d-inline-flex p-3 rounded-circle bg-primary-subtle text-primary fs-2">
                        <i class="bi bi-person-workspace text-primary"></i>
                    </span>
                </div>
                <h4 class="fw-bold text-navy mb-2">Staff Portal</h4>
                <p class="text-muted small mb-4">
                    View complaints assigned exclusively to your department and profile, start work in progress, and resolve grievances with detailed resolution remarks.
                </p>
                <div class="mt-auto d-grid">
                    <a href="${pageContext.request.contextPath}/staff-login" class="btn btn-teal">
                        <i class="bi bi-box-arrow-in-right me-1"></i> Staff Login
                    </a>
                </div>
            </div>
        </div>

        <!-- Admin Card -->
        <div class="col-md-4">
            <div class="card card-custom h-100 p-4 text-center">
                <div class="mb-3">
                    <span class="d-inline-flex p-3 rounded-circle bg-warning-subtle text-warning fs-2">
                        <i class="bi bi-shield-lock-fill text-warning"></i>
                    </span>
                </div>
                <h4 class="fw-bold text-navy mb-2">Administration</h4>
                <p class="text-muted small mb-4">
                    Comprehensive campus dashboard, smart filtering, staff grievance assignments and reassignments, workload statistics, and student/staff management.
                </p>
                <div class="mt-auto d-grid">
                    <a href="${pageContext.request.contextPath}/admin-login" class="btn btn-navy">
                        <i class="bi bi-shield-check me-1"></i> Admin Login
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Workflow Section -->
    <div class="card card-custom mt-5 p-4 p-md-5">
        <h3 class="fw-bold text-navy text-center mb-4">How CampusCare Works</h3>
        <div class="row g-4 text-center">
            <div class="col-md-3">
                <div class="p-3">
                    <div class="badge rounded-circle bg-navy p-3 fs-4 mb-3 text-white">1</div>
                    <h5 class="fw-semibold">Lodge Complaint</h5>
                    <p class="text-muted small">Student specifies category, priority, and detailed grievance description.</p>
                </div>
            </div>
            <div class="col-md-3">
                <div class="p-3">
                    <div class="badge rounded-circle bg-primary p-3 fs-4 mb-3 text-white">2</div>
                    <h5 class="fw-semibold">Admin Assignment</h5>
                    <p class="text-muted small">Admin reviews pending complaints and assigns an active department staff member.</p>
                </div>
            </div>
            <div class="col-md-3">
                <div class="p-3">
                    <div class="badge rounded-circle bg-teal p-3 fs-4 mb-3 text-white">3</div>
                    <h5 class="fw-semibold">Action in Progress</h5>
                    <p class="text-muted small">Staff accepts task, sets status to IN_PROGRESS, and works on corrective action.</p>
                </div>
            </div>
            <div class="col-md-3">
                <div class="p-3">
                    <div class="badge rounded-circle bg-success p-3 fs-4 mb-3 text-white">4</div>
                    <h5 class="fw-semibold">Resolution &amp; Audit</h5>
                    <p class="text-muted small">Issue is marked RESOLVED with mandatory remarks, recorded permanently in audit logs.</p>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
