package de.jorgenschaefer.flashcarddrill.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.jorgenschaefer.flashcarddrill.drill.CardRepository;

public class CardsDbHelper extends SQLiteOpenHelper implements CardRepository {
    private final int NUM_DECKS = 5;

    private final String SQL_CREATE_TABLE = "CREATE TABLE " + CardsDbContract.Card.TABLE_NAME + " ( " +
            CardsDbContract.Card._ID + " INTEGER PRIMARY KEY, " +
            CardsDbContract.Card.QUESTION + " TEXT, " +
            CardsDbContract.Card.ANSWER + " TEXT, " +
            CardsDbContract.Card.DECK + " INT) ";

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

    public List<Card> getDeck(int deck) {
        SQLiteDatabase db = getReadableDatabase();
        List<Card> cardList = new ArrayList<>();
        String[] projection = {
                CardsDbContract.Card._ID,
                CardsDbContract.Card.QUESTION,
                CardsDbContract.Card.ANSWER
        };
        String selection = CardsDbContract.Card.DECK + " = ?";
        String[] selectionArgs = {Integer.toString(deck)};
        Cursor cursor = db.query(
                CardsDbContract.Card.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            cardList.add(new Card(
                    cursor.getInt(cursor.getColumnIndexOrThrow(CardsDbContract.Card._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CardsDbContract.Card.QUESTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CardsDbContract.Card.ANSWER)))
            );
        }
        cursor.close();
        return cardList;
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
        cursor.close();
        return sizes;
    }

    public void moveCard(Card card, int deck) {
        SQLiteDatabase db = getWritableDatabase();
        deck = Math.min(deck, NUM_DECKS - 1);
        ContentValues values = new ContentValues();
        values.put(CardsDbContract.Card.DECK, deck);
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

            db.insert(
                    CardsDbContract.Card.TABLE_NAME,
                    null,
                    values
            );
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
