package pl.wojo.app.ecommerce_backend.service;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationPropertiesTest {
    @Autowired
    private Environment environment;

    @Test
    public void testPropertyFileLoaded() {
        System.out.println("spring.datasource.url: " + environment.getProperty("spring.datasource.url"));
        System.out.println("spring.mail.port: " + environment.getProperty("spring.mail.port"));
        
        assertNotNull(environment.getProperty("test.property"), "Plik application.properties NIE został załadowany!");
    }
}

