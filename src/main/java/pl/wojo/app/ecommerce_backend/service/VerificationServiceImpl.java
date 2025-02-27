package pl.wojo.app.ecommerce_backend.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pl.wojo.app.ecommerce_backend.model.LocalUser;
import pl.wojo.app.ecommerce_backend.model.VerificationToken;

@Service
public class VerificationServiceImpl implements VerificationService {
    private JWTService jwtService;

    @Value("${verification.jwt.unit}")
    private String unit;

    @Value("${verification.jwt.expirationTime}")
    private long expirationTime;

    public VerificationServiceImpl(JWTService jwtService) {
        this.jwtService = jwtService;
    }
    
    @Override
    public VerificationToken createVerificationToken(LocalUser user) {    
        // wygenerujemy po zweryfikowaniu konta po rejestracji
        VerificationToken verificationToken = VerificationToken.builder()
            .user(user)
            .token((jwtService.generateVerificationJWT(user.getEmail())).substring(7))
            .createdTimestamp(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plus(expirationTime, ChronoUnit.valueOf(unit)))
            .build();
        
            return verificationToken;
    }
    
}
