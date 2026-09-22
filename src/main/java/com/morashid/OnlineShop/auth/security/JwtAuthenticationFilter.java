package com.morashid.OnlineShop.auth.security;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * DEBUG: Ina-print wakati filter inaundwa na Spring.
     * Kama hii haionekani kwenye logs → Spring haioni filter!
     */
    @PostConstruct
    public void init() {
        log.info("========================================");
        log.info("JwtAuthenticationFilter BEAN CREATED!");
        log.info("JwtService: {}", jwtService);
        log.info("CustomUserDetailsService: {}", userDetailsService);
        log.info("========================================");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // DEBUG: Log KILA request
        log.info(">>> JwtAuthenticationFilter RUNNING for: {} {}", 
                 request.getMethod(), request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        log.info(">>> Authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info(">>> No Bearer token - skipping");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            final String userEmail = jwtService.extractUsername(jwt);
            log.info(">>> Extracted email: {}", userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                log.info(">>> Loaded user: {}", userDetails.getUsername());

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    log.info(">>> Token VALID - setting authentication");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn(">>> Token INVALID");
                }
            }
        } catch (Exception e) {
            log.error(">>> JWT FAILED: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}