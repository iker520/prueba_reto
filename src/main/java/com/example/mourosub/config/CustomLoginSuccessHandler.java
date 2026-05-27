package com.example.mourosub.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Redirige al usuario tras el login según su rol:
 *   ROLE_ADMIN → /admin
 *   ROLE_USER  → /mi-cuenta
 */
@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String targetUrl = "/mi-cuenta"; // por defecto: usuario normal va a su perfil

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                targetUrl = "/admin";
                break;
            }
        }

        response.sendRedirect(request.getContextPath() + targetUrl);
    }
}
