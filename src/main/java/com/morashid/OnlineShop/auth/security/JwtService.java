package com.morashid.OnlineShop.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtService - inahusika na kutengeneza, kusoma, na ku-validate JWT tokens.
 * 
 * JWT (JSON Web Token) = token ndogo inayohifadhi data ya user.
 * Ina sehemu 3 zilizotenganishwa kwa "." :
 *   HEADER.PAYLOAD.SIGNATURE
 * 
 * Mfano:
 *   eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQHguY29tIiwicm9sZSI6IkJVWUVSIn0.xxxxx
 */
@Service
public class JwtService {

    /**
     * Secret key - inatumika ku-sign na ku-verify tokens.
     * Inasomwa kutoka application.properties.
     * 
     * MUHIMU: Lazima iwe ndefu (256-bit = characters 32+) kwa HS256.
     * Kwa production, hii iwe environment variable, SI kwenye code!
     */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /**
     * Muda wa token kuisha (milliseconds).
     * Inasomwa kutoka application.properties.
     */
    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Extract username (email) kutoka token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract claim yoyote kutoka token.
     * 
     * @param token          JWT token
     * @param claimsResolver function ya kuchukua claim specific
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Tengeneza token kwa user (bila extra claims).
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Tengeneza token kwa user NA extra claims (mfano: role).
     * 
     * Flow:
     *   1. Unda map ya claims
     *   2. Set subject = username (email)
     *   3. Set issuedAt = sasa
     *   4. Set expiration = sasa + jwtExpiration
     *   5. Sign kwa secret key
     *   6. Return token string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate token.
     * 
     * Checks:
     *   1. Username kwenye token inalingana na userDetails
     *   2. Token haijaisha muda
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Check kama token imeisha muda.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract expiration date kutoka token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract claims ZOTE kutoka token.
     * Hapa ndipo signature ina-verify.
     * Kama signature si sahihi, itatupa exception.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Tengeneza SecretKey kutoka secret string.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}