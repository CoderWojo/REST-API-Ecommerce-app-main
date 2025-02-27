package pl.wojo.app.ecommerce_backend.exeption;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
