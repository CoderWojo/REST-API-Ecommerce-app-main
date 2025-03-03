package pl.wojo.app.ecommerce_backend.service;

import org.springframework.mail.MailSendException;

import jakarta.mail.MessagingException;
import pl.wojo.app.ecommerce_backend.api_model.LoginBody;
import pl.wojo.app.ecommerce_backend.api_model.LoginResponse;
import pl.wojo.app.ecommerce_backend.api_model.RegistrationBody;
import pl.wojo.app.ecommerce_backend.model.LocalUser;

public interface UserService {

    boolean verifyUser(String token);

    LoginResponse login(LoginBody loginBody) throws MessagingException;

    LocalUser register(RegistrationBody registrationBody) throws MessagingException, MailSendException;

    boolean checkEmailAlreadyExists(String email);

    boolean checkUsernameAlreadyExists(String username);
}
