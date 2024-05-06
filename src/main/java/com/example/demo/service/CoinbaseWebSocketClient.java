package com.example.demo.service;

import com.example.demo.strategies.BreakoutStrategy;
import com.example.demo.strategies.RSIStrategy;
import com.example.demo.strategies.SMACrossoverStrategy;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class CoinbaseWebSocketClient extends WebSocketClient {

    private static final long CONNECTION_DURATION = 5 * 60 * 1000;
    private static final long STRATEGY_SWITCH_INTERVAL = 2 * 60 * 1000;

    private double initialAmount = 1000;
    private double currentAmount = initialAmount;
    private Timer timer;
    private Queue<Double> priceHistory = new LinkedList<>();
    @Autowired
    private SMACrossoverStrategy smaCrossoverStrategy;
    @Autowired
    private RSIStrategy rsiStrategy;
    @Autowired
    private BreakoutStrategy breakoutStrategy;

    private Timer switchTimer;
    private int strategyCounter = 0;


    public CoinbaseWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("WebSocket connection opened");
        subscribe();
        startTimer();
        startSwitchTimer();
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        processTickerData(message);
        System.out.println("Current balance: " + currentAmount);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket connection closed");
        determineBestPerformer();
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Error: " + ex.getMessage());
    }

    private void subscribe() {
        String subscribeMessage = "{\"type\":\"subscribe\",\"product_ids\":[\"ETH-USD\",\"ETH-EUR\"],\"channels\":[\"level2\",\"heartbeat\",{\"name\":\"ticker\",\"product_ids\":[\"ETH-BTC\",\"ETH-USD\"]}]}";
        this.send(subscribeMessage);
    }

    private void unsubscribe() {
        String unsubscribeMessage = "{\"type\":\"unsubscribe\",\"channels\":[\"heartbeat\"]}";
        this.send(unsubscribeMessage);
    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                unsubscribe();
                close();
            }
        }, CONNECTION_DURATION);
    }

    private void startSwitchTimer() {
        switchTimer = new Timer();
        switchTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                switchStrategyIfNecessary();
            }
        }, STRATEGY_SWITCH_INTERVAL);
    }

    private void processTickerData(String message) {
        // Parse message and extract price
        double price = parsePriceFromMessage(message);
        if (strategyCounter == 0) {
            smaCrossoverStrategy.processTickerData(price);
        } else if (strategyCounter == 1) {
            rsiStrategy.processTickerData(price);
        } else {
            breakoutStrategy.processTickerData(price);
        }
    }

    private void switchStrategyIfNecessary() {
        if (strategyCounter == 0 && currentAmount < initialAmount) {
            strategyCounter = 1;
            System.out.println("Switching to RSIStrategy");
        } else if (strategyCounter == 1 && currentAmount < initialAmount) {
            strategyCounter = 2;
            System.out.println("Switching to BreakoutStrategy");
        }
        // Stop the switch timer if it's the end of the websocket duration
        if (strategyCounter == 2) {
            switchTimer.cancel();
        }
    }

    private void determineBestPerformer() {
        // Compare the profitability of different strategies and return the best performer
        // For simplicity, let's assume SMA Crossover is the best performer if nothing else is implemented
        System.out.println("Returning with the best performer strategy: SMACrossoverStrategy");
    }

    private double parsePriceFromMessage(String message) {
        try {
            // Parse the message as JSON
            JSONObject json = new JSONObject(message);

            // Check if the message type is "ticker"
            String type = json.optString("type");
            if (!"ticker".equals(type)) {
                return 0.0; // Return 0.0 if the type is not "ticker"
            }

            // Extract the price from the JSON object
            String priceStr = json.getString("price");

            // Parse the price string to double
            return Double.parseDouble(priceStr);
        } catch (JSONException e) {
            System.err.println("Error parsing message: " + e.getMessage());
            return 0.0; // Return 0.0 if parsing fails
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

