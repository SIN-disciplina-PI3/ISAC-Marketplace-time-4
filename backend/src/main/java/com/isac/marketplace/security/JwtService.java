package com.isac.marketplace.security;

import com.isac.marketplace.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret:isac-marketplace-super-secret-key-32-bytes-minimum-length-jwt-2026}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs
    ) {
        // Assegura chave com tamanho adequado para HMAC-SHA256 (mínimo 32 bytes)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
            this.signingKey = Keys.hmacShaKeyFor(padded);
        } else {
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        }
        this.expirationMs = expirationMs;
    }

    public String gerarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("papel", "ROLE_" + usuario.getPapel().name());
        claims.put("usuarioId", usuario.getId());
        if (usuario.getLoja() != null) {
            claims.put("lojaId", usuario.getLoja().getId());
        }
        return gerarToken(claims, usuario.getEmail());
    }

    public String gerarToken(Map<String, Object> extraClaims, String subject) {
        Date agora = new Date();
        Date expiraEm = new Date(agora.getTime() + expirationMs);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(agora)
                .expiration(expiraEm)
                .signWith(signingKey)
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public String extrairPapel(String token) {
        return (String) extrairTodosClaims(token).get("papel");
    }

    public <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extrairTodosClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValido(String token) {
        if (token == null || token.isBlank()) return false;
        try {
            return !isTokenExpirado(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenValido(String token, UserDetails userDetails) {
        if (!isTokenValido(token)) return false;
        final String email = extrairEmail(token);
        return (email != null && email.equalsIgnoreCase(userDetails.getUsername()));
    }

    public boolean isTokenExpirado(String token) {
        Date expiracao = extrairClaim(token, Claims::getExpiration);
        return expiracao != null && expiracao.before(new Date());
    }

    private Claims extrairTodosClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
