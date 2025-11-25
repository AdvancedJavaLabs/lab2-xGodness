package org.itmo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.itmo.util.EnvReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NameDetector {
    private static final String NAMES_FILENAME = EnvReader.readEnvVar("NAMES_FILENAME");

    private final static Set<String> names;

    static {
        names = new HashSet<>();
        fillNames();
    }

    public static boolean isName(String word) {
        return names.contains(word);
    }

    private static void fillNames() {
        try (InputStream inputStream = SentimentAnalyzer.class.getClassLoader().getResourceAsStream(NAMES_FILENAME)) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String name = reader.readLine();
                while (name != null) {
                    names.add(name.trim().toLowerCase());
                    name = reader.readLine();
                }
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
