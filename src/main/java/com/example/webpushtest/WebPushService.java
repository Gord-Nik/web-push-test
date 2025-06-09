package com.example.webpushtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Encoding;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebPushService {

    private final SubscriptionRepository repo;
    private final PushService client;
    private final ObjectMapper mapper;

    public void sendTo(String userId, Object payload) {
        repo.findByUserId(userId).ifPresent(dto -> {
            try {
                var json = mapper.writeValueAsString(payload);
                var n    = new Notification(
                        dto.getEndpoint(),
                        dto.getKeys().getP256dh(),
                        dto.getKeys().getAuth(),
                        json);

                var resp = client.send(n, Encoding.AES128GCM);
                log.info("Push response: {}", resp.getStatusLine());
                if (resp.getEntity() != null) {
                    log.debug("Push body   : {}", EntityUtils.toString(resp.getEntity()));
                }
            } catch (Exception ex) {
                log.error("Failed to send push to {}: {}", userId, ex.getMessage(), ex);
            }
        });
    }
}
