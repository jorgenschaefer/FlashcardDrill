package de.jorgenschaefer.flashcarddrill;

import android.databinding.Observable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import de.jorgenschaefer.flashcarddrill.StudyViewModel;
import de.jorgenschaefer.flashcarddrill.db.Card;
import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;
import de.jorgenschaefer.flashcarddrill.db.CardsDbContract;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class StudyViewModelTest {
    private CardsDbHelper dbHelper;
    private StudyViewModel drill;
    private TestObserver observer;

    @Before
    public void setUp() {
        dbHelper = new CardsDbHelper(InstrumentationRegistry.getTargetContext());
    }

    public StudyViewModel drill() {
        if (drill == null) {
            drill = new StudyViewModel(dbHelper);
            observer = new TestObserver();
            drill.addOnPropertyChangedCallback(observer);
        }
        return drill;
    }

    @After
    public void tearDown() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(CardsDbContract.DATABASE_NAME);
    }

    @Test
    public void getStatusText() {
        Card card1 = new Card(1, "", "");
        Card card2 = new Card(2, "", "");
        Card card3 = new Card(3, "", "");
        dbHelper.insertOrUpdateCard(card1);
        dbHelper.insertOrUpdateCard(card2);
        dbHelper.insertOrUpdateCard(card3);
        dbHelper.moveCard(card2, 1);
        assertEquals("Current deck: 1 | Decks: 2 / 1 / 0 / 0 / 0", drill().getStatusText());
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
        assertArrayEquals(drill().getDeckSizes(), new int[]{2, 1, 0, 0, 0});
    }

    @Test
    public void getCurrentSide() {
        StudyViewModel drill = drill();
        assertEquals("Q", drill.getCurrentSide());
        drill.onFlipCard(null);
        assertEquals("A", drill.getCurrentSide());
        drill.onFlipCard(null);
        assertEquals("Q", drill.getCurrentSide());
    }

    @Test
    public void getCurrentQuestion() {
        dbHelper.insertOrUpdateCard(new Card(1, "Que?", "Ye!"));
        assertEquals("Que?", drill().getCurrentQuestion());
    }

    @Test
    public void getCurrentAnswer() {
        dbHelper.insertOrUpdateCard(new Card(1, "Que?", "Ye!"));
        assertEquals("Ye?", drill().getCurrentAnswer());
    }

    @Test
    public void onFlipCard() {
        StudyViewModel drill = drill();
        assertEquals("Q", drill.getCurrentSide());
        drill.onFlipCard(null);
        assertEquals(BR.currentSide, observer.getChangedProperties().get(0).intValue());
        assertEquals("A", drill.getCurrentSide());
        drill.onFlipCard(null);
        assertEquals(BR.currentSide, observer.getChangedProperties().get(1).intValue());
    }

    @Test
    public void onAnswerCorrect() {
        // - Card is moved to the next slot
        // - New card is shown
        // - Q side is up
        // - If the current deck is empty, the next non-empty deck is chosen
        // - Notifies statusText, currentSide, currentQuestion, and currentAnswer
    }

    @Test
    public void onAnswerIncorrect() {
        // - Card is moved to deck 0
        // - New card is shown
        // - Q side is up
        // - If the current deck is empty, the next non-empty deck is chosen
        // - Notifies statusText, currentSide, currentQuestion, and currentAnswer
    }

    // FIXME: Test this
    @Test
    public void onLoadCards() {
    }

    @Test
    public void onClearCards() {
        Card card1 = new Card(1, "", "");
        Card card2 = new Card(2, "", "");
        dbHelper.insertOrUpdateCard(card1);
        dbHelper.insertOrUpdateCard(card2);

        drill().onClearCards(null);

        assertArrayEquals(dbHelper.getDeckSizes(), new int[]{0, 0, 0, 0, 0});
    }

    // FIXME: Test this
    @Test
    public void getStateSetState() {
        // Restores:
        // - currentDeck
        // - currentSide
        // - currentCardList
    }

    private class TestObserver extends Observable.OnPropertyChangedCallback {
        private List<Integer> changedProperties = new ArrayList<>();

        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            changedProperties.add(propertyId);
        }

        public List<Integer> getChangedProperties() {
            return changedProperties;
        }
    }

/*
    private Card card;
    private CardsDbHelper dbHelper;
    private StudyViewModel drill;

    @Before
    public void setUp() {
        card = new Card(0, "Q", "A");
        dbHelper = new CardsDbHelper(InstrumentationRegistry.getTargetContext());
        drill = new StudyViewModel(dbHelper);
    }

    @After
    public void tearDown() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(CardsDbContract.DATABASE_NAME);
    }

    private void addTestCard() {
        dbHelper.insertOrUpdateCard(card);
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
        drill.markAnswerCorrect();
        assertArrayEquals(drill.getDeckSizes(), new int[]{0, 1, 0, 0, 0});
    }

    @Test
    public void shouldMarkAnswerWrong() {
        addTestCard();
        drill.markAnswerCorrect();
        assertArrayEquals(drill.getDeckSizes(), new int[]{0, 1, 0, 0, 0});
        drill.markAnswerWrong();
        assertArrayEquals(drill.getDeckSizes(), new int[]{1, 0, 0, 0, 0});
    }

    @Test
    public void shouldAdvanceWhenSlotIsEmpty() {
        addTestCard();
        assertEquals(drill.getCurrentDeck(), 0);
        drill.markAnswerCorrect();
        assertEquals(drill.getCurrentDeck(), 1);
    }

    @Test
    public void shouldRestartWhenSlotIsEmptyAndPriorSlotsAreFull() {
        addTestCard();
        drill.markAnswerCorrect();
        assertEquals(drill.getCurrentDeck(), 1);
        drill.markAnswerWrong();
        assertEquals(drill.getCurrentDeck(), 0);
    }

    @Test
    public void shouldCallChangeListener() {
        TestChangeListener listener = new TestChangeListener();
        addTestCard();
        drill.setChangeListener(listener);
        drill.markAnswerCorrect();
        assertTrue(listener.cardChanged);
        assertTrue(listener.deckSizesChanged);
    }

    class TestChangeListener implements DrillSystemChangeListener {
        boolean cardChanged = false;
        boolean deckSizesChanged = false;

        @Override
        public void onCurrentCardChanged() {
            cardChanged = true;
        }

        @Override
        public void onDeckSizesChanged() {
            deckSizesChanged = true;
        }
    }
*/
}
