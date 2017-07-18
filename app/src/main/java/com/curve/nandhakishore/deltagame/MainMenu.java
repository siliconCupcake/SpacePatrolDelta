package com.curve.nandhakishore.deltagame;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainMenu extends AppCompatActivity {

    ImageView title, sign;
    ImageView bg;
    dbScores scoreDB = new dbScores(this);
    ImageButton start, scores, exit, sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        uiInit();
        AudioUtils.bgm.start();
        entryAnim();
        scoreDB.open();
        BasicUtils.scores = scoreDB.getData();
        for (int i = 0; i < BasicUtils.scores.size(); i++){
            Log.e("BasicUtils.scores", BasicUtils.scores.get(i).name
                    + ", " + String.valueOf(BasicUtils.scores.get(i).score) + " at index " + String.valueOf(i));
        }

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AudioUtils.bgm_volume == 1){
                    AudioUtils.bgm_volume = 0;
                    AudioUtils.setBgmVolume();
                    sound.setImageResource(R.drawable.mute_button);
                }
                else if(AudioUtils.bgm_volume == 0){
                    AudioUtils.bgm_volume = 1;
                    AudioUtils.setBgmVolume();
                    sound.setImageResource(R.drawable.volume_button);
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent startGame = new Intent(getApplicationContext(), GameActivity.class);
                startGame.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(startGame);
                overridePendingTransition(R.anim.activity_fadein, R.anim.activity_fadeout);
                stopAndPrepare(AudioUtils.bgm);
            }
        });

        scores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent viewScores = new Intent(getApplicationContext(), HighScores.class);
                viewScores.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(viewScores);
                overridePendingTransition(R.anim.activity_fadein, R.anim.activity_fadeout);
                stopAndPrepare(AudioUtils.bgm);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmExit();
            }
        });
    }

    private void uiInit() {
        title = (ImageView) findViewById(R.id.menu_title);
        bg = (ImageView) findViewById(R.id.menu_bg);
        start = (ImageButton) findViewById(R.id.start_button);
        scores = (ImageButton) findViewById(R.id.scores_button);
        sound = (ImageButton) findViewById(R.id.sound_button);
        exit = (ImageButton) findViewById(R.id.quit_button);
        sign = (ImageView) findViewById(R.id.signature);
        Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.splash_background);
        Point screen = new Point();
        getWindowManager().getDefaultDisplay().getSize(screen);
        bg.setImageBitmap(Bitmap.createScaledBitmap(bmp, screen.x, screen.y, false));
        if(AudioUtils.bgm_volume == 1){
            sound.setImageResource(R.drawable.volume_button);
        }
        else if(AudioUtils.bgm_volume == 0){
            sound.setImageResource(R.drawable.mute_button);
        }
    }

    private void stopAndPrepare(MediaPlayer mp) {
        mp.stop();
        try{
            mp.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void confirmExit(){
        AudioUtils.bgm.pause();
        final Dialog dialog = new Dialog(this, R.style.dialog);
        dialog.setContentView(R.layout.confirm_exit);
        ImageButton yesButton = (ImageButton) dialog.findViewById(R.id.yes_button);
        ImageButton noButton = (ImageButton) dialog.findViewById(R.id.no_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioUtils.bgm.stop();
                dialog.dismiss();
                finishAffinity();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioUtils.bgm.start();
                dialog.cancel();
            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                AudioUtils.bgm.start();
            }
        });
    }

    private void entryAnim(){
        title.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.menu_title));
        start.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.start_button));
        scores.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scores_button));
        sound.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.sound_button));
        exit.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.exit_button));
        sign.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.signature));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onPause() {
        if(AudioUtils.bgm.isPlaying())
            AudioUtils.bgm.pause();
        SharedPreferences sPrefs = getSharedPreferences("Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putInt("Volume", AudioUtils.bgm_volume);
        editor.apply();
        super.onPause();
    }

    @Override
    protected void onResume() {
        AudioUtils.bgm.start();
        entryAnim();
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        scoreDB.removeRows();
        for(int n = 0; n < 3; n++) {
            try{
                scoreDB.createEntry(BasicUtils.scores.get(n));
                Log.e("Entry values", "Name - " + BasicUtils.scores.get(n).name + "Score - " + String.valueOf(BasicUtils.scores.get(n).score));
            }catch (Exception e){
                Log.d("DB", "No item at position " + String.valueOf(n));
            }
        }
    }

    @Override
    protected void onDestroy() {
        scoreDB.close();
        super.onDestroy();
    }
}
