package com.ecom.orders.clients;

import com.ecom.orders.model.CartItems;
import com.ecom.orders.model.User;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "cart-service", url = "${cart.service.url}")
public interface CartRestClient {

    @PostMapping("/_internal/cartItems-qrCode")
    @CircuitBreaker(name="cart", fallbackMethod = "getDefaultCart")
    ResponseEntity<Void> generateQrCde(@RequestHeader("Authorization") String authorization, @RequestBody Map<String, Long> qrCode);

    @GetMapping("/_internal/allCartByQrCodeIsNotNull")
    @CircuitBreaker(name="cart", fallbackMethod = "getListCart")
    List<CartItems> findByQrCodeIsNotNull(@RequestHeader("Authorization") String authorization);

    default List<CartItems> getListCart(Exception exception){
        return List.of();
    }

    default ResponseEntity<Void> getDefaultCart(String authorization, Map<String, Long> qrCode, Throwable throwable) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
