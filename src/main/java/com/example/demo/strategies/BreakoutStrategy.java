package com.example.demo.strategies;

import org.springframework.stereotype.Service;

@Service
public class BreakoutStrategy {
    private double resistanceLevel = 3142.63;
    private double supportLevel = 3056.99;

    public void processTickerData(double price) {
        // Buy signal: Price breaks above resistance level
        if (price > resistanceLevel) {
            System.out.println("Breakout Strategy: Buy signal detected at price " + price);
        }

        // Sell signal: Price breaks below support level
        if (price < supportLevel) {
            System.out.println("Breakout Strategy: Sell signal detected at price " + price);
        }
    }
}

