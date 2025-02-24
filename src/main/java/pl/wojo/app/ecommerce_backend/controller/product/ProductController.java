package pl.wojo.app.ecommerce_backend.controller.product;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.wojo.app.ecommerce_backend.model.Product;
import pl.wojo.app.ecommerce_backend.service.ProductService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/products")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getMethodName() {
        return productService.getAllProducts();
    }
    
}
