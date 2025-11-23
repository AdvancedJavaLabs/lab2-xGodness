package org.itmo;

import lombok.extern.slf4j.Slf4j;
import org.itmo.util.EnvReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SentimentAnalyzer {
    private static final String POSITIVE_FILENAME = EnvReader.readEnvVar("POSITIVE_WORDS_FILENAME");
    private static final String NEGATIVE_FILENAME = EnvReader.readEnvVar("NEGATIVE_WORDS_FILENAME");

    private static final Map<String, Integer> dictionary;

    static {
        dictionary = new HashMap<>();
        fillDictionary(POSITIVE_FILENAME, true);
        fillDictionary(NEGATIVE_FILENAME, false);
    }

    public static int analyze(String word) {
        Integer score = dictionary.get(word);
        if (score == null) {
            return 0;
        }
        return score;
    }

    private static void fillDictionary(String filename, boolean isPositiveWords) {
        try (InputStream inputStream = SentimentAnalyzer.class.getClassLoader().getResourceAsStream(filename)) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String word = reader.readLine();
                while (word != null) {
                    dictionary.put(word.trim().toLowerCase(), isPositiveWords ? 1 : -1);
                    word = reader.readLine();
                }
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
