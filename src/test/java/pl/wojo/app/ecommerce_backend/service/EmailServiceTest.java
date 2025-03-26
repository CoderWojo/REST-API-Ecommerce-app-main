package pl.wojo.app.ecommerce_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.internet.MimeMessage;
import pl.wojo.app.ecommerce_backend.model.LocalUser;
import pl.wojo.app.ecommerce_backend.model.VerificationToken;

// Pozwala na działanie Mockito 
@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks    // TWORZY INSTANCJĘ 'EMAILSERVICE' i wstrzykuje do niego mocki
    private EmailServiceImpl emailService;

    private final String fake_from_Address = "from@junit.com";
    private final String fake_TO_Address = "testRecipient@junit.com";
    private final String expectedSubject = "Verify your email to active your account.";


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromAddress", fake_from_Address);
    }

    // 1. czy mail został wysłany Z POPRAWNYM ARG, RAZ
    // 2. treść maila czy jest poprawna, oczekiwana
    @Test
    public void shouldCreateMimeMessage() throws MessagingException {

        // atrapa MimeMessage
        MimeMessage mimeMessage = new MimeMessage((Session)null);

        VerificationToken token = new VerificationToken();
        LocalUser user = new LocalUser();
        user.setEmail(fake_TO_Address);
        token.setUser(user);

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.makeAndSendVerificationMail(token);

        // Przechwytujemy z mocka! MimeMEssage wysłany w argumencie .send(mimeMessage)
        // 5. Przechwytujemy MimeMessage
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender).send(captor.capture());
        MimeMessage capturedMessage = captor.getValue();

        // 6. Tworzymy MimeMessageHelper na podstawie przechwyconej wiadomości
        MimeMessageHelper helper = new MimeMessageHelper(capturedMessage, true, "UTF-8");

        // 7. Sprawdzamy właściwości wiadomości
        assertEquals(fake_TO_Address, helper.getMimeMessage().getRecipients(RecipientType.TO)[0].toString()); // Odbiorca
        assertEquals(fake_from_Address, helper.getMimeMessage().getFrom()[0].toString());
        assertEquals(expectedSubject, helper.getMimeMessage().getSubject()); // Temat

    }    

}
