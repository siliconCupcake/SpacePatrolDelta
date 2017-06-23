package com.curve.nandhakishore.deltagame;

public class scoreItem {

    int rank = 0, sno;
    String name;
    long score;

    public scoreItem (int r, String n, long s) {
        sno = r;
        name = n;
        score = s;
    }

    public void setRank(int r) {
        rank = r;
    }
}
