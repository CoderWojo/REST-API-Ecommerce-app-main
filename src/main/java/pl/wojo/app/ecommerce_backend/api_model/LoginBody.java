package pl.wojo.app.ecommerce_backend.api_model;

import lombok.Data;

@Data
public class LoginBody {
    private String email;
    
    private String password;
}
