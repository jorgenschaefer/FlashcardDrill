package de.jorgenschaefer.flashcarddrill;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jorgenschaefer.flashcarddrill.cards.Card;
import de.jorgenschaefer.flashcarddrill.cards.CardBox;
import de.jorgenschaefer.flashcarddrill.cards.CardBoxLoader;
import de.jorgenschaefer.flashcarddrill.cards.DrillSystem;
import de.jorgenschaefer.flashcarddrill.db.CardDbHelper;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DrillSystemTest {
    private Card card;
    private DrillSystem emptyDrill;
    private DrillSystem drill;

    @Before
    public void setUp() {
        card = new Card(0, "Q", "A");
        CardDbHelper dbHelper = new CardDbHelper(InstrumentationRegistry.getTargetContext());
        emptyDrill = new DrillSystem(null, dbHelper);
        drill = new DrillSystem(new TestLoader(new Card[]{card}), dbHelper);
    }

    @After
    public void tearDown() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(CardDbHelper.DATABASE_NAME);
    }

    @Test
    public void shouldReturnCurrentQuestion() {
        assertEquals(drill.getCurrentQuestion(), card.getQuestion());
    }

    @Test
    public void shouldReturnCurrentQuestionAsNull() {
        assertNull(emptyDrill.getCurrentQuestion());
    }

    @Test
    public void shouldReturnCurrentAnswer() {
        assertEquals(drill.getCurrentAnswer(), card.getAnswer());
    }

    @Test
    public void shouldReturnCurrentAnswerAsNull() {
        assertNull(emptyDrill.getCurrentAnswer());
    }

    @Test
    public void shouldMarkAnswerCorrectly() {
        assertArrayEquals(drill.getDeckSizes(), new int[]{1, 0, 0, 0, 0});
        drill.getCurrentQuestion();
        drill.markAnswerCorrect();
        assertArrayEquals(drill.getDeckSizes(), new int[]{0, 1, 0, 0, 0});
    }

    @Test
    public void shouldMarkAnswerWrong() {
        drill.getCurrentQuestion();
        drill.markAnswerCorrect();
        assertArrayEquals(drill.getDeckSizes(), new int[]{0, 1, 0, 0, 0});
        drill.getCurrentQuestion();
        drill.markAnswerWrong();
        assertArrayEquals(drill.getDeckSizes(), new int[]{1, 0, 0, 0, 0});
    }

    @Test
    public void shouldAdvanceWhenSlotIsEmpty() {
        assertEquals(drill.getCurrentDeck(), 0);
        drill.getCurrentQuestion();
        drill.markAnswerCorrect();
        assertEquals(drill.getCurrentDeck(), 1);
    }

    @Test
    public void shouldRestartWhenSlotIsEmptyAndPriorSlotsAreFull() {
        drill.getCurrentQuestion();
        drill.markAnswerCorrect();
        assertEquals(drill.getCurrentDeck(), 1);
        drill.getCurrentQuestion();
        drill.markAnswerWrong();
        assertEquals(drill.getCurrentDeck(), 0);
    }


    private class TestLoader implements CardBoxLoader {
        Card cards[];

        public TestLoader(Card cards[]) {
            this.cards = cards;
        }

        @Override
        public void load(CardBox box) {
            for (Card card : cards) {
                box.addCard(0, card);
            }
        }
    }
}
