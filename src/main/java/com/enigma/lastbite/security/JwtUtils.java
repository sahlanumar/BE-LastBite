package com.enigma.lastbite.security;

import Group3.CourseApp.dto.response.JwtClaims;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtils {

    @Value("${restaurant.jwt.secret-key}")
    private String secretKey;

    @Value("${restaurant.jwt.expiration-in-second}")
    private long expirationInSecond;

    @Value("${restaurant.jwt.issuer}")
    private String issuer;

    /**
     * Membuat token JWT berdasarkan UserDetails
     *
     * @param userDetails UserDetails pengguna yang terautentikasi
     * @return Token JWT yang telah dibuat
     * @throws JWTCreationException jika terjadi error saat membuat token
     */
    public String generateToken(UserDetails userDetails) {
        try {
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return JWT.create()
                    .withSubject(userDetails.getUsername())
                    .withClaim("roles", roles)
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plusSeconds(expirationInSecond))
                    .withIssuer(issuer)
                    .sign(Algorithm.HMAC256(secretKey));
        } catch (JWTCreationException e) {
            log.error("Error saat membuat token JWT: {}", e.getMessage());
            throw new RuntimeException("Tidak dapat membuat token JWT", e);
        }
    }

    /**
     * Memvalidasi token JWT
     *
     * @param token Token JWT yang akan divalidasi
     * @return true jika token valid, false jika tidak
     */
    public boolean validateJwtToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey))
                    .withIssuer(issuer)
                    .build();

            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error("Token JWT invalid: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Mendapatkan klaim dari token JWT
     *
     * @param token Token JWT
     * @return JwtClaims yang berisi informasi dari token
     * @throws JWTVerificationException jika token tidak valid
     */
    public JwtClaims getClaims(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey))
                    .withIssuer(issuer)
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            String username = decodedJWT.getSubject();
            List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

            return JwtClaims.builder()
                    .username(username)
                    .roles(roles)
                    .build();
        } catch (JWTVerificationException e) {
            log.error("Error saat mendapatkan klaim dari token: {}", e.getMessage());
            throw new JWTVerificationException("Token tidak valid");
        }
    }

    public String getTokenFromHeader() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    /**
     * Mengekstrak username dari token JWT
     *
     * @param token Token JWT
     * @return Username yang diekstrak
     * @throws JWTVerificationException jika token tidak valid
     */
    public String getUsernameFromJwtToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey))
                    .withIssuer(issuer)
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException e) {
            log.error("Error saat mendapatkan username dari token: {}", e.getMessage());
            throw new JWTVerificationException("Token tidak valid");
        }
    }
}
