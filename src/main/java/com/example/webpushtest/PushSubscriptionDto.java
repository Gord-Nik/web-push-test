package com.example.webpushtest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PushSubscriptionDto {

    private String endpoint;
    private Long   expirationTime;
    private Keys   keys;

    @Data
    public static class Keys {
        @JsonProperty("p256dh") private String p256dh;
        @JsonProperty("auth")   private String auth;
    }
}
