package pl.wojo.app.ecommerce_backend.service;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;

public class JWTServiceTest {

    @Mock
    private static JWT jwt;

    @InjectMocks
    private static JWTService jwtService;
    
    // w testach umożliwia dostęp do prywatnych pól
    @BeforeAll
    static void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret_key", "TESTING_SECRET_KEY");
        when(jwt.create()).thenReturn(JWT.Builder.class);
    }

    @Test
    public void shouldGenerateJWT() {
        Long user_id = 99l;

        

    }
}
