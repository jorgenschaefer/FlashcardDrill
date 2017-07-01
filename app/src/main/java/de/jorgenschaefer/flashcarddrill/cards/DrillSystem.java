package de.jorgenschaefer.flashcarddrill.cards;

public class DrillSystem {
    int NUM_DECKS = 5;

    private CardBox box = new CardBox(NUM_DECKS);
    private int currentDeck = 0;
    private Card currentCard;

    public DrillSystem(CardBoxLoader loader) {
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
