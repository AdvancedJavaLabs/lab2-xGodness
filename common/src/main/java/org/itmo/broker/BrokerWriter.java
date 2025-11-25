package org.itmo.broker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BrokerWriter extends BrokerClient {
    public BrokerWriter(String writeQueue) {
        super(writeQueue);
    }

    public void publish(Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            channel.basicPublish("", super.queue, null, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
