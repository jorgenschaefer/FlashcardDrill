package de.jorgenschaefer.flashcarddrill.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CardDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Cards.db";

    private String SQL_CREATE_TABLE = "CREATE TABLE " + CardContract.Card.TABLE_NAME + " ( " +
            CardContract.Card._ID + " INTEGER PRIMARY KEY, " +
            CardContract.Card.COLUMN_NAME_QUESTION + " TEXT, " +
            CardContract.Card.COLUMN_NAME_ANSWER + " TEXT, " +
            CardContract.Card.COLUMN_NAME_BUCKET + " INT) ";

    public CardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No changes so far
    }
}
