package com.ecom.orders.controller;

import com.ecom.orders.entity.Order;
import com.ecom.orders.services.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MicroServiceController {

    private final OrderService orderService;

    public MicroServiceController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(path = "/_internal/order-save")
    public void save(@RequestBody Order order) {
        this.orderService.updateOrderTotal(order);
    }

    @PostMapping(path = "/_internal/order-user")
    public void orderSave(@RequestBody Order order) {
        this.orderService.newOrder(order);
    }

    @PostMapping("/_internal/orderFindUserOrderStatus")
    public Order findByUserIdAndOrderStatus(@RequestBody Map<String, String> mapOrder){
        return this.orderService.findByUserIdAndOrderStatus(mapOrder);
    }
}

