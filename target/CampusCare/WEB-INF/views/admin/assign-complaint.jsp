<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Assign / Manage Complaint - ${complaint.complaintNumber}" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <div class="d-flex align-items-center gap-2 mb-1">
            <h2 class="fw-bold text-navy mb-0"><c:out value="${complaint.complaintNumber}"/></h2>
            <span class="badge-status badge-${complaint.status.toLowerCase()} fs-6">
                <c:out value="${complaint.status}"/>
            </span>
            <span class="badge-priority priority-${complaint.priority.toLowerCase()}">
                <c:out value="${complaint.priority}"/> PRIORITY
            </span>
        </div>
        <p class="text-muted small mb-0">Filed by <strong><c:out value="${complaint.studentName}"/></strong> on <c:out value="${complaint.createdAt}"/></p>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/admin/all-complaints" class="btn btn-outline-secondary btn-sm">
            <i class="bi bi-arrow-left me-1"></i> Back to All Complaints
        </a>
    </div>
</div>

<div class="row g-4">
    <!-- Complaint Details & Assignment Controls -->
    <div class="col-lg-7">
        <!-- Details Card -->
        <div class="card card-custom mb-4">
            <div class="card-header-custom d-flex justify-content-between align-items-center">
                <span><i class="bi bi-file-text me-1 text-teal"></i> Grievance Summary</span>
                <span class="badge bg-light text-dark border"><c:out value="${complaint.categoryName}"/></span>
            </div>
            <div class="card-body">
                <h5 class="fw-bold text-navy mb-3"><c:out value="${complaint.title}"/></h5>
                <div class="p-3 bg-light rounded border mb-4">
                    <p class="mb-0 text-secondary" style="white-space: pre-wrap;"><c:out value="${complaint.description}"/></p>
                </div>

                <div class="row g-3 small">
                    <div class="col-sm-6">
                        <span class="text-muted d-block">Student Info</span>
                        <strong><c:out value="${complaint.studentName}"/></strong> (<c:out value="${complaint.studentId}"/>)
                        <span class="d-block text-muted"><c:out value="${complaint.studentEmail}"/> | <c:out value="${complaint.studentMobile}"/></span>
                    </div>
                    <div class="col-sm-6">
                        <span class="text-muted d-block">Department &amp; Accommodation</span>
                        <strong><c:out value="${complaint.studentDepartment}"/></strong> &bull; <c:out value="${complaint.studentType}"/>
                        <c:if test="${complaint.studentType == 'HOSTEL'}">
                            <span class="d-block text-muted"><c:out value="${complaint.hostelName}"/> - Room <c:out value="${complaint.roomNumber}"/></span>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>

        <!-- Staff Assignment / Reassignment Card -->
        <div class="card card-custom mb-4">
            <div class="card-header-custom">
                <i class="bi bi-person-check-fill me-1 text-teal"></i> Assign / Reassign Staff Member
            </div>
            <div class="card-body">
                <div class="alert alert-light border small mb-3">
                    <span class="text-muted">Currently Assigned: </span>
                    <c:choose>
                        <c:when test="${not empty complaint.assignedStaffName}">
                            <strong class="text-navy"><c:out value="${complaint.assignedStaffName}"/></strong>
                            (<c:out value="${complaint.assignedStaffId}"/> &bull; <c:out value="${complaint.assignedStaffEmail}"/>)
                        </c:when>
                        <c:otherwise>
                            <span class="badge bg-warning text-dark">Unassigned</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <form action="${pageContext.request.contextPath}/admin/assign-complaint" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="grievanceId" value="${complaint.grievanceId}">

                    <div class="mb-3">
                        <label for="staffId" class="form-label">Select Active Staff Member <span class="text-danger">*</span></label>
                        <select class="form-select" id="staffId" name="staffId" required>
                            <option value="">-- Choose Staff Member --</option>
                            <c:forEach var="st" items="${activeStaff}">
                                <option value="${st.staffId}" ${complaint.assignedStaffId == st.staffId ? 'selected' : ''}>
                                    <c:out value="${st.fullName}"/> - <c:out value="${st.department}"/> (<c:out value="${st.staffId}"/>)
                                </option>
                            </c:forEach>
                        </select>
                        <div class="form-text small">Only active staff accounts are listed.</div>
                    </div>

                    <div class="mb-3">
                        <label for="assignRemarks" class="form-label">Assignment Instructions / Note (Optional)</label>
                        <input type="text" class="form-control" id="assignRemarks" name="remarks"
                               placeholder="e.g. Please prioritize; campus inspection scheduled...">
                    </div>

                    <button type="submit" class="btn btn-teal">
                        <i class="bi bi-check-lg me-1"></i> Save Staff Assignment
                    </button>
                </form>
            </div>
        </div>

        <!-- Admin Direct Status Controls (Reject / Force Resolve) -->
        <div class="card card-custom">
            <div class="card-header-custom">
                <i class="bi bi-shield-shaded me-1 text-teal"></i> Administrative Actions
            </div>
            <div class="card-body">
                <p class="text-muted small mb-3">Administrators can reject complaints that are invalid/in violation of campus policies, or update status with audited administrative justification.</p>

                <form action="${pageContext.request.contextPath}/admin/complaint-status" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="grievanceId" value="${complaint.grievanceId}">

                    <div class="row g-3">
                        <div class="col-md-5">
                            <label for="adminStatus" class="form-label">Update Status</label>
                            <select class="form-select" id="adminStatus" name="status" required>
                                <option value="REJECTED" ${complaint.status == 'REJECTED' ? 'selected' : ''}>REJECTED (Invalid / Out of Scope)</option>
                                <option value="RESOLVED" ${complaint.status == 'RESOLVED' ? 'selected' : ''}>RESOLVED (Administrative Closing)</option>
                                <option value="PENDING" ${complaint.status == 'PENDING' ? 'selected' : ''}>PENDING (Re-open for Review)</option>
                            </select>
                        </div>
                        <div class="col-md-7">
                            <label for="adminRemarks" class="form-label">Administrative Remarks <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="adminRemarks" name="remarks"
                                   placeholder="Reason for administrative override..." required maxlength="500">
                        </div>
                    </div>

                    <div class="mt-3">
                        <button type="submit" class="btn btn-outline-danger btn-sm" data-confirm="Are you sure you want to apply this administrative status change?">
                            <i class="bi bi-arrow-repeat me-1"></i> Apply Status Override
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Audit Trail / History Timeline -->
    <div class="col-lg-5">
        <div class="card card-custom">
            <div class="card-header-custom">
                <i class="bi bi-clock-history me-1 text-teal"></i> Complete Audit Trail
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty updates}">
                        <ul class="timeline">
                            <c:forEach var="u" items="${updates}">
                                <li class="timeline-item">
                                    <div class="timeline-card">
                                        <div class="d-flex justify-content-between align-items-start mb-1">
                                            <span class="badge-status badge-${u.newStatus.toLowerCase()}">
                                                <c:out value="${u.newStatus}"/>
                                            </span>
                                            <span class="text-muted small"><c:out value="${u.updatedAt}"/></span>
                                        </div>
                                        <div class="small text-muted mb-1">
                                            Updated by <strong class="text-navy"><c:out value="${u.updaterName != null ? u.updaterName : u.updatedById}"/></strong>
                                            (<span class="text-uppercase"><c:out value="${u.updatedByRole}"/></span>)
                                        </div>
                                        <c:if test="${not empty u.remarks}">
                                            <div class="p-2 bg-light rounded border text-secondary small mt-2">
                                                <i class="bi bi-chat-left-quote me-1 text-teal"></i>
                                                <c:out value="${u.remarks}"/>
                                            </div>
                                        </c:if>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted small text-center mb-0">No history records logged.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
