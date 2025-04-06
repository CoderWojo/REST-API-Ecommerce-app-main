package pl.wojo.app.ecommerce_backend.controller.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import pl.wojo.app.ecommerce_backend.model.WebOrder;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithUserDetails(value = "UserA")    // symuluje wysyłanie żądania jako konkretny USER (username)!!!
    public void testAuthenticatedOrderList() throws Exception {
        mockMvc.perform(get("/orders"))
            .andExpect(status().isOk())
            .andExpect((MvcResult result) -> {
                String json_response = result.getResponse().getContentAsString();
                System.out.println("Json_response to: " + json_response);
                // TypeReference to klasa generyczna którea zapamiętuje typy w czasie kompilacji i przekazuje je dalej Jacksonowi
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                List<WebOrder> orders = mapper.readValue(json_response, new TypeReference<List<WebOrder>> (){});
                // .readTree do niekonwertowania na obiekt klasy tylko do odczytywania pojedynczych pól
                System.out.println("uwaga: " + orders.get(0).getUser().getVerificationTokens().get(0));
                for(WebOrder order: orders) {
                    
                    // sprawdzamy czy wszystkie zamówienia otrzymane w rezultacie należą do UserA
                    assertEquals("UserA", order.getUser().getUsername());  
                }
            });
            /* Jackson robi serializację (Java -> JSON) i odwrotnie
             * Problemem jest deserializacja z JSON do JAVA bo on zaserializował metodę isExpired=true bo Jackson myśli że 
             */
    }

    @Test
    public void testUnauthenticatedOrderList() throws Exception {
        mockMvc.perform(get("/orders"))
            .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }
}
