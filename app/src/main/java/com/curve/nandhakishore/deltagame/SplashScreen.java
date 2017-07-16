package com.curve.nandhakishore.deltagame;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    ImageView title, tap, bg;
    FrameLayout fl;
    dbScores scoreDB = new dbScores(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        fl = (FrameLayout) findViewById(R.id.parent);
        title = (ImageView) findViewById(R.id.splash_title);
        tap = (ImageView) findViewById(R.id.splash_sub);
        bg = (ImageView) findViewById(R.id.splash_bg);
        audioUtils.init(this);

        Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.splash_background);
        Point screen = new Point();
        getWindowManager().getDefaultDisplay().getSize(screen);
        bg.setImageBitmap(Bitmap.createScaledBitmap(bmp, screen.x, screen.y, false));

        SharedPreferences prefs = getSharedPreferences("Preferences", MODE_PRIVATE);
        audioUtils.bgm_volume = prefs.getInt("Volume", 1);
        audioUtils.setBgmVolume();

        scoreDB.open();
        basicUtils.scores = scoreDB.getData();
        scoreDB.close();

        audioUtils.bgm.start();
        title.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_title));
        tap.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_sub));

        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMenu = new Intent(getApplicationContext(), MainMenu.class);
                goToMenu.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToMenu);
            }
        });
    }

    @Override
    public void onBackPressed() {
        confirmExit();
    }

    private void confirmExit(){
        audioUtils.bgm.pause();
        final Dialog dialog = new Dialog(this, R.style.dialog);
        dialog.setContentView(R.layout.confirm_exit);
        ImageButton yesButton = (ImageButton) dialog.findViewById(R.id.yes_button);
        ImageButton noButton = (ImageButton) dialog.findViewById(R.id.no_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioUtils.bgm.stop();
                finish();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioUtils.bgm.start();
                dialog.cancel();
            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                audioUtils.bgm.start();
            }
        });
    }

    @Override
    protected void onPause() {
        if(audioUtils.bgm.isPlaying()) {
            audioUtils.bgm.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        audioUtils.bgm.start();
        title.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_title));
        super.onResume();
    }
}
