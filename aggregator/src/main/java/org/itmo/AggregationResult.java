package org.itmo;

import lombok.Builder;
import lombok.Getter;

import java.util.PriorityQueue;

@Builder
@Getter
public class AggregationResult {
    private int wordCount;
    private PriorityQueue<WordFrequency> mostFrequentWords;
    private int positiveWordsCount;
    private int negativeWordsCount;
    private String[] sortedSentences;
    private long computeTime;
}

record WordFrequency(String text, int encountered) implements Comparable<WordFrequency> {
    @Override
    public int compareTo(WordFrequency wordFrequency) {
        return Integer.compare(this.encountered, wordFrequency.encountered);
    }
}