package com.ecom.orders.services;

import com.ecom.orders.clients.CartRestClient;
import com.ecom.orders.clients.UserRestClient;
import com.ecom.orders.dto.OrderDto;
import com.ecom.orders.dto.PlaceOrderDto;
import com.ecom.orders.entity.Order;
import com.ecom.orders.enums.OrderStatus;
import com.ecom.orders.model.User;
import com.ecom.orders.repository.OrderRepository;
import com.ecom.orders.response.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final TokenTechnicService tokenTechnicService;
    private final UserRestClient userRestClient;
    private final CartRestClient cartRestClient;

    public OrderService(OrderRepository orderRepository, TokenTechnicService tokenTechnicService, UserRestClient userRestClient, CartRestClient cartRestClient) {
        this.orderRepository = orderRepository;
        this.tokenTechnicService = tokenTechnicService;
        this.userRestClient = userRestClient;
        this.cartRestClient = cartRestClient;
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
        Order order = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.EnCours);
        return order;
    }

    @Transactional
    public OrderDto placeOrder(PlaceOrderDto placeOrderDto) throws NoSuchAlgorithmException {

        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(placeOrderDto.getUserId(), OrderStatus.EnCours);

        User optionalUser = userRestClient.findUserById("Bearer "+this.tokenTechnicService.getTechnicalToken(),placeOrderDto.getUserId());

        if (optionalUser.getId() != null && activeOrder != null) {
            activeOrder.setOrderStatus(OrderStatus.Valider);
            activeOrder.setDate(new Date());
            activeOrder.setSecretKey(this.generateAndEncryptKeyForDB());

            orderRepository.save(activeOrder);

            ResponseEntity<Void> resp = this.cartRestClient.generateQrCde("Bearer "+this.tokenTechnicService.getTechnicalToken(),Map.of("userId", placeOrderDto.getUserId(), "orderId", activeOrder.getId()));
            if (resp.getStatusCode().is2xxSuccessful()) {
                Order newOrder = new Order();
                newOrder.setAmount(0L);
                newOrder.setUserId(activeOrder.getUserId());
                newOrder.setTotalAmount(0L);
                newOrder.setOrderStatus(OrderStatus.EnCours);
                newOrder.setTrackingId(UUID.randomUUID());
                orderRepository.save(newOrder);
                return activeOrder.getOrderDto();
            } else {
                throw new UserNotFoundException("Service indisponible");
            }
        }
        return null;
    }

    public String generateAndEncryptKeyForDB() throws NoSuchAlgorithmException {
        // Génère une clé AES 256 bits
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();
        // Encode en Base64
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<OrderDto> getMyPlacedOrders(Long userId) {
        return orderRepository.findByUserIdAndOrderStatusIn(userId, List.of(OrderStatus.Valider)).stream()
                .map(Order::getOrderDto).collect(Collectors.toList());
    }



}
