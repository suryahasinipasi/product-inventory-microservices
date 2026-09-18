package com.surya.productservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@Profile("public-demo-read-only")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PublicDemoFilter extends OncePerRequestFilter {

    private static final Set<String> WRITE_METHODS =
            Set.of("POST", "PUT", "PATCH", "DELETE");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();

        boolean productWrite =
                path.startsWith("/api/products")
                        && WRITE_METHODS.contains(method);

        if (!productWrite) {
            chain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"message\":\"This operation is disabled in the public demo.\"}");
    }
}
