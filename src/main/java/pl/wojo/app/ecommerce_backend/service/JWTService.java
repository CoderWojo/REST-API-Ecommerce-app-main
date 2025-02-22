package pl.wojo.app.ecommerce_backend.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.annotation.PostConstruct;

@Service
public class JWTService {

    private JWT jwt;

    private Algorithm algorithm;

    private String issuer;

    private long expiresInSeconds; 

    @Value("${jwt.algorithm.secret_key}")  // 1.
    private String secret_key; 

    @PostConstruct // 2.
    public void postConstruct() {
        jwt = new JWT();
        algorithm = Algorithm.HMAC256(secret_key);
        expiresInSeconds =  60 * 60 * 1000;  // 60*60*1000=1h 60 * 60 * 1000;
        issuer = "Wojo W.";
    }

    public String generateJWT(Long user_id) {
        String JWT_token = jwt.create()
            .withSubject(user_id.toString()) // subject
            .withIssuedAt(new Date())
            .withIssuer(issuer)
            //TODO: roles
            .withExpiresAt(new Date(System.currentTimeMillis() + expiresInSeconds))
            .sign(algorithm);
            
        return "Bearer " + JWT_token;
    }

    public boolean verifyJWT(String jwtToken) {
        String cleanJwt = null;
        if(jwtToken.startsWith("Bearer "))
            cleanJwt = jwtToken.substring(7);
        else 
            cleanJwt = jwtToken;
        
        // weryfikacja przechodzi po wszystkich claims po dacie też, TODO: SPRAWDZ PODAJĄC NIEPOPRAWNY CLAIM (EDIT - DZIAŁA)
        jwt.require(algorithm)
            // .withSubject(cleanJwt) nie da sie zweryfikowac
            .withIssuer(issuer)
            .build()
            .verify(cleanJwt);

        return true;
    }
}
