package org.itmo;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import org.itmo.dto.BatchProcessingResult;

public class ResultsAggregator {
    private int wordCount;
    private final Map<String, Integer> mostFrequentWords;
    private int positiveWordsCount;
    private int negativeWordsCount;
    private final PriorityQueue<Sentence> sentencesLengthQueue;

    public ResultsAggregator() {
        wordCount = 0;
        mostFrequentWords = new HashMap<>();
        positiveWordsCount = 0;
        negativeWordsCount = 0;
        sentencesLengthQueue = new PriorityQueue<>();
    }

    public void merge(BatchProcessingResult result) {
        wordCount += result.getWordCount();
        result.getMostFrequentWords().forEach((k, v) -> mostFrequentWords.merge(k, v, Integer::sum));
        positiveWordsCount += result.getPositiveWordsCount();
        negativeWordsCount += result.getNegativeWordsCount();
        result.getSentencesLength().forEach((k, v) -> sentencesLengthQueue.add(new Sentence(k, v)));
    }

    public AggregationResult collect(long computeTime) {
        return AggregationResult.builder()
                .wordCount(wordCount)
                .mostFrequentWords(
                        mostFrequentWords.entrySet()
                                .stream()
                                .map(e -> new WordFrequency(e.getKey(), e.getValue()))
                                .collect(Collectors.toCollection(PriorityQueue::new))
                )
                .positiveWordsCount(positiveWordsCount)
                .negativeWordsCount(negativeWordsCount)
                .sentencesLengthQueue(sentencesLengthQueue)
                .computeTime(computeTime)
                .build();
    }
}
