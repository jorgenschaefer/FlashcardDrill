package de.jorgenschaefer.flashcarddrill.db;

import android.provider.BaseColumns;

public final class CardContract {
    private CardContract() {}

    public static class Card implements BaseColumns {
        public static final String TABLE_NAME = "card";
        public static final String COLUMN_NAME_QUESTION = "question";
        public static final String COLUMN_NAME_ANSWER = "answer";
        public static final String COLUMN_NAME_BUCKET = "bucket";
    }
}
