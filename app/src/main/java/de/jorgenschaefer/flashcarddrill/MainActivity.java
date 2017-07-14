package de.jorgenschaefer.flashcarddrill;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.jorgenschaefer.flashcarddrill.databinding.ActivityMainBinding;
import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;

public class MainActivity extends AppCompatActivity {
    private static final String STATE_STUDYVIEWMODEL = "studyViewModel";

    StudyViewModel drill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CardsDbHelper dbHelper = new CardsDbHelper(getApplicationContext());
        drill = new StudyViewModel(dbHelper);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setDrill(drill);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            drill.setState(savedInstanceState.getBundle(STATE_STUDYVIEWMODEL));
        }
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
                drill.onLoadCards(view);
                return true;
            case R.id.action_clear:
                drill.onClearCards(view);
                return true;
            case R.id.action_settings:
                Snackbar.make(view, "No settings yet!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
