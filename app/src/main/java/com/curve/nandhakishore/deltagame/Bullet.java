package com.curve.nandhakishore.deltagame;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;


public class Bullet {

    private Bitmap bullet;
    private int x;
    private int y;
    private int bulletHeight;
    private int bulletWidth;
    private int minX;
    private int maxX;
    private int speed;
    private int padding;
    private Rect collision;

    public Bullet(Context con, int sx, int sy, Player p, boolean top){
        bullet = BitmapFactory.decodeResource(con.getResources(), R.drawable.bullet);
        bulletHeight = sy/18;
        bulletWidth = sy/18;
        padding = bulletHeight;
        speed = 10;
        bullet = Bitmap.createScaledBitmap(bullet, bulletWidth, bulletHeight, false);
        minX = p.getX() + (3 * p.getShipWidth()/4);
        maxX = sx;
        x = minX;
        if(top)
            y = p.getY() + (5 * p.getShipHeight()/18) - bulletHeight;
        else
            y = p.getY() + (12 * p.getShipHeight()/18);
        collision = new Rect(x, y, x + bulletWidth, y + bulletHeight);
    }

    public void update(){

        x += speed;

        collision.left = x;
        collision.top = y;
        collision.right = x + bulletWidth;
        collision.bottom = y + bulletHeight;
    }

    public Bitmap getBullet() {
        return bullet;
    }

    public Rect getCollision() {
        return collision;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
