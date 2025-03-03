package pl.wojo.app.ecommerce_backend.controller.auth;

import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import pl.wojo.app.ecommerce_backend.api_model.LoginBody;
import pl.wojo.app.ecommerce_backend.api_model.LoginResponse;
import pl.wojo.app.ecommerce_backend.api_model.RegistrationBody;
import pl.wojo.app.ecommerce_backend.model.LocalUser;
import pl.wojo.app.ecommerce_backend.service.UserService;

import java.util.HashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<LocalUser> register(@RequestBody @Valid RegistrationBody registrationBody) throws MessagingException{
        LocalUser savedUser = userService.register(registrationBody);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(savedUser);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody(required = false) @Valid LoginBody loginBody, 
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String jwtFromHeader,
        @RequestHeader HashMap<String, String> headers) throws MessagingException {
        
        LoginResponse response = userService.login(loginBody);  // dolaczany jest jwt
        
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + response.getJwt())
            .body(response);
    }

    @PostMapping("/verify") // /auth/verify?token=371287463hjadfhaf
    public ResponseEntity<String> verify(@RequestParam String token) {
        System.out.println("ELOOOOOOOo");
        userService.verifyUser(token);

        return ResponseEntity.status(HttpStatus.OK).body("User verified successfully!");
    }
    

    @GetMapping("/me")
    public LocalUser profile(@AuthenticationPrincipal LocalUser me) {
        return me;
    }
}
