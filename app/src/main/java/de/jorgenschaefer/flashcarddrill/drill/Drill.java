package de.jorgenschaefer.flashcarddrill.drill;

import java.util.Date;

import de.jorgenschaefer.flashcarddrill.db.Card;

public class Drill {
    private static final String STATE_CURRENT_DECK = "currentDeck";
    private final CardRepository repository;

    private Card currentCard = null;
    private DeckInfo info = null;
    private Runnable onChangeListener;

    public Drill(CardRepository repository) {
        this.repository = repository;
        nextCard();
    }

    public void setOnChangeListener(Runnable onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    private void notifyChange() {
        if (onChangeListener != null) {
            onChangeListener.run();
        }
    }

    public boolean hasCards() {
        DeckInfo info = repository.getDeckInfo();
        return info.getTotal() > 0;
    }

    public boolean hasDueCards() {
        return currentCard != null;
    }

    public String getCurrentQuestion() {
        return currentCard.getQuestion();
    }

    public String getCurrentAnswer() {
        return currentCard.getAnswer();
    }

    public void onAnswerCorrect() {
        currentCard.markCorrect();
        repository.moveCard(currentCard);
        nextCard();
    }

    public void onAnswerIncorrect() {
        currentCard.markIncorrect();
        repository.moveCard(currentCard);
        nextCard();
    }

    public void onClearCards() {
        repository.clearCards();
        info = repository.getDeckInfo();
        currentCard = null;
        notifyChange();
    }

    public void reload() {
        if (currentCard == null) {
            nextCard();
        }
    }

    public int getNumberOfDueCards() {
        return info.getDue();
    }

    public int getNumberOfTotalCards() {
        return info.getTotal();
    }

    private void nextCard() {
        info = repository.getDeckInfo();
        currentCard = repository.getNextCard();
        notifyChange();
    }

    public int getCurrentDeck() {
        return currentCard.getDeck();
    }

    public Date getCurrentDueDate() {
        return new Date(currentCard.getDueAt());
    }
}