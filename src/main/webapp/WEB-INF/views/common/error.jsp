<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Access Notice - CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center my-5">
    <div class="col-md-7 col-lg-6 text-center">
        <div class="card card-custom p-5 shadow-sm">
            <div class="mb-3">
                <i class="bi bi-shield-slash text-danger" style="font-size: 4rem;"></i>
            </div>
            <h3 class="fw-bold text-navy mb-2">Notice</h3>
            <p class="text-muted mb-4">
                <c:choose>
                    <c:when test="${pageContext.errorData.statusCode == 403}">
                        <strong>403 - Access Denied:</strong> You do not possess the required permissions or role privileges to access this resource.
                    </c:when>
                    <c:when test="${pageContext.errorData.statusCode == 404}">
                        <strong>404 - Page Not Found:</strong> The requested resource could not be found.
                    </c:when>
                    <c:otherwise>
                        An unexpected application event occurred. If this problem persists, please contact campus support.
                    </c:otherwise>
                </c:choose>
            </p>
            <div>
                <a href="${pageContext.request.contextPath}/" class="btn btn-teal px-4">
                    <i class="bi bi-house-door me-1"></i> Return to Home Portal
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
