package pl.wojo.app.ecommerce_backend.api_model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginResponse {
    private Long user_id;

    // The jwt token to be used for authentication
    private String jwt;

    // Was the login process successful? 
    private boolean succes;

    private LoginFailureReason failure_reason;

    private String message;

    public static LoginResponse success(Long user_id, String jwt, String message) {
        return LoginResponse.builder()
            .user_id(user_id)
            .jwt(jwt)
            .succes(true)
            .failure_reason(null)
            .message(message)
            .build();
    }

    public static LoginResponse failure(LoginFailureReason reason, String message) {
        return LoginResponse.builder()
            .user_id(null)
            .jwt(null)
            .succes(false)
            .failure_reason(reason)
            .message(message)
            .build();
    }
}
