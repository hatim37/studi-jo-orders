package com.ecom.orders.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReviewProductDto {

    private List<ProductDto> productDtoList;
    private Long orderAmount;
    //private byte[] qrCode;
    private List<CartItemsDto> cartItemsDtoList;
}
