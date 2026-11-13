package com.campuscare.filter;

import com.campuscare.util.CSRFUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        String path = request.getServletPath();
        if (path == null || path.isEmpty()) {
            String uri = request.getRequestURI();
            String context = request.getContextPath();
            path = uri.substring(context.length());
        }
        String contextPath = request.getContextPath();

        boolean isLoggedIn = (session != null && session.getAttribute("user") != null && session.getAttribute("role") != null);

        if (!isLoggedIn) {
            if (path.startsWith("/student/")) {
                response.sendRedirect(contextPath + "/student-login?error=Please log in to access this page.");
            } else if (path.startsWith("/staff/")) {
                response.sendRedirect(contextPath + "/staff-login?error=Please log in to access this page.");
            } else if (path.startsWith("/admin/")) {
                response.sendRedirect(contextPath + "/admin-login?error=Please log in to access this page.");
            } else {
                response.sendRedirect(contextPath + "/index.jsp");
            }
            return;
        }

        // Ensure CSRF token is initialized for the session
        CSRFUtil.getToken(session);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
