package com.ecom.orders.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
public class ProductAnalyticsDto {
    private String productName;

    private Long currentMonthQuantity = 0L;
    private Long currentMonthTotal = 0L;

    private Long previousMonthQuantity = 0L;
    private Long previousMonthTotal = 0L;

    private Long totalQuantity = 0L;
    private Long totalAmount = 0L;

    public ProductAnalyticsDto(String productName) {
        this.productName = productName;
    }
}
