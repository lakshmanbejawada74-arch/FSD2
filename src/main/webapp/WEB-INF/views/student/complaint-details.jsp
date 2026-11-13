<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Complaint Details - ${complaint.complaintNumber}" scope="request"/>
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
        <p class="text-muted small mb-0">Filed on <c:out value="${complaint.createdAt}"/> &bull; Last updated on <c:out value="${complaint.updatedAt}"/></p>
    </div>
    <div class="d-flex gap-2">
        <a href="${pageContext.request.contextPath}/student/my-complaints" class="btn btn-outline-secondary btn-sm">
            <i class="bi bi-arrow-left me-1"></i> Back to My Complaints
        </a>
        <c:if test="${complaint.status == 'PENDING' or complaint.status == 'ASSIGNED'}">
            <!-- Student can withdraw complaint -->
            <button type="button" class="btn btn-outline-danger btn-sm" data-bs-toggle="modal" data-bs-target="#withdrawModal">
                <i class="bi bi-x-circle me-1"></i> Withdraw Complaint
            </button>
        </c:if>
    </div>
</div>

<div class="row g-4">
    <!-- Complaint Overview & Description -->
    <div class="col-lg-7">
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

                <div class="row g-3 text-sm">
                    <div class="col-sm-6">
                        <span class="text-muted d-block small">Responsible Department</span>
                        <strong class="text-navy"><c:out value="${complaint.categoryDepartment}"/></strong>
                    </div>
                    <div class="col-sm-6">
                        <span class="text-muted d-block small">Assigned Staff</span>
                        <c:choose>
                            <c:when test="${not empty complaint.assignedStaffName}">
                                <strong class="text-navy"><c:out value="${complaint.assignedStaffName}"/></strong>
                                <span class="d-block text-muted small"><c:out value="${complaint.assignedStaffEmail}"/></span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-secondary-subtle text-secondary">Awaiting Staff Assignment</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <!-- Student Details Card -->
        <div class="card card-custom">
            <div class="card-header-custom">
                <i class="bi bi-person-badge me-1 text-teal"></i> Submitter Details
            </div>
            <div class="card-body">
                <div class="row g-2 small">
                    <div class="col-sm-6">
                        <span class="text-muted">Student Name:</span>
                        <span class="fw-semibold ms-1"><c:out value="${complaint.studentName}"/></span>
                    </div>
                    <div class="col-sm-6">
                        <span class="text-muted">Student ID:</span>
                        <span class="fw-semibold ms-1"><c:out value="${complaint.studentId}"/></span>
                    </div>
                    <div class="col-sm-6">
                        <span class="text-muted">Department:</span>
                        <span class="fw-semibold ms-1"><c:out value="${complaint.studentDepartment}"/></span>
                    </div>
                    <div class="col-sm-6">
                        <span class="text-muted">Student Type:</span>
                        <span class="badge bg-light text-dark border ms-1"><c:out value="${complaint.studentType}"/></span>
                    </div>
                    <c:if test="${complaint.studentType == 'HOSTEL'}">
                        <div class="col-sm-6">
                            <span class="text-muted">Hostel:</span>
                            <span class="fw-semibold ms-1"><c:out value="${complaint.hostelName}"/></span>
                        </div>
                        <div class="col-sm-6">
                            <span class="text-muted">Room No:</span>
                            <span class="fw-semibold ms-1"><c:out value="${complaint.roomNumber}"/></span>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <!-- Timeline / Status History -->
    <div class="col-lg-5">
        <div class="card card-custom">
            <div class="card-header-custom">
                <i class="bi bi-clock-history me-1 text-teal"></i> Status History &amp; Remarks
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
                        <p class="text-muted small text-center mb-0">No progress updates recorded yet.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<!-- Withdraw Confirmation Modal -->
<c:if test="${complaint.status == 'PENDING' or complaint.status == 'ASSIGNED'}">
    <div class="modal fade" id="withdrawModal" tabindex="-1" aria-labelledby="withdrawModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title text-navy" id="withdrawModalLabel">Confirm Complaint Withdrawal</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/student/complaint-withdraw" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="grievanceId" value="${complaint.grievanceId}">

                    <div class="modal-body">
                        <p class="mb-2">Are you sure you want to withdraw complaint <strong><c:out value="${complaint.complaintNumber}"/></strong>?</p>
                        <div class="mb-3">
                            <label for="withdrawRemarks" class="form-label small">Withdrawal Reason (Optional)</label>
                            <textarea class="form-control" id="withdrawRemarks" name="remarks" rows="2" placeholder="e.g. Issue resolved independently..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-danger btn-sm">Yes, Withdraw Complaint</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
