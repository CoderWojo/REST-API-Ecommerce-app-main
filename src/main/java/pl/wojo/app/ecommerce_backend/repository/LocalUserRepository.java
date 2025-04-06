package pl.wojo.app.ecommerce_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pl.wojo.app.ecommerce_backend.model.LocalUser;

@Repository
public interface LocalUserRepository extends JpaRepository<LocalUser, Long> {

    //jpql
    @Query("SELECT u.id FROM LocalUser u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<Long> findUserIdByEmailIgnoreCase(String email);

    Optional<LocalUser> findUserByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    Optional<LocalUser> findByEmailIgnoreCase(String email);
}
