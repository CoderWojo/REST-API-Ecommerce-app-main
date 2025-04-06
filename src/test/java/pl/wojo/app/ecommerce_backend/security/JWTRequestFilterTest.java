package pl.wojo.app.ecommerce_backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import pl.wojo.app.ecommerce_backend.service.JWTService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JWTRequestFilterTest {
    // narazie sprawdzimy /auth/me
    // a pozniej logowanie z jwt
    private static final String AUTHENTICATED_PATH = "/auth/me";
    
    @Autowired
    private JWTService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testNullToken() throws Exception {
        mockMvc.perform(get(AUTHENTICATED_PATH))
            .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void testBadToken() throws Exception {
        mockMvc.perform(get(AUTHENTICATED_PATH)
                .header("Authorization", "BadTokenThatIsNotValid"))
            .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        
        mockMvc.perform(get(AUTHENTICATED_PATH)
                .header("Authorization", "Bearer BadTokenThatIsNotValid"))
            .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }


    // jwt user moze dostac tylko wtedy gdy sie poprawnie zaloguje
    // u nas nie mozliwa jest taka sytuacja bo jwt dostanie sie po poprawnym zalogowaniu (i rzecz jasna wczesniejszym zweryfikowaniu)
    @Test
    public void testUnverifiedUserAndValidJwt() throws Exception {
        String jwt = jwtService.generateJWT(1l);//userA not verified
        mockMvc.perform(get(AUTHENTICATED_PATH)
                .header("Authorization", jwt))
            .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void testSuccessful() throws Exception {
        String jwt = jwtService.generateJWT(3l); // userC - verified
        mockMvc.perform(get(AUTHENTICATED_PATH)
            .header("Authorization", jwt));
        // .andExpect(status().isOk());
    }
}
