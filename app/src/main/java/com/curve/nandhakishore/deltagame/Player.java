package com.curve.nandhakishore.deltagame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Player {

    private boolean first = true;
    private Bitmap ship;
    private Bitmap ship_up;
    private Bitmap ship_down;
    private Bitmap ship_gone;
    private Bitmap bmp;
    private int x;
    private int y;
    private int shipHeight;
    private int shipWidth;
    private int minY;
    private int maxY;
    private int laneHeight;
    private int currLane;
    private int now;
    private int speed;
    private int state;
    private int padding;
    private long score;
    private int lives;
    private Rect collision;

    public Player(Context context, int sx, int sy){
        x = sx/48;
        ship = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_straight);
        ship_up = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_up);
        ship_down = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_down);
        ship_gone = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_gone);
        score = 0;
        lives = 3;
        laneHeight = sy/3;
        padding = laneHeight/12;
        shipHeight = laneHeight - (padding * 2);
        shipWidth = shipHeight * 6/5;
        scaleBitmaps();
        bmp = ship;
        currLane = 0;
        maxY = sy - shipHeight - padding;
        minY = padding;
        speed = sy/30;
        state = 0;
        y = laneHeight + padding;
        collision = new Rect(x, y, x + shipWidth, y + shipHeight);
    }

    private void scaleBitmaps(){
        ship = Bitmap.createScaledBitmap(ship, shipWidth, shipHeight, false);
        ship_up = Bitmap.createScaledBitmap(ship_up, shipWidth, shipHeight, false);
        ship_down = Bitmap.createScaledBitmap(ship_down, shipWidth, shipHeight, false);
        ship_gone = Bitmap.createScaledBitmap(ship_gone, laneHeight * 6/5, laneHeight, false);
    }

    public long update(int toLane){
        if(first){
            now = y;
            first = false;
        }

        if(currLane != toLane) {
            state = 1;
            if (currLane - toLane > 0) {
                if (now - y < laneHeight) {
                    bmp = ship_up;
                    y -= speed;
                }
                else {
                    currLane = toLane;
                    first = true;
                    bmp = ship;
                    state = 0;
                }
            } else if (currLane - toLane < 0) {
                if (y - now < laneHeight) {
                    bmp = ship_down;
                    y += speed;
                }
                else {
                    currLane = toLane;
                    first = true;
                    bmp = ship;
                    state = 0;
                }
            }
        }

        if (y > maxY)
            y = maxY;
        if (y < minY)
            y = minY;

        collision.left = x;
        collision.right = x + shipWidth;
        collision.top = y;
        collision.bottom = y + shipHeight;

        return score;
    }

    public void shipCrashed(){
        bmp = ship_gone;
    }

    public Bitmap getShip() {
        return bmp;
    }

    public Rect getCollision() {
        return collision;
    }

    public void incScore() {
        score++;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLane() {
        return currLane;
    }

    public int getState() {
        return state;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getScore() {
        return score;
    }

    public void decLife() {
        lives--;
    }

    public int getLives() {
        return lives;
    }
}
