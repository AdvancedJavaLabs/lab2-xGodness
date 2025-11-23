package org.itmo;

import lombok.extern.slf4j.Slf4j;
import org.itmo.broker.BrokerReader;
import org.itmo.broker.BrokerWriter;
import org.itmo.util.EnvReader;

@Slf4j
public class Main {
    private static final String READ_QUEUE = EnvReader.readEnvVar("PROCESSOR_READ_QUEUE");
    private static final String WRITE_QUEUE = EnvReader.readEnvVar("PROCESSOR_WRITE_QUEUE");

    private static final BrokerReader brokerReader = new BrokerReader(READ_QUEUE);
    private static final BrokerWriter brokerWriter = new BrokerWriter(WRITE_QUEUE);

    public static void main(String[] args) {
        int processedCount = 0;
        log.info("Ready to accept batches to process");
        try {
            while (true) {
                String[] batch = brokerReader.consume(String[].class);
                ++processedCount;
                log.info("Consumed {} batch", processedCount);
                BatchProcessor.analyzeAndProcess(batch, brokerWriter::publish);
                log.info("Processed {} batch", processedCount);
            }
        } finally {
            brokerReader.shutdown();
            brokerWriter.shutdown();
        }
    }
}
