package org.itmo;

import lombok.extern.slf4j.Slf4j;
import org.itmo.broker.BrokerReader;
import org.itmo.broker.BrokerWriter;
import org.itmo.util.EnvReader;

@Slf4j
public class Main {
    private static final String BATCHES_INFO_WRITE_QUEUE = EnvReader.readEnvVar("SPLITTER_BATCHES_INFO_WRITE_QUEUE");
    private static final String READ_QUEUE = EnvReader.readEnvVar("SPLITTER_READ_QUEUE");
    private static final String WRITE_QUEUE = EnvReader.readEnvVar("SPLITTER_WRITE_QUEUE");
    private static final String SOURCE_TEXT_FILENAME = EnvReader.readEnvVar("SOURCE_TEXT_FILENAME");
    private static final int SENTENCES_PER_BATCH = Integer.parseInt(EnvReader.readEnvVar("SENTENCES_PER_BATCH"));

    private static final BrokerWriter brokerBatchesInfoWriter = new BrokerWriter(BATCHES_INFO_WRITE_QUEUE);
    private static final BrokerReader brokerReader = new BrokerReader(READ_QUEUE);
    private static final BrokerWriter brokerWriter = new BrokerWriter(WRITE_QUEUE);

    public static void main(String[] args) {
        String message = brokerReader.consume(String.class);
        while (message == null || !message.equals("/start")) {
            message = brokerReader.consume(String.class);
        }
        log.info("Received /start command, starting batching process...");
        int totalBatches = SentenceBatcher.makeAndProcessBatches(SOURCE_TEXT_FILENAME, SENTENCES_PER_BATCH, brokerWriter::publish);
        log.info("Completed batching process, total batches: {}", totalBatches);

        brokerBatchesInfoWriter.publish(totalBatches);

        brokerReader.shutdown();
        brokerWriter.shutdown();
    }
}
