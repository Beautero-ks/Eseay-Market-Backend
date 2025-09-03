package com.ninehub.dreamshops.security.jwt;


import com.ninehub.dreamshops.security.shopUser.ShopUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    @Value("${auth.token.jwtToken}")
    private String jwtToken;

    @Value("${auth.token.expirationInMils}")
    private int expirationTime;

    @Value("${app.jwtRememberMeExpirationMs:2592000000}") // 30 days default
    private long jwtRememberMeExpirationMs;

    public String generateJwtTokenForUser(Authentication authentication) {
        // Get the authenticated user
        ShopUserDetails userPrincipal = (ShopUserDetails) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(userPrincipal.getEmail())
                .claim("id", userPrincipal.getId())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + expirationTime))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

//    public String generateTokenWithCustomExpiry(Authentication authentication, long expiryMs) {
//        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
//        return Jwts.builder()
//                .setSubject(userPrincipal.getUsername())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(new Date().getTime() + expiryMs))
//                .signWith(key(), SignatureAlgorithm.HS256)
//                .compact();
//    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtToken));
    }

    // Extrac the user nme fron the token
    public String getUserNameFromJwtToken(String token){
        return Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // Valiate the token
    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            throw new JwtException(e.getMessage());
        }
    }

    public long getRememberMeJwtExpirationMs() {
        return jwtRememberMeExpirationMs;
    }

    public long getJwtExpirationMs() {
        return expirationTime;
    }

    public String generateTokenWithCustomExpiry(Authentication authentication, long expiryMs) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiryMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
}
