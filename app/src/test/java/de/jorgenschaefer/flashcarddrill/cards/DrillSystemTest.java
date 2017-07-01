package de.jorgenschaefer.flashcarddrill.cards;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DrillSystemTest {
    private Card card;
    private DrillSystem emptyDrill;
    private DrillSystem drill;

    @Before
    public void setUp() {
        card = new Card("Q", "A");
        emptyDrill = new DrillSystem(null);
        drill = new DrillSystem(new TestLoader(new Card[]{card}));
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
