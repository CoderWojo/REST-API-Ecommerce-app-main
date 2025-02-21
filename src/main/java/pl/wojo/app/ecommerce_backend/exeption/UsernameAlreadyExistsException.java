package pl.wojo.app.ecommerce_backend.exeption;

public class UsernameAlreadyExistsException extends RuntimeException{
    public UsernameAlreadyExistsException(String username) {
        super("Username is taken by another user. Please use different one.");
    }
}
