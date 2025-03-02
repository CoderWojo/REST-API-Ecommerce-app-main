package pl.wojo.app.ecommerce_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;

import jakarta.mail.MessagingException;
import jakarta.mail.Message.RecipientType;
import jakarta.transaction.Transactional;
import pl.wojo.app.ecommerce_backend.EcommerceBackendApplication;
import pl.wojo.app.ecommerce_backend.api_model.RegistrationBody;
import pl.wojo.app.ecommerce_backend.exeption.EmailAlreadyExistsException;
import pl.wojo.app.ecommerce_backend.exeption.UsernameAlreadyExistsException;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    // dodajemy 3000 zeby nie kolidować z 25 uzywanym przez smtp4dev
    private static ServerSetup smtpSetup = new ServerSetup(ServerSetup.PORT_SMTP+3000, "localhost", ServerSetup.PROTOCOL_SMTP);
    
    // automatyczne zarządzanie cyklem życia, brak koniecznosci pisania greenMail.start(), greenMail.stop()
    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(smtpSetup)
        .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
        .withPerMethodLifecycle(true);  // po każdym teście, wyczyść wiadomości na serwerze pocztowym

    @Autowired
    private Environment environment;
    
    @Value("${test.property}")
    private String testProperty;

    @Test
    public void printAllProperties() {
        System.out.println("=== LISTA WSZYSTKICH PROPERTIES ===");
        Arrays.stream(environment.getActiveProfiles()).forEach(profile -> 
            System.out.println("Profil aktywny: " + profile));

        System.getProperties().forEach((key, value) -> 
            System.out.println(key + " = " + value));

        System.out.println("=== KONKRETNE PROPERTIES ===");
        System.out.println("test.property: " + environment.getProperty("test.property"));
        System.out.println("verification.jwt.unit: " + environment.getProperty("verification.jwt.unit"));

    }

    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {

        System.out.println("Property: " + testProperty);

        RegistrationBody body = RegistrationBody.builder()
            .username("UserA")  // only username is the sam as in the exsisted user in db
            .email("UserServiceTest$testRegisterUser@junit.com")  // class$method ensure uniqueness
            .firstName("FirstName")
            .lastName("LastName")
            .password("MySecretPassword123")
            .build();

            //  ten sam username
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.register(body), "Username should be not taken by second one.");
        
        // tylko ten sam email
        body.setUsername("UserServiceTest$testRegisterUser");
        body.setEmail("UserA@junit.com");
        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(body), "Email should be different.");
        // wszystko unique, process goes successfully

        body.setEmail("UserService$testRegisterUser@junit.com");
        assertDoesNotThrow(() -> userService.register(body), "asercja nie przebiegla poprawnie");

        // sprawdz czy wiadomość o weryfikacji została wysłana na odpowiedni adres EMAIL
        // Serwer zatrzymuje te wiadomosci w sobie i jest w stanie je udostępnić poprzez getReceivedMessages()
        assertEquals(body.getEmail(), greenMailExtension.
            getReceivedMessages()[0].getRecipients(RecipientType.TO)[0].toString());        
    }   

    @Test
    public void testPropertyFileLoaded() {
        assertNotNull(environment.getProperty("test.property"), "application-test.properties nie został załadowany!");
    }
}
