package com.magic.investor_api.auth;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    // Inyección por constructor (buena práctica)
    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ===========================
        // 1. LEER HEADER AUTHORIZATION
        // ===========================
        String authHeader = request.getHeader("Authorization");

        // Si no hay token o no es Bearer, se continúa sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ===========================
        // 2. EXTRAER TOKEN
        // ===========================
        String token = authHeader.substring(7);

        try {
            // ===========================
            // 3. VALIDAR TOKEN (firma + expiración)
            // ===========================
            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);

            // ===========================
            // 4. CREAR AUTENTICACIÓN
            // ===========================
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email, // principal (usuario autenticado)
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            // ===========================
            // 5. GUARDAR EN CONTEXTO DE SEGURIDAD
            // ===========================
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Continuar filtro normal
            filterChain.doFilter(request, response);
            return;

        } catch (ExpiredJwtException e) {

            // ===========================
            // TOKEN EXPIRADO
            // ===========================
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expirado");
            return;

        } catch (Exception e) {

            // ===========================
            // TOKEN INVÁLIDO (firma, formato, etc)
            // ===========================
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido");
            return;
        }
    }
}