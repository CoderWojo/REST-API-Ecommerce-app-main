package pl.wojo.app.ecommerce_backend.controller.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.wojo.app.ecommerce_backend.api_model.RegistrationBody;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthenticationControllerTest {

    // Aby wstrzykiwanie dzialalo muszę dodać @SpringBootTest
    @Autowired
    private MockMvc mockMvc;

    // Test dotyczący walidacji
    @Test
    public void testRegister() throws Exception {
        RegistrationBody body = RegistrationBody.builder()
        .email("AuthenticationControllerTest$testRegister@junit.com")
        .username("")
        .password("Pass1234")
        .firstName("Wojciech")
        .lastName("Kowalski")
        .build();

        ObjectMapper objectMapper = new ObjectMapper();
        
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
    }
}
