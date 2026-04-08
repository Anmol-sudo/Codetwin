package com.example.codetwin.backend.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.codetwin.backend.config.JwtUtil;

import org.springframework.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        // 1. Check header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            email = jwtService.extractEmail(token);
        }

        // 2. If email found and not already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 3. Validate token
            if (jwtService.validateToken(token, userDetails)) {

                // 🔥 Extract role from JWT
                String role = jwtService.extractRole(token);
                System.out.println(role);
                // 🔥 Convert role → authority
                SimpleGrantedAuthority authority =
                        new SimpleGrantedAuthority("ROLE_" + role);

                // 🔥 Set authentication with role
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                List.of(authority)
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 4. Set authentication
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
