package com.curve.nandhakishore.deltagame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing = true;
    private int target = 0;
    private Thread gameThread = null;
    private Player player;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder sHolder;


    public GameView(Context con, int screenX, int screenY) {
        super(con);
        player = new Player(con, screenX, screenY);
        sHolder = getHolder();
        paint = new Paint();
    }


    @Override
    public void run() {
        while(playing){
            update();
            draw();
            control();
        }
    }

    private void update(){
        player.update(target);
    }

    private void draw(){
        if (sHolder.getSurface().isValid()) {
            canvas = sHolder.lockCanvas();
            canvas.drawColor(Color.rgb(7, 19, 42));
            canvas.drawBitmap(player.getShip(), player.getX(), player.getY(), paint);
            sHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            gameThread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public Player getPlayer() {
        return player;
    }
}

