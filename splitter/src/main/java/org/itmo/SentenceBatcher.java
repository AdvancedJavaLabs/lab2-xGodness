package org.itmo;

import com.ibm.icu.text.BreakIterator;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.function.Consumer;

@Slf4j
public class SentenceBatcher {

    public static int makeAndProcessBatches(String filename, int sentencesPerBatch, Consumer<String[]> processBatch) {
        int totalBatches = 0;

        String text = readText(filename);
        BreakIterator iter = BreakIterator.getSentenceInstance(Locale.ENGLISH);
        iter.setText(text);

        String[] batch = new String[sentencesPerBatch];
        int curBatchSize = 0;

        int start = iter.first();
        for (int end = iter.next(); end != BreakIterator.DONE; start = end, end = iter.next()) {
            String sentence = text.substring(start, end).trim();
            if (!sentence.isEmpty()) {
                batch[curBatchSize] = sentence;
                ++curBatchSize;
                if (curBatchSize == sentencesPerBatch) {
                    processBatch.accept(batch);
                    batch = new String[sentencesPerBatch];
                    curBatchSize = 0;
                    ++totalBatches;
                }
            }
        }

        if (batch[0] != null) {
            processBatch.accept(batch);
            ++totalBatches;
        }

        return totalBatches;
    }

    private static String readText(String filename) {
        try (InputStream inputStream = SentenceBatcher.class.getClassLoader().getResourceAsStream(filename)) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                StringJoiner sj = new StringJoiner(" ");
                String line = reader.readLine();
                while (line != null) {
                    line = line.replace('\u00A0', ' ')
                            .replaceAll("\\s+", " ")
                            .trim();
                    if (!line.isBlank()) {
                        sj.add(line);
                    }
                    line = reader.readLine();
                }
                return sj.toString();
            }
        } catch (IOException ex) {
            log.error("Something went wrong: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
