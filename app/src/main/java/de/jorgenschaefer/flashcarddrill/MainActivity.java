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

import de.jorgenschaefer.flashcarddrill.cards.DrillSystem;
import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
        cardText.setText(drill.getCurrentQuestion());
        setStatusText();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonShow:
                cardText.setText(drill.getCurrentAnswer());
                setStatusText();
                showButton.setVisibility(View.GONE);
                rightButton.setVisibility(View.VISIBLE);
                wrongButton.setVisibility(View.VISIBLE);
                break;
            case R.id.buttonRight:
                drill.markAnswerCorrect();
                cardText.setText(drill.getCurrentQuestion());
                setStatusText();
                showButton.setVisibility(View.VISIBLE);
                rightButton.setVisibility(View.GONE);
                wrongButton.setVisibility(View.GONE);
                break;
            case R.id.buttonWrong:
                drill.markAnswerWrong();
                cardText.setText(drill.getCurrentQuestion());
                setStatusText();
                showButton.setVisibility(View.VISIBLE);
                rightButton.setVisibility(View.GONE);
                wrongButton.setVisibility(View.GONE);
                break;
        }
    }

    private void setStatusText() {
        String status = "Current deck: " + (drill.getCurrentDeck() + 1);
        status += " | Sizes: ";
        for (int size : drill.getDeckSizes()) {
            status += size + " / ";
        }
        statusText.setText(status);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            View view = findViewById(R.id.card_text);
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
