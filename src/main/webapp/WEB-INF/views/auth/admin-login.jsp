<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Administrator Login - CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center my-4">
    <div class="col-md-6 col-lg-5">
        <div class="card card-custom p-4 shadow">
            <div class="text-center mb-4">
                <div class="mb-2">
                    <i class="bi bi-shield-lock-fill text-warning fs-1"></i>
                </div>
                <h3 class="fw-bold text-navy">Administrator Sign In</h3>
                <p class="text-muted small">CampusCare Administrative &amp; Operations Control Center</p>
            </div>

            <form action="${pageContext.request.contextPath}/admin-login" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="mb-3">
                    <label for="username" class="form-label">Admin Username</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light"><i class="bi bi-person-fill"></i></span>
                        <input type="text" class="form-control" id="username" name="username"
                               value="<c:out value='${param.username != null ? param.username : requestScope.username}'/>"
                               placeholder="e.g. admin" required autofocus>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="password" class="form-label">Password</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light"><i class="bi bi-key-fill"></i></span>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="Enter administrative password" required>
                    </div>
                </div>

                <div class="d-grid mt-4">
                    <button type="submit" class="btn btn-navy btn-lg">
                        <i class="bi bi-shield-check me-1"></i> Authenticate Administrator
                    </button>
                </div>
            </form>

            <div class="alert alert-warning border small mt-4 mb-0 text-dark">
                <i class="bi bi-exclamation-triangle me-1"></i>
                Restricted access. Unauthorized access attempts to administrative interfaces are logged and audited.
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
