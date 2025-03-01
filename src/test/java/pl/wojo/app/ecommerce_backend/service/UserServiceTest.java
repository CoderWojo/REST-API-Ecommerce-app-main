package pl.wojo.app.ecommerce_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import pl.wojo.app.ecommerce_backend.api_model.RegistrationBody;
import pl.wojo.app.ecommerce_backend.exeption.EmailAlreadyExistsException;
import pl.wojo.app.ecommerce_backend.exeption.UsernameAlreadyExistsException;

@SpringBootTest
public class UserServiceTest {
    
    // dodajemy 3000 zeby nie kolidować z 25 uzywanym przez smtp4dev
    private static ServerSetup smtpSetup = new ServerSetup(ServerSetup.PORT_SMTP+3000, "localhost", ServerSetup.PROTOCOL_SMTP);
    
    // automatyczne zarządzanie cyklem życia, brak koniecznosci pisania greenMail.start(), greenMail.stop()
    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(smtpSetup)
        .withConfiguration(GreenMailConfiguration.aConfig().withUser("wojowazaXXL", "wojo123"))
        .withPerMethodLifecycle(true);  // po każdym teście, wyczyść wiadomości na serwerze pocztowym

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {
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
        assertEquals(body.getEmail(), greenMailExtension.
            getReceivedMessages()[0].getFrom()[0].toString());        
    }   
}
