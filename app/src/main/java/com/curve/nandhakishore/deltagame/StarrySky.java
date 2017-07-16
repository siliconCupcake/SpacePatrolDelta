package com.curve.nandhakishore.deltagame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

/**
 * Created by Nandha Kishore on 24-06-2017.
 */

public class StarrySky {
    private int x;
    private int y;
    private int speed;
    private Bitmap star;
    private int maxX;
    private int maxY;
    private int minX;
    private int minY;



    public StarrySky(Context context, int screenX, int screenY) {
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;
        Random generator = new Random();
        speed = generator.nextInt(9) + 1;
        star = BitmapFactory.decodeResource(context.getResources(), R.drawable.star);
        x = generator.nextInt(maxX);
        y = generator.nextInt(maxY);
        setStarWidth();
    }

    public void update() {

        x -= speed;

        if (x < 0) {
            x = maxX;
            Random generator = new Random();
            y = generator.nextInt(maxY);
            speed = generator.nextInt(9) + 1;
        }
    }

    public void setStarWidth() {
        Random rand = new Random();
        int finalX = rand.nextInt(15) + 1;
        star = Bitmap.createScaledBitmap(star, finalX, finalX, false);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Bitmap getStar() {
        return star;
    }
}
