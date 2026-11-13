<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="All Complaints - CampusCare Administration" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold text-navy mb-1"><i class="bi bi-folder2-open text-teal me-2"></i>Campus Complaints Management</h2>
        <p class="text-muted small mb-0">Search, filter, assign, and audit all student grievances across all campus departments</p>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary btn-sm">
            <i class="bi bi-arrow-left me-1"></i> Dashboard
        </a>
    </div>
</div>

<!-- Comprehensive Filter Panel -->
<div class="card card-custom mb-4 p-4">
    <h6 class="fw-bold text-navy mb-3"><i class="bi bi-funnel-fill text-teal me-1"></i> Search &amp; Filter Complaints</h6>
    <form action="${pageContext.request.contextPath}/admin/all-complaints" method="GET">
        <div class="row g-3">
            <div class="col-md-3">
                <label for="complaintNum" class="form-label small">Ticket # / Keyword</label>
                <input type="text" class="form-control form-control-sm" id="complaintNum" name="complaintNum"
                       value="<c:out value='${filterComplaintNum}'/>" placeholder="e.g. GC1001 or keyword">
            </div>

            <div class="col-md-3">
                <label for="student" class="form-label small">Student ID / Name / Email</label>
                <input type="text" class="form-control form-control-sm" id="student" name="student"
                       value="<c:out value='${filterStudent}'/>" placeholder="Search student...">
            </div>

            <div class="col-md-3">
                <label for="department" class="form-label small">Department</label>
                <select class="form-select form-select-sm" id="department" name="department">
                    <option value="ALL">-- All Departments --</option>
                    <option value="Computer Science and Engineering" ${filterDepartment == 'Computer Science and Engineering' ? 'selected' : ''}>Computer Science &amp; Eng.</option>
                    <option value="Information Technology" ${filterDepartment == 'Information Technology' ? 'selected' : ''}>Information Technology</option>
                    <option value="Electronics and Communication" ${filterDepartment == 'Electronics and Communication' ? 'selected' : ''}>Electronics &amp; Comm.</option>
                    <option value="Electrical Engineering" ${filterDepartment == 'Electrical Engineering' ? 'selected' : ''}>Electrical Engineering</option>
                    <option value="Mechanical Engineering" ${filterDepartment == 'Mechanical Engineering' ? 'selected' : ''}>Mechanical Engineering</option>
                    <option value="Civil Engineering" ${filterDepartment == 'Civil Engineering' ? 'selected' : ''}>Civil Engineering</option>
                    <option value="HOSTEL" ${filterDepartment == 'HOSTEL' ? 'selected' : ''}>Hostel Affairs</option>
                    <option value="FACILITIES" ${filterDepartment == 'FACILITIES' ? 'selected' : ''}>Campus Facilities</option>
                    <option value="SANITATION" ${filterDepartment == 'SANITATION' ? 'selected' : ''}>Sanitation &amp; Hygiene</option>
                    <option value="TRANSPORT" ${filterDepartment == 'TRANSPORT' ? 'selected' : ''}>Transport</option>
                </select>
            </div>

            <div class="col-md-3">
                <label for="categoryId" class="form-label small">Category</label>
                <select class="form-select form-select-sm" id="categoryId" name="categoryId">
                    <option value="ALL">-- All Categories --</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.categoryId}" ${filterCategoryId == cat.categoryId ? 'selected' : ''}>
                            <c:out value="${cat.categoryName}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-md-2">
                <label for="status" class="form-label small">Status</label>
                <select class="form-select form-select-sm" id="status" name="status">
                    <option value="ALL">-- All Statuses --</option>
                    <option value="PENDING" ${filterStatus == 'PENDING' ? 'selected' : ''}>PENDING</option>
                    <option value="ASSIGNED" ${filterStatus == 'ASSIGNED' ? 'selected' : ''}>ASSIGNED</option>
                    <option value="IN_PROGRESS" ${filterStatus == 'IN_PROGRESS' ? 'selected' : ''}>IN_PROGRESS</option>
                    <option value="RESOLVED" ${filterStatus == 'RESOLVED' ? 'selected' : ''}>RESOLVED</option>
                    <option value="REJECTED" ${filterStatus == 'REJECTED' ? 'selected' : ''}>REJECTED</option>
                    <option value="WITHDRAWN" ${filterStatus == 'WITHDRAWN' ? 'selected' : ''}>WITHDRAWN</option>
                </select>
            </div>

            <div class="col-md-2">
                <label for="priority" class="form-label small">Priority</label>
                <select class="form-select form-select-sm" id="priority" name="priority">
                    <option value="ALL">-- All Priorities --</option>
                    <option value="LOW" ${filterPriority == 'LOW' ? 'selected' : ''}>LOW</option>
                    <option value="MEDIUM" ${filterPriority == 'MEDIUM' ? 'selected' : ''}>MEDIUM</option>
                    <option value="HIGH" ${filterPriority == 'HIGH' ? 'selected' : ''}>HIGH</option>
                </select>
            </div>

            <div class="col-md-3">
                <label for="staffId" class="form-label small">Assigned Staff</label>
                <select class="form-select form-select-sm" id="staffId" name="staffId">
                    <option value="ALL">-- All Staff --</option>
                    <option value="UNASSIGNED" ${filterStaffId == 'UNASSIGNED' ? 'selected' : ''}>[ Unassigned Only ]</option>
                    <c:forEach var="st" items="${staffList}">
                        <option value="${st.staffId}" ${filterStaffId == st.staffId ? 'selected' : ''}>
                            <c:out value="${st.fullName}"/> (<c:out value="${st.staffId}"/>)
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-md-2">
                <label for="dateFrom" class="form-label small">Filed From</label>
                <input type="date" class="form-control form-control-sm" id="dateFrom" name="dateFrom"
                       value="<c:out value='${filterDateFrom}'/>">
            </div>

            <div class="col-md-2">
                <label for="dateTo" class="form-label small">Filed To</label>
                <input type="date" class="form-control form-control-sm" id="dateTo" name="dateTo"
                       value="<c:out value='${filterDateTo}'/>">
            </div>
        </div>

        <div class="d-flex justify-content-end gap-2 mt-3 pt-2 border-top">
            <a href="${pageContext.request.contextPath}/admin/all-complaints" class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-arrow-counterclockwise me-1"></i> Clear Filters
            </a>
            <button type="submit" class="btn btn-navy btn-sm px-4">
                <i class="bi bi-funnel me-1"></i> Apply Filter
            </button>
        </div>
    </form>
</div>

<!-- Complaints Results Table -->
<div class="card card-custom">
    <div class="card-header-custom d-flex justify-content-between align-items-center">
        <span><i class="bi bi-table me-1 text-teal"></i> Complaints List (<c:out value="${complaints.size()}"/> results)</span>
    </div>
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
                                <th>Assigned Staff</th>
                                <th>Filed Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${complaints}">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/assign-complaint?id=${c.grievanceId}" class="fw-bold text-navy text-decoration-none">
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
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty c.assignedStaffName}">
                                                <i class="bi bi-person-check text-primary me-1"></i>
                                                <c:out value="${c.assignedStaffName}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-warning text-dark">Unassigned</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><span class="text-muted small"><c:out value="${c.createdAt}"/></span></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/assign-complaint?id=${c.grievanceId}" class="btn btn-sm btn-teal">
                                            <i class="bi bi-sliders me-1"></i> Manage
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
                    <p class="text-muted small">Try modifying or resetting the search filters above.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
