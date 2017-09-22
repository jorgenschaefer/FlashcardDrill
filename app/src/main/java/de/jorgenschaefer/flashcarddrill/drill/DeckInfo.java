package de.jorgenschaefer.flashcarddrill.drill;

public class DeckInfo {
    private final int total;
    private final int due;

    public DeckInfo(int total, int due) {
        this.total = total;
        this.due = due;
    }

    public int getTotal() {
        return total;
    }

    public int getDue() {
        return due;
    }
}