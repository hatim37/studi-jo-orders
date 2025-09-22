package com.ecom.orders.dto;

import lombok.Data;

@Data
public class AnalyticsResponse {
    private Long placed;
    private Long currentMonthOrders;
    private Long previousMonthOrders;
    private Long currentMonthEarnings;
    private Long previousMonthEarnings;


    /*public AnalyticsResponse(Long placed,
                             Long shipped,
                             Long delivered,
                             Long currentMonthOrders,
                             Long previousMonthOrders,
                             Long currentMonthEarnings,
                             Long previousMonthEarnings) {
    }*/
}
