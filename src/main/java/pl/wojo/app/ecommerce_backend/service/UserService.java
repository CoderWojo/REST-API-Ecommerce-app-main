package pl.wojo.app.ecommerce_backend.service;

import pl.wojo.app.ecommerce_backend.api_model.LoginBody;
import pl.wojo.app.ecommerce_backend.api_model.LoginResponse;
import pl.wojo.app.ecommerce_backend.api_model.RegistrationBody;
import pl.wojo.app.ecommerce_backend.model.LocalUser;

public interface UserService {

    LoginResponse login(LoginBody loginBody, String jwtFromHeader);

    LocalUser register(RegistrationBody registrationBody);

    boolean checkEmailAlreadyExists(String email);

    boolean checkUsernameAlreadyExists(String username);
}
