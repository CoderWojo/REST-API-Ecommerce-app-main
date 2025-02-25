package pl.wojo.app.ecommerce_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.wojo.app.ecommerce_backend.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{
    
}
