package com.curve.nandhakishore.deltagame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;


public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing = true;
    private int target = 0;
    private Thread gameThread = null;
    private long score;
    private Player player;
    private long threadTime, pastTime;
    private Context context;
    private int sx, sy;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder sHolder;
    private ArrayList<StarrySky> stars = new ArrayList<>();
    private ArrayList<Coins> coins = new ArrayList<>();
    private ArrayList<Meteor> meteors = new ArrayList<>();

    public GameView(Context con, int screenX, int screenY) {
        super(con);
        this.context = con;
        sx = screenX;
        sy = screenY;
        pastTime = 0;
        player = new Player(con, screenX, screenY);
        sHolder = getHolder();
        paint = new Paint();

        for (int i = 0; i < 75; i++) {
            StarrySky s = new StarrySky(con, screenX, screenY);
            stars.add(s);
        }

        for (int i = 0; i < 3; i++) {
            Coins c = new Coins(con, screenX, screenY, i, getMeteorAtLane(i));
            coins.add(c);
        }

        ArrayList<Integer> laneList = new ArrayList<>();
        laneList.add(0); laneList.add(1); laneList.add(2);

        for (int i = 0; i < 2; i++) {
            Random generator = new Random();
            int lane = generator.nextInt(laneList.size());
            Meteor m = new Meteor(con, screenX, screenY, laneList.get(lane), getCoinAtLane(laneList.get(lane)));
            laneList.remove(lane);
            meteors.add(m);
        }
    }


    @Override
    public void run() {
        while(playing){
            update();
            draw();
            control();
            BasicUtils.handleScores.sendMessage(new Message());
        }
    }

    private void update(){
        score = player.update(target);
        for(StarrySky s : stars) {
            s.update();
        }

        threadTime = SystemClock.currentThreadTimeMillis();
        for(Coins c : coins) {
            c.setRunTime(pastTime + threadTime);
            c.update();
            if(Rect.intersects(player.getCollision(), c.getCollision())){
                try {
                    AudioUtils.coin.stop();
                    AudioUtils.coin.prepare();
                }catch (Exception e){
                    e.printStackTrace();
                }
                AudioUtils.coin.start();
                c.setX(-200);
                player.incScore();
            }
        }

        ArrayList<Meteor> tmp = new ArrayList<>();
        for(Meteor m : meteors){
            m.setRunTime(pastTime + threadTime);
            int check = m.update();
            if(check == 1) {
                int newLane = nextRandomLane();
                Meteor tm = new Meteor(context, sx, sy, newLane, getCoinAtLane(newLane));
                tm.setSpeed(m.getSpeed());
                tmp.add(tm);
            }
            else if(m.getBmp() == m.getMeteor() && Rect.intersects(player.getCollision(), m.getCollision())){
                m.setBmp(m.getRubble());
                tmp.add(m);
                BasicUtils.handleLives.sendMessage(new Message());
                player.decLife();
                if(player.getLives() == 0){
                    player.shipCrashed();
                    try{
                        AudioUtils.explosion.stop();
                        AudioUtils.explosion.prepare();
                    }catch (Exception e) {
                    }
                    AudioUtils.explosion.start();
                }
                try {
                    AudioUtils.meteor.stop();
                    AudioUtils.meteor.prepare();
                }catch (Exception e){
                    e.printStackTrace();
                }
                AudioUtils.meteor.start();
            }
            else {
                tmp.add(m);
            }
        }
        meteors = tmp;
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

            for (Meteor m : meteors){
                canvas.drawBitmap(m.getBmp(), m.getX(), m.getY(), paint);
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
        pastTime += threadTime;
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private int nextRandomLane(){
        int nextLane = 3;
        for(Meteor m : meteors){
            nextLane -= m.getCurrLane();
        }
        return nextLane;
    }

    private Coins getCoinAtLane (int lane) {
        for(Coins c : coins){
            if(c.getCurrLane() == lane)
                return c;
        }
        return null;
    }

    private Meteor getMeteorAtLane (int lane) {
        for (Meteor m : meteors)
            if(m.getCurrLane() == lane)
                return m;
        return null;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public Player getPlayer() {
        return player;
    }
}

