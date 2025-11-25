package org.itmo;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.itmo.broker.BrokerReader;
import org.itmo.broker.BrokerWriter;
import org.itmo.dto.BatchProcessingResult;
import org.itmo.util.EnvReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    private static final int TOP_N_VALUE = Integer.parseInt(EnvReader.readEnvVar("TOP_N_VALUE"));
    private static final String SOURCE_TEXT_FILENAME = EnvReader.readEnvVar("SOURCE_TEXT_FILENAME");
    private static final int PROCESSOR_REPLICAS = Integer.parseInt(EnvReader.readEnvVar("PROCESSOR_REPLICAS"));
    private static final int SENTENCES_PER_BATCH = Integer.parseInt(EnvReader.readEnvVar("SENTENCES_PER_BATCH"));
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
        Files.createDirectories(Paths.get("results"));
        try (PrintWriter resultWriter = new PrintWriter("results/results-%s.txt".formatted(LocalDateTime.now()), StandardCharsets.UTF_8)) {
            resultWriter.println("COMPUTE TIME: %d".formatted(aggregationResult.getComputeTime()));
            resultWriter.println("PROCESSOR REPLICAS: %d".formatted(PROCESSOR_REPLICAS));
            resultWriter.println("SENTENCES PER BATCH: %d".formatted(SENTENCES_PER_BATCH));
            resultWriter.println("SOURCE FILE : %s".formatted(SOURCE_TEXT_FILENAME));
            resultWriter.println("-".repeat(32));
            resultWriter.println("Word count: %d".formatted(aggregationResult.getWordCount()));
            resultWriter.println("Positive words count: %d".formatted(aggregationResult.getPositiveWordsCount()));
            resultWriter.println("Negative words count: %d".formatted(aggregationResult.getNegativeWordsCount()));
            resultWriter.println("-".repeat(32));

            int cnt = 0;
            WordFrequency wordFrequency;
            while (!aggregationResult.getMostFrequentWords().isEmpty() && cnt < TOP_N_VALUE) {
                wordFrequency = aggregationResult.getMostFrequentWords().poll();
                resultWriter.println("Word %s encountered %d times".formatted(wordFrequency.text(), wordFrequency.encountered()));
                ++cnt;
            }
            resultWriter.println("-".repeat(32));

            cnt = 0;
            while (!aggregationResult.getSentencesLengthQueue().isEmpty() && cnt < TOP_N_VALUE) {
                resultWriter.println(aggregationResult.getSentencesLengthQueue().poll().text());
                resultWriter.println();
                ++cnt;
            }
        }
    }
}
