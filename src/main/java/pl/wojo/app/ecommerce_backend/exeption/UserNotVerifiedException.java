package pl.wojo.app.ecommerce_backend.exeption;

public class UserNotVerifiedException extends RuntimeException {
    private boolean isEmailActive;

    public UserNotVerifiedException(String message, boolean isEmailActive) {
        super(message);
        this.isEmailActive = isEmailActive;
    }

    public boolean getIsEmailActive() {
        return isEmailActive;
    }
}
