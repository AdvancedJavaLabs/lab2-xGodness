package org.itmo;

public record Sentence(String text, int length) implements Comparable<Sentence> {
    @Override
    public int compareTo(Sentence sentence) {
        if (sentence.length != this.length) {
            return Integer.compare(sentence.length, this.length);
        }
        return sentence.text.compareTo(this.text);
    }
}
