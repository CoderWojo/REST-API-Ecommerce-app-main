package pl.wojo.app.ecommerce_backend.service;

import jakarta.mail.MessagingException;
import pl.wojo.app.ecommerce_backend.model.VerificationToken;

public interface EmailService {
    
    void makeAndSendVerificationMail(VerificationToken verificationToken) throws MessagingException;
}
