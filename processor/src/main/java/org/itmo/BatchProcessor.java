package org.itmo;

import org.itmo.dto.BatchProcessingResult;
import org.itmo.util.EnvReader;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BatchProcessor {
    private static final String NAME_REPLACEMENT = EnvReader.readEnvVar("NAME_REPLACEMENT");

    public static void analyzeAndProcess(String[] batch, Consumer<BatchProcessingResult> processResult) {
        int wordCount = 0;
        Map<String, Integer> mostFrequentWords = new HashMap<>();
        int positiveWordCount = 0;
        int negativeWordCount = 0;
        Map<String, Integer> sentencesLength = new HashMap<>();

        WordSplitter wordSplitter = new WordSplitter();
        int sentimentScore;

        String word;
        String lowerWord;
        String processedSentence;
        for (String sentence : batch) {
            processedSentence = sentence;
            wordSplitter.setSentence(sentence);

            word = wordSplitter.nextWord();
            while (word != null) {
                lowerWord = word.toLowerCase();
                ++wordCount;
                mostFrequentWords.merge(lowerWord, 1, Integer::sum);
                sentimentScore = SentimentAnalyzer.analyze(lowerWord);
                if (sentimentScore > 0) {
                    ++positiveWordCount;
                } else if (sentimentScore < 0) {
                    ++negativeWordCount;
                }
                if (NameDetector.isName(lowerWord)) {
                    processedSentence = processedSentence.replaceAll(word, NAME_REPLACEMENT);
                }

                word = wordSplitter.nextWord();
            }

            sentencesLength.put(processedSentence, processedSentence.length());
        }

        processResult.accept(
                BatchProcessingResult.builder()
                        .wordCount(wordCount)
                        .mostFrequentWords(mostFrequentWords)
                        .positiveWordsCount(positiveWordCount)
                        .negativeWordsCount(negativeWordCount)
                        .sentencesLength(sentencesLength)
                        .build()
        );
    }
}

