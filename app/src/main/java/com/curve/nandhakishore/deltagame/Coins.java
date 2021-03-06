package com.curve.nandhakishore.deltagame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;

import java.util.Random;

/**
 * Created by Nandha Kishore on 25-06-2017.
 */

public class Coins {

    private Bitmap coin;
    private int x;
    private int y;
    private long slab = 500;
    private int coinHeight;
    private int coinWidth;
    private int maxX;
    private long runTime;
    private int laneHeight;
    private int currLane;
    private int speed;
    private int padding;
    private Rect collision;

    public Coins (Context context, int sx, int sy, int coin_lane, Meteor m) {

        coin = BitmapFactory.decodeResource(context.getResources(), R.drawable.coin);
        maxX = sx;
        laneHeight = sy/3;
        padding = laneHeight/3;
        coinHeight = laneHeight - (padding * 2);
        coinWidth = coinHeight;
        runTime = 0;
        currLane = coin_lane;
        coin = Bitmap.createScaledBitmap(coin, coinWidth, coinHeight, false);
        speed = 5;
        Random generator = new Random();
        y = padding + (currLane * laneHeight);
        x = maxX + generator.nextInt(maxX);
        if(m != null && x > m.getX() && x < m.getX() + m.getMeteorWidth())
            x += m.getMeteorWidth() + generator.nextInt(coinWidth);
        collision = new Rect(x, y, x + coin.getWidth(), y + coin.getHeight());
    }

    public void update() {
        if(speed < maxX/72 && runTime > slab) {
            speed += 2;
            slab += 500;
        }
        x -= speed;
        if(x < -coinWidth){
            Random generator = new Random();
            x = maxX + generator.nextInt(maxX);
        }

        collision.left = x;
        collision.right = x + coinWidth;
        collision.top = y;
        collision.bottom = y + coinHeight;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getCoinWidth() {
        return coinWidth;
    }

    public Rect getCollision() {
        return collision;
    }

    public Bitmap getCoin() {
        return coin;
    }

    public long getRunTime() {
        return runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }

    public int getX() {
        return x;
    }

    public int getSpeed() {
        return speed;
    }

    public int getY() {
        return y;
    }

    public int getCurrLane() {
        return currLane;
    }
}
