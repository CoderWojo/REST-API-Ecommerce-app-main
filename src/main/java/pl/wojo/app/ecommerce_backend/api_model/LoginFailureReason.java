package pl.wojo.app.ecommerce_backend.api_model;

public enum LoginFailureReason {
    INVALID_CREDENTIALS,
    USER_NOT_VERIFIED,
    USER_NOT_VERIFIED_EMAIL_RESENT,
    ACCOUNT_LOCKED,
    ACCESS_DENIED,
    SERVER_ERROR,
    MAIL_SEND_ERROR
}
