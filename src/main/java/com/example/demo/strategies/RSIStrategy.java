package com.example.demo.strategies;

import org.springframework.stereotype.Service;

@Service
public class RSIStrategy {
    private int rsiIndicator = 40;

    public void processTickerData(double price) {
        // Buy signal: RSI crosses above 30 (indicating oversold conditions)
        if (rsiIndicator > 30) {
            System.out.println("RSI Strategy: Buy signal detected at price: " + price);
        }

        // Sell signal: RSI crosses below 70 (indicating overbought conditions)
        if (rsiIndicator < 70) {
            System.out.println("RSI Strategy: Buy signal detected at price: " + price);
        }
    }
}

