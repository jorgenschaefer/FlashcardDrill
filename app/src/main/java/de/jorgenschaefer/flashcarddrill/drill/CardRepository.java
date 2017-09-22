package de.jorgenschaefer.flashcarddrill.drill;

import de.jorgenschaefer.flashcarddrill.db.Card;

public interface CardRepository {
    DeckInfo getDeckInfo();

    void moveCard(Card card);

    void insertOrUpdateCard(Card card);

    void clearCards();

    Card getNextCard();
}
