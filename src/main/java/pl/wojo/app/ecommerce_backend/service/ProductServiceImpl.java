package pl.wojo.app.ecommerce_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import pl.wojo.app.ecommerce_backend.model.Product;
import pl.wojo.app.ecommerce_backend.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService{
    private ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Product> getAllProducts() {
        return repository.findAll();
    }


}
