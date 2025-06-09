package com.example.webpushtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class WebPushTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebPushTestApplication.class, args);
    }

}
