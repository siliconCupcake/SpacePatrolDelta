package com.curve.nandhakishore.deltagame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class HighScores extends AppCompatActivity {

    ImageButton reset, back;
    ImageView title;
    dbScores hScores = new dbScores(this);
    ArrayList<CustomTextView> players = new ArrayList<>();
    ArrayList<CustomTextView> scores = new ArrayList<>();
    ArrayList<LinearLayout> lv = new ArrayList<>();
    int n = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.high_scores);

        for (int i = 0; i < BasicUtils.scores.size(); i++){
            Log.e("BasicUtils.scores", BasicUtils.scores.get(i).name
                    + ", " + String.valueOf(BasicUtils.scores.get(i).score) + " at index " + String.valueOf(i));
        }

        tvInit();
        hScores.open();
        reset = (ImageButton) findViewById(R.id.reset_button);
        back = (ImageButton) findViewById(R.id.back_button);
        title = (ImageView) findViewById(R.id.scores_title);
        entryAnim();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hScores.removeRows();
                BasicUtils.scores.clear();

                for(n = 0; n < 3; n++){
                    players.get(n).setText("NA");
                    scores.get(n).setText(String.format("%02d", 0));
                }
            }
        });
    }

    private void tvInit(){
        lv.add((LinearLayout) findViewById(R.id.rank_1));
        lv.add((LinearLayout) findViewById(R.id.rank_2));
        lv.add((LinearLayout) findViewById(R.id.rank_3));

        players.add((CustomTextView) findViewById(R.id.player_1));
        players.add((CustomTextView) findViewById(R.id.player_2));
        players.add((CustomTextView) findViewById(R.id.player_3));

        scores.add((CustomTextView) findViewById(R.id.score_1));
        scores.add((CustomTextView) findViewById(R.id.score_2));
        scores.add((CustomTextView) findViewById(R.id.score_3));

        for(n = 0; n < 3; n++) {
            try{
                Log.e("Display Scores", BasicUtils.scores.get(n).name + " ranked " + String.valueOf(n + 1));
                players.get(n).setText(BasicUtils.scores.get(n).name);
                scores.get(n).setText(String.format("%02d", BasicUtils.scores.get(n).score));
            }
            catch (IndexOutOfBoundsException e){
                players.get(n).setText("NA");
                scores.get(n).setText(String.format("%02d", 0));
            }
        }
    }

    private void entryAnim() {
        title.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_title));
        reset.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.start_button));
        back.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.start_button));
        lv.get(0).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rank_1));
        lv.get(1).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rank_2));
        lv.get(2).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rank_3));
    }

    @Override
    protected void onDestroy() {
        hScores.close();
        super.onDestroy();
    }
}
