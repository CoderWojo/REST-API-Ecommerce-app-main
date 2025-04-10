package pl.wojo.app.ecommerce_backend.model;

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
import lombok.Data;
import lombok.ToString.Exclude;

@Entity
@Table(name = "web_order_quantities")
@Data
public class WebOrderQuantities {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    @JsonIgnore
    private Long id;

    @ManyToOne(optional = false)  // każdy rekord należy do 1 zamówienia
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    @Exclude    // Exclude bo zapętla się gdy wypisujemy np WebOrder z toString
    private WebOrder webOrder;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
