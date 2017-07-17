package de.jorgenschaefer.flashcarddrill;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class MainActivity extends AppCompatActivity {
    private static final String STATE_STUDYVIEWMODEL = "studyViewModel";

    private Drill drill;
    private TextView statusText;
    private View noCards;
    private RecyclerView card;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CardsDbHelper dbHelper = new CardsDbHelper(getApplicationContext());
        drill = new Drill(dbHelper);
        if (savedInstanceState != null) {
            drill.setState(savedInstanceState.getBundle(STATE_STUDYVIEWMODEL));
        }

        setContentView(R.layout.activity_main);

        statusText = (TextView) findViewById(R.id.status_text);
        noCards = findViewById(R.id.no_cards);
        card = (RecyclerView) findViewById(R.id.card);

        card.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        card.setLayoutManager(layoutManager);
        adapter = new DrillAdapter(drill);
        card.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallbackItemTouchHelper);
        itemTouchHelper.attachToRecyclerView(card);

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
                Snackbar.make(view, "Loading cards", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
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

    private void render() {
        if (!drill.hasCards()) {
            noCards.setVisibility(View.VISIBLE);
            statusText.setVisibility(View.GONE);
            card.setVisibility(View.GONE);
        } else {
            statusText.setText(getStatusText());
            noCards.setVisibility(View.GONE);
            statusText.setVisibility(View.VISIBLE);
            card.setVisibility(View.VISIBLE);
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
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    private class DrillAdapter extends RecyclerView.Adapter<DrillAdapter.ViewHolder> {
        private Drill drill;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView front;
            public TextView back;

            public ViewHolder(CardView v) {
                super(v);
                front = (TextView) v.findViewById(R.id.card_front);
                back = (TextView) v.findViewById(R.id.card_back);
            }
        }

        public DrillAdapter(Drill drill) {
            this.drill = drill;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView v = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card, parent, false);
            final TextView front = (TextView) v.findViewById(R.id.card_front);
            final TextView back = (TextView) v.findViewById(R.id.card_back);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (front.getAlpha() < 0.5) {
                        front.setAlpha(1.0f);
                        back.setAlpha(0.0f);
                    } else {
                        front.setAlpha(0.0f);
                        back.setAlpha(1.0f);
                    }
                }
            });
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.front.setText(drill.getCurrentQuestion());
            holder.back.setText(drill.getCurrentAnswer());
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallbackItemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                drill.onAnswerIncorrect();
            } else {
                drill.onAnswerCorrect();
            }
            adapter.notifyDataSetChanged();
            render();
        }
    };
}