package de.jorgenschaefer.flashcarddrill.drill;

import java.util.Date;

public class DeckInfo {
    private final int total;
    private final int due;
    private Date firstDueDate;
    private Date lastDueDate;

    public DeckInfo(int total, int due, Date firstDueDate, Date lastDueDate) {
        this.total = total;
        this.due = due;
        this.firstDueDate = firstDueDate;
        this.lastDueDate = lastDueDate;
    }

    public int getTotal() {
        return total;
    }

    public int getDue() {
        return due;
    }

    public Date getFirstDueDate() {
        return firstDueDate;
    }

    public Date getLastDueDate() {
        return lastDueDate;
    }
}