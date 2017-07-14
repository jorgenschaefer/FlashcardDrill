package de.jorgenschaefer.flashcarddrill.drill;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;

import de.jorgenschaefer.flashcarddrill.db.Card;

public class Drill {
    private static final String STATE_CURRENT_DECK = "currentDeck";
    private static final String STATE_CURRENT_SIDE = "currentSide";
    private static final String STATE_CURRENT_CARDLIST = "currentCardList";
    private CardRepository repository;

    private int currentDeck = 0;
    private Side currentSide = Side.QUESTION;
    private ArrayList<Card> currentCardList = new ArrayList<>();

    public enum Side {
        QUESTION,
        ANSWER
    }

    public Drill(CardRepository repository) {
        this.repository = repository;
        nextCard();
    }

    public int getCurrentDeck() {
        return currentDeck;
    }

    public int[] getDeckSizes() {
        return repository.getDeckSizes();
    }

    public boolean hasCards() {
        return !currentCardList.isEmpty();
    }

    public Side getCurrentSide() {
        return currentSide;
    }

    public String getCurrentQuestion() {
        return currentCardList.get(0).getQuestion();
    }

    public String getCurrentAnswer() {
        return currentCardList.get(0).getAnswer();
    }

    public void onFlipCard() {
        if (currentSide == Side.QUESTION) {
            currentSide = Side.ANSWER;
        } else {
            currentSide = Side.QUESTION;
        }
    }

    public void onAnswerCorrect() {
        repository.moveCard(currentCardList.get(0), currentDeck + 1);
        nextCard();
    }

    public void onAnswerIncorrect() {
        repository.moveCard(currentCardList.get(0), 0);
        nextCard();
    }

    public void onLoadCards(Iterable<Card> cards) {
        for (Card card : cards) {
            repository.insertOrUpdateCard(card);
        }
        if (currentCardList.isEmpty()) {
            nextCard();
        }
    }

    public void onClearCards() {
        repository.clearCards();
        currentDeck = 0;
        currentSide = Side.QUESTION;
        currentCardList = new ArrayList<>();
    }

    public void setState(Bundle state) {
        currentDeck = state.getInt(STATE_CURRENT_DECK);
        currentSide = (Side)state.getSerializable(STATE_CURRENT_SIDE);
        currentCardList = state.getParcelableArrayList(STATE_CURRENT_CARDLIST);
    }

    public Bundle getState() {
        Bundle state = new Bundle();
        state.putInt(STATE_CURRENT_DECK, currentDeck);
        state.putSerializable(STATE_CURRENT_SIDE, currentSide);
        state.putParcelableArrayList(STATE_CURRENT_CARDLIST, currentCardList);
        return state;
    }

    private void nextCard() {
        if (!currentCardList.isEmpty()) {
            currentCardList.remove(0);
        }
        if (currentCardList.isEmpty()) {
            currentDeck = nextDeck();
            currentCardList = new ArrayList<>();
            for (Card card : repository.getDeck(currentDeck)) {
                currentCardList.add(card);
            }
            Collections.shuffle(currentCardList);
        }
        currentSide = Side.QUESTION;
    }

    private int nextDeck() {
        int[] deckSizes = repository.getDeckSizes();
        for (int i = 0; i < deckSizes.length; i++) {
            if (deckSizes[i] > 0)  {
                return i;
            }
        }
        return 0;
    }
}