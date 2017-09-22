package de.jorgenschaefer.flashcarddrill.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

import de.jorgenschaefer.flashcarddrill.drill.CardRepository;
import de.jorgenschaefer.flashcarddrill.drill.DeckInfo;

public class CardsDbHelper extends SQLiteOpenHelper implements CardRepository {
    private final String SQL_CREATE_TABLE = "CREATE TABLE " + CardsDbContract.Card.TABLE_NAME + " ( " +
            CardsDbContract.Card._ID + " INTEGER PRIMARY KEY, " +
            CardsDbContract.Card.QUESTION + " TEXT, " +
            CardsDbContract.Card.ANSWER + " TEXT, " +
            CardsDbContract.Card.DECK + " INT, " +
            CardsDbContract.Card.UPDATED_AT + " INT " +
            CardsDbContract.Card.DUE_AT + " INT " +
            ")";

    private final String SQL_UPDATE_V1_TO_V2 = "ALTER TABLE " + CardsDbContract.Card.TABLE_NAME +
            " ADD COLUMN " + CardsDbContract.Card.UPDATED_AT + " INT";
    private final String SQL_UPDATE_V2_TO_V3 = "ALTER TABLE " + CardsDbContract.Card.TABLE_NAME +
            " ADD COLUMN " + CardsDbContract.Card.DUE_AT + " INT";

    public CardsDbHelper(Context context) {
        super(context, CardsDbContract.DATABASE_NAME, null, CardsDbContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion <= 1) {
            db.execSQL(SQL_UPDATE_V1_TO_V2);
        }
        if (oldVersion <= 2) {
            db.execSQL(SQL_UPDATE_V2_TO_V3);
        }
        if (oldVersion <= 6) {
            String[] projection = {
                    CardsDbContract.Card._ID,
                    CardsDbContract.Card.DECK,
                    CardsDbContract.Card.UPDATED_AT
            };
            String selection = CardsDbContract.Card.DUE_AT + " <= ?";
            String[] selectionArgs = { Long.toString(new Date().getTime()) };
            Cursor cursor = db.query(
                    CardsDbContract.Card.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    CardsDbContract.Card.DECK + ", " + CardsDbContract.Card.UPDATED_AT + " DESC",
                    null
            );

            if (cursor.moveToNext()) {
                Card card = new Card(
                        cursor.getInt(cursor.getColumnIndexOrThrow(CardsDbContract.Card._ID)),
                        "",
                        "",
                        cursor.getInt(cursor.getColumnIndexOrThrow(CardsDbContract.Card.DECK)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(CardsDbContract.Card.UPDATED_AT))
                );
                ContentValues values = new ContentValues();
                values.put(CardsDbContract.Card.DECK, card.getDeck());
                values.put(CardsDbContract.Card.UPDATED_AT, card.getUpdatedAt());
                values.put(CardsDbContract.Card.DUE_AT, card.getDueAt());

                db.update(
                        CardsDbContract.Card.TABLE_NAME,
                        values,
                        CardsDbContract.Card._ID + " = ?",
                        new String[]{Integer.toString(card.getId())}
                );
            }
            cursor.close();
        }
    }

    @Override
    public DeckInfo getDeckInfo() {
        int total = 0, due = 0;
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { "COUNT(*)" };
        Cursor cursor = db.query(CardsDbContract.Card.TABLE_NAME, projection, null, null, null, null, null);
        if (cursor.moveToNext()) {
            total = cursor.getInt(0);
        }
        String selection = CardsDbContract.Card.DUE_AT + " <= ?";
        String[] selectionArgs = new String[]{ Long.toString(new Date().getTime()) };
        cursor = db.query(CardsDbContract.Card.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            due = cursor.getInt(0);
        }
        return new DeckInfo(total, due);
    }

    @Override
    public Card getNextCard() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                CardsDbContract.Card._ID,
                CardsDbContract.Card.QUESTION,
                CardsDbContract.Card.ANSWER,
                CardsDbContract.Card.DECK,
                CardsDbContract.Card.UPDATED_AT
        };
        String selection = CardsDbContract.Card.DUE_AT + " <= ?";
        String[] selectionArgs = { Long.toString(new Date().getTime()) };
        Cursor cursor = db.query(
                CardsDbContract.Card.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                CardsDbContract.Card.DECK + ", " + CardsDbContract.Card.UPDATED_AT + " DESC",
                null
        );

        if (cursor.moveToNext()) {
            Card card = new Card(
                    cursor.getInt(cursor.getColumnIndexOrThrow(CardsDbContract.Card._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CardsDbContract.Card.QUESTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CardsDbContract.Card.ANSWER)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(CardsDbContract.Card.DECK)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(CardsDbContract.Card.UPDATED_AT))
            );
            cursor.close();
            return card;
        }
        cursor.close();
        return null;
    }

    public void moveCard(Card card) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CardsDbContract.Card.DECK, card.getDeck());
        values.put(CardsDbContract.Card.UPDATED_AT, card.getUpdatedAt());
        values.put(CardsDbContract.Card.DUE_AT, card.getDueAt());

        db.update(
                CardsDbContract.Card.TABLE_NAME,
                values,
                CardsDbContract.Card._ID + " = ?",
                new String[]{Integer.toString(card.getId())}
        );
    }

    public void insertOrUpdateCard(Card card) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CardsDbContract.Card.QUESTION, card.getQuestion());
        values.put(CardsDbContract.Card.ANSWER, card.getAnswer());

        int count = db.update(
                CardsDbContract.Card.TABLE_NAME,
                values,
                CardsDbContract.Card._ID + " = ?",
                new String[]{Integer.toString(card.getId())}
        );
        if (count == 0) {
            values.put(CardsDbContract.Card._ID, card.getId());
            values.put(CardsDbContract.Card.DECK, 0);
            values.put(CardsDbContract.Card.UPDATED_AT, 0);
            values.put(CardsDbContract.Card.DUE_AT, 0);
            db.insert(CardsDbContract.Card.TABLE_NAME, null, values);
        }
    }

    public void clearCards() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                CardsDbContract.Card.TABLE_NAME,
                null,
                null
        );
    }
}
