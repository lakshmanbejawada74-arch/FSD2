package com.campuscare.filter;

import com.campuscare.util.CSRFUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        String path = request.getServletPath();
        if (path == null || path.isEmpty()) {
            String uri = request.getRequestURI();
            String context = request.getContextPath();
            path = uri.substring(context.length());
        }

        if (path.startsWith("/student/") && !"STUDENT".equalsIgnoreCase(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Student privileges required.");
            return;
        }

        if (path.startsWith("/staff/") && !"STAFF".equalsIgnoreCase(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Staff privileges required.");
            return;
        }

        if (path.startsWith("/admin/") && !"ADMIN".equalsIgnoreCase(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator privileges required.");
            return;
        }

        // Validate CSRF token for state-modifying requests (POST)
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            if (!CSRFUtil.isValidToken(request)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Security Validation Failed: Invalid or expired CSRF token.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
