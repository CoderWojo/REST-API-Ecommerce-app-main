package pl.wojo.app.ecommerce_backend.service;

import pl.wojo.app.ecommerce_backend.model.LocalUser;
import pl.wojo.app.ecommerce_backend.model.VerificationToken;

public interface VerificationService {
    VerificationToken createVerificationToken(LocalUser user);
}
