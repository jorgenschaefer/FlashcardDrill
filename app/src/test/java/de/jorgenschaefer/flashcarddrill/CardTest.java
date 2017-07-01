package de.jorgenschaefer.flashcarddrill;

import org.junit.Before;
import org.junit.Test;

import de.jorgenschaefer.flashcarddrill.cards.Card;

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
    public void shouldEqualById() {
        Card card2 = new Card(1, "Q", "A");
        Card card3 = new Card(card.getId(), "X", "U");

        assertNotEquals(card, card2);
        assertEquals(card, card3);
    }
}
