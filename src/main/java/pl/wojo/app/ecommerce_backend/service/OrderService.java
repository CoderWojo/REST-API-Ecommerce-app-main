package pl.wojo.app.ecommerce_backend.service;

import java.util.List;

import pl.wojo.app.ecommerce_backend.model.LocalUser;
import pl.wojo.app.ecommerce_backend.model.WebOrder;

public interface OrderService {
    List<WebOrder> getOrdersForLoggedUser(LocalUser user);    
}
