package de.jorgenschaefer.flashcarddrill.db;

public class Card {
    private final int id;
    private final String question;
    private final String answer;

    public Card(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Card other = (Card)obj;
        return this.id == other.id;
    }
}
