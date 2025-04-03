package pl.wojo.app.ecommerce_backend.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import pl.wojo.app.ecommerce_backend.model.VerificationToken;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${email.from}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String url;

    private JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void makeAndSendVerificationMail(VerificationToken verificationToken) throws MessagingException { // user, token, time
        String logoPath = "C:\\Users\\wojo4\\Desktop\\workspace\\REST-API-Ecommerce-app-main-main\\src\\main\\resources\\images\\dzik.jpg";
        String emailHtml = "<div style='font-family: Arial, sans-serif; color: #333;'>"
            + "<h2 style='color: #007bff;'>Witaj!</h2>"
            + "<p>Proszę kliknij poniższy link, aby zweryfikować swój adres e-mail i aktywować konto w sklepie PomocnychDzików:</p>"
            + "<p style='text-align: center; margin: 20px 0;'>"
            + "<a href='" + url + "/auth/verify?token=" + verificationToken.getToken() 
            + "' style='display: inline-block; padding: 12px 24px; font-size: 16px; color: white; background-color: #28a745; text-decoration: none; border-radius: 5px;'>"
            + "Zweryfikuj e-mail</a></p>"
            + "<p>Jeśli nie rejestrowałeś się w naszym serwisie, zignoruj tę wiadomość.</p>"
            + "<p>Pozdrawiamy,<br><strong>Zespół PomocneDziki</strong></p>"
            + "<hr style='border: none; border-top: 1px solid #ddd;'>"
            + "<p style='text-align: center;'><img src=\"cid:logo1\" style='width: 150px;'></p>"
            + "</div>";
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();   // niech Spring Boot utworzy Session na podstawie konfiguracji .yml
        
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setFrom(fromAddress);
            messageHelper.setTo(verificationToken.getUser().getEmail());
            messageHelper.setSubject("Verify your email to active your account.");
            messageHelper.setText(emailHtml, true);
            messageHelper.addInline("logo1", new File(logoPath));
        } catch (MessagingException e) {
            // TODO: handle exception
            throw new MessagingException("We encountered an error while compising your email. Please try again later.", e);
        }
        
        try {
            javaMailSender.send(mimeMessage);
        } catch (MailSendException e) {
            throw new MailSendException("Failed to send verification email to " + verificationToken.getUser().getEmail());
        }
    }
}
