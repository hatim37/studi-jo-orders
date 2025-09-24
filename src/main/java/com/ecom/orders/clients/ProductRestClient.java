package com.ecom.orders.clients;

import com.ecom.orders.dto.ProductDto;
import com.ecom.orders.model.Product;
import com.ecom.orders.model.User;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "products-service", url = "${products.service.url}")
public interface ProductRestClient {

    @PostMapping("/_internal/productFindListById")
    @CircuitBreaker(name="product", fallbackMethod = "getDefaultProductList")
    List<ProductDto> findListById(@RequestHeader("Authorization") String authorization, @RequestBody List<Long> ids);

    default List<ProductDto> getDefaultProductList(Exception exception) {
        return List.of();
    }

}
