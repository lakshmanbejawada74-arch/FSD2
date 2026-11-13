<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Staff Dashboard - CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold text-navy mb-1"><i class="bi bi-person-workspace text-teal me-2"></i>Staff Dashboard</h2>
        <p class="text-muted small mb-0">Logged in as <strong><c:out value="${sessionScope.userName}"/></strong> &bull; Department: <strong><c:out value="${sessionScope.department}"/></strong></p>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/staff/assigned-complaints" class="btn btn-navy">
            <i class="bi bi-inbox-fill me-1"></i> View Assigned Complaints Queue
        </a>
    </div>
</div>

<!-- Staff Summary Metrics -->
<div class="row g-3 mb-4">
    <div class="col-sm-6 col-lg-3">
        <div class="stat-card">
            <div>
                <div class="stat-value"><c:out value="${counts.TOTAL != null ? counts.TOTAL : 0}"/></div>
                <div class="stat-label">Total Assigned</div>
            </div>
            <i class="bi bi-briefcase-fill text-secondary fs-1"></i>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="stat-card stat-assigned">
            <div>
                <div class="stat-value"><c:out value="${counts.ASSIGNED != null ? counts.ASSIGNED : 0}"/></div>
                <div class="stat-label">Awaiting Action</div>
            </div>
            <i class="bi bi-hourglass-split text-primary fs-1"></i>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="stat-card stat-progress">
            <div>
                <div class="stat-value"><c:out value="${counts.IN_PROGRESS != null ? counts.IN_PROGRESS : 0}"/></div>
                <div class="stat-label">In Progress</div>
            </div>
            <i class="bi bi-tools text-indigo fs-1" style="color: #6366f1;"></i>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="stat-card stat-resolved">
            <div>
                <div class="stat-value"><c:out value="${counts.RESOLVED != null ? counts.RESOLVED : 0}"/></div>
                <div class="stat-label">Resolved</div>
            </div>
            <i class="bi bi-check2-circle text-success fs-1"></i>
        </div>
    </div>
</div>

<!-- Active Task Queue -->
<div class="card card-custom">
    <div class="card-header-custom d-flex justify-content-between align-items-center">
        <span><i class="bi bi-list-task me-1 text-teal"></i> Active Assigned Tasks</span>
        <a href="${pageContext.request.contextPath}/staff/assigned-complaints" class="btn btn-sm btn-outline-primary">
            Full Work Queue <i class="bi bi-arrow-right ms-1"></i>
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
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${recentComplaints}">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/staff/complaint-details?id=${c.grievanceId}" class="fw-bold text-navy text-decoration-none">
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
                                        <a href="${pageContext.request.contextPath}/staff/complaint-details?id=${c.grievanceId}" class="btn btn-sm btn-teal">
                                            <i class="bi bi-wrench-adjustable me-1"></i> Manage
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
                        <i class="bi bi-check2-all text-success"></i>
                    </div>
                    <h5 class="fw-bold text-navy">All Caught Up!</h5>
                    <p class="text-muted small">You currently have no pending tasks assigned in your queue.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
