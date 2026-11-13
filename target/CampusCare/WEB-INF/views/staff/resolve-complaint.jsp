<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Manage Complaint - ${complaint.complaintNumber}" scope="request"/>
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
        <p class="text-muted small mb-0">Assigned to: <strong><c:out value="${complaint.assignedStaffName}"/></strong> &bull; Filed on <c:out value="${complaint.createdAt}"/></p>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/staff/assigned-complaints" class="btn btn-outline-secondary btn-sm">
            <i class="bi bi-arrow-left me-1"></i> Back to Queue
        </a>
    </div>
</div>

<div class="row g-4">
    <!-- Complaint Info & Student Details -->
    <div class="col-lg-7">
        <div class="card card-custom mb-4">
            <div class="card-header-custom d-flex justify-content-between align-items-center">
                <span><i class="bi bi-file-text me-1 text-teal"></i> Grievance Details</span>
                <span class="badge bg-light text-dark border"><c:out value="${complaint.categoryName}"/></span>
            </div>
            <div class="card-body">
                <h5 class="fw-bold text-navy mb-3"><c:out value="${complaint.title}"/></h5>
                <div class="p-3 bg-light rounded border mb-4">
                    <p class="mb-0 text-secondary" style="white-space: pre-wrap;"><c:out value="${complaint.description}"/></p>
                </div>

                <div class="card bg-light border p-3">
                    <h6 class="fw-bold text-navy mb-2"><i class="bi bi-person-fill me-1"></i> Student Information</h6>
                    <div class="row g-2 small">
                        <div class="col-sm-6">
                            <span class="text-muted">Name:</span>
                            <span class="fw-semibold ms-1"><c:out value="${complaint.studentName}"/></span>
                        </div>
                        <div class="col-sm-6">
                            <span class="text-muted">Student ID:</span>
                            <span class="fw-semibold ms-1"><c:out value="${complaint.studentId}"/></span>
                        </div>
                        <div class="col-sm-6">
                            <span class="text-muted">Email:</span>
                            <a href="mailto:${complaint.studentEmail}" class="ms-1 text-teal"><c:out value="${complaint.studentEmail}"/></a>
                        </div>
                        <div class="col-sm-6">
                            <span class="text-muted">Mobile:</span>
                            <a href="tel:${complaint.studentMobile}" class="ms-1 text-teal"><c:out value="${complaint.studentMobile}"/></a>
                        </div>
                        <div class="col-sm-6">
                            <span class="text-muted">Department:</span>
                            <span class="ms-1"><c:out value="${complaint.studentDepartment}"/></span>
                        </div>
                        <div class="col-sm-6">
                            <span class="text-muted">Type:</span>
                            <span class="badge bg-white text-dark border ms-1"><c:out value="${complaint.studentType}"/></span>
                        </div>
                        <c:if test="${complaint.studentType == 'HOSTEL'}">
                            <div class="col-sm-6">
                                <span class="text-muted">Hostel:</span>
                                <strong class="ms-1 text-navy"><c:out value="${complaint.hostelName}"/></strong>
                            </div>
                            <div class="col-sm-6">
                                <span class="text-muted">Room No:</span>
                                <strong class="ms-1 text-navy"><c:out value="${complaint.roomNumber}"/></strong>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>

        <!-- Staff Action Form -->
        <div class="card card-custom">
            <div class="card-header-custom">
                <i class="bi bi-wrench-adjustable me-1 text-teal"></i> Update Progress &amp; Resolution
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${complaint.status == 'RESOLVED'}">
                        <div class="alert alert-success d-flex align-items-center mb-0">
                            <i class="bi bi-check-circle-fill fs-3 me-3 text-success"></i>
                            <div>
                                <h6 class="fw-bold mb-1">Issue Resolved</h6>
                                <p class="mb-0 small">This grievance was resolved. Detailed remarks are recorded in the history log.</p>
                            </div>
                        </div>
                    </c:when>

                    <c:when test="${complaint.status == 'REJECTED' or complaint.status == 'WITHDRAWN'}">
                        <div class="alert alert-secondary d-flex align-items-center mb-0">
                            <i class="bi bi-info-circle-fill fs-3 me-3"></i>
                            <div>
                                <h6 class="fw-bold mb-1">Case Closed (<c:out value="${complaint.status}"/>)</h6>
                                <p class="mb-0 small">No further action is required on this complaint.</p>
                            </div>
                        </div>
                    </c:when>

                    <c:when test="${complaint.status == 'ASSIGNED'}">
                        <p class="text-muted small mb-3">This ticket has been assigned to you. Click below to begin working on the grievance.</p>
                        <form action="${pageContext.request.contextPath}/staff/complaint-status" method="POST">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="grievanceId" value="${complaint.grievanceId}">
                            <input type="hidden" name="status" value="IN_PROGRESS">

                            <div class="mb-3">
                                <label for="startRemarks" class="form-label">Initial Working Note (Optional)</label>
                                <input type="text" class="form-control" id="startRemarks" name="remarks"
                                       placeholder="e.g. Work initiated; inspecting location...">
                            </div>

                            <button type="submit" class="btn btn-teal">
                                <i class="bi bi-play-fill me-1"></i> Start Work (Mark IN_PROGRESS)
                            </button>
                        </form>
                    </c:when>

                    <c:when test="${complaint.status == 'IN_PROGRESS'}">
                        <p class="text-muted small mb-3">Work is ongoing. Once the issue has been completely fixed, provide resolution remarks and mark the complaint resolved.</p>
                        <form id="resolveForm" action="${pageContext.request.contextPath}/staff/complaint-status" method="POST">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="grievanceId" value="${complaint.grievanceId}">
                            <input type="hidden" id="statusSelect" name="status" value="RESOLVED">

                            <div class="mb-3">
                                <label for="resolutionRemarks" class="form-label">Resolution Remarks <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="resolutionRemarks" name="remarks" rows="4"
                                          placeholder="Detail the corrective actions taken, components replaced, or measures implemented to resolve the complaint..."
                                          maxlength="500" required></textarea>
                                <div class="form-text small">Mandatory requirement (up to 500 characters). This will be shown to the student.</div>
                            </div>

                            <button type="submit" class="btn btn-success" data-confirm="Are you sure you have resolved this issue?">
                                <i class="bi bi-check-circle-fill me-1"></i> Mark as RESOLVED
                            </button>
                        </form>
                    </c:when>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Status History Timeline -->
    <div class="col-lg-5">
        <div class="card card-custom">
            <div class="card-header-custom">
                <i class="bi bi-clock-history me-1 text-teal"></i> Complete Case History
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
                        <p class="text-muted small text-center mb-0">No history available.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
