package pl.wojo.app.ecommerce_backend.controller.orders;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.wojo.app.ecommerce_backend.model.LocalUser;
import pl.wojo.app.ecommerce_backend.model.WebOrder;
import pl.wojo.app.ecommerce_backend.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
    
    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<WebOrder> getUserOrders(@AuthenticationPrincipal LocalUser user) {
        return orderService.getOrdersForLoggedUser(user);
    }
}
