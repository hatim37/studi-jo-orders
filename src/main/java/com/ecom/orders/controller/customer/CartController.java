package com.ecom.orders.controller.customer;


import com.ecom.orders.dto.OrderDto;
import com.ecom.orders.dto.PlaceOrderDto;
import com.ecom.orders.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/customer")
public class CartController {


    private final OrderService orderService;

    public CartController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/placeOrder")
    public ResponseEntity<OrderDto> placeOrder(@RequestBody PlaceOrderDto placeOrderDto) throws GeneralSecurityException {

        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(placeOrderDto));
    }


}
