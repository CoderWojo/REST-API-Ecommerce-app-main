package pl.wojo.app.ecommerce_backend.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;

import jakarta.annotation.PostConstruct;

@Service
public class JWTService {

    private JWT jwt;

    private Algorithm algorithm;

    private String issuer;

    @Value("${verification.jwt.expirationTime}")
    private long VerificationJWTexpirationTime;

    @Value("${verification.jwt.unit}")
    private String VerificationJWTunit;

    @Value("${jwt.expirationTime}")
    private long JWTexpirationTime;

    @Value("${jwt.unit}")
    private String JWTunit;

    @Value("${jwt.algorithm.secret_key}")  // 1.
    private String secret_key; 

    @PostConstruct // 2.
    public void postConstruct() {
        jwt = new JWT();
        algorithm = Algorithm.HMAC256(secret_key);
        issuer = "Wojo W.";
    }

    public String generateJWT(Long user_id) {
        String JWT_token = jwt.create()
            .withSubject(user_id.toString()) // subject
            .withIssuedAt(new Date())
            .withIssuer(issuer)
            // Tutaj nie możemy wysłać LocalDateTime bo nie jest to nasz obiekt, tylko narzucony przez JWT 
            .withExpiresAt(Date.from(
                LocalDateTime.now()
                    .plus(JWTexpirationTime, ChronoUnit.valueOf(JWTunit))
                    // Musimy zamienić na Zone (ZoneDateTime) aby pozniej zamienic na punkt w czasie czyl Instant
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            ))
            .sign(algorithm);
            
        return "Bearer " + JWT_token;
    }

    public String generateVerificationJWT(String email) {
        String JWT_token = jwt.create()
            .withSubject(email) // subject
            .withIssuedAt(new Date())
            .withIssuer(issuer)
            //TODO: roles
            // TODO: chyba trzeba ExpiresAt!
            .withExpiresAt(Date.from(
                LocalDateTime.now()
                    .plus(VerificationJWTexpirationTime, ChronoUnit.valueOf(VerificationJWTunit))
                    // Musimy zamienić na Zone (ZoneDateTime) aby pozniej zamienic na punkt w czasie czyl Instant
                    .atZone(ZoneId.systemDefault())
                    .toInstant()))
            .sign(algorithm);
            
        return "Bearer " + JWT_token;
    }

    public boolean verifyJWT(String jwtToken) {
        String cleanJwt = null;
        if(jwtToken.startsWith("Bearer "))
            cleanJwt = jwtToken.substring(7);
        else 
            cleanJwt = jwtToken;
    
        try {
            // weryfikacja przechodzi po wszystkich claims po dacie też,
            jwt.require(algorithm)
                // .withSubject(cleanJwt) nie da sie zweryfikowac
                .withIssuer(issuer)
                .build()
                .verify(cleanJwt);
        } catch (JWTDecodeException e) {
            return false;   // niepoprawna sygnatura
        }
        
        return true;
    }

    public Long getId(String token) {
        return Long.parseLong(jwt.decodeJwt(token).getSubject());
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }
}
