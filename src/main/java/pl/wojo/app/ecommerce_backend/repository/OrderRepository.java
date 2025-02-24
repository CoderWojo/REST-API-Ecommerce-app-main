package pl.wojo.app.ecommerce_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.wojo.app.ecommerce_backend.model.LocalUser;
import pl.wojo.app.ecommerce_backend.model.WebOrder;

public interface OrderRepository extends JpaRepository<WebOrder, Long>{
    List<WebOrder> findByUser(LocalUser user);
}
