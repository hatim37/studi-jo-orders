package com.ecom.orders.controller.admin;


import com.ecom.orders.dto.AnalyticsResponse;
import com.ecom.orders.dto.OrderDto;
import com.ecom.orders.services.AdminService;
import com.ecom.orders.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminOrderController {

    private final AdminService adminService;

    public AdminOrderController(OrderService orderService, AdminService adminService) {this.adminService = adminService;}

    @GetMapping("/placedOrders")
    public ResponseEntity<List<OrderDto>> getAllPlacedOrders(){
        return ResponseEntity.ok(adminService.getAllPlacedOrders());
    }

    @GetMapping("/order/analytics")
    public ResponseEntity<AnalyticsResponse> getAnalyticsResponse(){
        return ResponseEntity.ok(adminService.calculateAnalytics());
    }

}
