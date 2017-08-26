package de.jorgenschaefer.flashcarddrill.drill;

public class DeckInfo {
    private int size;
    private long oldest;

    public DeckInfo(int size, long oldestCard) {
        this.size = size;
        this.oldest = oldestCard;
    }

    public int getSize() {
        return size;
    }

    public long getOldest() {
        return oldest;
    }
}
