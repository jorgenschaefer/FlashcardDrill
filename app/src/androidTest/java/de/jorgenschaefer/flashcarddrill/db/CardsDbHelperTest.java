package de.jorgenschaefer.flashcarddrill.db;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class CardsDbHelperTest {
    private int NUM_DECKS = 5;
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
    public void shouldAddCards() {
        Card card = new Card(0, "", "");
        dbHelper.addCard(0, card);

        assertArrayEquals(dbHelper.getDeckSizes(), new int[]{1, 0, 0, 0, 0});
        assertEquals(dbHelper.getRandomCardFromDeck(0), card);
    }

    @Test
    public void shouldNotDuplicateCards() {
        Card card = new Card(0, "", "");
        dbHelper.addCard(0, card);
        dbHelper.addCard(1, card);

        assertArrayEquals(dbHelper.getDeckSizes(), new int[]{0, 1, 0, 0, 0});
        assertEquals(dbHelper.getRandomCardFromDeck(1), card);
    }

    @Test
    public void shouldNotPutCardsAfterLastSlot() {
        Card card = new Card(0, "", "");
        dbHelper.addCard(10, card);

        assertArrayEquals(dbHelper.getDeckSizes(), new int[]{0, 0, 0, 0, 1});
    }

    @Test
    public void shouldRunOnDbChanged() {
        Card card = new Card(0, "", "");
        TestOnDbChanged listener = new TestOnDbChanged();
        dbHelper.setChangeListener(listener);
        dbHelper.addCard(0, card);
        assertTrue(listener.dbChanged);
    }

    private class TestOnDbChanged implements CardsDbChangeListener {
        boolean dbChanged = false;

        @Override
        public void onDatabaseChange() {
            dbChanged = true;
        }
    }
    @Test
    public void shouldReturnNonemptyBucket() {
        Card card = new Card(0, "", "");
        dbHelper.addCard(0, card);
        assertEquals(dbHelper.getFirstNonemptyDeck(), 0);
        dbHelper.addCard(3, card);
        assertEquals(dbHelper.getFirstNonemptyDeck(), 3);
    }

    @Test
    public void shouldReturnCards() {
        Card card1 = new Card(0, "", "");
        Card card2 = new Card(1, "", "");
        dbHelper.addCard(0, card1);
        dbHelper.addCard(1, card2);

        assertEquals(dbHelper.getRandomCardFromDeck(0), card1);
        assertEquals(dbHelper.getRandomCardFromDeck(1), card2);
    }

    @Test
    public void shouldReturnNullIfNoCards() {
        assertNull(dbHelper.getRandomCardFromDeck(0));
    }
}
