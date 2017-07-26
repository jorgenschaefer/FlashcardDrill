package de.jorgenschaefer.flashcarddrill.drill;

import org.junit.Before;

public class DrillTest {
    CardRepository db;

    @Before
    public void setUp() {
        db = new MockCardRepository();
    }

}
