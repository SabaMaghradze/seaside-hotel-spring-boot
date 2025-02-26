package com.seaside.seasidehotel.security.jwt;

import com.seaside.seasidehotel.model.Role;
import com.seaside.seasidehotel.security.user.UserDtls;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;


@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.jwtExpirationTime}")
    private int jwtExpirationTime;

    // generates jwt token for a user
    public String generateJwt(Authentication authentication) {

        // holds authenticated user's information, cast to custom implementation of UserDetails interface
        UserDtls userPrincipal = (UserDtls) authentication.getPrincipal();

        List<String> roles = userPrincipal
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();

        return Jwts.builder() // builds and signs JWT tokens
                .setSubject(userPrincipal.getUsername()) // sets the subject of JWT to the authenticated user's username.
                .claim("roles", roles) // adds custom claims to JWT, later decoded to check user's roles.
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationTime))
                .signWith(key(), SignatureAlgorithm.HS256).compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromToken(String token) {
        return Jwts.parserBuilder() // initializes a JWT parser to process the token.
                .setSigningKey(key()) // This sets the secret key used to sign the JWT. ensures that the token was issued by the correct authority and hasn't been tampered with
                .build() // finalizes the parser construction
                .parseClaimsJws(token) // This decodes and verifies the token. If the token is valid, it extracts the claims (payload) from the JWT.
                .getBody() // This retrieves the claims payload from the token.
                .getSubject(); // Extracts the "sub" (subject) claim, which is typically the username.
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
            return true;
        } catch (MalformedJwtException exc) {
            logger.error("invalid jwt token : {} ", exc.getMessage());
        } catch (ExpiredJwtException exc) {
            logger.error("expired jwt token : {} ", exc.getMessage());
        } catch (UnsupportedJwtException exc) {
            logger.error("unsupported token : {} ", exc.getMessage());
        } catch (IllegalArgumentException exc) {
            logger.error("no claims found : {} ", exc.getMessage());
        }
        return false;
    }
}









