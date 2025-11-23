package org.itmo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BatchProcessingResult {
    private int wordCount;
    private Map<String, Integer> mostFrequentWords;
    private int positiveWordsCount;
    private int negativeWordsCount;
    private Map<String, Integer> sentencesLength;
}
