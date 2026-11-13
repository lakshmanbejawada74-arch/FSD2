<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Student Login - CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center my-4">
    <div class="col-md-6 col-lg-5">
        <div class="card card-custom p-4 shadow">
            <div class="text-center mb-4">
                <div class="mb-2">
                    <i class="bi bi-mortarboard-fill text-teal fs-1"></i>
                </div>
                <h3 class="fw-bold text-navy">Student Sign In</h3>
                <p class="text-muted small">Access your student grievance dashboard and track complaints</p>
            </div>

            <form action="${pageContext.request.contextPath}/student-login" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="mb-3">
                    <label for="identifier" class="form-label">Student ID or Registered Email</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light"><i class="bi bi-person"></i></span>
                        <input type="text" class="form-control" id="identifier" name="identifier"
                               value="<c:out value='${param.identifier != null ? param.identifier : requestScope.identifier}'/>"
                               placeholder="e.g. STU101 or student@campus.edu" required autofocus>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="password" class="form-label">Password</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light"><i class="bi bi-lock"></i></span>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="Enter your account password" required>
                    </div>
                </div>

                <div class="d-grid mt-4">
                    <button type="submit" class="btn btn-teal btn-lg">
                        <i class="bi bi-box-arrow-in-right me-1"></i> Sign In to Portal
                    </button>
                </div>
            </form>

            <hr class="my-4">

            <div class="text-center">
                <p class="text-muted small mb-1">New to CampusCare?</p>
                <a href="${pageContext.request.contextPath}/student-register" class="fw-semibold text-teal text-decoration-none">
                    <i class="bi bi-person-plus me-1"></i> Create a Student Account
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
