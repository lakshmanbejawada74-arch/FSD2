<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Student Dashboard - CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold text-navy mb-1">Student Dashboard</h2>
        <p class="text-muted small mb-0">Welcome back, <strong><c:out value="${sessionScope.userName}"/></strong> (<c:out value="${sessionScope.userId}"/>)</p>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/student/new-complaint" class="btn btn-teal btn-lg shadow-sm">
            <i class="bi bi-plus-circle-fill me-1"></i> Raise New Complaint
        </a>
    </div>
</div>

<!-- Summary Cards -->
<div class="row g-3 mb-4">
    <div class="col-sm-6 col-lg-3">
        <div class="stat-card">
            <div>
                <div class="stat-value"><c:out value="${counts.TOTAL != null ? counts.TOTAL : 0}"/></div>
                <div class="stat-label">Total Filed</div>
            </div>
            <i class="bi bi-files text-secondary fs-1"></i>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="stat-card stat-pending">
            <div>
                <div class="stat-value"><c:out value="${counts.PENDING != null ? counts.PENDING : 0}"/></div>
                <div class="stat-label">Pending Review</div>
            </div>
            <i class="bi bi-clock-history text-warning fs-1"></i>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="stat-card stat-progress">
            <div>
                <div class="stat-value"><c:out value="${(counts.ASSIGNED != null ? counts.ASSIGNED : 0) + (counts.IN_PROGRESS != null ? counts.IN_PROGRESS : 0)}"/></div>
                <div class="stat-label">In Progress</div>
            </div>
            <i class="bi bi-gear-wide-connected text-primary fs-1"></i>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="stat-card stat-resolved">
            <div>
                <div class="stat-value"><c:out value="${counts.RESOLVED != null ? counts.RESOLVED : 0}"/></div>
                <div class="stat-label">Resolved</div>
            </div>
            <i class="bi bi-check-circle-fill text-success fs-1"></i>
        </div>
    </div>
</div>

<!-- Recent Complaints Section -->
<div class="card card-custom">
    <div class="card-header-custom d-flex justify-content-between align-items-center">
        <span><i class="bi bi-clock-history me-1 text-teal"></i> Recent Complaints</span>
        <a href="${pageContext.request.contextPath}/student/my-complaints" class="btn btn-sm btn-outline-primary">
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
                                <th>Complaint No.</th>
                                <th>Category</th>
                                <th>Title</th>
                                <th>Priority</th>
                                <th>Status</th>
                                <th>Submitted On</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${recentComplaints}">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/student/complaint-details?id=${c.grievanceId}" class="fw-bold text-decoration-none text-navy">
                                            <c:out value="${c.complaintNumber}"/>
                                        </a>
                                    </td>
                                    <td>
                                        <span class="badge bg-light text-dark border">
                                            <c:out value="${c.categoryName}"/>
                                        </span>
                                    </td>
                                    <td><c:out value="${c.title}"/></td>
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
                                        <span class="text-muted small">
                                            <c:out value="${c.createdAt}"/>
                                        </span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/student/complaint-details?id=${c.grievanceId}" class="btn btn-sm btn-outline-teal">
                                            Track <i class="bi bi-chevron-right"></i>
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
                        <i class="bi bi-inbox"></i>
                    </div>
                    <h5 class="fw-bold text-navy">No Complaints Found</h5>
                    <p class="text-muted small">You haven't submitted any complaints yet. Need assistance? Click below.</p>
                    <a href="${pageContext.request.contextPath}/student/new-complaint" class="btn btn-teal">
                        <i class="bi bi-plus-circle me-1"></i> Lodge Your First Complaint
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
