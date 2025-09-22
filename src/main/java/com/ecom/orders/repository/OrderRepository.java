package com.ecom.orders.repository;



import com.ecom.orders.entity.Order;
import com.ecom.orders.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByUserIdAndOrderStatus(Long userId, OrderStatus status);

    Order findByUserId(Long id);

    List<Order> findByOrderStatusIn(List<OrderStatus> orderStatusList);

    List<Order> findByUserIdAndOrderStatusIn(Long userId, List<OrderStatus> orderStatusList);

    List<Order> findByDateBetweenAndOrderStatus(Date startOfMonth, Date endOfMonth, OrderStatus orderStatus);

    Long countByOrderStatus(OrderStatus orderStatus);
}
