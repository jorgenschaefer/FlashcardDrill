package de.jorgenschaefer.flashcarddrill;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.jorgenschaefer.flashcarddrill.db.Card;
import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;
import de.jorgenschaefer.flashcarddrill.drill.Drill;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private static final String STATE_STUDYVIEWMODEL = "studyViewModel";

    private GestureDetectorCompat detector;
    private Drill drill;
    private View noCards;
    private ViewGroup card;
    private TextView cardFront;
    private TextView cardBack;
    private TextView statusText;
    private boolean animationRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new GestureDetectorCompat(this, this);

        CardsDbHelper dbHelper = new CardsDbHelper(getApplicationContext());
        drill = new Drill(dbHelper);
        if (savedInstanceState != null) {
            drill.setState(savedInstanceState.getBundle(STATE_STUDYVIEWMODEL));
        }

        setContentView(R.layout.activity_main);

        statusText = (TextView) findViewById(R.id.status_text);
        noCards = findViewById(R.id.no_cards);
        card = (ViewGroup) findViewById(R.id.card);
        cardFront = (TextView) findViewById(R.id.card_front);
        cardBack = (TextView) findViewById(R.id.card_back);
        cardFront.setCameraDistance(R.integer.camera_distance);
        cardBack.setCameraDistance(R.integer.camera_distance);

        render();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBundle(STATE_STUDYVIEWMODEL, drill.getState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        View view = findViewById(R.id.toolbar);

        switch (id) {
            case R.id.action_load:
                loadCards();
                return true;
            case R.id.action_clear:
                drill.onClearCards();
                render();
                return true;
            case R.id.action_settings:
                Snackbar.make(view, "No settings yet!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private final String DEBUG_TAG = "main";

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(DEBUG_TAG, "onShowPress: " + e.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (noCards.getVisibility() != View.GONE) {
            return false;
        }
        if (animationRunning) {
            return false;
        }
        animationRunning = true;

        drill.onFlipCard();

        AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_out);
        flipOut.setTarget(cardFront);
        AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_in);
        flipIn.setTarget(cardBack);

        AnimatorSet flip = new AnimatorSet();
        flip.playTogether(flipOut, flipIn);
        flip.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationRunning = false;
                render();
            }
        });
        flip.start();

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(DEBUG_TAG, "onLongPress: " + e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (noCards.getVisibility() != View.GONE) {
            return false;
        }
        if (animationRunning) {
            return false;
        }
        animationRunning = true;

        float distanceX = e2.getX() - e1.getX();
        int endX;
        if (distanceX < 0) {
            drill.onAnswerIncorrect();
            endX = -1000;
        } else {
            drill.onAnswerCorrect();
            endX = 1000;
        }
        Resources res = getResources();
        cardBack.setText(drill.getCurrentFrontText());
        ObjectAnimator slide = ObjectAnimator.ofFloat(cardFront, "x", 0, endX)
                .setDuration(res.getInteger(R.integer.flip_duration));

        slide.setInterpolator(new AccelerateDecelerateInterpolator());
        slide.addListener(new AnimatorListenerAdapter() {
                              @Override
                              public void onAnimationEnd(Animator animation) {
                                  super.onAnimationEnd(animation);
                                  animationRunning = false;
                                  render();
                                  super.onAnimationEnd(animation);
                              }

                          }
        );
        slide.start();
        return true;
    }

    private void render() {
        if (!drill.hasCards()) {
            noCards.setVisibility(View.VISIBLE);
            statusText.setVisibility(View.GONE);
            card.setVisibility(View.GONE);
        } else {
            noCards.setVisibility(View.GONE);
            statusText.setVisibility(View.VISIBLE);
            card.setVisibility(View.VISIBLE);
            cardFront.setAlpha(1.0f);
            cardFront.setRotationY(0);
            cardFront.setX(0);
            cardFront.setText(drill.getCurrentFrontText());
            cardBack.setAlpha(1.0f);
            cardBack.setRotationY(0);
            cardBack.setX(0);
            cardBack.setText(drill.getCurrentBackText());
            statusText.setText(getStatusText());
        }
    }

    private String getStatusText() {
        String statusText = "Current deck: " + (drill.getCurrentDeck() + 1) + " | Decks: ";
        for (int size : drill.getDeckSizes()) {
            statusText += Integer.toString(size) + " / ";
        }
        return statusText.substring(0, statusText.length() - 3);
    }

    private void loadCards() {
        View view = findViewById(R.id.toolbar);
        Snackbar.make(view, "Loading cards", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
        final InputStream cardStream = getResources().openRawResource(R.raw.cards);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                drill.onLoadCards(cardsFromStream(cardStream));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                render();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    private List<Card> cardsFromStream(InputStream stream) {
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