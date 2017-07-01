package de.jorgenschaefer.flashcarddrill.cards;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.logging.Logger;

import de.jorgenschaefer.flashcarddrill.db.CardContract;
import de.jorgenschaefer.flashcarddrill.db.CardDbHelper;

public class CardBox {
    private int maxDeck;
    private CardDbHelper dbHelper;

    public CardBox(int nDecks, CardDbHelper dbHelper) {
        maxDeck = nDecks - 1;
        this.dbHelper = dbHelper;
    }

    public Card getRandomCardFromDeck(int deck) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                CardContract.Card._ID,
                CardContract.Card.COLUMN_NAME_QUESTION,
                CardContract.Card.COLUMN_NAME_ANSWER
        };
        String selection = CardContract.Card.COLUMN_NAME_BUCKET + " = ?";
        String[] selectionArgs = { Integer.toString(deck) };
        Cursor cursor = db.query(
                CardContract.Card.TABLE_NAME,
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
                    cursor.getInt(cursor.getColumnIndexOrThrow(CardContract.Card._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CardContract.Card.COLUMN_NAME_QUESTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CardContract.Card.COLUMN_NAME_ANSWER))
            );
        } else {
            card = null;
        }
        cursor.close();
        return card;
    }

    public int[] getDeckSizes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "COUNT(*) AS count",
                CardContract.Card.COLUMN_NAME_BUCKET
        };
        Cursor cursor = db.query(
                CardContract.Card.TABLE_NAME,
                projection,
                null,
                null,
                CardContract.Card.COLUMN_NAME_BUCKET,
                null,
                null
        );
        int[] sizes = new int[maxDeck+1];
        Cursor c = db.rawQuery("SELECT bucket FROM card", new String[]{});
        while (c.moveToNext()) {
            Logger logger = Logger.getLogger("test");
            logger.info("Row: " + c.getInt(0));
        }
        while (cursor.moveToNext()) {
            int bucket = cursor.getInt(cursor.getColumnIndexOrThrow(CardContract.Card.COLUMN_NAME_BUCKET));
            int size = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
            sizes[bucket] = size;
        }
        return sizes;
    }

    public void addCard(int deckNum, Card card) {
        deckNum = Math.min(deckNum, maxDeck);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(CardContract.Card.COLUMN_NAME_BUCKET, deckNum);

        int count = db.update(
                CardContract.Card.TABLE_NAME,
                values,
                CardContract.Card._ID + " = ?",
                new String[]{ Integer.toString(card.getId()) }
        );
        if (count == 0) {
            values.put(CardContract.Card._ID, card.getId());
            values.put(CardContract.Card.COLUMN_NAME_QUESTION, card.getQuestion());
            values.put(CardContract.Card.COLUMN_NAME_ANSWER, card.getAnswer());

            db.insert(
                    CardContract.Card.TABLE_NAME,
                    null,
                    values
            );
        }
    }

    public int getFirstNonemptyDeck() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                "COUNT(*) AS count",
                CardContract.Card.COLUMN_NAME_BUCKET
        };
        Cursor cursor = db.query(
                CardContract.Card.TABLE_NAME,
                projection,
                null,
                null,
                CardContract.Card.COLUMN_NAME_BUCKET,
                "count > 0",
                "bucket ASC"
        );
        int bucket = 0;
        if (cursor.moveToNext()) {
            bucket = cursor.getInt(cursor.getColumnIndexOrThrow(CardContract.Card.COLUMN_NAME_BUCKET));
        }
        cursor.close();
        return bucket;
    }
}
