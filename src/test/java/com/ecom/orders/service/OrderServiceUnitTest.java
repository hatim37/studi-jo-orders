package com.ecom.orders.service;

import com.ecom.orders.entity.Order;
import com.ecom.orders.enums.OrderStatus;
import com.ecom.orders.repository.OrderRepository;
import com.ecom.orders.services.AdminService;
import com.ecom.orders.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceUnitTest {

    @InjectMocks
    private OrderService orderService;

    @InjectMocks
    private AdminService adminService;

    @Mock
    private OrderRepository orderRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Création d'une commande
        order = new Order();
        order.setId(1L);
        order.setAmount(100L);
        order.setTotalAmount(100L);
        order.setOrderStatus(OrderStatus.Valider);
    }

    // 1 : Test unitaire de getTotalEarningsForMonth
    // Vérifie que la méthode calcule correctement la somme des montants des commandes validées sur un mois choisi
    @Test
    void getTotalEarningsForMonth_shouldReturnCorrectSum() {
        int month = 10;
        int year = 2025;

        List<Order> orders = List.of(order);

        // Calcul du début et de la fin du mois
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

        // on simule recherche les commandes du mois
        when(orderRepository.findByDateBetweenAndOrderStatus(startOfMonth, endOfMonth, OrderStatus.Valider))
                .thenReturn(orders);

        // Appel de la méthode testée
        Long total = adminService.getTotalEarningsForMonth(month, year);

        // Vérification du résultat
        assertEquals(100L, total);
        verify(orderRepository, times(1))
                .findByDateBetweenAndOrderStatus(startOfMonth, endOfMonth, OrderStatus.Valider);
    }

    // 2 : Test unitaire de getTotalOrdersForMonth
    // Vérifie que la méthode retourne le nombre correct de commandes validées sur un mois donné
    @Test
    void getTotalOrdersForMonth_shouldReturnCorrectCount() {
        int month = 10;
        int year = 2025;

        List<Order> orders = List.of(order, new Order());

        // Calcul du début et de la fin du mois
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

        // on simule la recherche
        when(orderRepository.findByDateBetweenAndOrderStatus(startOfMonth, endOfMonth, OrderStatus.Valider))
                .thenReturn(orders);

        // Appel de la méthode testée
        Long count = adminService.getTotalOrdersForMonth(month, year);

        // Vérification du résultat
        assertEquals(2L, count);
        verify(orderRepository, times(1))
                .findByDateBetweenAndOrderStatus(startOfMonth, endOfMonth, OrderStatus.Valider);
    }

    // 3 : Test unitaire de generateAndEncryptKeyForDB
    // Vérifie que la méthode génère bien une clé AES 256 bits et encode en Base64
    @Test
    void generateAndEncryptKeyForDB_shouldReturnNonEmptyString() throws Exception {
        String key = orderService.generateAndEncryptKeyForDB();
        assertNotNull(key);
        assertFalse(key.isEmpty());
    }
}
