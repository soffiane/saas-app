package com.boudissa.saasapp.security;

import com.boudissa.saasapp.config.JwtProperties;
import com.boudissa.saasapp.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtProperties jwtProperties;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    void init() {
        try {
            //KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            //privateKey = (PrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(jwtProperties.getPrivateKeyPath().getBytes()));
            //publicKey = (PublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(jwtProperties.getPublicKeyPath().getBytes()));
            privateKey = loadPrivateKey(jwtProperties.getPrivateKeyPath());
            publicKey = loadPublicKey(jwtProperties.getPublicKeyPath());
            log.info("JWT service initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize JWT service", e);
            throw new RuntimeException("Failed to initialize JWT service", e);
        }
    }

    public String generateToken(@Nonnull final String tenantId, @Nonnull final String user, final String role) {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());
        return Jwts.builder()
                .subject(user)
                .claim("role", role)
                .claim("tenantId", tenantId)
                .issuedAt(new Date())
                .expiration(expirationTime)
                .issuer("saas-app")
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public String getUserIdFromToken(final String token) {
        final Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public String getTenantIdFromToken(final String token) {
        final Claims claims = getClaimsFromToken(token);
        return claims.get("tenantId", String.class);
    }

    public String getRoleFromToken(final String token) {
        final Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    public boolean isTokenExpired(final String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public boolean validateToken(final String token) {
        try {
            Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Invalid token");
        } catch (UnauthorizedException e) {
            throw new UnauthorizedException("Token is not signed");
        } catch (MalformedJwtException e) {
            throw new UnauthorizedException("Token is malformed");
        } catch (UnsupportedJwtException e) {
            throw new UnauthorizedException("Token is unsupported");
        } catch (SecurityException e) {
            throw new UnauthorizedException("Invalid JWT signature");
        }
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private PublicKey loadPublicKey(String publicKeyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try (InputStream is = JwtService.class.getResourceAsStream(publicKeyPath)) {
            if (is == null) {
                throw new IllegalArgumentException("Public key not found");
            }

            final String key = new String(is.readAllBytes());
            final String publicKeyString = key.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");
            final X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        }
    }

    private PrivateKey loadPrivateKey(String privateKeyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try (InputStream is = JwtService.class.getResourceAsStream(privateKeyPath)) {
            if (is == null) {
                throw new IllegalArgumentException("Private key not found");
            }

            final String key = new String(is.readAllBytes());
            final String privateKeyString = key.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");
            final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString));
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        }
    }
}
