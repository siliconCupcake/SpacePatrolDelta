package com.curve.nandhakishore.deltagame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import java.util.Random;

/**
 * Created by Nandha Kishore on 16-07-2017.
 */

public class Meteor {

    private Bitmap meteor;
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

    public Meteor(Context context, int sx, int sy, int meteor_lane){
        meteor = BitmapFactory.decodeResource(context.getResources(), R.drawable.meteor);
        maxX = sx;
        laneHeight = sy/3;
        padding = laneHeight/6;
        meteorHeight = laneHeight - (padding * 2);
        meteorWidth = meteorHeight;
        currLane = meteor_lane;
        meteor = Bitmap.createScaledBitmap(meteor, meteorWidth, meteorHeight, false);
        speed = 10;
        Random generator = new Random();
        y = padding + (currLane * laneHeight);
        x = maxX + generator.nextInt(maxX);
        Log.e("Coin", "Width = " + String.valueOf(meteorWidth) + ", Height = " + String.valueOf(meteorHeight));
        collision = new Rect(x, y, x + meteor.getWidth(), y + meteor.getHeight());
    }


}
