<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Administrator Dashboard - CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold text-navy mb-1"><i class="bi bi-speedometer2 text-teal me-2"></i>Campus Operations Dashboard</h2>
        <p class="text-muted small mb-0">System Administrator: <strong><c:out value="${sessionScope.userName}"/></strong></p>
    </div>
    <div class="d-flex gap-2">
        <a href="${pageContext.request.contextPath}/admin/all-complaints" class="btn btn-teal">
            <i class="bi bi-folder2-open me-1"></i> All Complaints
        </a>
        <a href="${pageContext.request.contextPath}/admin/manage-staff" class="btn btn-navy">
            <i class="bi bi-person-plus me-1"></i> Staff Management
        </a>
    </div>
</div>

<!-- Primary Metrics -->
<div class="row g-3 mb-4">
    <div class="col-sm-6 col-lg-2">
        <div class="stat-card">
            <div>
                <div class="stat-value"><c:out value="${counts.TOTAL != null ? counts.TOTAL : 0}"/></div>
                <div class="stat-label">Total</div>
            </div>
        </div>
    </div>
    <div class="col-sm-6 col-lg-2">
        <div class="stat-card stat-pending">
            <div>
                <div class="stat-value"><c:out value="${counts.PENDING != null ? counts.PENDING : 0}"/></div>
                <div class="stat-label">Pending</div>
            </div>
        </div>
    </div>
    <div class="col-sm-6 col-lg-2">
        <div class="stat-card stat-assigned">
            <div>
                <div class="stat-value"><c:out value="${counts.ASSIGNED != null ? counts.ASSIGNED : 0}"/></div>
                <div class="stat-label">Assigned</div>
            </div>
        </div>
    </div>
    <div class="col-sm-6 col-lg-2">
        <div class="stat-card stat-progress">
            <div>
                <div class="stat-value"><c:out value="${counts.IN_PROGRESS != null ? counts.IN_PROGRESS : 0}"/></div>
                <div class="stat-label">In Progress</div>
            </div>
        </div>
    </div>
    <div class="col-sm-6 col-lg-2">
        <div class="stat-card stat-resolved">
            <div>
                <div class="stat-value"><c:out value="${counts.RESOLVED != null ? counts.RESOLVED : 0}"/></div>
                <div class="stat-label">Resolved</div>
            </div>
        </div>
    </div>
    <div class="col-sm-6 col-lg-2">
        <div class="stat-card stat-rejected">
            <div>
                <div class="stat-value"><c:out value="${counts.REJECTED != null ? counts.REJECTED : 0}"/></div>
                <div class="stat-label">Rejected</div>
            </div>
        </div>
    </div>
</div>

<div class="row g-4 mb-4">
    <!-- Staff Workload & Resolution Statistics -->
    <div class="col-lg-8">
        <div class="card card-custom h-100">
            <div class="card-header-custom d-flex justify-content-between align-items-center">
                <span><i class="bi bi-bar-chart-fill me-1 text-teal"></i> Staff Workload &amp; Resolution Performance</span>
                <a href="${pageContext.request.contextPath}/admin/manage-staff" class="btn btn-sm btn-outline-secondary">
                    Manage Staff Directory
                </a>
            </div>
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${not empty staffWorkload}">
                        <div class="table-responsive">
                            <table class="table table-custom table-hover mb-0">
                                <thead>
                                    <tr>
                                        <th>Staff Name</th>
                                        <th>Department</th>
                                        <th>Assigned</th>
                                        <th>In Progress</th>
                                        <th>Resolved</th>
                                        <th>Total Handled</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="w" items="${staffWorkload}">
                                        <tr>
                                            <td>
                                                <span class="fw-semibold text-navy"><c:out value="${w.fullName}"/></span>
                                                <span class="d-block small text-muted"><c:out value="${w.staffId}"/></span>
                                            </td>
                                            <td><c:out value="${w.department}"/></td>
                                            <td><span class="badge bg-primary-subtle text-primary"><c:out value="${w.assignedCount}"/></span></td>
                                            <td><span class="badge bg-indigo-subtle text-indigo" style="color:#4338ca;"><c:out value="${w.inProgressCount}"/></span></td>
                                            <td><span class="badge bg-success-subtle text-success"><c:out value="${w.resolvedCount}"/></span></td>
                                            <td><strong><c:out value="${w.totalAssigned}"/></strong></td>
                                            <td>
                                                <span class="badge ${w.accountStatus == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">
                                                    <c:out value="${w.accountStatus}"/>
                                                </span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted p-4 mb-0 text-center">No staff members enrolled yet.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Campus Population Overview -->
    <div class="col-lg-4">
        <div class="card card-custom h-100">
            <div class="card-header-custom">
                <i class="bi bi-people-fill me-1 text-teal"></i> Campus Demographics
            </div>
            <div class="card-body d-flex flex-column justify-content-center">
                <div class="d-flex align-items-center justify-content-between p-3 bg-light rounded border mb-3">
                    <div class="d-flex align-items-center">
                        <i class="bi bi-mortarboard fs-2 text-teal me-3"></i>
                        <div>
                            <h6 class="mb-0 fw-bold text-navy">Registered Students</h6>
                            <small class="text-muted">Total student user base</small>
                        </div>
                    </div>
                    <span class="fs-4 fw-bold text-navy"><c:out value="${totalStudents}"/></span>
                </div>

                <div class="d-flex align-items-center justify-content-between p-3 bg-light rounded border mb-3">
                    <div class="d-flex align-items-center">
                        <i class="bi bi-person-workspace fs-2 text-primary me-3"></i>
                        <div>
                            <h6 class="mb-0 fw-bold text-navy">Staff Members</h6>
                            <small class="text-muted">Grievance handling agents</small>
                        </div>
                    </div>
                    <span class="fs-4 fw-bold text-navy"><c:out value="${totalStaff}"/></span>
                </div>

                <div class="mt-auto d-grid gap-2">
                    <a href="${pageContext.request.contextPath}/admin/manage-students" class="btn btn-outline-teal btn-sm">
                        <i class="bi bi-people me-1"></i> Review Student Accounts
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/manage-staff" class="btn btn-outline-navy btn-sm">
                        <i class="bi bi-person-plus me-1"></i> Add / Configure Staff Member
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Recent Complaints Feed -->
<div class="card card-custom">
    <div class="card-header-custom d-flex justify-content-between align-items-center">
        <span><i class="bi bi-clock-history me-1 text-teal"></i> Recent Campus Complaints</span>
        <a href="${pageContext.request.contextPath}/admin/all-complaints" class="btn btn-sm btn-outline-primary">
            View All Complaints <i class="bi bi-arrow-right ms-1"></i>
        </a>
    </div>
    <div class="card-body p-0">
        <c:choose>
            <c:when test="${not empty recentComplaints}">
                <div class="table-responsive">
                    <table class="table table-custom table-hover mb-0">
                        <thead>
                            <tr>
                                <th>Ticket #</th>
                                <th>Student</th>
                                <th>Category</th>
                                <th>Title</th>
                                <th>Priority</th>
                                <th>Status</th>
                                <th>Assigned Staff</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${recentComplaints}">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/complaint-details?id=${c.grievanceId}" class="fw-bold text-navy text-decoration-none">
                                            <c:out value="${c.complaintNumber}"/>
                                        </a>
                                    </td>
                                    <td>
                                        <span class="fw-semibold"><c:out value="${c.studentName}"/></span>
                                        <span class="d-block small text-muted"><c:out value="${c.studentId}"/></span>
                                    </td>
                                    <td><span class="badge bg-light text-dark border"><c:out value="${c.categoryName}"/></span></td>
                                    <td>
                                        <span class="badge-priority priority-${c.priority.toLowerCase()}">
                                            <c:out value="${c.priority}"/>
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge-status badge-${c.status.toLowerCase()}">
                                            <c:out value="${c.status}"/>
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty c.assignedStaffName}">
                                                <i class="bi bi-person-check text-primary me-1"></i><c:out value="${c.assignedStaffName}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-warning-subtle text-warning">Unassigned</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/assign-complaint?id=${c.grievanceId}" class="btn btn-sm btn-teal">
                                            <i class="bi bi-pencil-square me-1"></i> Review / Assign
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">
                        <i class="bi bi-check2-circle text-success"></i>
                    </div>
                    <h5 class="fw-bold text-navy">No Complaints Recorded</h5>
                    <p class="text-muted small">No grievances have been filed in the system yet.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
