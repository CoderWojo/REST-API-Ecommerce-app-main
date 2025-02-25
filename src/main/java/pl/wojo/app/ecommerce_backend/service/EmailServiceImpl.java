package pl.wojo.app.ecommerce_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

    // @Override
    // public SimpleMailMessage makeMailMessage() {
    //     SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    //     simpleMailMessage.setFrom(fromAddress);
        
    //     return simpleMailMessage;
    // }

    @Override
    public void makeAndSendVerificationMail(VerificationToken verificationToken) { // user, token, time
        // SimpleMailMessage message = makeMailMessage();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Verify your email to active your account.");
        message.setText("Please follow the link below to verify your email to activate your account.\n" + 
            url + "/auth/verify?token=" + verificationToken.getToken());

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
}
