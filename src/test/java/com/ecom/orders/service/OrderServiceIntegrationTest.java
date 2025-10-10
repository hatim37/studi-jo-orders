package com.ecom.orders.service;

import com.ecom.orders.clients.CartRestClient;
import com.ecom.orders.clients.UserRestClient;
import com.ecom.orders.dto.OrderDto;
import com.ecom.orders.dto.PlaceOrderDto;
import com.ecom.orders.entity.Order;
import com.ecom.orders.enums.OrderStatus;
import com.ecom.orders.model.User;
import com.ecom.orders.repository.OrderRepository;
import com.ecom.orders.response.UserNotFoundException;
import com.ecom.orders.services.OrderService;
import com.ecom.orders.services.TokenTechnicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private TokenTechnicService tokenTechnicService;

    @MockBean
    private UserRestClient userRestClient;

    @MockBean
    private CartRestClient cartRestClient;

    private Order order;
    private User user;

    @BeforeEach
    void setUp() {
        // Création d'un utilisateur fictif
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        // Création d'une commande fictive
        order = new Order();
        order.setId(1L);
        order.setUserId(user.getId());
        order.setAmount(100L);
        order.setTotalAmount(100L);
        order.setOrderStatus(OrderStatus.EnCours);
    }

    // 1 : Mise à jour de la commande existante
    @Test
    void updateOrderTotal_shouldUpdateOrder() {
        // Simulation récupération commande existante
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // Appel de la méthode testée
        orderService.updateOrderTotal(order);

        // Vérification, montant correct et sauvegarde
        assertEquals(100L, order.getAmount());
        verify(orderRepository, times(1)).save(order);
    }

    // 2 : Mise à jour d'une commande inexistante
    @Test
    void updateOrderTotal_shouldThrow_whenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        Order notFoundOrder = new Order();
        notFoundOrder.setId(99L);

        // Vérification, exception levée si commande introuvable
        assertThrows(UserNotFoundException.class, () -> orderService.updateOrderTotal(notFoundOrder));
    }

    // 3 : Création d'une nouvelle commande
    @Test
    void newOrder_shouldCreateOrder() {
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // Appel de la méthode testée
        orderService.newOrder(order);

        // Vérification, méthode save appelée
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // 4 : Recherche de commande par userId et statut
    @Test
    void findByUserIdAndOrderStatus_shouldReturnOrder() {
        Map<String, String> mapOrder = Map.of("userId", "1");
        when(orderRepository.findByUserIdAndOrderStatus(1L, OrderStatus.EnCours)).thenReturn(order);

        // Appel de la méthode testée
        Order result = orderService.findByUserIdAndOrderStatus(mapOrder);

        // Vérification, commande retournée correcte
        assertEquals(order, result);
    }

    // 5 : Placement d'une commande avec succès
    @Test
    void placeOrder_shouldValidateOrderAndCreateNew() throws Exception {
        PlaceOrderDto dto = new PlaceOrderDto();
        dto.setUserId(user.getId());

        // Simulation récupération commande en cours
        when(orderRepository.findByUserIdAndOrderStatus(user.getId(), OrderStatus.EnCours)).thenReturn(order);
        when(userRestClient.findUserById(anyString(), eq(user.getId()))).thenReturn(user);
        when(tokenTechnicService.getTechnicalToken()).thenReturn("token");
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(cartRestClient.generateQrCde(anyString(), anyMap())).thenReturn(ResponseEntity.ok().build());

        // Appel de la méthode testée
        OrderDto result = orderService.placeOrder(dto);

        // Vérification, commande validée et nouvelle commande créée
        assertNotNull(result);
        assertEquals(OrderStatus.Valider, order.getOrderStatus());
        verify(orderRepository, times(2)).save(any(Order.class)); // update + new order
        verify(cartRestClient, times(1)).generateQrCde(anyString(), anyMap());
    }

    // 6 : Placement commande - utilisateur inexistant
    @Test
    void placeOrder_shouldReturnNull_whenUserNotFound() throws Exception {
        PlaceOrderDto dto = new PlaceOrderDto();
        dto.setUserId(user.getId());

        when(orderRepository.findByUserIdAndOrderStatus(user.getId(), OrderStatus.EnCours)).thenReturn(order);
        when(userRestClient.findUserById(anyString(), eq(user.getId()))).thenReturn(new User()); // id null

        // Appel de la méthode testée
        OrderDto result = orderService.placeOrder(dto);

        // Vérification, null car utilisateur invalide
        assertNull(result);
    }

    // 7 : Placement commande - échec génération QR code
    @Test
    void placeOrder_shouldThrow_whenQrCodeFails() throws Exception {
        PlaceOrderDto dto = new PlaceOrderDto();
        dto.setUserId(user.getId());

        when(orderRepository.findByUserIdAndOrderStatus(user.getId(), OrderStatus.EnCours)).thenReturn(order);
        when(userRestClient.findUserById(anyString(), eq(user.getId()))).thenReturn(user);
        when(tokenTechnicService.getTechnicalToken()).thenReturn("token");
        when(cartRestClient.generateQrCde(anyString(), anyMap())).thenReturn(ResponseEntity.status(500).build());

        // Vérification, exception levée si QR code KO
        assertThrows(UserNotFoundException.class, () -> orderService.placeOrder(dto));
    }

    // 8 : Recherche commande par ID existant
    @Test
    void findById_shouldReturnOrder() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // Appel de la méthode testée
        Order result = orderService.findById(order.getId());

        // Vérification, commande retournée
        assertEquals(order, result);
    }

    // 9 : Recherche commande par ID inexistant
    @Test
    void findById_shouldReturnNull_whenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Appel de la méthode testée
        Order result = orderService.findById(99L);

        // Vérification, null retourné
        assertNull(result);
    }

    // 10 : Récupération des commandes validées d'un utilisateur
    @Test
    void getMyPlacedOrders_shouldReturnList() {
        order.setOrderStatus(OrderStatus.Valider);
        when(orderRepository.findByUserIdAndOrderStatusIn(user.getId(), List.of(OrderStatus.Valider)))
                .thenReturn(List.of(order));

        // Appel de la méthode testée
        List<OrderDto> result = orderService.getMyPlacedOrders(user.getId());

        // Vérification, liste retournée avec commande correcte
        assertEquals(1, result.size());
        assertEquals(order.getId(), result.get(0).getId());
    }
}
