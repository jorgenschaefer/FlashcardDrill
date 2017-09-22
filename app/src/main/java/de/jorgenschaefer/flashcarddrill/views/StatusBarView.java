package de.jorgenschaefer.flashcarddrill.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.jorgenschaefer.flashcarddrill.drill.Drill;

public class StatusBarView extends LinearLayout {
    private Drill drill;
    private TextView status;

    public StatusBarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setDrill(Drill drill) {
        this.drill = drill;
        status = new TextView(this.getContext());
        status.setTextSize(TypedValue.COMPLEX_UNIT_PT, 12);
        status.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
        );
        status.setLayoutParams(lp);
        this.addView(status);
    }

    public void notifyDataSetChanged() {
        status.setText(String.format("%d / %d", drill.getNumberOfDueCards(), drill.getNumberOfTotalCards()));
    }
}
