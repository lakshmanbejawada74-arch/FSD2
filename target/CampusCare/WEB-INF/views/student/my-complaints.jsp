<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="My Complaints - CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold text-navy mb-1"><i class="bi bi-card-checklist text-teal me-2"></i>My Complaints</h2>
        <p class="text-muted small mb-0">Track and view status history for all your submitted grievances</p>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/student/new-complaint" class="btn btn-teal">
            <i class="bi bi-plus-circle me-1"></i> New Complaint
        </a>
    </div>
</div>

<!-- Filters Card -->
<div class="card card-custom mb-4 p-3">
    <form action="${pageContext.request.contextPath}/student/my-complaints" method="GET" class="row g-2 align-items-center">
        <div class="col-md-5">
            <div class="input-group">
                <span class="input-group-text bg-light"><i class="bi bi-search"></i></span>
                <input type="text" class="form-control" name="search"
                       value="<c:out value='${searchQuery}'/>"
                       placeholder="Search by GC ticket ID, title, or keyword...">
            </div>
        </div>

        <div class="col-md-4">
            <select class="form-select" name="status">
                <option value="ALL" ${selectedStatus == 'ALL' ? 'selected' : ''}>-- All Statuses --</option>
                <option value="PENDING" ${selectedStatus == 'PENDING' ? 'selected' : ''}>PENDING</option>
                <option value="ASSIGNED" ${selectedStatus == 'ASSIGNED' ? 'selected' : ''}>ASSIGNED</option>
                <option value="IN_PROGRESS" ${selectedStatus == 'IN_PROGRESS' ? 'selected' : ''}>IN_PROGRESS</option>
                <option value="RESOLVED" ${selectedStatus == 'RESOLVED' ? 'selected' : ''}>RESOLVED</option>
                <option value="REJECTED" ${selectedStatus == 'REJECTED' ? 'selected' : ''}>REJECTED</option>
                <option value="WITHDRAWN" ${selectedStatus == 'WITHDRAWN' ? 'selected' : ''}>WITHDRAWN</option>
            </select>
        </div>

        <div class="col-md-3 d-flex gap-2">
            <button type="submit" class="btn btn-navy flex-fill">
                <i class="bi bi-funnel me-1"></i> Filter
            </button>
            <a href="${pageContext.request.contextPath}/student/my-complaints" class="btn btn-outline-secondary">
                Reset
            </a>
        </div>
    </form>
</div>

<!-- Complaints Table -->
<div class="card card-custom">
    <div class="card-body p-0">
        <c:choose>
            <c:when test="${not empty complaints}">
                <div class="table-responsive">
                    <table class="table table-custom table-hover mb-0">
                        <thead>
                            <tr>
                                <th>Ticket #</th>
                                <th>Category</th>
                                <th>Title</th>
                                <th>Priority</th>
                                <th>Status</th>
                                <th>Assigned Staff</th>
                                <th>Date Filed</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${complaints}">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/student/complaint-details?id=${c.grievanceId}" class="fw-bold text-navy text-decoration-none">
                                            <c:out value="${c.complaintNumber}"/>
                                        </a>
                                    </td>
                                    <td>
                                        <span class="badge bg-light text-dark border">
                                            <c:out value="${c.categoryName}"/>
                                        </span>
                                    </td>
                                    <td>
                                        <span class="fw-medium"><c:out value="${c.title}"/></span>
                                    </td>
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
                                                <i class="bi bi-person-check text-primary me-1"></i>
                                                <c:out value="${c.assignedStaffName}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted small fst-italic">Pending Assignment</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span class="text-muted small"><c:out value="${c.createdAt}"/></span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/student/complaint-details?id=${c.grievanceId}" class="btn btn-sm btn-outline-teal">
                                            View Details
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
                        <i class="bi bi-search"></i>
                    </div>
                    <h5 class="fw-bold text-navy">No Complaints Found</h5>
                    <p class="text-muted small">No complaints matched your filter criteria.</p>
                    <a href="${pageContext.request.contextPath}/student/new-complaint" class="btn btn-teal">
                        <i class="bi bi-plus-circle me-1"></i> File New Complaint
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
