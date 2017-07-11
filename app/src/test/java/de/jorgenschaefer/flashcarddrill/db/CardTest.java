package de.jorgenschaefer.flashcarddrill.db;

import org.junit.Before;
import org.junit.Test;

import de.jorgenschaefer.flashcarddrill.db.Card;

import static org.junit.Assert.*;

public class CardTest {
    Card card;

    @Before
    public void setUp() {
        card = new Card(0, "Q", "A");
    }

    @Test
    public void shouldHaveId() {
        assertEquals(0, card.getId());
    }

    @Test
    public void shouldHaveQuestion() {
        assertEquals("Q", card.getQuestion());
    }

    @Test
    public void shouldHaveAnswer() {
        assertEquals("A", card.getAnswer());
    }

    @Test
    public void shouldSupportEquality() {
        assertEquals(new Card(1, "", ""), new Card(1, "foo", "bar"));
        assertNotEquals(new Card(1, "", ""), new Card(2, "", ""));
    }
}
