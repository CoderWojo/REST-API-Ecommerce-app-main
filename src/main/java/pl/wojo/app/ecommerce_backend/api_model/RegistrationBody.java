package pl.wojo.app.ecommerce_backend.api_model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationBody {
    
    // komentuję @NotNull bo @NotBlank zawiera w sobie NotNull
    @NotBlank(message = "Cannot be blank.")
    private String username;
    
    @NotNull
    @Email(message = "Not correct.")
    @NotBlank(message = "Cannot be blank.")
    @Size(max = 255)
    private String email;
    
    //@Length tylko do stringów, @Size do roznych typow danych, kolekcji tez
    @NotNull
    @NotBlank(message = "Password field cannot be blank.")
    // lookhead sprawdza czy w hasle znajduje sie conajmniej jedna litera, jedna cyfra, i główna część pasująca mówi że dozwolone tylko Litery i cyfry
    @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,32}$")
    private String password;

    @NotBlank
    @NotNull
    @Pattern(regexp = "^[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]{2,30}$", message = "Must be between 2 and 30 characters and contains only letters.")
    private String firstName;
    
    @NotBlank
    @NotNull
    @Pattern(regexp = "^[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ-]{2,30}$", message = "Must be between 2 and 30 characters and can only contains letters and hyphen.")
    private String lastName;
}
