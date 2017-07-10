package de.jorgenschaefer.flashcarddrill;

import android.content.res.Resources;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Logger;

import de.jorgenschaefer.flashcarddrill.db.Card;
import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;

public class CardLoader {
    private CardsDbHelper dbHelper;

    public CardLoader(CardsDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    protected void load(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            String line;
            int id = 0;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split("\t");
                id++;
                String question = row[0];
                String answer = row[1];
                dbHelper.insertOrUpdateCard(new Card(id, question, answer));
            }
        } catch (IOException e) {
        }
    }
}
