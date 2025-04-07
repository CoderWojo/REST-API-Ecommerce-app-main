package pl.wojo.app.ecommerce_backend.api_model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeResponse {

    private Long user_id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;
}
