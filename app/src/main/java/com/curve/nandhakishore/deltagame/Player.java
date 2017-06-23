package com.curve.nandhakishore.deltagame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by Nandha Kishore on 22-06-2017.
 */

public class Player {

    private boolean first = true;
    private Bitmap ship;
    private int x, y, shipHeight, shipWidth, minY, maxY, laneHeight, currLane, now, speed;

    public Player(Context context, int sx, int sy){
        x = 40;
        ship = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_straight);
        shipHeight = ship.getHeight();
        shipWidth = ship.getWidth();
        currLane = 0;
        maxY = sy - shipHeight - 30;
        minY = 30;
        speed = sy/36;
        laneHeight = sy/3;
        y = laneHeight + 30;
    }

    public void update(int toLane){
        if(first){
            now = y;
            first = false;
        }

        if(currLane == toLane){
            currLane = toLane;
            first = true;
        }
        else if(currLane - toLane > 0){
            if(now - y < laneHeight)
                y-=speed;
            else{
                currLane = toLane;
                first = true;
            }
        }
        else if(currLane - toLane < 0){
            if(y - now < laneHeight)
                y+=speed;
            else{
                currLane = toLane;
                first = true;
            }
        }

        if (y > maxY)
            y = maxY;
        if (y < minY)
            y = minY;
    }

    public Bitmap getShip() {
        return ship;
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
}
