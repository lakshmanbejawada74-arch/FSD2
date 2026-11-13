package com.campuscare.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

public class CSRFUtil {
    public static final String CSRF_PARAM = "csrfToken";
    public static final String CSRF_SESSION_ATTR = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String getToken(HttpSession session) {
        if (session == null) return null;
        String token = (String) session.getAttribute(CSRF_SESSION_ATTR);
        if (token == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(CSRF_SESSION_ATTR, token);
        }
        return token;
    }

    public static boolean isValidToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        String sessionToken = (String) session.getAttribute(CSRF_SESSION_ATTR);
        String requestToken = request.getParameter(CSRF_PARAM);
        if (requestToken == null) {
            requestToken = request.getHeader("X-CSRF-Token");
        }
        return sessionToken != null && sessionToken.equals(requestToken);
    }
}
