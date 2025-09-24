package com.ecom.orders.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalyticsResponse {
    private Long placed;
    private Long currentMonthOrders;
    private Long previousMonthOrders;
    private Long currentMonthEarnings;
    private Long previousMonthEarnings;
    private List<ProductAnalyticsDto> productStats;
}
