package com.ecom.orders.controller;

import com.ecom.orders.config.UsersOrderInitializer;
import com.ecom.orders.entity.Order;
import com.ecom.orders.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MicroServiceController {

    private final OrderService orderService;
    private final UsersOrderInitializer usersOrderInitializer;

    public MicroServiceController(OrderService orderService, UsersOrderInitializer usersOrderInitializer) {
        this.orderService = orderService;
        this.usersOrderInitializer = usersOrderInitializer;
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

    @GetMapping("/_internal/orderFindById/{id}")
    public Order findById(@PathVariable Long id){
        return this.orderService.findById(id);
    }

    @PostMapping("/_internal/orders/sync")
    public ResponseEntity<Void> synchronizeOrders() {
        usersOrderInitializer.synchronize();
        return ResponseEntity.ok().build();
    }
}

