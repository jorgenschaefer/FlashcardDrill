package de.jorgenschaefer.flashcarddrill.cards;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jorgenschaefer.flashcarddrill.db.Card;
import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;
import de.jorgenschaefer.flashcarddrill.db.CardsDbContract;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class DrillSystemTest {
    private Card card;
    private CardsDbHelper dbHelper;
    private DrillSystem drill;

    @Before
    public void setUp() {
        card = new Card(0, "Q", "A");
        dbHelper = new CardsDbHelper(InstrumentationRegistry.getTargetContext());
        drill = new DrillSystem(dbHelper);
    }

    @After
    public void tearDown() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(CardsDbContract.DATABASE_NAME);
    }

    private void addTestCard() {
        dbHelper.addCard(0, card);
        drill.onDbChanged();
    }

    @Test
    public void shouldReturnCurrentQuestion() {
        addTestCard();
        assertEquals(drill.getCurrentQuestion(), card.getQuestion());
    }

    @Test
    public void shouldReturnCurrentQuestionAsNull() {
        assertNull(drill.getCurrentQuestion());
    }

    @Test
    public void shouldReturnCurrentAnswer() {
        addTestCard();
        assertEquals(drill.getCurrentAnswer(), card.getAnswer());
    }

    @Test
    public void shouldReturnCurrentAnswerAsNull() {
        assertNull(drill.getCurrentAnswer());
    }

    @Test
    public void shouldMarkAnswerCorrectly() {
        addTestCard();
        assertArrayEquals(drill.getDeckSizes(), new int[]{1, 0, 0, 0, 0});
        drill.getCurrentQuestion();
        drill.markAnswerCorrect();
        assertArrayEquals(drill.getDeckSizes(), new int[]{0, 1, 0, 0, 0});
    }

    @Test
    public void shouldMarkAnswerWrong() {
        addTestCard();
        drill.getCurrentQuestion();
        drill.markAnswerCorrect();
        assertArrayEquals(drill.getDeckSizes(), new int[]{0, 1, 0, 0, 0});
        drill.getCurrentQuestion();
        drill.markAnswerWrong();
        assertArrayEquals(drill.getDeckSizes(), new int[]{1, 0, 0, 0, 0});
    }

    @Test
    public void shouldAdvanceWhenSlotIsEmpty() {
        addTestCard();
        assertEquals(drill.getCurrentDeck(), 0);
        drill.getCurrentQuestion();
        drill.markAnswerCorrect();
        assertEquals(drill.getCurrentDeck(), 1);
    }

    @Test
    public void shouldRestartWhenSlotIsEmptyAndPriorSlotsAreFull() {
        addTestCard();
        drill.getCurrentQuestion();
        drill.markAnswerCorrect();
        assertEquals(drill.getCurrentDeck(), 1);
        drill.getCurrentQuestion();
        drill.markAnswerWrong();
        assertEquals(drill.getCurrentDeck(), 0);
    }
}
