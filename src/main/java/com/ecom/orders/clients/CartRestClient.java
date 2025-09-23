package com.ecom.orders.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "cart-service", url = "${cart.service.url}")
public interface CartRestClient {

    @PostMapping("/_internal/cartItems-qrCode")
    ResponseEntity<Void> generateQrCde(@RequestHeader("Authorization") String authorization, @RequestBody Map<String, Long> qrCode);


}
