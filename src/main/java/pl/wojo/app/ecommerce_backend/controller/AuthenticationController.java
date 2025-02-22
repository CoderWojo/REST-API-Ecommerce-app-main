package pl.wojo.app.ecommerce_backend.controller;

import org.springframework.web.bind.annotation.RestController;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<LocalUser> register(@RequestBody @Valid RegistrationBody registrationBody) {
        LocalUser savedUser = userService.register(registrationBody);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(savedUser);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody(required = false) LoginBody loginBody, 
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String jwtFromHeader,
        @RequestHeader HashMap<String, String> headers) {
        
        LoginResponse response = userService.login(loginBody, jwtFromHeader);  // dolaczany jest jwt

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + response.getJwt())
            .body(response);
    }

    @PostMapping("/auth/test")
    public String postMethodName() {
        //TODO: process POST request
        
        return "TO JEST TEST!";
    }
    
}
