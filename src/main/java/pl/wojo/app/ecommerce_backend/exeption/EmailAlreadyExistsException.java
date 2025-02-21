package pl.wojo.app.ecommerce_backend.exeption;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email is already taken! Choose another one.");
    }
}
