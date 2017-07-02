package de.jorgenschaefer.flashcarddrill;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;

import de.jorgenschaefer.flashcarddrill.cards.DrillSystem;
import de.jorgenschaefer.flashcarddrill.cards.DrillSystemChangeListener;
import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DrillSystemChangeListener {
    private static final String STATE_DECK = "currentDeck";
    private static final String STATE_CARDID = "currentCardId";

    DrillSystem drill;
    TextView statusText;
    TextView cardText;
    Button showButton;
    Button rightButton;
    Button wrongButton;
    CardsDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showButton = (Button)findViewById(R.id.buttonShow);
        showButton.setOnClickListener(this);
        rightButton = (Button)findViewById(R.id.buttonRight);
        rightButton.setOnClickListener(this);
        wrongButton = (Button)findViewById(R.id.buttonWrong);
        wrongButton.setOnClickListener(this);

        cardText = (TextView)findViewById(R.id.card_text);
        statusText = (TextView)findViewById(R.id.status_text);

        dbHelper = new CardsDbHelper(getApplicationContext());
        drill = new DrillSystem(dbHelper);
        drill.setChangeListener(this);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_CARDID)) {
                drill.setCurrentId(savedInstanceState.getInt(STATE_CARDID));
            }
            if (savedInstanceState.containsKey(STATE_DECK)) {
                drill.setCurrentDeck(savedInstanceState.getInt(STATE_DECK));
            }
        }
        displayQuestion();
        updateStatusText();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CARDID, drill.getCurrentId());
        outState.putInt(STATE_DECK, drill.getCurrentDeck());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonShow:
                displayAnswer();
                break;
            case R.id.buttonRight:
                drill.markAnswerCorrect();
                break;
            case R.id.buttonWrong:
                drill.markAnswerWrong();
                break;
        }
    }

    private void updateStatusText() {
        String status = "Current deck: " + (drill.getCurrentDeck() + 1);
        status += " | Sizes: ";
        for (int size : drill.getDeckSizes()) {
            status += size + " / ";
        }
        statusText.setText(status);
    }

    private void displayQuestion() {
        String question = drill.getCurrentQuestion();
        if (question == null) {
            cardText.setText("No questions :-(\nLoad them from the menu!");
            showButton.setVisibility(View.GONE);
            rightButton.setVisibility(View.GONE);
            wrongButton.setVisibility(View.GONE);
        } else {
            cardText.setText(question);
            showButton.setVisibility(View.VISIBLE);
            rightButton.setVisibility(View.GONE);
            wrongButton.setVisibility(View.GONE);
        }
    }

    private void displayAnswer() {
        cardText.setText(drill.getCurrentAnswer());
        showButton.setVisibility(View.GONE);
        rightButton.setVisibility(View.VISIBLE);
        wrongButton.setVisibility(View.VISIBLE);
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
        View view = findViewById(R.id.card_text);

        switch (id) {
            case R.id.action_load:
                Snackbar.make(view, "Loading...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                InputStream cardStream = getResources().openRawResource(R.raw.cards);
                new CardLoader(dbHelper).execute(cardStream);
                return true;
            case R.id.action_settings:
                Snackbar.make(view, "No settings yet!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCurrentCardChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayQuestion();
            }
        });
    }

    @Override
    public void onDeckSizesChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateStatusText();
            }
        });
    }
}
