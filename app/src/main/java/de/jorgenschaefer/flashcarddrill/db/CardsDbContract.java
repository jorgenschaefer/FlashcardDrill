package de.jorgenschaefer.flashcarddrill.db;

import android.provider.BaseColumns;

public final class CardsDbContract {
    public static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "Cards.db";

    private CardsDbContract() {}

    public static class Card implements BaseColumns {
        public static final String TABLE_NAME = "card";
        public static final String QUESTION = "question";
        public static final String ANSWER = "answer";
        public static final String DECK = "deck";
        public static final String UPDATED_AT = "updated_at";
        public static final String DUE_AT = "due_at";
    }
}
