package pl.wojo.app.ecommerce_backend.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;  

public class JWTServiceTest {

    private static JWTService jwtService;

    private static final String key = "TESTING_SECRET_KEY";
    
    private final String expected_issuer = "Wojo W.";
    
    private final String expected_subject = "99";

    // w testach umożliwia dostęp do prywatnych pól
    // Dla każdej metody testowej tworzona jest oddzielna instancja klasy testowej z domyslnymi wartosciami pol
    @BeforeEach
    void setUp() {
        jwtService = new JWTService();
        
        // Ustawianie kluczowych wartości
        ReflectionTestUtils.setField(jwtService, "secret_key", key);
        ReflectionTestUtils.setField(jwtService, "algorithm", Algorithm.HMAC256(key));

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
}
