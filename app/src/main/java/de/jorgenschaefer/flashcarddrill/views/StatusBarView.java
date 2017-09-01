package de.jorgenschaefer.flashcarddrill.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import de.jorgenschaefer.flashcarddrill.R;
import de.jorgenschaefer.flashcarddrill.drill.DeckInfo;
import de.jorgenschaefer.flashcarddrill.drill.Drill;

public class StatusBarView extends LinearLayout {
    private Drill drill;
    private TextView decks[];

    public StatusBarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setDrill(Drill drill) {
        this.drill = drill;
        decks = new TextView[drill.getDeckSizes().size()];
        for (int i = 0; i < decks.length; i++) {
            decks[i] = new TextView(this.getContext());
            decks[i].setTextSize(TypedValue.COMPLEX_UNIT_PT, 12);
            decks[i].setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
            );
            decks[i].setLayoutParams(lp);
            decks[i].setOnClickListener(new CardClickListener(i));
            this.addView(decks[i]);
        }
        notifyDataSetChanged();
    }

    public class CardClickListener implements View.OnClickListener {
        private int deck;

        public CardClickListener(int deck) {
            this.deck = deck;
        }

        @Override
        public void onClick(View v) {
            drill.setCurrentDeck(deck);
        }
    }

    public void notifyDataSetChanged() {
        List<DeckInfo> infos = drill.getDeckSizes();
        for (int i = 0; i < infos.size(); i++) {
            DeckInfo info = infos.get(i);
            decks[i].setText(String.format("%d", info.getSize()));
            decks[i].setBackground(getBackground(i, info.getOldest()));
        }
    }

    private Drawable getBackground(int deck, long oldest) {
        boolean current = deck == drill.getCurrentDeck();
        boolean outdated = isOutdated(deck, oldest);

        int resource;

        if (current && outdated) {
            resource = R.drawable.status_current_outdated;
        } else if (current && !outdated) {
            resource = R.drawable.status_current_notoutdated;
        } else if (!current && outdated) {
            resource = R.drawable.status_notcurrent_outdated;
        } else {
            resource = R.drawable.status_notcurrent_notoutdated;
        }
        return ResourcesCompat.getDrawable(getResources(), resource, null);
    }

    private boolean isOutdated(int deck, long oldest) {
        long day = 1000 * 60 * 60 * 24;
        long age = new Date().getTime() - oldest;

        if (oldest == 0) {
            return false;
        }
        switch (deck) {
            case 0:
                return age > 1 * day;
            case 1:
                return age > 3 * day;
            case 2:
                return age > 7 * day;
            case 3:
                return age > 14 * day;
            case 4:
                return age > 28 * day;
            default:
                return false;
        }
    }
}
