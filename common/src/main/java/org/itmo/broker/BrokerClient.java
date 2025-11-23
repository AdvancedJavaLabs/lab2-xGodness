package org.itmo.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.itmo.util.EnvReader;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public abstract class BrokerClient {
    private final Connection connection;
    protected final Channel channel;
    protected final String queue;
    protected final ObjectMapper objectMapper;

    public BrokerClient(String queue) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(EnvReader.readEnvVar("BROKER_HOST"));
        factory.setUsername(EnvReader.readEnvVar("BROKER_USER"));
        factory.setPassword(EnvReader.readEnvVar("BROKER_PASSWORD"));

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(queue, true, false, false, null);
            this.queue = queue;
            this.objectMapper = new ObjectMapper();
        } catch (IOException | TimeoutException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public void shutdown() {
        try {
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException ex) {
            log.warn(ex.getMessage());
        }
    }
}
