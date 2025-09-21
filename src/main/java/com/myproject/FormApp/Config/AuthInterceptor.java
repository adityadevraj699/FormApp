package com.myproject.FormApp.Config;

import jakarta.servlet.http.*;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String uri = req.getRequestURI();
        HttpSession session = req.getSession(false);

        // Public URLs allowed without login
        if (uri.equals("/") || uri.equals("/index") || uri.equals("/login") || uri.equals("/register")
                || uri.startsWith("/css") || uri.startsWith("/js") || uri.startsWith("/images") || uri.startsWith("/assets")) {
            return true;
        }

        // Admin URLs
        if (uri.startsWith("/admin")) {
            if (session != null && session.getAttribute("loggedInAdmin") != null) return true;
            res.sendRedirect("/index?msg=Please+login+as+Admin");
            return false;
        }

        // Student URLs
        if (uri.startsWith("/student")) {
            if (session != null && session.getAttribute("loggedInStudent") != null) return true;
            res.sendRedirect("/index?msg=Please+login+as+Student");
            return false;
        }

        // Teacher URLs
        if (uri.startsWith("/teacher")) {
            if (session != null && session.getAttribute("loggedInTeacher") != null) return true;
            res.sendRedirect("/index?msg=Please+login+as+Teacher");
            return false;
        }

        return true;
    }
}
