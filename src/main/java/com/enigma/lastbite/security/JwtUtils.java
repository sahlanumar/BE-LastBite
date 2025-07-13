package com.enigma.lastbite.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.auth0.jwt.interfaces.JWTVerifier;
import com.enigma.lastbite.constant.UserRole;
import com.enigma.lastbite.dto.response.JwtClaims;
import com.enigma.lastbite.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Date;
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

    @Value("${restaurant.jwt.refresh-token-expiration-in-second}")
    private int refreshTokenExpirationInSecond;

    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        System.out.println(" ini role" + roles);

        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(Instant.now().plusSeconds(expirationInSecond))
                .withClaim("roles", roles)
                .withClaim("tokenType", "access")
                .sign(Algorithm.HMAC256(secretKey));
    }

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
     * Generate JWT token from username
     */
    public String generateJwtToken(String username, List<String> roles) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(Instant.now().plusSeconds(expirationInSecond))
                .withClaim("roles", roles)
                .withClaim("tokenType", "access")
                .sign(Algorithm.HMAC256(secretKey));
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date((new Date()).getTime() + refreshTokenExpirationInSecond))
                .withClaim("tokenType", "refresh")
                .sign(Algorithm.HMAC256(secretKey));
    }

    /**
     * Validate JWT token
     */
    public boolean validateJwtToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token is a refresh token
     */
    public boolean isRefreshToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            String tokenType = jwt.getClaim("tokenType").asString();
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if token is an access token
     */
    public boolean isAccessToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            String tokenType = jwt.getClaim("tokenType").asString();
            return "access".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get username from JWT token
     */
    public String getUsernameFromJwtToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getSubject();
    }

    /**
     * Get roles from JWT token
     */
    public List<String> getRolesFromJwtToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("roles").asList(String.class);
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

    public boolean hasRole(User user, UserRole userRole) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == userRole);
    }

}
