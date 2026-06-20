package com.magic.investor_api.auth;

import com.magic.investor_api.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // ===========================
    // CLAVE SECRETA JWT
    // ===========================
    // IMPORTANTE:
    // - Debe ser suficientemente larga
    // - En producción debe ir en application.properties o vault
    private static final String SECRET_KEY =
            "clave_secreta_muy_larga_para_que_sea_segura_1234567890";

    // ===========================
    // KEY FIRMA JWT (ÚNICA Y FIJA)
    // ===========================
    // Se crea una sola vez para evitar inconsistencias entre firma y verificación
    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // ===========================
    // GENERAR TOKEN JWT
    // ===========================
    public String generateToken(User user) {

        return Jwts.builder()

                // ID del usuario dentro del token
                .claim("userId", user.getId())

                // Email como subject estándar JWT
                .subject(user.getEmail())

                // Rol del usuario
                .claim("role", user.getRole())

                // Fecha de creación
                .issuedAt(new Date())

                // Expiración (24 horas)
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))

                // FIRMA DEL TOKEN (IMPORTANTE: HS256)
                .signWith(key, Jwts.SIG.HS256)

                .compact();
    }

    // ===========================
    // PARSEAR TOKEN (VALIDAR FIRMA)
    // ===========================
    private Claims parseToken(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ===========================
    // EXTRAER USER ID
    // ===========================
    public Long extractUserId(String token) {

        return parseToken(token)
                .get("userId", Long.class);
    }

    // ===========================
    // EXTRAER EMAIL
    // ===========================
    public String extractEmail(String token) {

        return parseToken(token)
                .getSubject();
    }

    // ===========================
    // EXTRAER ROLE
    // ===========================
    public String extractRole(String token) {

        return parseToken(token)
                .get("role", String.class);
    }
}