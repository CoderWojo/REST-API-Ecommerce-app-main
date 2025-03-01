package pl.wojo.app.ecommerce_backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {
    
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public EncryptionService() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    }

    public String encode(String password) {
        System.out.println("hash dla " + password + ":" + bCryptPasswordEncoder.encode(password));
        return bCryptPasswordEncoder.encode(password);
    }

    public boolean verify(String rawPassword, String hashedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, hashedPassword);
    }
}
