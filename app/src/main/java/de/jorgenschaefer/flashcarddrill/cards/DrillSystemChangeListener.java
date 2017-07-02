package de.jorgenschaefer.flashcarddrill.cards;

/**
 * Created by jorgen on 02.07.17.
 */

public interface DrillSystemChangeListener {
    void onCurrentCardChanged();
    void onDeckSizesChanged();
}
