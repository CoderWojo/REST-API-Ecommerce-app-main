package pl.wojo.app.ecommerce_backend.api_model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginResponse {
    private Long user_id;

    private String jwt;
}
