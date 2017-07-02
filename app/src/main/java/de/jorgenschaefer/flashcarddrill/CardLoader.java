package de.jorgenschaefer.flashcarddrill;

import android.content.res.Resources;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.jorgenschaefer.flashcarddrill.db.Card;
import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;

public class CardLoader extends AsyncTask<InputStream, Void, Void> {
    CardsDbHelper dbHelper;

    public CardLoader(CardsDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    protected Void doInBackground(InputStream... params) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(params[0]));
        try {
            String line;
            int id = 0;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split("\t");
                id++;
                String question = row[0];
                String answer = row[1];
                dbHelper.addCard(0, new Card(id, question, answer));
            }
        } catch (IOException e) {
        }
        return null;
    }
}
