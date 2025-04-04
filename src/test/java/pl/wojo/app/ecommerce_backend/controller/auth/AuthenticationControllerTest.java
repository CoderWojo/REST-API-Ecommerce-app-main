package pl.wojo.app.ecommerce_backend.controller.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;

import pl.wojo.app.ecommerce_backend.api_model.RegistrationBody;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthenticationControllerTest {

    // Aby wstrzykiwanie dzialalo muszę dodać @SpringBootTest
    @Autowired
    private MockMvc mockMvc;

    // na końcu testu wysyłamy poprawne żądanie na /register, więc GreenMail musi zostać uruchomiony
    @RegisterExtension
    private GreenMailExtension greenMail = new GreenMailExtension(new ServerSetup(ServerSetup.PORT_SMTP + 3000, "localhost", "smtp"))
        // dodajemy aConfig zwracający instancję klasy konfiguracyjnej GreenMail i dodajemy użytkownika aby było elegancko    
        .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
        .withPerMethodLifecycle(true);

    // Test dotyczący walidacji
    @Test
    public void testRegister() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        RegistrationBody body = RegistrationBody.builder()
        .email("AuthenticationControllerTest$testRegister@junit.com")
        .username("")
        .password("Pass1234")
        .firstName("Wojciech")
        .lastName("Kowalski")
        .build();
        
        // null username
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().is((HttpStatus.BAD_REQUEST).value()));
        body.setUsername("username");

        // null email
        body.setEmail(null);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().is((HttpStatus.BAD_REQUEST).value()));
        body.setEmail("email@gmail.com");

        // null password
        body.setPassword(null);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().is((HttpStatus.BAD_REQUEST).value()));
        body.setPassword("password123");
        
        // null firstName
        body.setFirstName(null);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().is((HttpStatus.BAD_REQUEST).value()));
        body.setFirstName("firstName");

        // null lastName
        body.setLastName(null);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().is((HttpStatus.BAD_REQUEST).value()));
        body.setLastName("lastName");

        // all "ok"
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated());

        
        // incorrect password, niepoprawne znaki
        body.setPassword("woj1111!!!!");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setPassword("123");

        // incorrect password, za mało znaków
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setPassword("Password123");

        // niepoprawny adres email
        body.setEmail("wojoExample.com");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setEmail("wojo@junit.com");

        // niepoprawny znak w firstName
        body.setFirstName("!23!");
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setFirstName("firstName");

        // niepoprawny lastName
        body.setLastName("!23!");
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setLastName("lastName");
    }

    // w UserServiceTest testowaliśmy samą logikę tj. dla niepoprawnych danych ale
    // nie weryfikowaliśmy czy faktycznie weryfikacja przechodzi poprawnie i czy nie zwraca przypadkiem danych innego usera
    // to sprawdzimy tu

}
