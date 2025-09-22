package com.ecom.orders.dto;

import com.ecom.orders.enums.OrderStatus;
import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;
@Data
public class OrderDto {

    private Long id;
    private Date date;
    private Long amount;
    private OrderStatus orderStatus;
    private Long totalAmount;
    private UUID trackingId;
    private Long userId;
    private String userEmail;
    private Collection<CartItemsDto> cartItems;
}
