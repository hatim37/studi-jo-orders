package com.ecom.orders.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private final UsersOrderInitializer usersOrderInitializer;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        usersOrderInitializer.synchronize();
    }
}
