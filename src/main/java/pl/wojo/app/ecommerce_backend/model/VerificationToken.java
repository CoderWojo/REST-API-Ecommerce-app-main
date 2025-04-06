package pl.wojo.app.ecommerce_backend.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "verification_token")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerificationToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    // auto-commit (default true w Hibernate) - Każde INSERT, UPDATE, SELECT, DELETE jest od razu zatwierdzane (COMMIT) przez PostreSQL,
    // dopóki nie użyjesz @Transactional!
    //wtedy każda operacja wymaga BEGIN; oraz COMMIT; w przeciwnym wypadku nie zostanie wykonana
    @Column(columnDefinition = "TEXT", name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "created_timestamp", nullable = false)
    private LocalDateTime createdTimestamp;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // User może nie kliknąć w odpowiednim czasie linku aktywacyjnego, wtedy tworzymy dla niego nowy jwt
    // Klucz obcy zawsze znajduje sie po stronie 'wiele' i wskazuje na 'jeden'
    @ManyToOne(optional = false)    // nie pozwól na utworzenie 'VerificationToken' LocalUser
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private LocalUser user;

    @JsonIgnore
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalUser getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setUser(LocalUser user) {
        this.user = user;
    }
// Jackson pomylił za przez konwencję JavaBeans czyli To takie zasady, które mówią:
// "jak nazwać metody, żeby narzędzia (np. Jackson, Spring, itd.) wiedziały, że chodzi o jakieś pole klasy."


// @Override
    // public String toString() {
    //     return "VerificationToken [id=" + id + ", token=" + token + ", createdTimestamp=" + createdTimestamp
    //             + ", expiresAt=" + expiresAt + ", user=" + user + "]";
    // }
    
}
