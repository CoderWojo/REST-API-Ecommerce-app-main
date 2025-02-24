package pl.wojo.app.ecommerce_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "product")
@Data
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_description", nullable = false)
    private String short_description;
    
    @Column(name = "long_description", nullable = false)
    private String long_description;

    @Column(name = "price", nullable = false)
    private Double price;

    // Narazie dodam, kiedyś zmodyfikuję
    // orphanRemoval = true, jeśli rodzic przestanie wskazywać na dziecko (np id_dziecka=null), to rekord dziecka zostanie usunięty
    @OneToOne(mappedBy = "product", optional = false, orphanRemoval = true)
    @JsonManagedReference
    @JsonIgnore
    private Inventory inventory;
}
