package de.jorgenschaefer.flashcarddrill.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.jorgenschaefer.flashcarddrill.drill.Drill;

public class StatusBarView extends LinearLayout {
    private Drill drill;
    private TextView decks[];
    private OnClickListener onClickListener;

    public StatusBarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setDrill(Drill drill) {
        this.drill = drill;
        decks = new TextView[drill.getDeckSizes().length];
        for (int i = 0; i < decks.length; i++) {
            decks[i] = new TextView(this.getContext());
            decks[i].setTextSize(TypedValue.COMPLEX_UNIT_PT, 12);
            decks[i].setGravity(Gravity.CENTER|Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
            );
            decks[i].setLayoutParams(lp);
            decks[i].setOnClickListener(new CardClickListener(i));
            this.addView(decks[i]);
        }
        update();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    public class CardClickListener implements View.OnClickListener {
        private int deck;

        public CardClickListener(int deck) {
            this.deck = deck;
        }

        @Override
        public void onClick(View v) {
            drill.setCurrentDeck(deck);
            update();
            onClickListener.onClick(StatusBarView.this);
        }
    }

    public void update() {
        int sizes[] = drill.getDeckSizes();
        for (int i = 0; i < sizes.length; i++) {
            decks[i].setText(String.format("%d", sizes[i]));
            if (i == drill.getCurrentDeck()) {
                decks[i].setBackgroundColor(Color.RED);
            } else {
                decks[i].setBackgroundColor(Color.WHITE);
            }
        }
    }

}
