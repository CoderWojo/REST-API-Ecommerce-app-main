package pl.wojo.app.ecommerce_backend.service;

 import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EncryptionServiceTest {
    
    @Autowired
    private EncryptionService encryptionService;

    @Test
    public void testPasswordExcryption() {
        String password = "myPassword123!";
        String hash = encryptionService.encode(password);
        assertTrue(encryptionService.verify(password, hash), "Hashed password should match original.");
        assertFalse(encryptionService.verify(password + "Bike!", hash), "altered password should not be valid."); 

    }
}
