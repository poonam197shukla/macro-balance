package com.macrobalance.security.jwt;

import com.macrobalance.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {
                String email = jwtUtil.extractUsername(token);
                Long userId = jwtUtil.extractUserId(token);

                if (email != null && userId != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    if (jwtUtil.validateToken(token, email)) {

                        //  NO DB CALL HERE
                        var authToken = new UsernamePasswordAuthenticationToken(
                                userId, // principal
                                null,
                                null // roles later
                        );

                        authToken.setDetails(
                                new WebAuthenticationDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }

            } catch (Exception e) {
                // invalid token → ignore and continue
            }
        }

        filterChain.doFilter(request, response);
    }
}