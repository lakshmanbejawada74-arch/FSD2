<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Raise New Complaint - CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
                <h3 class="fw-bold text-navy mb-1"><i class="bi bi-pencil-square text-teal me-2"></i>Lodge a Complaint</h3>
                <p class="text-muted small mb-0">Submit your grievance for official administrative review and resolution</p>
            </div>
            <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-arrow-left me-1"></i> Back to Dashboard
            </a>
        </div>

        <div class="card card-custom p-4 shadow-sm mb-4">
            <!-- Student Readonly Context Pill -->
            <div class="alert alert-light border d-flex align-items-center justify-content-between flex-wrap gap-2 mb-4">
                <div>
                    <span class="text-muted small">Submitting Student:</span>
                    <strong class="text-navy ms-1"><c:out value="${sessionScope.userName}"/></strong>
                    <span class="badge bg-secondary ms-1"><c:out value="${sessionScope.userId}"/></span>
                </div>
                <div class="text-muted small">
                    <i class="bi bi-shield-lock me-1 text-success"></i> Identity verified via session
                </div>
            </div>

            <form id="complaintForm" action="${pageContext.request.contextPath}/student/complaint-create" method="POST" novalidate>
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="row g-3">
                    <div class="col-md-7">
                        <label for="categoryId" class="form-label">Grievance Category <span class="text-danger">*</span></label>
                        <select class="form-select" id="categoryId" name="categoryId" required>
                            <option value="">-- Choose Category --</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.categoryId}" ${requestScope.categoryId == cat.categoryId ? 'selected' : ''}>
                                    <c:out value="${cat.categoryName}"/> (<c:out value="${cat.department}"/>)
                                </option>
                            </c:forEach>
                        </select>
                        <div class="form-text small">Select the department/functional area corresponding to the issue.</div>
                    </div>

                    <div class="col-md-5">
                        <label for="priority" class="form-label">Urgency / Priority <span class="text-danger">*</span></label>
                        <select class="form-select" id="priority" name="priority" required>
                            <option value="LOW" ${requestScope.priority == 'LOW' ? 'selected' : ''}>Low (General Inquiry / Non-Urgent)</option>
                            <option value="MEDIUM" ${empty requestScope.priority or requestScope.priority == 'MEDIUM' ? 'selected' : ''}>Medium (Standard Campus Issue)</option>
                            <option value="HIGH" ${requestScope.priority == 'HIGH' ? 'selected' : ''}>High (Critical / Immediate Attention)</option>
                        </select>
                    </div>

                    <div class="col-12">
                        <label for="title" class="form-label">Complaint Subject / Title <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="title" name="title"
                               value="<c:out value='${requestScope.title}'/>"
                               placeholder="Brief, specific summary (e.g. Water leakage in Room B-204)"
                               minlength="5" maxlength="100" required>
                        <div class="form-text small">Between 5 and 100 characters.</div>
                    </div>

                    <div class="col-12">
                        <label for="description" class="form-label">Detailed Description of Grievance <span class="text-danger">*</span></label>
                        <textarea class="form-control" id="description" name="description" rows="5"
                                  placeholder="Provide exact location, time, frequency, and details about the problem to help staff resolve it efficiently..."
                                  minlength="10" maxlength="1000" required><c:out value="${requestScope.description}"/></textarea>
                        <div class="form-text small">Between 10 and 1000 characters. Be concise and factual.</div>
                    </div>
                </div>

                <div class="d-flex justify-content-between align-items-center mt-4 pt-2 border-top">
                    <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-outline-secondary">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-teal px-4">
                        <i class="bi bi-send-fill me-1"></i> Submit Complaint
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
