package org.itmo;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class WordSplitter {
    private char[] sentenceChars = {0};
    private int ptr = 0;
    private final StringBuilder stringBuilder = new StringBuilder();

    public void setSentence(String sentence) {
        sentenceChars = sentence.toCharArray();
        ptr = 0;
    }

    public String nextWord() {
        stringBuilder.setLength(0);

        while (ptr < sentenceChars.length && !Character.isLetterOrDigit(sentenceChars[ptr])) {
            ++ptr;
        }

        while (ptr < sentenceChars.length && Character.isLetterOrDigit(sentenceChars[ptr])) {
            stringBuilder.append(sentenceChars[ptr]);
            ++ptr;
        }

        if (stringBuilder.isEmpty()) {
            return null;
        }

        return stringBuilder.toString();
    }
}