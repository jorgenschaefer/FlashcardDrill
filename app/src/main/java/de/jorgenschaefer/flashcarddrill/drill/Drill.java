package de.jorgenschaefer.flashcarddrill.drill;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.jorgenschaefer.flashcarddrill.db.Card;

public class Drill {
    private static final String STATE_CURRENT_DECK = "currentDeck";
    private static final String STATE_CURRENT_CARDLIST = "currentCardList";
    private final CardRepository repository;

    private int currentDeck = 0;
    private List<Card> currentCardList = new ArrayList<>();
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

    public int getCurrentDeck() {
        return currentDeck;
    }

    public void setCurrentDeck(int i) {
        List<Card> newCards = repository.getDeck(i);
        if (newCards.isEmpty()) {
            return;
        }
        currentDeck = i;
        currentCardList = newCards;
        notifyChange();
    }

    public int[] getDeckSizes() {
        return repository.getDeckSizes();
    }

    public boolean hasCards() {
        return !currentCardList.isEmpty();
    }

    public String getCurrentQuestion() {
        return currentCardList.get(0).getQuestion();
    }

    public String getCurrentAnswer() {
        return currentCardList.get(0).getAnswer();
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
        reload();
    }

    public void onClearCards() {
        repository.clearCards();
        currentDeck = 0;
        currentCardList = new ArrayList<>();
        notifyChange();
    }

    public void setState(Bundle state) {
        currentDeck = state.getInt(STATE_CURRENT_DECK);
        currentCardList = state.getParcelableArrayList(STATE_CURRENT_CARDLIST);
        notifyChange();
    }

    public Bundle getState() {
        Bundle state = new Bundle();
        state.putInt(STATE_CURRENT_DECK, currentDeck);
        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(currentCardList);
        state.putParcelableArrayList(STATE_CURRENT_CARDLIST, cards);
        return state;
    }

    private void nextCard() {
        if (!currentCardList.isEmpty()) {
            currentCardList.remove(0);
        }
        if (currentCardList.isEmpty()) {
            currentDeck = nextDeck(0);
            currentCardList = repository.getDeck(currentDeck);
        }
        notifyChange();
    }

    private int nextDeck(int start) {
        int[] deckSizes = repository.getDeckSizes();
        for (int i = start; i < deckSizes.length; i++) {
            if (deckSizes[i] > 0) {
                return i;
            }
        }
        return 0;
    }

    public void reload() {
        if (currentCardList.isEmpty()) {
            nextCard();
            notifyChange();
        }
    }
}