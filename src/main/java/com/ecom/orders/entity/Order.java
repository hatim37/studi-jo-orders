package com.ecom.orders.entity;


import com.ecom.orders.dto.OrderDto;
import com.ecom.orders.enums.OrderStatus;
import com.ecom.orders.model.CartItems;
import com.ecom.orders.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data @Builder @NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private Long amount;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private Long totalAmount;
    private UUID trackingId;
    @Transient
    private User user;
    private Long userId;
    private String secretKey;
    @Transient
    private Collection<CartItems> cartItems;
    @Version
    @Column(nullable = false, columnDefinition = "bigint default 0")
    private long version = 0L;

    public OrderDto getOrderDto(){
        OrderDto orderDto = new OrderDto();
        orderDto.setId(id);
        orderDto.setAmount(amount);
        orderDto.setDate(date);
        orderDto.setOrderStatus(orderStatus);
        orderDto.setTrackingId(trackingId);
        orderDto.setOrderStatus(orderStatus);
        return orderDto;
    }
}
