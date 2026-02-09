package com.zezame.lipayz.util;

import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class RateLimiterUtil {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final Map<String, Integer> burstMap = new ConcurrentHashMap<>();
    private final Map<String, Instant> cooldownMap = new ConcurrentHashMap<>();

    private static final Duration BASE = Duration.ofMinutes(3);
    private static final Duration MAX_COOLDOWN = Duration.ofMinutes(15);

    public boolean tryConsume(String key) {
        Bucket bucket = resolveBucket(key);
        return bucket.tryConsume(1);
    }

    public Duration onRateLimited(String key) {
        Instant now = Instant.now();

        Instant cooldownEnd = cooldownMap.get(key);

        if (cooldownEnd != null && now.isBefore(cooldownEnd)) {
            return Duration.between(now, cooldownEnd);
        }

        int burst = burstMap.merge(key, 1, Integer::sum);
        Duration cooldown = BASE.multipliedBy(burst);

        if (cooldown.compareTo(MAX_COOLDOWN) > 0) {
            cooldown = MAX_COOLDOWN;
        }

        cooldownMap.put(key, now.plus(cooldown));

        return cooldown;
    }

    public void reset(String key) {
        burstMap.remove(key);
        cooldownMap.remove(key);
    }

    private Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(5)
                        .refillIntervally(5, Duration.ofSeconds(5)))
                .build();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resetAll() {
        log.info("Resetting all rate limiter cache...");
        cache.clear();
        burstMap.clear();
        cooldownMap.clear();
    }
}
