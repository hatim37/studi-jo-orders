package com.ecom.orders;


import com.ecom.orders.config.RsakeysConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(RsakeysConfig.class)
public class OrdersApplication {


    public static void main(String[] args) {
        SpringApplication.run(OrdersApplication.class, args);
    }

}
