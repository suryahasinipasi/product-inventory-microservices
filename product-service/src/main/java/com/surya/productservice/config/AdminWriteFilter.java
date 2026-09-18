package com.surya.productservice.config;

import com.surya.productservice.security.AdminPasswordService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AdminWriteFilter extends OncePerRequestFilter {

    private static final Set<String> WRITE_METHODS =
            Set.of("POST", "PUT", "PATCH", "DELETE");

    private final AdminPasswordService adminPasswordService;

    public AdminWriteFilter(AdminPasswordService adminPasswordService) {
        this.adminPasswordService = adminPasswordService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        boolean protectedWrite =
                request.getServletPath().startsWith("/api/products")
                        && WRITE_METHODS.contains(request.getMethod());

        if (!protectedWrite) {
            chain.doFilter(request, response);
            return;
        }

        String password = request.getHeader("X-Admin-Password");

        if (adminPasswordService.isValid(password)) {
            chain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"message\":\"Owner authentication is required.\"}"
        );
    }
}
