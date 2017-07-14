package de.jorgenschaefer.flashcarddrill.drill;

import java.util.List;

import de.jorgenschaefer.flashcarddrill.db.Card;

public interface CardRepository {
    int[] getDeckSizes();

    void moveCard(Card card, int deck);

    void insertOrUpdateCard(Card card);

    void clearCards();

    List<Card> getDeck(int currentDeck);
}
