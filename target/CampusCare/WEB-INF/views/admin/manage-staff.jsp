<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Staff Management - CampusCare Administration" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold text-navy mb-1"><i class="bi bi-person-badge-fill text-teal me-2"></i>Staff Directory &amp; Enrollment</h2>
        <p class="text-muted small mb-0">Create, manage, and toggle active status for staff handling student grievances</p>
    </div>
    <div class="d-flex gap-2">
        <button type="button" class="btn btn-teal" data-bs-toggle="modal" data-bs-target="#createStaffModal">
            <i class="bi bi-person-plus-fill me-1"></i> Add New Staff Member
        </button>
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
                <input type="text" class="form-control" id="quickTableSearch" placeholder="Filter staff by name, department, ID, or email...">
            </div>
        </div>
        <div class="col-md-6 text-md-end text-muted small mt-2 mt-md-0">
            Enrolled Staff Members: <strong><c:out value="${staffList.size()}"/></strong>
        </div>
    </div>
</div>

<!-- Staff Table -->
<div class="card card-custom">
    <div class="card-body p-0">
        <c:choose>
            <c:when test="${not empty staffList}">
                <div class="table-responsive">
                    <table class="table table-custom table-hover mb-0">
                        <thead>
                            <tr>
                                <th>Staff ID</th>
                                <th>Full Name</th>
                                <th>Department</th>
                                <th>Contact Information</th>
                                <th>Account Status</th>
                                <th>Created On</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="st" items="${staffList}">
                                <tr>
                                    <td><strong class="text-navy"><c:out value="${st.staffId}"/></strong></td>
                                    <td><c:out value="${st.fullName}"/></td>
                                    <td>
                                        <span class="badge bg-light text-dark border">
                                            <c:out value="${st.department}"/>
                                        </span>
                                    </td>
                                    <td>
                                        <div><i class="bi bi-envelope me-1 text-muted"></i><c:out value="${st.email}"/></div>
                                        <div class="small text-muted"><i class="bi bi-telephone me-1"></i><c:out value="${st.mobile}"/></div>
                                    </td>
                                    <td>
                                        <span class="badge ${st.accountStatus == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">
                                            <c:out value="${st.accountStatus}"/>
                                        </span>
                                    </td>
                                    <td><span class="text-muted small"><c:out value="${st.createdAt}"/></span></td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/admin/manage-staff" method="POST" class="d-inline">
                                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="action" value="toggleStatus">
                                            <input type="hidden" name="staffId" value="${st.staffId}">

                                            <c:choose>
                                                <c:when test="${st.accountStatus == 'ACTIVE'}">
                                                    <input type="hidden" name="status" value="INACTIVE">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger" data-confirm="Deactivate staff member ${st.fullName}? They will not be able to log in or receive new assignments.">
                                                        <i class="bi bi-slash-circle me-1"></i> Deactivate
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="hidden" name="status" value="ACTIVE">
                                                    <button type="submit" class="btn btn-sm btn-outline-success" data-confirm="Reactivate staff member ${st.fullName}?">
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
                        <i class="bi bi-person-x"></i>
                    </div>
                    <h5 class="fw-bold text-navy">No Staff Members Registered</h5>
                    <p class="text-muted small">Click "Add New Staff Member" above to create your first staff account.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Create Staff Modal -->
<div class="modal fade" id="createStaffModal" tabindex="-1" aria-labelledby="createStaffModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title text-navy fw-bold" id="createStaffModalLabel">
                    <i class="bi bi-person-plus text-teal me-2"></i> Enroll New Staff Member
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/manage-staff" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="create">

                <div class="modal-body p-4">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="newStaffId" class="form-label">Staff ID <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="newStaffId" name="staffId"
                                   placeholder="e.g. STF101" required>
                        </div>
                        <div class="col-md-6">
                            <label for="newStaffName" class="form-label">Full Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="newStaffName" name="fullName"
                                   placeholder="e.g. Dr. Robert Vance" required minlength="2" maxlength="100">
                        </div>
                        <div class="col-md-6">
                            <label for="newStaffDept" class="form-label">Assigned Department <span class="text-danger">*</span></label>
                            <select class="form-select" id="newStaffDept" name="department" required>
                                <option value="">-- Choose Department --</option>
                                <option value="HOSTEL">Hostel Affairs</option>
                                <option value="FACILITIES">Campus Facilities</option>
                                <option value="IT">IT &amp; Network Services</option>
                                <option value="ELECTRICAL">Electrical Maintenance</option>
                                <option value="SANITATION">Sanitation &amp; Hygiene</option>
                                <option value="TRANSPORT">Transport Operations</option>
                                <option value="ACADEMICS">Academic Services</option>
                                <option value="GENERAL">General Administration</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label for="newStaffEmail" class="form-label">Official Email <span class="text-danger">*</span></label>
                            <input type="email" class="form-control" id="newStaffEmail" name="email"
                                   placeholder="e.g. rvance@campus.edu" required>
                        </div>
                        <div class="col-md-6">
                            <label for="newStaffMobile" class="form-label">Mobile Number <span class="text-danger">*</span></label>
                            <input type="tel" class="form-control" id="newStaffMobile" name="mobile"
                                   placeholder="e.g. 9876543210" required>
                        </div>
                        <div class="col-md-6">
                            <label for="newStaffPassword" class="form-label">Initial Password <span class="text-danger">*</span></label>
                            <input type="password" class="form-control" id="newStaffPassword" name="password"
                                   placeholder="Min 8 chars with Upper, Lower, Digit &amp; Symbol" required>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-teal">
                        <i class="bi bi-check-circle me-1"></i> Create Staff Account
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
