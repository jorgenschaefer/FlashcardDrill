package de.jorgenschaefer.flashcarddrill.cards;

import de.jorgenschaefer.flashcarddrill.db.Card;
import de.jorgenschaefer.flashcarddrill.db.CardsDbChangeListener;
import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;

public class DrillSystem {
    private CardsDbHelper dbHelper;
    private Card currentCard;
    private int currentDeck = 0;
    private DrillSystemChangeListener changeListener;

    public DrillSystem(CardsDbHelper dbHelper) {
        this.dbHelper = dbHelper;
        nextCard();
    }

    public void onDbChanged() {
        if (currentCard == null) {
            nextCard();
        }
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
        dbHelper.addCard(currentDeck + 1, currentCard);
        nextCard();
    }

    public void markAnswerWrong() {
        dbHelper.addCard(0, currentCard);
        nextCard();
    }

    public int[] getDeckSizes() {
        return dbHelper.getDeckSizes();
    }

    public int getCurrentDeck() {
        return currentDeck;
    }

    private void nextCard() {
        currentCard = dbHelper.getRandomCardFromDeck(currentDeck);
        if (currentCard == null) {
            currentDeck = dbHelper.getFirstNonemptyDeck();
            currentCard = dbHelper.getRandomCardFromDeck(currentDeck);
        }
        if (changeListener != null) {
            changeListener.onCurrentCardChanged();
            changeListener.onDeckSizesChanged();
        }
    }

    public void setChangeListener(DrillSystemChangeListener changeListener) {
        this.changeListener = changeListener;
    }
}
