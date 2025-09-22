package com.ecom.orders.model;

import com.ecom.orders.dto.CartItemsDto;
import com.ecom.orders.entity.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
public class CartItems {

    private Long id;
    private Long price;
    private Long quantity;
    @ManyToOne()
    private Product product;
    @Transient
    private User user;
    private Long userId;
    @ManyToOne
    private Order order;
    @Lob
    @Column(columnDefinition = "longblob")
    private byte[] qrCode;

    public CartItemsDto getCartDto() {
        CartItemsDto cartItemsDto = new CartItemsDto();
        cartItemsDto.setId(id);
        cartItemsDto.setOrderId(order.getId());
        cartItemsDto.setPrice(price);
        cartItemsDto.setQuantity(quantity);
        cartItemsDto.setProductId(product.getId());
        cartItemsDto.setProductName(product.getName());
        cartItemsDto.setUserId(userId);
        cartItemsDto.setReturnedImg(product.getImg());
        cartItemsDto.setQrCode(qrCode);
        return cartItemsDto;
    }
}
