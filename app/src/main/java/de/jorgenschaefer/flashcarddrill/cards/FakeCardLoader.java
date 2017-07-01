package de.jorgenschaefer.flashcarddrill.cards;

public class FakeCardLoader implements CardBoxLoader {
    @Override
    public void load(CardBox box) {
        for (int i = 0; i < 5; i++) {
            box.addCard(0, new Card(i, "Question " + i, "Answer " + i));
        }
    }
}
