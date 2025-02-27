package pl.wojo.app.ecommerce_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.wojo.app.ecommerce_backend.model.LocalUser;
import pl.wojo.app.ecommerce_backend.model.VerificationToken;
import java.util.Optional;


public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{
    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(LocalUser user);
}
