package pl.wojo.app.ecommerce_backend.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;

@Entity
@Table(name = "local_user")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LocalUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @OrderBy("created_timestamp DESC")
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<VerificationToken> verificationTokens;

    
    // Problem w tym że tabela zawiera rekordy ktore nie mają tej kolumny czyli niejako ich wartosci są null
    // a my okreslilismy ze nullable=false
    @Column(name = "email_verified", nullable = false)
    private boolean isEmailVerified = false;

    // Jeśli usuniesz encję podrzędną (dziecko) z kolekcji w encji 
    // nadrzędnej (rodzic) to Hibernate automatycznie usunie ją z bazy danych
    // Ładowanie LAZY, po pobraniu podstawowych informacji o użytkowniku (repository.findById(user_id)) Hibernate zamyka sesję po ich pobraniu.
    // Później gdy niejawnie jest wywyoływana user.getAddresses(), Hibernate nie ma już aktywnej sesji 
    // @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Exclude
    @JsonIgnore
    private List<Address> addresses;    
}
