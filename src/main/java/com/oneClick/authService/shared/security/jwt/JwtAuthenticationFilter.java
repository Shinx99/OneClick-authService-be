/*
package com.oneClick.authService.shared.security.jwt;

import com.oneClick.authService.shared.audit.AuditContextHolder;
import com.oneClick.authService.shared.security.CustomUserDetail.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// infrastructure/security/jwt/JwtAuthenticationFilter.java
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 1. if there's no Bearer token -> skip + continue filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(7);

            // 2. Validate token
            if (jwtService.isTokenValid(jwt)) {

                // 3. Get information from Payload
                String userId = jwtService.extractUserId(jwt);

                // 4. Check SecurityContext. Handle only if not have authentication in context
                if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null){

                    // 5. Check active user and not be banned
                    // Use loadUserById instead of loadUserByUsername
                    // Query DB at first time
                    UserDetails userDetails = userDetailsService.loadUserById(userId);

                    if(userDetails.isEnabled() && userDetails.isAccountNonLocked()){

                        // 6. Create authentication from userDetails (already has ROLE_ prefix)
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, // userPrincipal
                                        null,
                                        userDetails.getAuthorities()); // get from DB

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        // 7. Set in SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        UUID accountId = UUID.fromString(jwtService.extractUserId(jwt));
                        log.debug("get account id: {}", accountId);
                        AuditContextHolder.setCurrentAccountId(accountId);     //set accountId for auditContextHolder


                    }
                }


            }
        } catch (JwtException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        filterChain.doFilter(request, response);
    }
}
*/
