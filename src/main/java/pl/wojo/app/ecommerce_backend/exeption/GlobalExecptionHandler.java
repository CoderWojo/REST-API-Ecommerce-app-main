package pl.wojo.app.ecommerce_backend.exeption;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.mail.MessagingException;
import pl.wojo.app.ecommerce_backend.api_model.LoginFailureReason;
import pl.wojo.app.ecommerce_backend.api_model.LoginResponse;

@ControllerAdvice("pl.wojo.app.ecommerce_backend.controller")
public class GlobalExecptionHandler {
    
    @ExceptionHandler(exception = UsernameAlreadyExistsException.class)
    public ResponseEntity<HashMap<String, Object>> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e) {
        HashMap<String, Object> errorHashMap = new HashMap<>();
        HttpStatus statusCode = HttpStatus.CONFLICT;
        
        errorHashMap.put("timestamp", LocalDateTime.now());
        errorHashMap.put("status", statusCode + "(" + statusCode.getReasonPhrase() + ")");
        errorHashMap.put("error", e.getMessage());

        return new ResponseEntity<HashMap<String,Object>>(errorHashMap, statusCode);
    }

    @ExceptionHandler(exception = EmailAlreadyExistsException.class)
    public ResponseEntity<HashMap<String, Object>> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        HashMap<String, Object> errorHashMap = new HashMap<>();
        HttpStatus statusCode = HttpStatus.CONFLICT;

        errorHashMap.put("timestamp", LocalDateTime.now());
        errorHashMap.put("status", statusCode.value() + "(" + statusCode.getReasonPhrase() + ")");
        errorHashMap.put("error", e.getMessage());

        return new ResponseEntity<>(errorHashMap, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<HashMap<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        HashMap<String, Object> errorResponseBody = new HashMap<>();
        int statusCode = HttpStatus.BAD_REQUEST.value();

        errorResponseBody.put("timestamp", LocalDateTime.now());
        errorResponseBody.put("status", statusCode + "(" + HttpStatus.BAD_REQUEST.getReasonPhrase() + ")");

        String firstError = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .map((FieldError field) -> field.getField() + ": " + field.getDefaultMessage())// rozpakój Optional<FieldError>
            .get();   // .map() dalej zwraca Optional<T> więc jeśli wartość jest nieobecna to zwróć wyjątek
        
        errorResponseBody.put("error", firstError);

        return ResponseEntity.badRequest()
            .body(errorResponseBody);
    }
    //test it
    @ExceptionHandler(exception = UserNotVerifiedException.class)
    public ResponseEntity<LoginResponse> handleUserNotVerifiedException(UserNotVerifiedException e) {
        LoginResponse response;
        if(e.getIsEmailActive()) {
            response = LoginResponse.failure(LoginFailureReason.USER_NOT_VERIFIED, "Please verify your account, you have a mail message on your mailbox.");
        } else {
            response = LoginResponse.failure(LoginFailureReason.USER_NOT_VERIFIED_EMAIL_RESENT, "Please verify your account, we just sent a new link allowing you to verify.");
        }

        // użytkownik nie spełnił wszystkich warunków logowania
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    //test it
    @ExceptionHandler(exception = MailSendException.class)
    public ResponseEntity<LoginResponse> handleMailSendException(MailSendException e) {
        LoginResponse response = LoginResponse.failure(LoginFailureReason.MAIL_SEND_ERROR, "An email cannot be send...");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(exception = UserAlreadyVerifiedException.class)
    public ResponseEntity<String> handleUserAlreadyVerifiedException(UserAlreadyVerifiedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(exception = TokenNotFoundException.class)
    public ResponseEntity<String> handleTokenNotFoundException(TokenNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(exception = IncorrectCredentialsException.class)
    public ResponseEntity<String> handleIncorrectCredentialsException(IncorrectCredentialsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    
    @ExceptionHandler(exception = MessagingException.class)
    public ResponseEntity<String> handleMessagingException(MessagingException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
