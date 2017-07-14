package de.jorgenschaefer.flashcarddrill;

import android.databinding.Observable;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
        assertEquals("Ye!", drill().getCurrentAnswer());
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
        assertPropertyChanged(BR.currentSide);
    }

    @Test
    public void onAnswerCorrect() {
        // - Card is moved to the next slot
        // - New card is shown
        // - Q side is up
        // - If the current deck is empty, the next non-empty deck is chosen
        // - Notifies statusText, currentSide, currentQuestion, and currentAnswer
        assertPropertyChanged(BR.statusText);
    }

    @Test
    public void onAnswerIncorrect() {
        // - Card is moved to deck 0
        // - New card is shown
        // - Q side is up
        // - If the current deck is empty, the next non-empty deck is chosen
        // - Notifies statusText, currentSide, currentQuestion, and currentAnswer
        assertPropertyChanged(BR.statusText);
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
        dbHelper.insertOrUpdateCard(new Card(1, "Q1", "A1"));
        dbHelper.insertOrUpdateCard(new Card(2, "Q2", "A2"));
        StudyViewModel drill1 = new StudyViewModel(dbHelper);
        StudyViewModel drill2 = drill();
        drill1.getCurrentAnswer();
        drill1.onAnswerCorrect(null);
        drill1.onAnswerCorrect(null);
        drill1.onAnswerCorrect(null);
        drill1.onFlipCard(null);
        assertArrayEquals(new int[]{0, 1, 1, 0, 0}, drill1.getDeckSizes());

        assertEquals("Current deck: 2", drill1.getStatusText().substring(0, 15));
        assertEquals("A", drill1.getCurrentSide());
        String oldAnswer = drill1.getCurrentAnswer();

        drill2.setState(drill1.getState());

        // Restores:
        // - currentDeck
        assertEquals("Current deck: 2", drill2.getStatusText().substring(0, 15));
        // - currentSide
        assertEquals("A", drill2.getCurrentSide());
        // - currentCardList
        assertEquals(oldAnswer, drill2.getCurrentAnswer());

        assertPropertyChanged(BR.currentQuestion);
        assertPropertyChanged(BR.currentAnswer);
        assertPropertyChanged(BR.currentSide);
        assertPropertyChanged(BR.statusText);
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

    private void assertPropertyChanged(int prop) {
        // prop 0 is sent if "all" properties changed
        assertTrue(observer.getChangedProperties().contains(prop)
                   || observer.getChangedProperties().contains(0));
    }
}
