package de.jorgenschaefer.flashcarddrill;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jorgenschaefer.flashcarddrill.cards.Card;
import de.jorgenschaefer.flashcarddrill.cards.CardBox;
import de.jorgenschaefer.flashcarddrill.db.CardContract;
import de.jorgenschaefer.flashcarddrill.db.CardDbHelper;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class CardBoxTest {
    private int NUM_DECKS = 5;
    private CardBox box;

    @Before
    public void setUp() {
        CardDbHelper dbHelper = new CardDbHelper(InstrumentationRegistry.getTargetContext());
        box = new CardBox(NUM_DECKS, dbHelper);
    }

    @After
    public void tearDown() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(CardDbHelper.DATABASE_NAME);
    }

    @Test
    public void testShouldCreateEmptyBox() {
        assertArrayEquals(box.getDeckSizes(), new int[]{0, 0, 0, 0, 0});
    }

    @Test
    public void shouldAddCards() {
        Card card = new Card(0, "", "");
        box.addCard(0, card);

        assertArrayEquals(box.getDeckSizes(), new int[]{1, 0, 0, 0, 0});
        assertEquals(box.getRandomCardFromDeck(0), card);
    }

    @Test
    public void shouldNotDuplicateCards() {
        Card card = new Card(0, "", "");
        box.addCard(0, card);
        box.addCard(1, card);

        assertArrayEquals(box.getDeckSizes(), new int[]{0, 1, 0, 0, 0});
        assertEquals(box.getRandomCardFromDeck(1), card);
    }

    @Test
    public void shouldNotPutCardsAfterLastSlot() {
        Card card = new Card(0, "", "");
        box.addCard(10, card);

        assertArrayEquals(box.getDeckSizes(), new int[]{0, 0, 0, 0, 1});
    }

    @Test
    public void shouldReturnNonemptyBucket() {
        Card card = new Card(0, "", "");
        box.addCard(0, card);
        assertEquals(box.getFirstNonemptyDeck(), 0);
        box.addCard(3, card);
        assertEquals(box.getFirstNonemptyDeck(), 3);
    }


    @Test
    public void shouldReturnCards() {
        Card card1 = new Card(0, "", "");
        Card card2 = new Card(1, "", "");
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
