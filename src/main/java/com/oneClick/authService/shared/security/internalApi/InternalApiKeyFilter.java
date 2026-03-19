package com.oneClick.authService.shared.security.internalApi;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    @Value("${internal.api.key}")
    private String expectedKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if(!path.startsWith("/internal/")){
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(InternalApiConstant.INTERNAL_API_KEY_HEADER);
        if(apiKey == null || !apiKey.equals(expectedKey)){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(InternalApiConstant.INVALID_API_KEY_MSG);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
