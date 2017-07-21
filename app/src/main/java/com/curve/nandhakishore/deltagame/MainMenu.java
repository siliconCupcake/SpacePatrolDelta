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

import java.util.Collections;
import java.util.Comparator;

public class MainMenu extends AppCompatActivity {

    ImageView title, sign;
    ImageView bg;
    dbScores scoreDB = new dbScores(this);
    ImageButton start, scores, exit, sound, music, info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        uiInit();
        AudioUtils.bgm.start();
        entryAnim();
        scoreDB.open();
        BasicUtils.scores = scoreDB.getData();
        Collections.sort(BasicUtils.scores, new Comparator<ScoreItem>() {
            @Override
            public int compare(ScoreItem scoreItem, ScoreItem t1) {
                return ((Integer) scoreItem.score).compareTo(t1.score);
            }
        });
        Collections.reverse(BasicUtils.scores);
        for (int i = 0; i < BasicUtils.scores.size(); i++){
            Log.e("MainMenu", "ArrayList: " + BasicUtils.scores.get(i).name
                    + ", " + String.valueOf(BasicUtils.scores.get(i).score) + " at index " + String.valueOf(i));
        }

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AudioUtils.sound_volume == 1){
                    AudioUtils.sound_volume = 0;
                    AudioUtils.setSoundVolume();
                    sound.setImageResource(R.drawable.mute_button);
                }
                else if(AudioUtils.sound_volume == 0){
                    AudioUtils.sound_volume = 1;
                    AudioUtils.setSoundVolume();
                    sound.setImageResource(R.drawable.volume_button);
                }
            }
        });

        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AudioUtils.music_volume == 1){
                    AudioUtils.music_volume = 0;
                    AudioUtils.setMusicVolume();
                    music.setImageResource(R.drawable.musicoff_button);
                }
                else if(AudioUtils.music_volume == 0){
                    AudioUtils.music_volume = 1;
                    AudioUtils.setMusicVolume();
                    music.setImageResource(R.drawable.music_button);
                }
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent help = new Intent(getApplicationContext(), HowToPlay.class);
                startActivity(help);
                overridePendingTransition(R.anim.activity_fadein, R.anim.activity_fadeout);
                stopAndPrepare(AudioUtils.bgm);
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
        music = (ImageButton) findViewById(R.id.music_button);
        info = (ImageButton) findViewById(R.id.info);
        Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.splash_background);
        Point screen = new Point();
        getWindowManager().getDefaultDisplay().getSize(screen);
        bg.setImageBitmap(Bitmap.createScaledBitmap(bmp, screen.x, screen.y, false));
        if(AudioUtils.sound_volume == 1){
            sound.setImageResource(R.drawable.volume_button);
        }
        else if(AudioUtils.sound_volume == 0){
            sound.setImageResource(R.drawable.mute_button);
        }

        if(AudioUtils.music_volume == 1){
            music.setImageResource(R.drawable.music_button);
        }
        else if(AudioUtils.music_volume == 0){
            music.setImageResource(R.drawable.musicoff_button);
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
        music.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.sound_button));
        info.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.sound_button));
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
        editor.putInt("soundVolume", AudioUtils.sound_volume);
        editor.putInt("musicVolume", AudioUtils.music_volume);
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
    protected void onDestroy() {
        scoreDB.close();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        scoreDB.open();
        scoreDB.removeRows();
        for(int n = 0; n < 3; n++) {
            try{
                Log.e("GameActivity", "Values sent to DB: Name - " + BasicUtils.scores.get(n).name + " Score - " + String.valueOf(BasicUtils.scores.get(n).score));
                scoreDB.createEntry(BasicUtils.scores.get(n));
            }catch (Exception e){
                Log.d("DB", "No item at position " + String.valueOf(n));
            }
        }
        scoreDB.close();
    }
}
