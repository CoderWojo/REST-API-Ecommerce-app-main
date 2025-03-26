package pl.wojo.app.ecommerce_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static pl.wojo.app.ecommerce_backend.api_model.LoginBody.createLoginBody;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;

import jakarta.mail.MessagingException;
import jakarta.mail.Message.RecipientType;
import jakarta.transaction.Transactional;
import pl.wojo.app.ecommerce_backend.api_model.LoginBody;
import pl.wojo.app.ecommerce_backend.api_model.LoginResponse;
import pl.wojo.app.ecommerce_backend.api_model.RegistrationBody;
import pl.wojo.app.ecommerce_backend.exeption.EmailAlreadyExistsException;
import pl.wojo.app.ecommerce_backend.exeption.IncorrectCredentialsException;
import pl.wojo.app.ecommerce_backend.exeption.UserNotVerifiedException;
import pl.wojo.app.ecommerce_backend.exeption.UsernameAlreadyExistsException;
import pl.wojo.app.ecommerce_backend.model.LocalUser;

@SpringBootTest
public class UserServiceTest {

    private static RegistrationBody usernameInUse;
    private static RegistrationBody emailInUse;
    private static RegistrationBody unique;

    private static LoginBody invalidPassword;
    private static LoginBody nonExistedEmail;
    private static LoginBody notVerified;
    private static LoginBody notVerifiedSendNew;
    private static LoginBody validLogin;

    @Autowired
    private UserServiceImpl userService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    // Bez tej adnotacji, GreenMail nie zostanie uruchomiony -> Spring Boot nie połączy się z smtp -> email weryfikacyjny nie zostanie wysłany
    @RegisterExtension
    private GreenMailExtension greenMailExtension = new GreenMailExtension(
        new ServerSetup(ServerSetup.PORT_SMTP + 3000, "localhost", ServerSetup.PROTOCOL_SMTP)
    ).withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
     .withPerMethodLifecycle(true);

    @BeforeAll
    static void setUp() {
        usernameInUse = RegistrationBody.builder()
            .username("UserA")
            .email("UserServiceTest$testRegisterUser@junit.com")
            .firstName("FirstName")
            .lastName("LastName")
            .password("MySecretPassword123")
            .build();

        emailInUse = RegistrationBody.builder()
            .username("UserServiceTest$testRegisterUser")
            .email("UserA@junit.com")
            .firstName("FirstName")
            .lastName("LastName")
            .password("MySecretPassword123")
            .build();

        unique = RegistrationBody.builder()
            .username("UserServiceTest$testRegisterUser@junit.com")
            .email("test3uniqueEmail@junit.com")
            .firstName("FirstName")
            .lastName("LastName")
            .password("MySecretPassword123")
            .build();

        invalidPassword = createLoginBody("UserA@junit.com", "invalidPassword123");
        nonExistedEmail = createLoginBody("nonExistedEmail@junit.com", "Password123");
        notVerified = createLoginBody("UserA@junit.com", "Password123");
        notVerifiedSendNew = createLoginBody("UserB@junit.com", "Password123");
        validLogin = createLoginBody("UserC@junit.com", "Password123"); // weryfikacja wykonana       
    }

    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.register(usernameInUse));
        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(emailInUse));

        assertDoesNotThrow(() -> {
            LocalUser testResultLocalUser = userService.register(unique);

            assertEquals(unique.getEmail(), testResultLocalUser.getEmail());
            assertEquals(unique.getUsername(), testResultLocalUser.getUsername());
            assertEquals(unique.getFirstName(), testResultLocalUser.getFirstName());
            assertEquals(unique.getLastName(), testResultLocalUser.getLastName());

            assertTrue(passwordEncoder.matches("MySecretPassword123", testResultLocalUser.getPassword()));
        });

        assertEquals(unique.getEmail(), greenMailExtension.getReceivedMessages()[0].getRecipients(RecipientType.TO)[0].toString());
    }

    @Test
    public void testLoginFailsWhenInvalidPassword()  {
        // 1. invalid password
        assertThrows(IncorrectCredentialsException.class, () -> userService.login(invalidPassword));
    }

    @Test
    public void testLoginFailsWhenNonExistentEmail() {
        // 2. invalid email
        assertThrows(IncorrectCredentialsException.class, () -> userService.login(nonExistedEmail));
    }

    @Test
    @Transactional
    public void testLoginFailsWhenUserNotVerifiedAndTokenActive() throws MessagingException {
        // 3. not verified, token active
        try {
            userService.login(notVerified);
            assertTrue(false); // never
        } catch (UserNotVerifiedException e) {
            // sprawdzamy dodatkowo czy pole 'resend'=false
            assertTrue(e.getIsEmailActive());
        }
    }

    @Test
    @Transactional
    public void testLoginFailsWhenUserNotVerifiedAndTokenExpired() throws MessagingException {
        // 4. not verified, token has expired
        try {
            userService.login(notVerifiedSendNew);
            assertTrue(false); // never
        } catch (UserNotVerifiedException e) {
            // sprawdzamy dodatkowo czy pole 'resend'=false
            assertFalse(e.getIsEmailActive());
        }   
    }

    @Test
    public void testLoginSucceedsWhenEverythingIsCorrect() throws MessagingException {
        // 5. everything correct
        LoginResponse response = userService.login(validLogin); 
        assertNotNull(response, "LoginResponse should not be null.");
        assertNotNull(response.getJwt(), "JWT should not be null.");
        assertFalse(response.getJwt().isEmpty(), "JWT should not be empty.");
    }
}

