package com.curve.nandhakishore.deltagame;

import android.app.Dialog;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    Dialog dialog;
    int life = 2;
    ArrayList<ImageView> lives;
    ImageButton pause;
    float density;
    GameView gameView;
    Point size = new Point();
    LinearLayout lv;
    customTextView score;
    private int SWIPE_MIN_DISTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        density = getResources().getDisplayMetrics().density;
        Log.e("Density", String.valueOf(density));
        SWIPE_MIN_DISTANCE = (size.y/6);

        lives = new ArrayList<>();
        livesInit();

        score = (customTextView) findViewById(R.id.score);
        basicUtils.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                score.setText(String.valueOf(gameView.getPlayer().getScore()));
                super.handleMessage(msg);
            }
        };

        gameView = new GameView(this, size.x, size.y, density);
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

        dialog.setContentView(R.layout.pause_menu);
        ImageButton resumeButton = (ImageButton) dialog.findViewById(R.id.resume_button);
        ImageButton quitButton = (ImageButton) dialog.findViewById(R.id.quit_button);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
        dialog.setContentView(R.layout.game_over);
        ImageButton retryButton = (ImageButton) dialog.findViewById(R.id.retry_button);
        ImageButton homeButton = (ImageButton) dialog.findViewById(R.id.home_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                gameView = new GameView(getApplicationContext(), size.x, size.y, density);
                gameView.resume();
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dialog.show();
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            if(gameView.getPlayer().getState() == 0) {
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        gameView.pause();
        gamePaused();
    }

    @Override
    protected void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }
}
