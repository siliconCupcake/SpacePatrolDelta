package com.curve.nandhakishore.deltagame;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Set;

public class GameActivity extends AppCompatActivity {

    Dialog dialog;
    ImageButton pause;
    GameView gameView;
    LinearLayout lv;
    private int SWIPE_MIN_DISTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SWIPE_MIN_DISTANCE = (size.y/6);

        gameView = new GameView(this, size.x, size.y);
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

    public class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            try {
                if (Math.abs(e1.getX() - e2.getX()) > 250){
                    return false;
                }

                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityY) > 80) {
                    if(gameView.getPlayer().getLane() > -1)
                        gameView.setTarget(gameView.getPlayer().getLane() - 1);
                    else
                        gameView.setTarget(gameView.getPlayer().getLane());
                }

                else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityY) > 80) {
                    if(gameView.getPlayer().getLane() < 1)
                        gameView.setTarget(gameView.getPlayer().getLane() + 1);
                    else
                        gameView.setTarget(gameView.getPlayer().getLane());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }
    }

    @Override
    protected void onPause() {
        gamePaused();
        gameView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        gameView.resume();
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
