package com.curve.nandhakishore.deltagame;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    Dialog dialog;
    int life = 2;
    boolean swipe, detect;
    ArrayList<ImageView> lives;
    ImageButton pause;
    GameView gameView;
    Point size = new Point();
    float last_x;
    LinearLayout lv;
    CustomTextView score, bullets;
    Sensor accelerometer;
    long lastUpdate;
    SensorManager aManager;
    SensorEventListener aListener;
    private int SWIPE_MIN_DISTANCE;
    private int ACCEL_MIN_SPEED = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        SWIPE_MIN_DISTANCE = (size.y/6);
        lastUpdate = 0;
        detect = true;
        swipe = getIntent().getBooleanExtra("controls", swipe);

        aManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = aManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        aListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(!swipe && sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER && gameView.getPlayer().getState() == 0){
                    float x = sensorEvent.values[0];
                    if (x > -3 && x < 3)
                        detect = true;
                    else
                        detect = false;

                    if (detect && (System.currentTimeMillis() - lastUpdate) > 100) {
                        Log.e("GameActivity", "Accelerometer X: " + String.valueOf(x));
                        long diffTime = (System.currentTimeMillis() - lastUpdate);
                        lastUpdate = System.currentTimeMillis();
                        float speed = (x - last_x)/ diffTime * 10000;
                        try {
                            if (speed < 0 && Math.abs(speed) > ACCEL_MIN_SPEED) {    //swipe up
                                if (gameView.getPlayer().getLane() > -1)
                                    gameView.setTarget(gameView.getPlayer().getLane() - 1);
                                else
                                    gameView.setTarget(gameView.getPlayer().getLane());
                            } else if (speed > 0 && Math.abs(speed) > ACCEL_MIN_SPEED) {     //swipe down
                                if (gameView.getPlayer().getLane() < 1)
                                    gameView.setTarget(gameView.getPlayer().getLane() + 1);
                                else
                                    gameView.setTarget(gameView.getPlayer().getLane());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        last_x = x;
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        lives = new ArrayList<>();
        livesInit();
        score = (CustomTextView) findViewById(R.id.score);
        bullets = (CustomTextView) findViewById(R.id.bullet_count);
        BasicUtils.handleScores = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                score.setText(String.valueOf(gameView.getPlayer().getScore()));
                bullets.setText(String.valueOf(gameView.getBulletCount()));
                super.handleMessage(msg);
            }
        };

        BasicUtils.handleLives = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Animation life_lost = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.life_lost);
                life_lost.setAnimationListener(new Animation.AnimationListener() {
                    int curr;

                    @Override
                    public void onAnimationStart(Animation animation) {
                        curr = life;
                        life--;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        lives.get(curr).setVisibility(View.GONE);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                try {
                    lives.get(gameView.getPlayer().getLives()).startAnimation(life_lost);
                }catch (Exception e){
                    if(gameView.getPlayer().getLives() < 0)
                        gameView.getPlayer().setLives(0);
                }
                if(gameView.getPlayer().getLives() == 0)
                    gameOver();
                super.handleMessage(msg);
            }
        };

        gameView = new GameView(this, size.x, size.y);
        gameView.resume();
        lv = (LinearLayout) findViewById(R.id.linear_canvas);
        pause = (ImageButton) findViewById(R.id.pause_button);
        lv.addView(gameView);
        dialog = new Dialog(this, R.style.dialog);

        final GestureDetector gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        pause = (ImageButton) findViewById(R.id.pause_button);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameView.pause();
                gamePaused();
            }
        });
    }

    private void gamePaused(){

        aManager.unregisterListener(aListener);
        dialog.setContentView(R.layout.pause_menu);
        dialog.setCancelable(false);
        ImageButton resumeButton = (ImageButton) dialog.findViewById(R.id.resume_button);
        ImageButton quitButton = (ImageButton) dialog.findViewById(R.id.quit_button);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                aManager.registerListener(aListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
                gameView.resume();
            }
        });
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dialog.show();
    }

    private void gameOver() {
        gameView.pause();
        aManager.unregisterListener(aListener);
        dialog.setContentView(R.layout.game_over);
        dialog.setCancelable(false);
        CustomTextView score = (CustomTextView) dialog.findViewById(R.id.your_score);
        CustomTextView highScore = (CustomTextView) dialog.findViewById(R.id.high_score);
        score.setText(String.valueOf(gameView.getPlayer().getScore()));
        if(!BasicUtils.scores.isEmpty() && gameView.getPlayer().getScore() < BasicUtils.scores.get(0).score) {
            highScore.setText("HIGH SCORE: ".concat(String.valueOf(BasicUtils.scores.get(0).score)));
        }
        else {
            highScore.setText("HIGH SCORE: ".concat(String.valueOf(gameView.getPlayer().getScore())));
        }
        score.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.final_score));
        ImageButton retryButton = (ImageButton) dialog.findViewById(R.id.retry_button);
        ImageButton homeButton = (ImageButton) dialog.findViewById(R.id.home_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomEditText pName = (CustomEditText) dialog.findViewById(R.id.get_name);
                if(TextUtils.isEmpty(pName.getText())){
                    pName.setText("FBI");
                }
                ScoreItem current = new ScoreItem(BasicUtils.scores.size(), pName.getText().toString(), (int) gameView.getPlayer().getScore());
                BasicUtils.scores.add(current);
                BasicUtils.sortScores();
                dialog.dismiss();
                reload();
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomEditText pName = (CustomEditText) dialog.findViewById(R.id.get_name);
                if(TextUtils.isEmpty(pName.getText())){
                    pName.setText("FBI");
                }
                ScoreItem current = new ScoreItem(BasicUtils.scores.size(), pName.getText().toString(), (int) gameView.getPlayer().getScore());
                BasicUtils.scores.add(current);
                BasicUtils.sortScores();
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            if(swipe && gameView.getPlayer().getState() == 0) {
                try {
                    if (Math.abs(e1.getX() - e2.getX()) > 250) {
                        return false;
                    }

                    if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                            && Math.abs(velocityY) > 50) {
                        if (gameView.getPlayer().getLane() > -1)
                            gameView.setTarget(gameView.getPlayer().getLane() - 1);
                        else
                            gameView.setTarget(gameView.getPlayer().getLane());
                    } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                            && Math.abs(velocityY) > 50) {
                        if (gameView.getPlayer().getLane() < 1)
                            gameView.setTarget(gameView.getPlayer().getLane() + 1);
                        else
                            gameView.setTarget(gameView.getPlayer().getLane());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            gameView.createBullet();
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }
    }

    private void livesInit() {
        lives.add((ImageView) findViewById(R.id.life_1));
        lives.add((ImageView) findViewById(R.id.life_2));
        lives.add((ImageView) findViewById(R.id.life_3));
    }

    @Override
    protected void onPause() {
        gameView.pause();
        gamePaused();
        super.onPause();
    }

    @Override
    protected void onResume(){
        try {
            aManager.registerListener(aListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }catch (Exception e) {
            Log.e("GameActivity", "Listener already registered");
        }
        if(gameView.getPlayer().getLives() == 0) {
            dialog.dismiss();
            gameOver();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        gameView.pause();
        gamePaused();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }
}
