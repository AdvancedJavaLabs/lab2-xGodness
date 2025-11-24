package org.itmo;

public record Sentence(String text, int length) implements Comparable<Sentence> {
    @Override
    public int compareTo(Sentence sentence) {
        return Integer.compare(sentence.length, this.length);
    }
}
