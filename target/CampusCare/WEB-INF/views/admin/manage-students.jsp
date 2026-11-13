<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Manage Students - CampusCare Administration" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold text-navy mb-1"><i class="bi bi-people-fill text-teal me-2"></i>Student Accounts Management</h2>
        <p class="text-muted small mb-0">Review student registrations and manage account activation states</p>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary btn-sm">
            <i class="bi bi-arrow-left me-1"></i> Dashboard
        </a>
    </div>
</div>

<!-- Search Bar -->
<div class="card card-custom mb-4 p-3">
    <div class="row align-items-center">
        <div class="col-md-6">
            <div class="input-group">
                <span class="input-group-text bg-light"><i class="bi bi-search"></i></span>
                <input type="text" class="form-control" id="quickTableSearch" placeholder="Type student name, ID, or department to filter table...">
            </div>
        </div>
        <div class="col-md-6 text-md-end text-muted small mt-2 mt-md-0">
            Total Students Registered: <strong><c:out value="${students.size()}"/></strong>
        </div>
    </div>
</div>

<div class="card card-custom">
    <div class="card-body p-0">
        <c:choose>
            <c:when test="${not empty students}">
                <div class="table-responsive">
                    <table class="table table-custom table-hover mb-0">
                        <thead>
                            <tr>
                                <th>Student ID</th>
                                <th>Full Name</th>
                                <th>Department &amp; Year</th>
                                <th>Type / Accommodation</th>
                                <th>Contact Information</th>
                                <th>Account Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${students}">
                                <tr>
                                    <td><strong class="text-navy"><c:out value="${s.studentId}"/></strong></td>
                                    <td><c:out value="${s.fullName}"/></td>
                                    <td>
                                        <c:out value="${s.department}"/>
                                        <span class="d-block small text-muted">Year <c:out value="${s.studyYear}"/></span>
                                    </td>
                                    <td>
                                        <span class="badge bg-light text-dark border"><c:out value="${s.studentType}"/></span>
                                        <c:if test="${s.studentType == 'HOSTEL'}">
                                            <span class="d-block small text-muted"><c:out value="${s.hostelName}"/> (Rm: <c:out value="${s.roomNumber}"/>)</span>
                                        </c:if>
                                    </td>
                                    <td>
                                        <div><i class="bi bi-envelope me-1 text-muted"></i><c:out value="${s.email}"/></div>
                                        <div class="small text-muted"><i class="bi bi-telephone me-1"></i><c:out value="${s.mobile}"/></div>
                                    </td>
                                    <td>
                                        <span class="badge ${s.accountStatus == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">
                                            <c:out value="${s.accountStatus}"/>
                                        </span>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/admin/manage-students" method="POST" class="d-inline">
                                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="action" value="toggleStatus">
                                            <input type="hidden" name="studentId" value="${s.studentId}">

                                            <c:choose>
                                                <c:when test="${s.accountStatus == 'ACTIVE'}">
                                                    <input type="hidden" name="status" value="INACTIVE">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger" data-confirm="Deactivate student account ${s.studentId}? The student will not be able to log in.">
                                                        <i class="bi bi-slash-circle me-1"></i> Deactivate
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="hidden" name="status" value="ACTIVE">
                                                    <button type="submit" class="btn btn-sm btn-outline-success" data-confirm="Reactivate student account ${s.studentId}?">
                                                        <i class="bi bi-check-circle me-1"></i> Activate
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
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
                        <i class="bi bi-people"></i>
                    </div>
                    <h5 class="fw-bold text-navy">No Students Registered Yet</h5>
                    <p class="text-muted small">Registered students will appear here.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
