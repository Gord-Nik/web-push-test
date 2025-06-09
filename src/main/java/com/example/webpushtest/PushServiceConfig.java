package com.example.webpushtest;

import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.PushService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.GeneralSecurityException;

@Configuration
@RequiredArgsConstructor
public class PushServiceConfig {
    private final VapidProperties vapidProperties;

    @Bean
    public PushService initClient() throws GeneralSecurityException {
        return new PushService()
                .setPublicKey(vapidProperties.getPublicKey())
                .setPrivateKey(vapidProperties.getPrivateKey())
                .setSubject(vapidProperties.getSubject());
    }
}
