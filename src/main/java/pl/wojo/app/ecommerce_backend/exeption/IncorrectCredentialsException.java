package pl.wojo.app.ecommerce_backend.exeption;

public class IncorrectCredentialsException extends RuntimeException{
    public IncorrectCredentialsException(String message) {
        super(message);
    }
}
