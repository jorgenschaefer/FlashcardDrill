package de.jorgenschaefer.flashcarddrill.cards;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class CardBoxTest {
    private int NUM_DECKS = 5;
    private CardBox box;

    @Before
    public void setUp() {
        box = new CardBox(NUM_DECKS);
    }

    @Test
    public void shouldCreateEmptyBox() {
        assertArrayEquals(box.getDeckSizes(), new int[]{0, 0, 0, 0, 0});
    }

    @Test
    public void shouldAddCards() {
        Card card = new Card("", "");
        box.addCard(0, card);

        assertArrayEquals(box.getDeckSizes(), new int[]{1, 0, 0, 0, 0});
        assertEquals(box.getRandomCardFromDeck(0), card);
    }

    @Test
    public void shouldNotDuplicateCards() {
        Card card = new Card("", "");
        box.addCard(0, card);
        box.addCard(1, card);

        assertArrayEquals(box.getDeckSizes(), new int[]{0, 1, 0, 0, 0});
        assertEquals(box.getRandomCardFromDeck(1), card);
    }

    @Test
    public void shouldNotPutCardsAfterLastSlot() {
        Card card = new Card("", "");
        box.addCard(10, card);

        assertArrayEquals(box.getDeckSizes(), new int[]{0, 0, 0, 0, 1});
    }

    @Test
    public void shouldReturnNonemptyBucket() {
        Card card = new Card("", "");
        box.addCard(0, card);
        assertEquals(box.getFirstNonemptyDeck(), 0);
        box.addCard(3, card);
        assertEquals(box.getFirstNonemptyDeck(), 3);
    }


    @Test
    public void shouldReturnCards() {
        Card card1 = new Card("", "");
        Card card2 = new Card("", "");
        box.addCard(0, card1);
        box.addCard(1, card2);

        assertEquals(box.getRandomCardFromDeck(0), card1);
        assertEquals(box.getRandomCardFromDeck(1), card2);
    }

    @Test
    public void shouldReturnNullIfNoCards() {
        assertNull(box.getRandomCardFromDeck(0));
    }
}
