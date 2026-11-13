<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'CampusCare - Student Complaint Management System'}"/></title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark navbar-custom sticky-top">
    <div class="container-fluid px-lg-4">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">
            <i class="bi bi-shield-check text-warning fs-3"></i>
            <span>CampusCare</span>
            <span class="brand-badge">CMS</span>
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent"
                aria-controls="navbarContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <c:choose>
                    <c:when test="${sessionScope.role == 'STUDENT'}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/student/dashboard">
                                <i class="bi bi-speedometer2 me-1"></i> Dashboard
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/student/new-complaint">
                                <i class="bi bi-plus-circle me-1"></i> Raise Complaint
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/student/my-complaints">
                                <i class="bi bi-card-checklist me-1"></i> My Complaints
                            </a>
                        </li>
                    </c:when>

                    <c:when test="${sessionScope.role == 'STAFF'}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/staff/dashboard">
                                <i class="bi bi-speedometer2 me-1"></i> Dashboard
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/staff/assigned-complaints">
                                <i class="bi bi-inbox-fill me-1"></i> Assigned Complaints
                            </a>
                        </li>
                    </c:when>

                    <c:when test="${sessionScope.role == 'ADMIN'}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
                                <i class="bi bi-speedometer2 me-1"></i> Dashboard
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/admin/all-complaints">
                                <i class="bi bi-folder2-open me-1"></i> All Complaints
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/admin/manage-students">
                                <i class="bi bi-people me-1"></i> Students
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/admin/manage-staff">
                                <i class="bi bi-person-badge me-1"></i> Staff Directory
                            </a>
                        </li>
                    </c:when>

                    <c:otherwise>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/index.jsp">
                                <i class="bi bi-house-door me-1"></i> Home
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>

            <div class="d-flex align-items-center gap-2">
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <div class="nav-user-pill">
                            <i class="bi bi-person-circle"></i>
                            <span><c:out value="${sessionScope.userName}"/> (<c:out value="${sessionScope.role}"/>)</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-light rounded-pill px-3">
                            <i class="bi bi-box-arrow-right me-1"></i> Logout
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/student-login" class="btn btn-sm btn-teal">
                            <i class="bi bi-person me-1"></i> Student Portal
                        </a>
                        <a href="${pageContext.request.contextPath}/staff-login" class="btn btn-sm btn-outline-light">
                            <i class="bi bi-person-workspace me-1"></i> Staff Login
                        </a>
                        <a href="${pageContext.request.contextPath}/admin-login" class="btn btn-sm btn-outline-warning">
                            <i class="bi bi-shield-lock me-1"></i> Admin Login
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>

<main class="py-4">
    <div class="container-fluid px-lg-4">
        <!-- Messages include -->
        <jsp:include page="/WEB-INF/views/common/messages.jsp"/>
