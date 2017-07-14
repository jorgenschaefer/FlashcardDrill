package de.jorgenschaefer.flashcarddrill;

import android.databinding.DataBindingUtil;
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

import de.jorgenschaefer.flashcarddrill.databinding.ActivityMainBinding;
import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;
import de.jorgenschaefer.flashcarddrill.drill.Drill;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private static final String STATE_STUDYVIEWMODEL = "studyViewModel";

    private GestureDetectorCompat detector;
    private StudyViewModel studyView;
    private Drill drill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new GestureDetectorCompat(this, this);

        CardsDbHelper dbHelper = new CardsDbHelper(getApplicationContext());
        drill = new Drill(dbHelper);
        studyView = new StudyViewModel(drill);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setStudyView(studyView);

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
                studyView.onLoadCards(view);
                return true;
            case R.id.action_clear:
                studyView.onClearCards(view);
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
        studyView.onFlipCard();
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
        View view = findViewById(android.R.id.content);
        float distanceX = e2.getX() - e1.getX();
        if (distanceX < 0) {
            studyView.onAnswerIncorrect(view);
        } else {
            studyView.onAnswerCorrect(view);
        }
        return true;
    }
}
