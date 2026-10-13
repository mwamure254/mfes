package com.mfano.mfes.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.mfano.mfes.auth.services.AuditService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthHandler implements AuthenticationSuccessHandler,
        AuthenticationFailureHandler, LogoutHandler {

    private final AuditService auditService;
    String message = "Invalid username or password";

    private String msg(String ms) {
        return "/login?error=true&message=" +
                URLEncoder.encode(ms, StandardCharsets.UTF_8);
    }

     public AuthHandler(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        // log activity info
        if (authentication != null &&
                authentication.getPrincipal() instanceof CustomUserDetails user) {

        auditService.record(
                "user_login", "SUCCESS",
                "User " + user.getEmail() + " logged in successfully.");
            response.sendRedirect("/");
            return;
        }
        response.sendRedirect("/login");
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        // Capture the email submitted on the login form
        String email = request.getParameter("username");

        // If your login form uses name="email", use:
        // String email = request.getParameter("email");
        if (email == null || email.isBlank()) {
            email = "UNKNOWN";
        }
        
        if (exception instanceof DisabledException) {
            message = " account is disabled";

            response.sendRedirect(
                    msg(message));
        } else if (exception instanceof LockedException) {
            message = " account is locked";
            response.sendRedirect(
                    msg(message));
        } else if (exception instanceof CredentialsExpiredException) {
            message = " password has expired";
            response.sendRedirect(
                    msg(message));
        } else {
            message = "Invalid user, check your credentials and try again.";
            response.sendRedirect(
                    msg(message));
        }

        auditService.record(
                "USER_LOGIN", "FAIL",
                "User " + email + message);
    }

    @Override
    public void logout(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        if (authentication != null &&
                authentication.getPrincipal() instanceof CustomUserDetails user) {

            auditService.record(
                    "user_logout", "SUCCESS",
                    "User " + user.getEmail() + " logged out");
        }
    }
}
