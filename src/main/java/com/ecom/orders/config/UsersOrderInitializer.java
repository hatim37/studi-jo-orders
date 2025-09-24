package com.ecom.orders.config;

import com.ecom.orders.clients.UserRestClient;
import com.ecom.orders.entity.Order;
import com.ecom.orders.enums.OrderStatus;
import com.ecom.orders.model.User;
import com.ecom.orders.repository.OrderRepository;
import com.ecom.orders.services.TokenTechnicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsersOrderInitializer {

    private final UserRestClient userRestClient;
    private final OrderRepository orderRepository;
    private final TokenTechnicService tokenTechnicService;

    @Transactional
    public void synchronize() {
        try {
            String token = "Bearer " + tokenTechnicService.getTechnicalToken();

            List<User> users = userRestClient.findAll(token);

            if (users == null || users.isEmpty()) {
                log.warn("Aucun utilisateur trouvé.");
                return;
            }

            log.info("Nombre d'utilisateurs récupérés: {}", users.size());

            for (User user : users) {
                if (user == null || user.getId() == null) continue;

                Order existingOrder = orderRepository.findByUserIdAndOrderStatus(user.getId(), OrderStatus.EnCours);

                if (existingOrder == null) {
                    Order order = Order.builder()
                            .amount(0L)
                            .totalAmount(0L)
                            .userId(user.getId())
                            .orderStatus(OrderStatus.EnCours)
                            .trackingId(UUID.randomUUID())
                            .build();
                    orderRepository.save(order);
                    log.info("Commande créée pour l'utilisateur: {}", user.getEmail());
                } else {
                    log.debug("Commande déjà existante pour l'utilisateur: {}", user.getEmail());
                }
            }

        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation des commandes utilisateurs", e);
        }
    }
}

