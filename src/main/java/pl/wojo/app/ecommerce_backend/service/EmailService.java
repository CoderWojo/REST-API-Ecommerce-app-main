package pl.wojo.app.ecommerce_backend.service;

import pl.wojo.app.ecommerce_backend.model.VerificationToken;

public interface EmailService {
    
    void makeAndSendVerificationMail(VerificationToken verificationToken);
}
