package com.example.webpushtest;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SubscriptionRepository {

    private final Map<String, PushSubscriptionDto> store = new ConcurrentHashMap<>();

    public void save(String userId, PushSubscriptionDto dto) {
        store.put(userId, dto);
    }

    public Optional<PushSubscriptionDto> findByUserId(String userId) {
        return Optional.ofNullable(store.get(userId));
    }
}
