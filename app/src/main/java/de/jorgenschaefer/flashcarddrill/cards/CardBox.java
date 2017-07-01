package de.jorgenschaefer.flashcarddrill.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CardBox {
    List<Card> decks[];

    public CardBox(int nDecks) {
        decks = new ArrayList[nDecks];
        for (int i = 0; i < nDecks; i++) {
            decks[i] = new ArrayList<Card>();
        }
    }

    public Card getRandomCardFromDeck(int i) {
        int deckSize = decks[i].size();
        if (deckSize == 0) {
            return null;
        }
        int n = ThreadLocalRandom.current().nextInt(deckSize);
        return decks[i].get(n);
    }

    public int[] getDeckSizes() {
        int[] sizes = new int[decks.length];
        for (int i = 0; i < decks.length; i++) {
            sizes[i] = decks[i].size();
        }
        return sizes;
    }

    public void addCard(int deckNum, Card card) {
        for (List<Card> deck : decks) {
            deck.remove(card);
        }
        if (deckNum >= decks.length) {
            deckNum = decks.length - 1;
        }
        decks[deckNum].add(card);
    }


    public int getFirstNonemptyDeck() {
        for (int i = 0; i < decks.length; i++) {
            if (!decks[i].isEmpty()) {
                return i;
            }
        }
        return 0;
    }
}
