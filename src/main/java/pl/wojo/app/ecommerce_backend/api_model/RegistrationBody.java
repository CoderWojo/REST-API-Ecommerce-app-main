package pl.wojo.app.ecommerce_backend.api_model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationBody {
    
    @NotBlank(message = "Cannot be blank.")
    private String username;
    
    @Email(message = "Not correct.")
    @NotBlank(message = "Cannot be blank.")
    @Size(max = 255)
    private String email;
    
    //@Length tylko do stringów, @Size do roznych typow danych, kolekcji tez
    @NotBlank(message = "Password field cannot be blank.")
    @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,32}$")
    private String password;

    @NotBlank
    @Pattern(regexp = "^[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]{2,30}$", message = "Must be between 2 and 30 characters and contains only letters.")
    private String firstName;
    
    @NotBlank
    @Pattern(regexp = "^[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ-]{2,30}$", message = "Must be between 2 and 30 characters and can only contains letters and hyphen.")
    private String lastName;
}
