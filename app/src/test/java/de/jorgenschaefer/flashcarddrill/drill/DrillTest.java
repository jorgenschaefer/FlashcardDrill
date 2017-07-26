package de.jorgenschaefer.flashcarddrill.drill;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import de.jorgenschaefer.flashcarddrill.db.Card;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DrillTest {
    CardRepository repository;
    Drill drill;
    private boolean changeListenerDidRun;

    @Before
    public void setUp() {
        repository = new MockCardRepository();
        drill = null;
        changeListenerDidRun = false;
    }

    private Drill getDrill() {
        if (drill == null) {
            drill = new Drill(repository);
            drill.setOnChangeListener(new Runnable() {
                @Override
                public void run() {
                    changeListenerDidRun = true;
                }
            });
        }
        return drill;
    }

    @Test
    public void testConstructor() {
        repository.insertOrUpdateCard(new Card(1, "Q", "A"));

        assertEquals(getDrill().getCurrentDeck(), 0);
        assertArrayEquals(getDrill().getDeckSizes(), new int[]{1, 0, 0, 0, 0});
        assertTrue(getDrill().hasCards());
        assertEquals(getDrill().getCurrentQuestion(), "Q");
        assertEquals(getDrill().getCurrentAnswer(), "A");
    }

    @Test
    public void testForEmptyDrill() {
        assertEquals(getDrill().getCurrentDeck(), 0);
        assertArrayEquals(getDrill().getDeckSizes(), new int[]{0, 0, 0, 0, 0});
        assertFalse(getDrill().hasCards());
    }

    @Test
    public void setCurrentDeck() {
        Card c1 = new Card(1, "Q1", "");
        Card c2 = new Card(2, "Q2", "");
        repository.moveCard(c1, 0);
        repository.moveCard(c2, 1);

        getDrill().setCurrentDeck(1);

        assertTrue(changeListenerDidRun);
        assertEquals(getDrill().getCurrentQuestion(), "Q2");
    }

    @Test
    public void onAnswerCorrect() {
        Card c1 = new Card(1, "Q1", "");
        Card c2 = new Card(2, "Q2", "");
        repository.moveCard(c1, 0);
        repository.moveCard(c2, 0);
        String currentQuestion = getDrill().getCurrentQuestion();

        getDrill().onAnswerCorrect();

        assertTrue(changeListenerDidRun);
        assertEquals(getDrill().getCurrentDeck(), 0);
        getDrill().setCurrentDeck(1);
        assertEquals(getDrill().getCurrentQuestion(), currentQuestion);
    }

    @Test
    public void testOnAnswerIncorrect() {
        Card c1 = new Card(1, "Q1", "");
        repository.moveCard(c1, 1);

        getDrill().onAnswerIncorrect();

        assertTrue(changeListenerDidRun);
        assertEquals(getDrill().getCurrentDeck(), 0);
        assertEquals(getDrill().getCurrentQuestion(), "Q1");
    }

    @Test
    public void onLoadCards() {
        Card c1 = new Card(1, "Q1", "");
        Card c2 = new Card(2, "Q2", "");
        Card c3 = new Card(3, "Q3", "");
        repository.moveCard(c1, 1);
        repository.moveCard(c2, 2);
        List<Card> cards = new ArrayList<>();
        cards.add(c1);
        cards.add(c2);
        cards.add(c3);

        getDrill().onLoadCards(cards);

        assertFalse(changeListenerDidRun);
        getDrill().setCurrentDeck(0);
        assertEquals(getDrill().getCurrentQuestion(), "Q3");
        getDrill().setCurrentDeck(1);
        assertEquals(getDrill().getCurrentQuestion(), "Q1");
        getDrill().setCurrentDeck(2);
        assertEquals(getDrill().getCurrentQuestion(), "Q2");
    }

    @Test
    public void onLoadCardsRunsChangeListenerOnEmptyDecks() {
        Card c1 = new Card(1, "Q1", "");
        List<Card> cards = new ArrayList<>();
        cards.add(c1);

        getDrill().onLoadCards(cards);

        assertTrue(changeListenerDidRun);
    }

    @Test
    public void onClearCards() {
        Card c1 = new Card(1, "Q1", "");
        repository.moveCard(c1, 1);

        getDrill().onClearCards();

        assertFalse(getDrill().hasCards());
    }
}
