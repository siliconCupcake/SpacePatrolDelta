package com.curve.nandhakishore.deltagame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;


public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing = true;
    private int target = 0;
    private Thread gameThread = null;
    private long score;
    private Player player;
    private float dpi;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder sHolder;
    private ArrayList<StarrySky> stars = new ArrayList<>();
    private ArrayList<Coins> coins = new ArrayList<>();


    public GameView(Context con, int screenX, int screenY, float dpi) {
        super(con);
        player = new Player(con, screenX, screenY);
        sHolder = getHolder();
        paint = new Paint();
        this.dpi = dpi;

        for (int i = 0; i < 75; i++) {
            StarrySky s = new StarrySky(con, screenX, screenY);
            stars.add(s);
        }

        for (int i = 0; i < 3; i++) {
            Coins c = new Coins(con, screenX, screenY, i);
            coins.add(c);
        }
    }


    @Override
    public void run() {
        while(playing){
            update();
            draw();
            control();
            basicUtils.handler.sendMessage(new Message());
        }
    }

    private void update(){
        score = player.update(target);
        for(StarrySky s : stars) {
            s.update();
        }
        for(Coins c : coins) {
            c.update(score);
            if(Rect.intersects(player.getCollision(), c.getCollision())){
                try {
                    audioUtils.coin.stop();
                    audioUtils.coin.prepare();
                }catch (Exception e){
                    e.printStackTrace();
                }
                audioUtils.coin.start();
                c.setX(-200);
                player.incScore();
            }
        }

    }

    private void draw(){
        if (sHolder.getSurface().isValid()) {
            canvas = sHolder.lockCanvas();
            canvas.drawColor(Color.parseColor("#000010"));

            for (StarrySky s : stars) {
                canvas.drawBitmap(s.getStar(), s.getX(), s.getY(), paint);
            }

            for (Coins c : coins) {
                canvas.drawBitmap(c.getCoin(), c.getX(), c.getY(), paint);
            }

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
            e.printStackTrace();
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

