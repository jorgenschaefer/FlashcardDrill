package de.jorgenschaefer.flashcarddrill.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import de.jorgenschaefer.flashcarddrill.R;
import de.jorgenschaefer.flashcarddrill.drill.Drill;

public class FlashCardView extends RecyclerView {
    private Drill drill;

    public FlashCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setHasFixedSize(true);
        this.setLayoutManager(new LinearLayoutManager(context));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeLeftRightCallback);
        itemTouchHelper.attachToRecyclerView(this);
    }

    public void setDrill(Drill drill) {
        this.drill = drill;
        setAdapter(new FlashCardView.DrillAdapter(drill));
    }

    public void notifyDataSetChanged() {
        getAdapter().notifyDataSetChanged();
    }

    private final ItemTouchHelper.SimpleCallback swipeLeftRightCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                drill.onAnswerIncorrect();
            } else {
                drill.onAnswerCorrect();
            }
        }
    };

    private class DrillAdapter extends RecyclerView.Adapter<DrillAdapter.ViewHolder> {
        private Drill drill;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView front;
            public TextView back;
            public TextView deck;
            public TextView due;

            public ViewHolder(CardView v, TextView front, TextView back, TextView deck, TextView due) {
                super(v);
                this.front = front;
                this.back = back;
                this.deck = deck;
                this.due = due;
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
            final TextView deck = (TextView) v.findViewById(R.id.card_deck);
            final TextView due = (TextView) v.findViewById(R.id.card_due);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (front.getVisibility() != View.VISIBLE) {
                        front.setVisibility(View.VISIBLE);
                        back.setVisibility(View.INVISIBLE);
                    } else {
                        front.setVisibility(View.INVISIBLE);
                        back.setVisibility(View.VISIBLE);
                    }
                }
            });
            return new ViewHolder(v, front, back, deck, due);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String currentDueDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(drill.getCurrentDueDate());

            holder.deck.setText("Deck " + drill.getCurrentDeck());
            holder.due.setText("Due at " + currentDueDate);
            holder.front.setText(drill.getCurrentQuestion());
            holder.back.setText(drill.getCurrentAnswer());
            if (drill.getCurrentAnswer().contains("\n")) {
                holder.back.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            } else {
                holder.back.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            }
            holder.front.setVisibility(View.VISIBLE);
            holder.back.setVisibility(View.INVISIBLE);
        }

        @Override
        public int getItemCount() {
            if (drill.hasDueCards()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
