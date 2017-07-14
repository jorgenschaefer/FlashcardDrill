package de.jorgenschaefer.flashcarddrill;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.jorgenschaefer.flashcarddrill.db.Card;
import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;
import de.jorgenschaefer.flashcarddrill.drill.Drill;

public class StudyViewModel extends BaseObservable {
    private static final String STATE_CURRENT_DECK = "currentDeck";
    private static final String STATE_CURRENT_SIDE = "currentSide";
    private static final String STATE_CURRENT_CARDLIST = "currentCardList";
    private Drill drill;

    public StudyViewModel(Drill drill) {
        this.drill = drill;
    }

    @Bindable
    public int getCardVisibility() {
        return drill.hasCards() ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getNoCardVisibility() {
        return drill.hasCards() ? View.GONE : View.VISIBLE;
    }

    @Bindable
    public String getStatusText() {
        String statusText = "Current deck: " + (drill.getCurrentDeck() + 1) + " | Decks: ";
        for (int size : drill.getDeckSizes()) {
            statusText += Integer.toString(size) + " / ";
        }
        return statusText.substring(0, statusText.length() - 3);
    }

    @Bindable
    public String getCurrentText() {
        if (!drill.hasCards()) {
            return "";
        } else if (drill.getCurrentSide() == Drill.Side.QUESTION) {
            return drill.getCurrentQuestion();
        } else {
            return drill.getCurrentAnswer();
        }
    }

    public void onFlipCard() {
        drill.onFlipCard();
        notifyPropertyChanged(BR.currentText);
    }

    public void onAnswerCorrect(View view) {
        drill.onAnswerCorrect();
        notifyChange();
        Snackbar.make(view, "Correct! :-)", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void onAnswerIncorrect(View view) {
        drill.onAnswerIncorrect();
        notifyChange();
        Snackbar.make(view, "Incorrect :-(", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void onLoadCards(View view) {
        Snackbar.make(view, "Loading...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        final InputStream cardStream = view.getResources().openRawResource(R.raw.cards);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                drill.onLoadCards(loadCards(cardStream));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                StudyViewModel.this.notifyChange();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void onClearCards(View view) {
        drill.onClearCards();
        notifyChange();
    }

    public void setState(Bundle state) {
        drill.setState(state);
        notifyChange();
    }

    public Bundle getState() {
        return drill.getState();
    }

    private List<Card> loadCards(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        List<Card> cards = new ArrayList<>();
        try {
            String line;
            int id = 0;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split("\t");
                id++;
                String question = row[0];
                String answer = row[1];
                cards.add(new Card(id, question, answer));
            }
        } catch (IOException e) {
        }
        return cards;
    }
}