package org.itmo;

public record WordFrequency(String text, int encountered) implements Comparable<WordFrequency> {
    @Override
    public int compareTo(WordFrequency wordFrequency) {
        return Integer.compare(wordFrequency.encountered, this.encountered);
    }
}