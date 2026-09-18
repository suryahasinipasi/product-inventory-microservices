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

@Component
@Profile("public-demo")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PublicDemoFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();

        boolean productRead =
                ("GET".equals(method) || "HEAD".equals(method))
                && path.matches("/api/products(?:/(?:[0-9]+|filter|views))?");

        boolean aiChat =
                "POST".equals(method)
                && "/api/ai/chat".equals(path);

        if (productRead || aiChat) {
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
