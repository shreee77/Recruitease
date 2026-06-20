package com.recruitease.security;

import jakarta.servlet.http.*;
import org.springframework.security.core.*;
import org.springframework.*;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String redirectUrl = "/";

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
                break;
            } else if (role.equals("ROLE_HR")) {
                redirectUrl = "/hr/dashboard";
                break;
            } else if (role.equals("ROLE_STUDENT")) {
                redirectUrl = "/student/dashboard";
                break;
            }
        }

        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}
