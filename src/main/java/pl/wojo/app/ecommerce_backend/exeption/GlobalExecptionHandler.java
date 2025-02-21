package pl.wojo.app.ecommerce_backend.exeption;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
}
