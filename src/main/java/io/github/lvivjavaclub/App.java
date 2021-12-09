package io.github.lvivjavaclub;

import dev.failsafe.CircuitBreaker;
import dev.failsafe.Failsafe;
import dev.failsafe.FailsafeExecutor;
import dev.failsafe.Fallback;
import dev.failsafe.RateLimiter;
import dev.failsafe.RetryPolicy;

import java.time.Duration;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        retry(client);
        circuitBreaker(client);
        rateLimiter(client);
        fallback(client);
    }

    private static void fallback(Client client) {
        Fallback<String> fallback = Fallback.of(App::fallbackData);
        System.out.println(Failsafe.with(fallback).get(client::fetchData));
    }

    private static void rateLimiter(Client client) throws InterruptedException {
        RateLimiter<String> rateLimiter = RateLimiter.<String>smoothBuilder(10, Duration.ofSeconds(1)).build();
        FailsafeExecutor<String> with = Failsafe.with(rateLimiter);
        for (int i = 0; i < 20; i++) {
            System.out.println(with.get(client::callApi));
            Thread.sleep(80);
        }
    }

    private static void circuitBreaker(Client client) {
        CircuitBreaker<String> circuitBreaker = CircuitBreaker.<String>builder()
                .handle(IllegalAccessError.class)
                .withFailureThreshold(2, 5)
                .withSuccessThreshold(2)
                .withDelay(Duration.ofMillis(500))
                .onOpen(e -> System.out.println("The circuit breaker was opened"))
                .onClose(e -> System.out.println("The circuit breaker was closed"))
                .onHalfOpen(e -> System.out.println("The circuit breaker was half-opened"))
                .build();
        FailsafeExecutor<String> with = Failsafe.with(circuitBreaker);
        for (int i = 0; i < 50; i++) {
            try {
                String data = with.get(client::tryNotToFail);
                System.out.println(data);
            } catch (Error | Exception e) {
                // ignore
            }
        }
    }

    private static void retry(Client client) {
        RetryPolicy<String> retry = RetryPolicy.<String>builder()
                .handle(IllegalAccessError.class)
                .withDelay(Duration.ofSeconds(2))
                .onFailedAttempt(event -> System.out.println(event.getLastFailure().toString()))
                .withMaxRetries(4)
                .build();
        String data = Failsafe.with(retry).get(client::fetchData);
        System.out.println(data);
    }

    private static String fallbackData() {
        return "Fallback data";
    }
}
