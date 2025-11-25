package org.itmo;

import java.util.PriorityQueue;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AggregationResult {
    private int wordCount;
    private PriorityQueue<WordFrequency> mostFrequentWords;
    private int positiveWordsCount;
    private int negativeWordsCount;
    private PriorityQueue<Sentence> sentencesLengthQueue;
    private long computeTime;
}