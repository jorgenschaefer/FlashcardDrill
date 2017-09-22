package de.jorgenschaefer.flashcarddrill;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;
import de.jorgenschaefer.flashcarddrill.drill.Drill;
import de.jorgenschaefer.flashcarddrill.views.FlashCardView;
import de.jorgenschaefer.flashcarddrill.views.StatusBarView;

public class MainActivity extends AppCompatActivity {
    private static final String STATE_STUDYVIEWMODEL = "studyViewModel";

    private Drill drill;
    private StatusBarView statusRow;
    private FlashCardView card;
    private View noCards;
    private View noDueCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        CardsDbHelper dbHelper = new CardsDbHelper(getApplicationContext());
        drill = new Drill(dbHelper);

        statusRow = (StatusBarView) findViewById(R.id.status_row);
        statusRow.setDrill(drill);

        card = (FlashCardView) findViewById(R.id.card);
        card.setDrill(drill);

        noCards = findViewById(R.id.no_cards);
        noDueCards = findViewById(R.id.no_due_cards);

        drill.setOnChangeListener(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.update();
                    }
                });
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        update();
    }

    @Override
    protected void onResume() {
        super.onResume();
        drill.reload();
        update();
    }

    private void update() {
        if (drill.hasDueCards()) {
            noCards.setVisibility(View.GONE);
            noDueCards.setVisibility(View.GONE);
            statusRow.setVisibility(View.VISIBLE);
            card.setVisibility(View.VISIBLE);
            statusRow.notifyDataSetChanged();
            card.notifyDataSetChanged();
        } else if (drill.hasCards()) {
            noCards.setVisibility(View.GONE);
            noDueCards.setVisibility(View.VISIBLE);
            statusRow.setVisibility(View.VISIBLE);
            card.setVisibility(View.GONE);
            statusRow.notifyDataSetChanged();
        } else {
            noCards.setVisibility(View.VISIBLE);
            noDueCards.setVisibility(View.GONE);
            statusRow.setVisibility(View.GONE);
            card.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_load:
                Intent intent = new Intent(this, LoadCardsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_clear:
                new AlertDialog.Builder(this)
                        .setTitle("Remove all cards")
                        .setMessage("Do you really want to remove all cards? This will lose all your progress!")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                drill.onClearCards();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}