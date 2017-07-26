package de.jorgenschaefer.flashcarddrill.drill;

import java.util.ArrayList;
import java.util.List;

import de.jorgenschaefer.flashcarddrill.db.Card;

public class MockCardRepository implements CardRepository {
    private int[] deckSizes;
    private List<CardMove> cardMoves = new ArrayList<>();
    private List<Card> cards = new ArrayList<>();
    private int numCleared = 0;
    private List<Card> deck;

    public class CardMove {
        Card card;
        int deck;

        public CardMove(Card card, int deck) {
            this.card = card;
            this.deck = deck;
        }
    }

    public void setDeckSizes(int[] deckSizes) {
        this.deckSizes = deckSizes;
    }

    public int getNumCleared() {
        return numCleared;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }

    @Override
    public int[] getDeckSizes() {
        return deckSizes;
    }

    @Override
    public void moveCard(Card card, int deck) {
        cardMoves.add(new CardMove(card, deck));
    }

    @Override
    public void insertOrUpdateCard(Card card) {
        cards.add(card);
    }

    @Override
    public void clearCards() {
        numCleared++;
    }

    @Override
    public List<Card> getDeck(int currentDeck) {
        return deck;
    }
}
