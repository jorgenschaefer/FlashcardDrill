package de.jorgenschaefer.flashcarddrill.db;

import java.util.Date;

public class Card {
    private final int id;
    private final String question;
    private final String answer;
    private int deck;
    private long updatedAt;
    private long dueAt;

    private static int DAYS = 24 * 60 * 60 * 1000;

    public Card(int id, String question, String answer) {
        this(id, question, answer, 0, 0);
    }

    public Card(int id, String question, String answer, int deck, long updatedAt) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.deck = deck;

        setUpdatedAt(updatedAt);
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getDeck() {
        return deck;
    }

    public void markCorrect() {
        deck++;
        setUpdatedAt();
    }

    public void markIncorrect() {
        deck = 0;
        setUpdatedAt();
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt() {
        setUpdatedAt(new Date().getTime());
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
        this.dueAt = (long)(updatedAt + Math.pow(2, deck) * DAYS);
    }

    public long getDueAt() {
        return dueAt;
    }
}
