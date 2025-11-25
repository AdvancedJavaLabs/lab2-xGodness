package org.itmo;

public record WordFrequency(String text, int encountered) implements Comparable<WordFrequency> {
    @Override
    public int compareTo(WordFrequency wordFrequency) {
        if (wordFrequency.encountered != this.encountered) {
            return Integer.compare(wordFrequency.encountered, this.encountered);
        }
        return wordFrequency.text.compareTo(this.text);
    }
}
