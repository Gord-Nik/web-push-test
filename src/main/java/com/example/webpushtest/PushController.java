package com.example.webpushtest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/push")
public class PushController {

    private final SubscriptionRepository repo;
    private final WebPushService         push;

    public PushController(SubscriptionRepository repo, WebPushService push) {
        this.repo = repo;
        this.push = push;
    }

    @PostMapping("/subscribe/{userId}")
    public ResponseEntity<Void> subscribe(@PathVariable String userId,
                                          @RequestBody PushSubscriptionDto dto) {
        log.info("dto={}", dto);
        repo.save(userId, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notify/{userId}")
    public ResponseEntity<Void> notify(@PathVariable String userId,
                                       @RequestBody Map<String, String> body) throws InterruptedException {
        Thread.sleep(5000L);
        log.info("body={}", body);
        push.sendTo(userId, body);
        return ResponseEntity.accepted().build();
    }
}

