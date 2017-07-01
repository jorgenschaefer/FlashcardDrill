package de.jorgenschaefer.flashcarddrill.cards;

import de.jorgenschaefer.flashcarddrill.db.CardDbHelper;

public class DrillSystem {
    int NUM_DECKS = 5;

    private int currentDeck = 0;
    private CardBox box;
    private Card currentCard;

    public DrillSystem(CardBoxLoader loader, CardDbHelper dbHelper) {
        box = new CardBox(NUM_DECKS, dbHelper);
        if (loader != null) {
            loader.load(box);
        }
        nextCard();
    }

    public String getCurrentQuestion() {
        if (currentCard == null) {
            return null;
        }
        return currentCard.getQuestion();
    }

    public String getCurrentAnswer() {
        if (currentCard == null) {
            return null;
        }
        return currentCard.getAnswer();
    }

    public void markAnswerCorrect() {
        box.addCard(currentDeck + 1, currentCard);
        nextCard();
    }

    public void markAnswerWrong() {
        box.addCard(0, currentCard);
        nextCard();
    }

    public int[] getDeckSizes() {
        return box.getDeckSizes();
    }

    public int getCurrentDeck() {
        return currentDeck;
    }

    private void nextCard() {
        currentCard = box.getRandomCardFromDeck(currentDeck);
        if (currentCard == null) {
            currentDeck = box.getFirstNonemptyDeck();
            currentCard = box.getRandomCardFromDeck(currentDeck);
        }
    }
}
