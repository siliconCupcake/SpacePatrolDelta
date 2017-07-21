package com.curve.nandhakishore.deltagame;

import android.support.annotation.NonNull;

public class ScoreItem implements Comparable<ScoreItem>{

    int sno;
    String name;
    int score;

    public ScoreItem(int no, String n, int s) {
        sno = no;
        name = n;
        score = s;
    }

    @Override
    public int compareTo(@NonNull ScoreItem scoreItem) {
        return ((Integer) score).compareTo(scoreItem.score);
    }


}
