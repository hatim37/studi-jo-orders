package com.ecom.orders.services;

import com.ecom.orders.entity.Order;
import com.ecom.orders.enums.OrderStatus;
import com.ecom.orders.repository.OrderRepository;
import com.ecom.orders.response.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    @Transactional
    public void updateOrderTotal(Order order) {
        Order setOrder = orderRepository.findById(order.getId()).orElseThrow(()->new UserNotFoundException("echec"));

        setOrder.setAmount(order.getAmount());
        setOrder.setUserId(order.getUserId());
        setOrder.setTotalAmount(order.getTotalAmount());
        orderRepository.save(setOrder);
    }

    public void newOrder(Order order) {
        Order newOrder = new Order();
        newOrder.setAmount(0L);
        newOrder.setOrderStatus(OrderStatus.EnCours);
        newOrder.setTrackingId(UUID.randomUUID());
        newOrder.setUserId(order.getUserId());
        newOrder.setTotalAmount(0L);
        orderRepository.save(newOrder);
    }

    public Order findByUserIdAndOrderStatus(Map<String, String> mapOrder) {
        Long userId = Long.valueOf(mapOrder.get("userId"));
        return orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.EnCours);
    }



}
