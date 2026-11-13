<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Student Registration - CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center my-4">
    <div class="col-md-9 col-lg-8">
        <div class="card card-custom p-4 p-md-5 shadow">
            <div class="text-center mb-4">
                <div class="mb-2">
                    <i class="bi bi-person-plus-fill text-teal fs-1"></i>
                </div>
                <h3 class="fw-bold text-navy">Student Account Registration</h3>
                <p class="text-muted small">Register to file and monitor your campus grievances</p>
            </div>

            <form id="studentRegisterForm" action="${pageContext.request.contextPath}/student-register" method="POST" novalidate>
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="row g-3">
                    <div class="col-md-6">
                        <label for="studentId" class="form-label">Student ID / Roll No. <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="studentId" name="studentId"
                               value="<c:out value='${requestScope.studentId}'/>"
                               placeholder="e.g. STU1001 or 2024CS01" required>
                        <div class="form-text small">3 to 30 alphanumeric characters.</div>
                    </div>

                    <div class="col-md-6">
                        <label for="fullName" class="form-label">Full Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="fullName" name="fullName"
                               value="<c:out value='${requestScope.fullName}'/>"
                               placeholder="e.g. John Doe" required>
                        <div class="form-text small">2 to 100 characters.</div>
                    </div>

                    <div class="col-md-6">
                        <label for="department" class="form-label">Department / Branch <span class="text-danger">*</span></label>
                        <select class="form-select" id="department" name="department" required>
                            <option value="">-- Select Department --</option>
                            <option value="Computer Science and Engineering" ${requestScope.department == 'Computer Science and Engineering' ? 'selected' : ''}>Computer Science &amp; Engineering</option>
                            <option value="Information Technology" ${requestScope.department == 'Information Technology' ? 'selected' : ''}>Information Technology</option>
                            <option value="Electronics and Communication" ${requestScope.department == 'Electronics and Communication' ? 'selected' : ''}>Electronics &amp; Communication</option>
                            <option value="Electrical Engineering" ${requestScope.department == 'Electrical Engineering' ? 'selected' : ''}>Electrical Engineering</option>
                            <option value="Mechanical Engineering" ${requestScope.department == 'Mechanical Engineering' ? 'selected' : ''}>Mechanical Engineering</option>
                            <option value="Civil Engineering" ${requestScope.department == 'Civil Engineering' ? 'selected' : ''}>Civil Engineering</option>
                            <option value="Management Studies" ${requestScope.department == 'Management Studies' ? 'selected' : ''}>Management Studies</option>
                            <option value="Applied Sciences" ${requestScope.department == 'Applied Sciences' ? 'selected' : ''}>Applied Sciences</option>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label for="studyYear" class="form-label">Study Year <span class="text-danger">*</span></label>
                        <select class="form-select" id="studyYear" name="studyYear" required>
                            <option value="">-- Select Year --</option>
                            <option value="1" ${requestScope.studyYear == '1' ? 'selected' : ''}>1st Year (Freshman)</option>
                            <option value="2" ${requestScope.studyYear == '2' ? 'selected' : ''}>2nd Year (Sophomore)</option>
                            <option value="3" ${requestScope.studyYear == '3' ? 'selected' : ''}>3rd Year (Junior)</option>
                            <option value="4" ${requestScope.studyYear == '4' ? 'selected' : ''}>4th Year (Senior)</option>
                            <option value="5" ${requestScope.studyYear == '5' ? 'selected' : ''}>5th Year (Dual/Integrated)</option>
                            <option value="6" ${requestScope.studyYear == '6' ? 'selected' : ''}>6th Year (Research/Doctoral)</option>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label for="email" class="form-label">Email Address <span class="text-danger">*</span></label>
                        <input type="email" class="form-control" id="email" name="email"
                               value="<c:out value='${requestScope.email}'/>"
                               placeholder="e.g. student@college.edu" required>
                    </div>

                    <div class="col-md-6">
                        <label for="mobile" class="form-label">Mobile Number <span class="text-danger">*</span></label>
                        <input type="tel" class="form-control" id="mobile" name="mobile"
                               value="<c:out value='${requestScope.mobile}'/>"
                               placeholder="e.g. +919876543210 or 9876543210" required>
                    </div>

                    <div class="col-12">
                        <label for="studentType" class="form-label">Student Type <span class="text-danger">*</span></label>
                        <select class="form-select" id="studentType" name="studentType" required>
                            <option value="DAY_SCHOLAR" ${empty requestScope.studentType or requestScope.studentType == 'DAY_SCHOLAR' ? 'selected' : ''}>Day Scholar (Off-Campus Commuter)</option>
                            <option value="HOSTEL" ${requestScope.studentType == 'HOSTEL' ? 'selected' : ''}>Hostel Student (Resident)</option>
                        </select>
                        <div class="form-text small">Selecting Hostel enables required accommodation fields.</div>
                    </div>

                    <!-- Dynamic Hostel Details -->
                    <div id="hostelDetailsContainer" class="col-12" style="display: none;">
                        <div class="p-3 bg-light rounded border border-teal-subtle">
                            <h6 class="fw-bold text-navy mb-3"><i class="bi bi-building me-1"></i> Hostel Accommodation Details</h6>
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="hostelName" class="form-label">Hostel Name <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="hostelName" name="hostelName"
                                           value="<c:out value='${requestScope.hostelName}'/>"
                                           placeholder="e.g. Aryabhata Hall / Ganga Hostel">
                                </div>
                                <div class="col-md-6">
                                    <label for="roomNumber" class="form-label">Room Number <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="roomNumber" name="roomNumber"
                                           value="<c:out value='${requestScope.roomNumber}'/>"
                                           placeholder="e.g. B-204">
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label for="password" class="form-label">Password <span class="text-danger">*</span></label>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="Enter secure password" required>
                        <div class="form-text small">Minimum 8 characters with upper, lower, digit &amp; symbol.</div>
                    </div>

                    <div class="col-md-6">
                        <label for="confirmPassword" class="form-label">Confirm Password <span class="text-danger">*</span></label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                               placeholder="Re-enter password" required>
                    </div>
                </div>

                <div class="d-grid mt-4">
                    <button type="submit" class="btn btn-teal btn-lg">
                        <i class="bi bi-check-circle me-1"></i> Register Student Account
                    </button>
                </div>
            </form>

            <div class="text-center mt-4">
                <p class="text-muted small mb-0">Already registered?
                    <a href="${pageContext.request.contextPath}/student-login" class="fw-semibold text-teal text-decoration-none">
                        Sign In Here
                    </a>
                </p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
