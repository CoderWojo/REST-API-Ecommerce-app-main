package pl.wojo.app.ecommerce_backend.service;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application.properties") // 🔹 Wymuszenie pliku
public class ApplicationPropertiesTest {
    @Autowired
    private Environment environment;

    @Test
    public void testPropertyFileLoaded() {
        System.out.println("test.property: " + environment.getProperty("test.property"));
        assertNotNull(environment.getProperty("test.property"), "Plik application.properties NIE został załadowany!");
    }
}

