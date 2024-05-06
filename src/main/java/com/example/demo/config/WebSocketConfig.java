package com.example.demo.config;

import com.example.demo.service.CoinbaseWebSocketClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class WebSocketConfig {

    @Bean
    public CoinbaseWebSocketClient webSocketClient() {
        return new CoinbaseWebSocketClient(URI.create("wss://ws-feed.exchange.coinbase.com"));
    }
}
