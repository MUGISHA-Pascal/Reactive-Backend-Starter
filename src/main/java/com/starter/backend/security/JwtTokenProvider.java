package com.starter.backend.security;

import com.starter.backend.models.User;
import com.starter.backend.repository.UserRepository;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiresIn}")
    private int jwtExpirationInMs;

    public Mono<String> generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (GrantedAuthority role : userPrincipal.getAuthorities()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }

        return userRepository.findById(userPrincipal.getId())
                .map(authUser -> {
                    logger.debug("authUser in the Jwt auth Provider: {}", authUser);
                    return Jwts.builder()
                            .setId(authUser.getId().toString())
                            .setSubject(userPrincipal.getId().toString())
                            .claim("authorities", grantedAuthorities)
                            .claim("user", authUser)
                            .setIssuedAt(new Date(System.currentTimeMillis()))
                            .setExpiration(expiryDate)
                            .signWith(SignatureAlgorithm.HS512, jwtSecret)
                            .compact();
                });
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret).build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT Signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT Token", ex);
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT Token", ex);
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT Token", ex);
        } catch (IllegalArgumentException ex) {
            logger.error("JWT Claims String is empty", ex);
        }
        return false;
    }
}
