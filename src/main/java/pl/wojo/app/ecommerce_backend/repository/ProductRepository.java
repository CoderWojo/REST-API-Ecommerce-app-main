package pl.wojo.app.ecommerce_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.wojo.app.ecommerce_backend.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAll();
}
