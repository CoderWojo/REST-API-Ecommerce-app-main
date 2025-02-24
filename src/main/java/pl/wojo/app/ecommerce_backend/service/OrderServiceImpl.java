package pl.wojo.app.ecommerce_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import pl.wojo.app.ecommerce_backend.model.LocalUser;
import pl.wojo.app.ecommerce_backend.model.WebOrder;
import pl.wojo.app.ecommerce_backend.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService{

    private OrderRepository repository;
    
    public OrderServiceImpl(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<WebOrder> getOrdersForLoggedUser(LocalUser user) {
        return repository.findByUser(user);
    }
    
}
