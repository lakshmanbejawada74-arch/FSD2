<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Staff Login - CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center my-4">
    <div class="col-md-6 col-lg-5">
        <div class="card card-custom p-4 shadow">
            <div class="text-center mb-4">
                <div class="mb-2">
                    <i class="bi bi-person-workspace text-primary fs-1"></i>
                </div>
                <h3 class="fw-bold text-navy">Staff Portal Login</h3>
                <p class="text-muted small">Sign in to manage and resolve complaints assigned to your queue</p>
            </div>

            <form action="${pageContext.request.contextPath}/staff-login" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="mb-3">
                    <label for="identifier" class="form-label">Staff ID or Official Email</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light"><i class="bi bi-person-badge"></i></span>
                        <input type="text" class="form-control" id="identifier" name="identifier"
                               value="<c:out value='${param.identifier != null ? param.identifier : requestScope.identifier}'/>"
                               placeholder="e.g. STF001 or staff@campus.edu" required autofocus>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="password" class="form-label">Staff Password</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light"><i class="bi bi-lock"></i></span>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="Enter your staff password" required>
                    </div>
                </div>

                <div class="d-grid mt-4">
                    <button type="submit" class="btn btn-navy btn-lg">
                        <i class="bi bi-box-arrow-in-right me-1"></i> Staff Sign In
                    </button>
                </div>
            </form>

            <div class="alert alert-light border small mt-4 mb-0 text-muted">
                <i class="bi bi-info-circle me-1 text-primary"></i>
                Staff accounts are provisioned and managed by the Campus Administration. If you require credentials, please contact the IT Administrator.
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
