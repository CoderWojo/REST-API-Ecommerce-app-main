package pl.wojo.app.ecommerce_backend.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;  

@SpringBootTest
@AutoConfigureMockMvc
public class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Value("${jwt.algorithm.secret_key}")
    private static String key;
    
    private final String expected_issuer = "Wojo W.";   
    
    private final String expected_subject = "99";   // for shouldGenerateJWT test

    @Value("${jwt.expirationTime}")
    private int JWTexpirationTime;

    @Value("${jwt.unit}")
    private String JWTunit;

    // w testach umożliwia dostęp do prywatnych pól
    // Dla każdej metody testowej tworzona jest oddzielna instancja klasy testowej z domyslnymi wartosciami pol
    @BeforeEach
    void setUp() {
        
        // Ustawianie kluczowych wartości
        // ReflectionTestUtils.setField(jwtService, "secret_key", key);
        // ReflectionTestUtils.setField(jwtService, "algorithm", Algorithm.HMAC256(key));

        // ręcznie wywołujemy postConstruct
        jwtService.postConstruct();
    }

    @Test
    public void shouldGenerateJWT() {
        Long user_id = 99l;
        String resultJwt = jwtService.generateJWT(user_id);

        assertNotNull(resultJwt);
        assertTrue(resultJwt.startsWith("Bearer "));

        DecodedJWT decodedJWT = JWT.decode(resultJwt.substring(7));
        assertEquals(decodedJWT.getSubject(), expected_subject);
        assertEquals(decodedJWT.getIssuer(), expected_issuer);
        assertEquals(decodedJWT.getAlgorithm(), Algorithm.HMAC256(key).getName());
    }

    @Test
    public void shouldVerifyJWT() {
        String jwt = jwtService.generateJWT(1l);

        assertDoesNotThrow(() -> jwtService.verifyJWT(jwt), "This method should not throws an exception.");
        assertTrue(jwtService.verifyJWT(jwt));
    }

    // 1. wysyłamy żądanie z jwt wygenerowanym bez znajomości secret_key --> a nawet nie wysyłamy, tylko już od razu sprawdzamy weryfikację
    @Test
    public void testJWTNotGeneratedByUs() {
        String jwt = JWT.create()
            .withSubject("1")// user_id jako String, tam data nie taka wazna i wgl. wazne ze nie ma odpowiedniego klucza
            .withIssuedAt(new Date())
            .withIssuer(expected_issuer)
            .withExpiresAt(Date.from(
                LocalDateTime.now()
                    .plus(JWTexpirationTime, ChronoUnit.valueOf(JWTunit))
                    // Musimy zamienić na Zone (ZoneDateTime) aby pozniej zamienic na punkt w czasie czyl Instant
                    .atZone(ZoneId.systemDefault())
                    .toInstant()))
            .sign(Algorithm.HMAC256("NotRealSecret")); // podpisuje z kluczem

        assertThrows(SignatureVerificationException.class, () -> JWT.require(jwtService.getAlgorithm()).build().verify(jwt), "verifying jwt with NotRealSecretKey should throw SignatureVerificationException.");
    }

    @Test
    public void testJWTGeneratedByUs() {
        String jwt = JWT.create()
            .withSubject("1")
            .withIssuedAt(new Date())
            .withIssuer(expected_issuer)
            .withExpiresAt(Date.from(
                LocalDateTime.now().plus(JWTexpirationTime, ChronoUnit.valueOf(JWTunit))
                .atZone(ZoneId.systemDefault())
                .toInstant()
            ))
            .sign(Algorithm.HMAC256(key));

    }
}
