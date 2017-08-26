package de.jorgenschaefer.flashcarddrill.db;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class CardsDbHelperTest {
    private CardsDbHelper dbHelper;

    @Before
    public void setUp() {
        dbHelper = new CardsDbHelper(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void tearDown() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(CardsDbContract.DATABASE_NAME);
    }

    @Test
    public void getDeck() {
        Card card1 = new Card(1, "", "");
        Card card2 = new Card(2, "", "");
        Card card3 = new Card(3, "", "");
        dbHelper.insertOrUpdateCard(card1);
        dbHelper.insertOrUpdateCard(card2);
        dbHelper.insertOrUpdateCard(card3);
        dbHelper.moveCard(card2, 1);
        List<Card> cards = dbHelper.getDeck(0);
        assertTrue(cards.contains(card1));
        assertFalse(cards.contains(card2));
        assertTrue(cards.contains(card3));
    }

    @Test
    public void getDeckSortsByAge() {
        Card card1 = new Card(1, "", "");
        Card card2 = new Card(2, "", "");
        Card card3 = new Card(3, "", "");
        dbHelper.insertOrUpdateCard(card1);
        dbHelper.insertOrUpdateCard(card2);
        dbHelper.insertOrUpdateCard(card3);
        dbHelper.moveCard(card2, 1);
        dbHelper.moveCard(card1, 1);
        List<Card> cards = dbHelper.getDeck(1);
        assertEquals(cards.get(0), card2);
        assertEquals(cards.get(1), card1);
    }

    @Test
    public void getDeckSizes() {
        Card card1 = new Card(1, "", "");
        Card card2 = new Card(2, "", "");
        Card card3 = new Card(3, "", "");
        dbHelper.insertOrUpdateCard(card1);
        dbHelper.insertOrUpdateCard(card2);
        dbHelper.insertOrUpdateCard(card3);
        dbHelper.moveCard(card2, 1);
        assertArrayEquals(dbHelper.getDeckInfos(), new int[]{2, 1, 0, 0, 0});
    }

    @Test
    public void moveCard() {
        Card card1 = new Card(1, "", "");
        Card card2 = new Card(2, "", "");
        dbHelper.insertOrUpdateCard(card1);
        dbHelper.insertOrUpdateCard(card2);
        dbHelper.moveCard(card2, 1);
        assertEquals(card1, dbHelper.getDeck(0).get(0));
        assertEquals(card2, dbHelper.getDeck(1).get(0));
    }

    @Test
    public void insertOrUpdateCard() {
        Card card = new Card(1, "oldq", "olda");
        dbHelper.insertOrUpdateCard(card);
        dbHelper.moveCard(card, 3);
        Card oldCard = dbHelper.getDeck(3).get(0);
        assertEquals("oldq", oldCard.getQuestion());
        assertEquals("olda", oldCard.getAnswer());

        card = new Card(1, "newq", "newa");
        dbHelper.insertOrUpdateCard(card);
        Card newCard = dbHelper.getDeck(3).get(0);
        assertEquals("newq", newCard.getQuestion());
        assertEquals("newa", newCard.getAnswer());
    }

    @Test
    public void clearCards() {
        Card card1 = new Card(1, "", "");
        Card card2 = new Card(2, "", "");
        dbHelper.insertOrUpdateCard(card1);
        dbHelper.insertOrUpdateCard(card2);

        dbHelper.clearCards();

        assertArrayEquals(dbHelper.getDeckInfos(), new int[]{0, 0, 0, 0, 0});
    }
}
