<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Success Alert --%>
<c:if test="${not empty param.success or not empty requestScope.successMessage}">
    <div class="alert alert-success alert-dismissible fade show card-custom shadow-sm mb-4 border-0 border-start border-success border-4" role="alert">
        <div class="d-flex align-items-center">
            <i class="bi bi-check-circle-fill fs-4 me-2 text-success"></i>
            <div>
                <strong>Success!</strong>
                <span><c:out value="${not empty param.success ? param.success : requestScope.successMessage}"/></span>
            </div>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<%-- General Error Alert --%>
<c:if test="${not empty param.error or not empty requestScope.errorMessage}">
    <div class="alert alert-danger alert-dismissible fade show card-custom shadow-sm mb-4 border-0 border-start border-danger border-4" role="alert">
        <div class="d-flex align-items-center">
            <i class="bi bi-exclamation-triangle-fill fs-4 me-2 text-danger"></i>
            <div>
                <strong>Notice:</strong>
                <span><c:out value="${not empty param.error ? param.error : requestScope.errorMessage}"/></span>
            </div>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<%-- Multiple Form Validation Errors --%>
<c:if test="${not empty requestScope.errors}">
    <div class="alert alert-danger alert-dismissible fade show card-custom shadow-sm mb-4 border-0 border-start border-danger border-4" role="alert">
        <div class="d-flex align-items-center mb-2">
            <i class="bi bi-x-circle-fill fs-4 me-2 text-danger"></i>
            <strong>Please correct the following errors:</strong>
        </div>
        <ul class="mb-0 ps-3">
            <c:forEach var="err" items="${requestScope.errors}">
                <li><c:out value="${err}"/></li>
            </c:forEach>
        </ul>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>
