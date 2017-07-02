package de.jorgenschaefer.flashcarddrill.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CardsDbHelper extends SQLiteOpenHelper {
    int NUM_DECKS = 5;

    private String SQL_CREATE_TABLE = "CREATE TABLE " + CardsDbContract.Card.TABLE_NAME + " ( " +
            CardsDbContract.Card._ID + " INTEGER PRIMARY KEY, " +
            CardsDbContract.Card.QUESTION + " TEXT, " +
            CardsDbContract.Card.ANSWER + " TEXT, " +
            CardsDbContract.Card.DECK + " INT) ";
    private CardsDbChangeListener changeListener;

    public CardsDbHelper(Context context) {
        super(context, CardsDbContract.DATABASE_NAME, null, CardsDbContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No changes so far
    }

    public Card getRandomCardFromDeck(int deck) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                CardsDbContract.Card._ID,
                CardsDbContract.Card.QUESTION,
                CardsDbContract.Card.ANSWER
        };
        String selection = CardsDbContract.Card.DECK + " = ?";
        String[] selectionArgs = { Integer.toString(deck) };
        Cursor cursor = db.query(
                CardsDbContract.Card.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                "RANDOM()",
                "1"
        );

        Card card;
        if (cursor.moveToNext()) {
            card = new Card(
                    cursor.getInt(cursor.getColumnIndexOrThrow(CardsDbContract.Card._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CardsDbContract.Card.QUESTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CardsDbContract.Card.ANSWER))
            );
        } else {
            card = null;
        }
        cursor.close();
        return card;
    }

    public int[] getDeckSizes() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                "COUNT(*) AS count",
                CardsDbContract.Card.DECK
        };
        Cursor cursor = db.query(
                CardsDbContract.Card.TABLE_NAME,
                projection,
                null,
                null,
                CardsDbContract.Card.DECK,
                null,
                null
        );
        int[] sizes = new int[NUM_DECKS];
        while (cursor.moveToNext()) {
            int bucket = cursor.getInt(cursor.getColumnIndexOrThrow(CardsDbContract.Card.DECK));
            int size = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
            sizes[bucket] = size;
        }
        return sizes;
    }

    public void addCard(int deckNum, Card card) {
        deckNum = Math.min(deckNum, NUM_DECKS - 1);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CardsDbContract.Card.DECK, deckNum);

        int count = db.update(
                CardsDbContract.Card.TABLE_NAME,
                values,
                CardsDbContract.Card._ID + " = ?",
                new String[]{ Integer.toString(card.getId()) }
        );
        if (count == 0) {
            values.put(CardsDbContract.Card._ID, card.getId());
            values.put(CardsDbContract.Card.QUESTION, card.getQuestion());
            values.put(CardsDbContract.Card.ANSWER, card.getAnswer());

            db.insert(
                    CardsDbContract.Card.TABLE_NAME,
                    null,
                    values
            );
        }
        if (changeListener != null) {
            changeListener.onDatabaseChange();
        }
    }

    public int getFirstNonemptyDeck() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "COUNT(*) AS count",
                CardsDbContract.Card.DECK
        };
        Cursor cursor = db.query(
                CardsDbContract.Card.TABLE_NAME,
                projection,
                null,
                null,
                CardsDbContract.Card.DECK,
                "count > 0",
                CardsDbContract.Card.DECK + " ASC"
        );
        int bucket = 0;
        if (cursor.moveToNext()) {
            bucket = cursor.getInt(cursor.getColumnIndexOrThrow(CardsDbContract.Card.DECK));
        }
        cursor.close();
        return bucket;
    }

    public void setChangeListener(CardsDbChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public Card getCardById(int cardId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                CardsDbContract.Card._ID,
                CardsDbContract.Card.QUESTION,
                CardsDbContract.Card.ANSWER
        };
        String selection = CardsDbContract.Card._ID + " = ?";
        String[] selectionArgs = { Integer.toString(cardId) };
        Cursor cursor = db.query(
                CardsDbContract.Card.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Card card;
        if (cursor.moveToNext()) {
            card = new Card(
                    cursor.getInt(cursor.getColumnIndexOrThrow(CardsDbContract.Card._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CardsDbContract.Card.QUESTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CardsDbContract.Card.ANSWER))
            );
        } else {
            card = null;
        }
        cursor.close();
        return card;
    }

    public void addOrUpdateCard(Card card) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CardsDbContract.Card.QUESTION, card.getQuestion());
        values.put(CardsDbContract.Card.ANSWER, card.getAnswer());

        if (getCardById(card.getId()) == null) {
            values.put(CardsDbContract.Card.DECK, 0);
            values.put(CardsDbContract.Card._ID, card.getId());
            db.insert(
                    CardsDbContract.Card.TABLE_NAME,
                    null,
                    values
            );
        } else {
            String where = CardsDbContract.Card._ID + " = ?";
            String whereArgs[] = { Integer.toString(card.getId()) };
            db.update(
                    CardsDbContract.Card.TABLE_NAME,
                    values,
                    where,
                    whereArgs
            );
        }
        if (changeListener != null) {
            changeListener.onDatabaseChange();
        }
    }
}
