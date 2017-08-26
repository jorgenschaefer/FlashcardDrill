package de.jorgenschaefer.flashcarddrill.drill;

import java.util.ArrayList;
import java.util.List;

import de.jorgenschaefer.flashcarddrill.db.Card;

public class MockCardRepository implements CardRepository {
    private int NUM_DECKS = 5;
    private List<List<Card>> decks;

    public MockCardRepository() {
        clearCards();
    }

    @Override
    public List<DeckInfo> getDeckInfos() {
        List<DeckInfo> infos = new ArrayList<>();
        for (List<Card> deck : decks) {
            infos.add(new DeckInfo(deck.size(), 0));
        }
        return infos;
    }

    @Override
    public void moveCard(Card card, int deckNum) {
        for (List<Card> deck : decks) {
            deck.remove(card);
        }
        decks.get(deckNum).add(card);
    }

    @Override
    public void insertOrUpdateCard(Card card) {
        for (List<Card> deck : decks) {
            if (deck.remove(card)) {
                deck.add(card);
                return;
            }
        }
        decks.get(0).add(card);
    }

    @Override
    public void clearCards() {
        decks = new ArrayList<>(NUM_DECKS);
        for (int i = 0; i < NUM_DECKS; i++) {
            decks.add(new ArrayList<Card>());
        }
    }

    @Override
    public List<Card> getDeck(int currentDeck) {
        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(decks.get(currentDeck));
        return cards;
    }
}
