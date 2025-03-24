package pl.wojo.app.ecommerce_backend.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import pl.wojo.app.ecommerce_backend.model.LocalUser;
import pl.wojo.app.ecommerce_backend.model.VerificationToken;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceTest {

    private static String unit_value = "HOURS";
    private static long expirationTime_value = 1l;

    @Mock   // Tworzy mocka dla JWTService
    private JWTService jwtService;

    @InjectMocks    // Automatycznie wstrzykuje mocki do testowej klasy
    private static VerificationService verificationService;

    @BeforeAll
    static void setUp() {
        verificationService = new VerificationServiceImpl(null);
        ReflectionTestUtils.setField(verificationService, "unit", unit_value);
        ReflectionTestUtils.setField(verificationService, "expirationTime", expirationTime_value);
    }

    @Test 
    void shouldCreateVerificationToken() {
        LocalUser user = LocalUser.builder()
            .email("test@example.com")
            .build();

        String fakeJwt = "Bearer faketoken123";
        when(jwtService.generateVerificationJWT(user.getEmail())).thenReturn(fakeJwt);

        VerificationToken token = verificationService.createVerificationToken(user);

        assertNotNull(token);
        assertEquals(user, token.getUser());
        assertEquals(fakeJwt.substring(7), token.getToken());
        assertNotNull(token.getCreatedTimestamp());
        assertNotNull(token.getExpiresAt());

        assertEquals(token.getCreatedTimestamp().plus(1l, ChronoUnit.HOURS), 
            token.getExpiresAt());

        // verify ONLY mocked objects, czyli czy verificationService poprawnie, z odpowiednimi arg wywołuje RAZ metodę 
        verify(jwtService, times(1)).generateVerificationJWT(user.getEmail());
    }
}
