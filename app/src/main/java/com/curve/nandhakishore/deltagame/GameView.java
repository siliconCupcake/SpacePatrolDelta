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
import android.view.MotionEvent;
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
    private int bulletCount = 2;
    private boolean top = true;
    private long threadTime, pastTime, bulletTime;
    private Context context;
    private int sx, sy;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder sHolder;
    private ArrayList<StarrySky> stars = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Coins> coins = new ArrayList<>();
    private ArrayList<Meteor> meteors = new ArrayList<>();

    public GameView(Context con, int screenX, int screenY) {
        super(con);
        this.context = con;
        sx = screenX;
        sy = screenY;
        pastTime = 0;
        bulletTime = 0;
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

        ArrayList<Bullet> bt = new ArrayList<>();
        for (Bullet b : bullets){
            b.update();
            if (b.getX() != sx)
                bt.add(b);
        }
        bullets = bt;

        ArrayList<Meteor> tmp = new ArrayList<>();
        for(Meteor m : meteors){
            m.setRunTime(pastTime + threadTime);
            int check = m.update();
            if(check == 1) {
                int newLane = nextRandomLane(m);
                Meteor tm = new Meteor(context, sx, sy, newLane, getCoinAtLane(newLane));
                tm.setSpeed(m.getSpeed());
                tmp.add(tm);
            }
            else if (hitBullet(m)){
                m.setBmp(m.getRubble());
                tmp.add(m);
                try {
                    AudioUtils.meteor.stop();
                    AudioUtils.meteor.prepare();
                }catch (Exception e){
                    e.printStackTrace();
                }
                AudioUtils.meteor.start();
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

            for (Bullet b : bullets){
                canvas.drawBitmap(b.getBullet(), b.getX(), b.getY(), paint);
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

    public void createBullet(){
        if(bulletCount > 0){
            final Bullet b = new Bullet(context, sx, sy, player, top);
            top = !top;
            bullets.add(b);
            bulletCount--;
            Thread timer = new Thread(){
                @Override
                public void run() {
                    try{
                        sleep(2000);
                    }catch (Exception e){
                        Log.e("TimerThread", "Some error");
                    }
                    finally {
                        bulletCount++;
                    }
                    super.run();
                }
            };
            timer.start();
        }
    }

    private boolean hitBullet(Meteor m){
        ArrayList<Bullet> tb = new ArrayList<>();
        boolean ans = false;
        for (Bullet b : bullets){
            if (b.getCollision().intersect(m.getCollision()))
                ans = true;
            else
                tb.add(b);
        }
        bullets = tb;
        return ans;
    }

    private int nextRandomLane(Meteor met){
        int nextLane = 3;
        if(meteors.get(0).getX() == meteors.get(1).getX())
            return met.getCurrLane();
        for(Meteor m : meteors){
            nextLane -= m.getCurrLane();
        }
        return nextLane;
    }

    public int getBulletCount() {
        return bulletCount;
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

