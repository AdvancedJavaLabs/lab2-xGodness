package org.itmo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.itmo.broker.BrokerReader;
import org.itmo.broker.BrokerWriter;
import org.itmo.dto.BatchProcessingResult;
import org.itmo.util.EnvReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
public class Main {
    private static final int TOP_N_VALUE = Integer.parseInt(EnvReader.readEnvVar("TOP_N_VALUE"));
    private static final String BATCHES_INFO_READ_QUEUE = EnvReader.readEnvVar("AGGREGATOR_BATCHES_INFO_READ_QUEUE");
    private static final String BATCHES_READ_QUEUE = EnvReader.readEnvVar("AGGREGATOR_BATCHES_READ_QUEUE");
    private static final String TIME_INFO_WRITE_QUEUE = EnvReader.readEnvVar("AGGREGATOR_TIME_INFO_WRITE_QUEUE");
    private static final String TIME_INFO_READ_QUEUE = EnvReader.readEnvVar("AGGREGATOR_TIME_INFO_READ_QUEUE");

    private static final BrokerReader brokerBatchesInfoReader = new BrokerReader(BATCHES_INFO_READ_QUEUE);
    private static final BrokerReader brokerBatchesReader = new BrokerReader(BATCHES_READ_QUEUE);
    private static final BrokerWriter brokerTimeInfoWriteQueue = new BrokerWriter(TIME_INFO_WRITE_QUEUE);
    private static final BrokerReader brokerTimeInfoReadQueue = new BrokerReader(TIME_INFO_READ_QUEUE);

    public static void main(String[] args) throws IOException {
        int totalBatches = brokerBatchesInfoReader.consume(Integer.class);
        int receivedBatches = 0;

        ResultsAggregator aggregator = new ResultsAggregator();
        BatchProcessingResult batchResult;
        while (receivedBatches < totalBatches) {
            ++receivedBatches;
            log.info("Received {} batch", receivedBatches);
            batchResult = brokerBatchesReader.consume(BatchProcessingResult.class);
            aggregator.merge(batchResult);
        }

        log.info("Completed aggregation, requesting total time");
        brokerTimeInfoWriteQueue.publish("/done");
        long[] times = brokerTimeInfoReadQueue.consume(long[].class);
        long totalTime = times[1] - times[0];
        log.info("Total time: {}", totalTime);

        brokerBatchesInfoReader.shutdown();
        brokerBatchesReader.shutdown();
        brokerTimeInfoWriteQueue.shutdown();
        brokerTimeInfoReadQueue.shutdown();

        AggregationResult aggregationResult = aggregator.collect(totalTime);
        try (PrintWriter resultWriter = new PrintWriter("results-%s.txt".formatted(LocalDateTime.now()), StandardCharsets.UTF_8)) {
            resultWriter.println("Word count: %d".formatted(aggregationResult.getWordCount()));
            resultWriter.println("Positive words count: %d".formatted(aggregationResult.getPositiveWordsCount()));
            resultWriter.println("Negative words count: %d".formatted(aggregationResult.getNegativeWordsCount()));
            resultWriter.println("-".repeat(32));

            int i = 0;
            WordFrequency wordFrequency;
            while (!aggregationResult.getMostFrequentWords().isEmpty() && i < TOP_N_VALUE) {
                wordFrequency = aggregationResult.getMostFrequentWords().poll();
                resultWriter.println("Word %s encountered %d times".formatted(wordFrequency.text(), wordFrequency.encountered()));
                ++i;
            }
            resultWriter.println("-".repeat(32));

            for (String sentence : aggregationResult.getSortedSentences()) {
                resultWriter.println(sentence);
            }
        }
    }
}
