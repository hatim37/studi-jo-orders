package com.ecom.orders.services;

import com.ecom.orders.clients.CartRestClient;
import com.ecom.orders.clients.ProductRestClient;
import com.ecom.orders.dto.AnalyticsResponse;
import com.ecom.orders.dto.OrderDto;
import com.ecom.orders.dto.ProductAnalyticsDto;
import com.ecom.orders.dto.ProductDto;
import com.ecom.orders.entity.Order;
import com.ecom.orders.enums.OrderStatus;
import com.ecom.orders.model.CartItems;
import com.ecom.orders.repository.OrderRepository;
import com.ecom.orders.response.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminService {

    private final OrderRepository orderRepository;
    private final CartRestClient cartRestClient;
    private final ProductRestClient productRestClient;
    private final TokenTechnicService tokenTechnicService;

    public AdminService(OrderRepository orderRepository, CartRestClient cartRestClient, ProductRestClient productRestClient, TokenTechnicService tokenTechnicService) {
        this.orderRepository = orderRepository;
        this.cartRestClient = cartRestClient;
        this.productRestClient = productRestClient;
        this.tokenTechnicService = tokenTechnicService;
    }

    public List<OrderDto> getAllPlacedOrders() {
        List<Order> orderList = orderRepository.findByOrderStatusIn(List.of(OrderStatus.Valider));
        return orderList.stream().map(item ->{
            OrderDto dto = new OrderDto();
            dto.setId(item.getId());
            dto.setOrderStatus(item.getOrderStatus());
            dto.setDate(item.getDate());
            dto.setUserId(item.getUserId());
            dto.setTotalAmount(item.getTotalAmount());
            dto.setAmount(item.getAmount());
            dto.setTrackingId(item.getTrackingId());

            /*User user = userRestClient.findUserById("Bearer "+this.tokenTechnicService.getTechnicalToken(),item.getUserId());
            log.info(user.getName());
            if (user.getId() == null) {
                throw new UserNotFoundException("Service indisponible");
            }
            dto.setUserEmail(user.getEmail());*/

            return dto;

        }).collect(Collectors.toList());
    }

    public AnalyticsResponse calculateAnalytics() {
        log.info("analytics go");

        LocalDate currentDate = LocalDate.now();
        LocalDate previousMonthday = currentDate.minusMonths(1);

        Long currentMonthOrders = getTotalOrdersForMonth(currentDate.getMonthValue(), currentDate.getYear());
        Long previousMonthOrders = getTotalOrdersForMonth(previousMonthday.getMonthValue(), previousMonthday.getYear());

        Long currentMonthEarnings = getTotalEarningsForMonth(currentDate.getMonthValue(), currentDate.getYear());
        Long previousMonthEarnings = getTotalEarningsForMonth(previousMonthday.getMonthValue(), previousMonthday.getYear());

        Long placed = orderRepository.countByOrderStatus(OrderStatus.Valider);
        log.info("placed: " + placed);

        List<ProductAnalyticsDto> productAnalyticsDtos = this.getProductStatsByMonth();
        AnalyticsResponse analyticsResponse = new AnalyticsResponse();
        analyticsResponse.setPlaced(placed);

        analyticsResponse.setCurrentMonthOrders(currentMonthOrders);
        analyticsResponse.setPreviousMonthOrders(previousMonthOrders);
        analyticsResponse.setCurrentMonthEarnings(currentMonthEarnings);
        analyticsResponse.setPreviousMonthEarnings(previousMonthEarnings);
        analyticsResponse.setProductStats(productAnalyticsDtos);

        return analyticsResponse;

    }

    private Long getTotalEarningsForMonth(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date startOfMonth = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        Date endOfMonth = calendar.getTime();

        List<Order> orders = orderRepository.findByDateBetweenAndOrderStatus(startOfMonth, endOfMonth, OrderStatus.Valider);

        Long sum = 0L;
        for (Order order : orders) {
            sum+= order.getAmount();
        }

        return sum;
    }

    public Long getTotalOrdersForMonth(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date startOfMonth = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        Date endOfMonth = calendar.getTime();

        List<Order> orders = orderRepository.findByDateBetweenAndOrderStatus(startOfMonth, endOfMonth, OrderStatus.Valider);

        return (long) orders.size();
    }

    public List<ProductAnalyticsDto> getProductStatsByMonth() {
        LocalDate currentDate = LocalDate.now();
        LocalDate previousMonthDate = currentDate.minusMonths(1);

        //Récupérer liste CartItems validés
        List<CartItems> items = cartRestClient.findByQrCodeIsNotNull("Bearer "+this.tokenTechnicService.getTechnicalToken());

        //if (items != null && !items.isEmpty()) {
            //classer par productId
            Map<Long, List<CartItems>> itemsByProduct = items.stream()
                    .collect(Collectors.groupingBy(CartItems::getProductId));

            List<Long> productIds = new ArrayList<>(itemsByProduct.keySet());

            List<ProductDto> products = productRestClient.findListById(
                    "Bearer " + this.tokenTechnicService.getTechnicalToken(), productIds
            );
            Map<Long, ProductDto> productMap = products.stream()
                    .collect(Collectors.toMap(ProductDto::getId, Function.identity()));

            List<ProductAnalyticsDto> statsList = new ArrayList<>();

            // Calculer les stats par produit
            for (Map.Entry<Long, List<CartItems>> entry : itemsByProduct.entrySet()) {
                Long productId = entry.getKey();
                List<CartItems> productItems = entry.getValue();

                ProductDto product = productMap.get(productId);
                if (product == null) continue;

                ProductAnalyticsDto stats = new ProductAnalyticsDto(product.getName());

                for (CartItems ci : productItems) {
                    // Récupérer la date
                    Order order = orderRepository.findById(ci.getOrderId()).orElse(null);
                    if (order == null) continue;

                    LocalDate date = order.getDate().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();

                    long quantity = ci.getQuantity();
                    long amount = ci.getQuantity() * ci.getPrice();

                    // Faire le total
                    stats.setTotalQuantity(stats.getTotalQuantity() + quantity);
                    stats.setTotalAmount(stats.getTotalAmount() + amount);

                    // Classer par mois en cours
                    if (date.getMonthValue() == currentDate.getMonthValue()
                            && date.getYear() == currentDate.getYear()) {
                        stats.setCurrentMonthQuantity(stats.getCurrentMonthQuantity() + quantity);
                        stats.setCurrentMonthTotal(stats.getCurrentMonthTotal() + amount);
                    }

                    // Classer par mois précédent
                    if (date.getMonthValue() == previousMonthDate.getMonthValue()
                            && date.getYear() == previousMonthDate.getYear()) {
                        stats.setPreviousMonthQuantity(stats.getPreviousMonthQuantity() + quantity);
                        stats.setPreviousMonthTotal(stats.getPreviousMonthTotal() + amount);
                    }
                }

                statsList.add(stats);
            }
            return statsList;
        //}
        //else {
            //throw new UserNotFoundException("Service indisponible");
        //}

    }
}
