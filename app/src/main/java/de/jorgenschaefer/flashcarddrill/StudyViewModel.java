package de.jorgenschaefer.flashcarddrill;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

public class StudyViewModel extends BaseObservable {
    private static final String STATE_CURRENT_DECK = "currentDeck";
    private static final String STATE_CURRENT_SIDE = "currentSide";
    private static final String STATE_CURRENT_CARDLIST = "currentCardList";
    private CardsDbHelper dbHelper;
    private int currentDeck = 0;
    private String currentSide = "Q";
    private ArrayList<Card> currentCardList = new ArrayList<>();

    public StudyViewModel(CardsDbHelper dbHelper) {
        this.dbHelper = dbHelper;
        nextCard();
    }

    @Bindable
    public String getStatusText() {
        String statusText = "Current deck: " + (currentDeck + 1) + " | Decks: ";
        for (int size : getDeckSizes()) {
            statusText += Integer.toString(size) + " / ";
        }
        return statusText.substring(0, statusText.length() - 3);
    }

    @Bindable
    public int[] getDeckSizes() {
        return dbHelper.getDeckSizes();
    }

    @Bindable
    public String getCurrentSide() {
        return currentSide;
    }

    @Bindable
    public String getCurrentQuestion() {
        if (currentCardList.isEmpty()) {
            return null;
        }
        return currentCardList.get(0).getQuestion();
    }

    @Bindable
    public String getCurrentAnswer() {
        if (currentCardList.isEmpty()) {
            return null;
        }
        return currentCardList.get(0).getAnswer();
    }

    public void onFlipCard(View view) {
        if (currentSide.equals("Q")) {
            currentSide = "A";
        } else {
            currentSide = "Q";
        }
        notifyPropertyChanged(BR.currentSide);
    }

    public void onAnswerCorrect(View view) {
        dbHelper.moveCard(currentCardList.get(0), currentDeck + 1);
        notifyPropertyChanged(BR.statusText);
        nextCard();
    }

    public void onAnswerIncorrect(View view) {
        dbHelper.moveCard(currentCardList.get(0), 0);
        notifyPropertyChanged(BR.statusText);
        nextCard();
    }

    public void onLoadCards(View view) {
        Snackbar.make(view, "Loading...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        final InputStream cardStream = view.getResources().openRawResource(R.raw.cards);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                for (Card card : loadCards(cardStream)) {
                    dbHelper.insertOrUpdateCard(card);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (currentCardList.isEmpty()) {
                    nextCard();
                }
                StudyViewModel.this.notifyChange();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public void onClearCards(View view) {
        dbHelper.clearCards();
        currentDeck = 0;
        currentSide = "Q";
        currentCardList = new ArrayList<>();

        notifyChange();
    }

    public void setState(Bundle state) {
        currentDeck = state.getInt(STATE_CURRENT_DECK);
        currentSide = state.getString(STATE_CURRENT_SIDE);
        currentCardList = state.getParcelableArrayList(STATE_CURRENT_CARDLIST);
        notifyChange();
    }

    public Bundle getState() {
        Bundle state = new Bundle();
        state.putInt(STATE_CURRENT_DECK, currentDeck);
        state.putString(STATE_CURRENT_SIDE, currentSide);
        state.putParcelableArrayList(STATE_CURRENT_CARDLIST, currentCardList);
        return state;
    }

    private void nextCard() {
        if (!currentCardList.isEmpty()) {
            currentCardList.remove(0);
        }
        if (currentCardList.isEmpty()) {
            currentDeck = nextDeck();
            currentCardList = new ArrayList<>();
            for (Card card : dbHelper.getDeck(currentDeck)) {
                currentCardList.add(card);
            }
            Collections.shuffle(currentCardList);
        }
        currentSide = "Q";
        notifyPropertyChanged(BR.currentSide);
        notifyPropertyChanged(BR.currentQuestion);
        notifyPropertyChanged(BR.currentAnswer);
    }

    private int nextDeck() {
        int[] deckSizes = dbHelper.getDeckSizes();
        for (int i = 0; i < deckSizes.length; i++) {
            if (deckSizes[i] > 0)  {
                return i;
            }
        }
        return 0;
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