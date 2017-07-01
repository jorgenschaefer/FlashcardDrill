package de.jorgenschaefer.flashcarddrill.cards;

public class Card {
    private int id;
    private String question;
    private String answer;

    public Card(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (other.getClass() != getClass())
            return false;
        Card o = (Card)other;
        return o.getId() == getId();
    }

    public int getId() {
        return id;
    }
}
