package com.curve.nandhakishore.deltagame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;
import java.util.Random;

public class Meteor {

    private Bitmap meteor, rubble, bmp;
    private int x;
    private int y;
    private long slab = 10;
    private int meteorHeight;
    private int meteorWidth;
    private int maxX;
    private int laneHeight;
    private int currLane;
    private int speed;
    private int padding;
    private Rect collision;

    public Meteor(Context context, int sx, int sy, int meteor_lane, Coins c){
        meteor = BitmapFactory.decodeResource(context.getResources(), R.drawable.meteor);
        rubble = BitmapFactory.decodeResource(context.getResources(), R.drawable.rubble);
        maxX = sx;
        laneHeight = sy/3;
        padding = laneHeight/4;
        meteorHeight = laneHeight - (padding * 2);
        meteorWidth = meteorHeight;
        currLane = meteor_lane;
        meteor = Bitmap.createScaledBitmap(meteor, meteorWidth, meteorHeight, false);
        rubble = Bitmap.createScaledBitmap(rubble, meteorWidth, meteorHeight, false);
        bmp = meteor;
        speed = 10;
        Random generator = new Random();
        y = padding + (currLane * laneHeight);
        x = maxX + generator.nextInt(maxX);
        if (c != null && x > c.getX() && x < c.getX() + c.getCoinWidth())
            x += c.getCoinWidth() + generator.nextInt(meteorWidth);
        collision = new Rect(x, y, x + meteor.getWidth(), y + meteor.getHeight());
    }

    public int update(long score){
        if(speed < maxX/72 && score > slab){
            speed += 2;
            slab += 10000;
        }
        x -= speed;

        collision.left = x;
        collision.right = x + meteorWidth;
        collision.top = y;
        collision.bottom = y + meteorHeight;

        if(x < -meteorWidth)
            return 1;
        else
            return 0;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public Bitmap getMeteor() {
        return meteor;
    }

    public Bitmap getRubble() {
        return rubble;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getMeteorWidth() {
        return meteorWidth;
    }

    public int getCurrLane() {
        return currLane;
    }

    public Rect getCollision() {
        return collision;
    }

    public void setX(int x) {
        this.x = x;
    }
}
