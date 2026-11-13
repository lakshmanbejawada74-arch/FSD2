<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="My Assigned Complaints - CampusCare Staff" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold text-navy mb-1"><i class="bi bi-inbox-fill text-teal me-2"></i>My Assigned Complaints Queue</h2>
        <p class="text-muted small mb-0">Complaints assigned to staff account: <strong><c:out value="${sessionScope.userName}"/></strong> (<c:out value="${sessionScope.userId}"/>)</p>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/staff/dashboard" class="btn btn-outline-secondary btn-sm">
            <i class="bi bi-arrow-left me-1"></i> Dashboard
        </a>
    </div>
</div>

<!-- Search & Filters -->
<div class="card card-custom mb-4 p-3">
    <form action="${pageContext.request.contextPath}/staff/assigned-complaints" method="GET" class="row g-2 align-items-center">
        <div class="col-md-5">
            <div class="input-group">
                <span class="input-group-text bg-light"><i class="bi bi-search"></i></span>
                <input type="text" class="form-control" name="search"
                       value="<c:out value='${searchQuery}'/>"
                       placeholder="Search by ticket GC number, student name, or keyword...">
            </div>
        </div>

        <div class="col-md-4">
            <select class="form-select" name="status">
                <option value="ALL" ${selectedStatus == 'ALL' ? 'selected' : ''}>-- All Statuses --</option>
                <option value="ASSIGNED" ${selectedStatus == 'ASSIGNED' ? 'selected' : ''}>ASSIGNED (Awaiting Action)</option>
                <option value="IN_PROGRESS" ${selectedStatus == 'IN_PROGRESS' ? 'selected' : ''}>IN_PROGRESS (Currently Working)</option>
                <option value="RESOLVED" ${selectedStatus == 'RESOLVED' ? 'selected' : ''}>RESOLVED</option>
            </select>
        </div>

        <div class="col-md-3 d-flex gap-2">
            <button type="submit" class="btn btn-navy flex-fill">
                <i class="bi bi-funnel me-1"></i> Filter
            </button>
            <a href="${pageContext.request.contextPath}/staff/assigned-complaints" class="btn btn-outline-secondary">
                Reset
            </a>
        </div>
    </form>
</div>

<!-- Table -->
<div class="card card-custom">
    <div class="card-body p-0">
        <c:choose>
            <c:when test="${not empty complaints}">
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
                                <th>Last Updated</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${complaints}">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/staff/complaint-details?id=${c.grievanceId}" class="fw-bold text-navy text-decoration-none">
                                            <c:out value="${c.complaintNumber}"/>
                                        </a>
                                    </td>
                                    <td>
                                        <span class="fw-semibold"><c:out value="${c.studentName}"/></span>
                                        <span class="d-block small text-muted"><c:out value="${c.studentId}"/> &bull; <c:out value="${c.studentDepartment}"/></span>
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
                                    <td><span class="text-muted small"><c:out value="${c.updatedAt}"/></span></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/staff/complaint-details?id=${c.grievanceId}" class="btn btn-sm btn-teal">
                                            <i class="bi bi-gear-fill me-1"></i> Take Action
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
                    <h5 class="fw-bold text-navy">No Assigned Complaints</h5>
                    <p class="text-muted small">No complaints found matching your search and filter criteria.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
