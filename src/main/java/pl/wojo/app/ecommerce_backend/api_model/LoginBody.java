package pl.wojo.app.ecommerce_backend.api_model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginBody {
    @Email(message = "Not correct.")
    @NotBlank(message = "Cannot be blank.")
    private String email;
    
    @NotBlank(message = "Password field cannot be blank.")
    @Pattern(regexp = "^(?=.*[A-za-z])(?=.*\\d)[A-za-z\\d]{6,32}$")
    private String password;
}
