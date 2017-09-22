package de.jorgenschaefer.flashcarddrill;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;
import de.jorgenschaefer.flashcarddrill.drill.DeckInfo;

public class DeckInfoActivity extends AppCompatActivity {
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        layout = (LinearLayout) findViewById(R.id.deck_info);
        CardsDbHelper repository = new CardsDbHelper(this.getApplicationContext());
        List<DeckInfo> infoList = repository.getDeckInfoList();
        for (int i = 0; i < infoList.size(); i++) {
            addInfo("Deck %d, %d due of %d total", i, infoList.get(i).getDue(), infoList.get(i).getTotal());
        }
    }

    public void addInfo(String format, Object... args) {
        TextView tv = new TextView(this.getApplicationContext());
        tv.setText(String.format(format, args));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PT, 14);
        layout.addView(tv);
    }
}
