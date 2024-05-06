package com.example.demo.strategies;

import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

@Service
public class SMACrossoverStrategy {
    private Queue<Double> priceHistory = new LinkedList<>();
    private double shortTermSMA;
    private double longTermSMA;

    public void processTickerData(double price) {
        priceHistory.add(price);
        if (priceHistory.size() > 200) {
            priceHistory.poll();
        }

        if (priceHistory.size() >= 200) {
            shortTermSMA = calculateSMA(priceHistory, 50);
            longTermSMA = calculateSMA(priceHistory, 200);

            // Buy signal: Short-term SMA crosses above long-term SMA
            if (shortTermSMA > longTermSMA) {
                System.out.println("SMA Crossover: Buy signal detected at price " + price);
            }

            // Sell signal: Short-term SMA crosses below long-term SMA
            if (shortTermSMA < longTermSMA) {
                System.out.println("SMA Crossover: Sell signal detected at price " + price);
            }
        }
    }

    private double calculateSMA(Queue<Double> prices, int period) {
        double sum = 0.0;
        for (double price : prices) {
            sum += price;
        }
        return sum / period;
    }
}

