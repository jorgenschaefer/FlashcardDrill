package de.jorgenschaefer.flashcarddrill;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.jorgenschaefer.flashcarddrill.db.Card;
import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;

public class StudyViewModel extends BaseObservable {
    private CardsDbHelper dbHelper;
    private int currentDeck = 0;
    private String currentSide = "Q";
    private List<Card> currentCardList = new ArrayList<>();

    public StudyViewModel(CardsDbHelper dbHelper) {
        this.dbHelper = dbHelper;
        nextCard();
    }

    @Bindable
    public String getStatusText() {
        String statusText = "Current deck: " + currentDeck + " | Decks: ";
        for (int size : getDeckSizes()) {
            statusText += Integer.toString(size) + " / ";
        }
        return statusText.substring(0, statusText.length() - 3);
    }

    @Bindable
    public int getCurrentDeck() {
        return currentDeck;
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
                new CardLoader(dbHelper).load(cardStream);
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

    // TODO: Implement me!
    public void setState(Bundle bundle) {
    }

    // TODO: Implement me!
    public Bundle getState() {
        return null;
    }

    private void nextCard() {
        if (!currentCardList.isEmpty()) {
            currentCardList.remove(0);
        }
        if (currentCardList.isEmpty()) {
            currentDeck = nextDeck();
            currentCardList = dbHelper.getDeck(currentDeck);
            notifyPropertyChanged(BR.currentDeck);
        }
        currentSide = "Q";
        notifyPropertyChanged(BR.currentSide);
        notifyPropertyChanged(BR.currentQuestion);
        notifyPropertyChanged(BR.currentAnswer);
    }

    private int nextDeck() {
        int[] deckSizes = dbHelper.getDeckSizes();
        for (int i = currentDeck; i < deckSizes.length; i++) {
            if (deckSizes[i] > 0)  {
                return i;
            }
        }
        return 0;
    }
}