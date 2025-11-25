package org.itmo.broker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbitmq.client.DeliverCallback;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BrokerReader extends BrokerClient {
    private final BlockingQueue<String> consumedQueue;

    public BrokerReader(String readQueue) {
        super(readQueue);
        consumedQueue = new LinkedBlockingQueue<>();

        final DeliverCallback callback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            try {
                consumedQueue.put(message);
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
                throw new RuntimeException(ex);
            }
        };

        new Thread(() -> {
            try {
                channel.basicConsume(queue, true, callback, consumerTag -> {
                });
            } catch (IOException ex) {
                log.error("Couldn't receive message from '{}' queue: {}", queue, ex.getMessage());
                throw new RuntimeException(ex);
            }
        }).start();
    }

    public <T> T consume(Class<T> clazz) {
        try {
            String json = consumedQueue.take();
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException | InterruptedException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
