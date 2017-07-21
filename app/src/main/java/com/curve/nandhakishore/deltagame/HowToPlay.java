package com.curve.nandhakishore.deltagame;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

public class HowToPlay extends AppCompatActivity {

    ImageView current, follow;
    ImageButton next, prev, back;
    int index;
    ArrayList<Integer> images = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.how_to_play);

        images.add(R.drawable.swipe);
        images.add(R.drawable.collect);
        images.add(R.drawable.avoid);
        index = 0;

        next = (ImageButton) findViewById(R.id.next_button);
        prev = (ImageButton) findViewById(R.id.prev_button);
        back = (ImageButton) findViewById(R.id.return_menu);
        current = (ImageView) findViewById(R.id.first);
        follow = (ImageView) findViewById(R.id.second);
        current.setImageResource(images.get(0));
        prev.setVisibility(View.GONE);

        final Animation next_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.device_in_next);
        final Animation next_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.device_out_next);
        final Animation prev_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.device_in_prev);
        final Animation prev_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.device_out_prev);

        next_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                follow.setImageResource(images.get(index + 1));
                follow.startAnimation(next_in);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                current.setImageResource(images.get(index + 1));
                index++;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        prev_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                follow.setImageResource(images.get(index - 1));
                follow.startAnimation(prev_in);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                current.setImageResource(images.get(index - 1));
                index--;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prev.setVisibility(View.VISIBLE);
                if (index < images.size() - 1)
                    current.startAnimation(next_out);
                if (index == images.size() - 2) {
                    next.setVisibility(View.GONE);
                    back.setVisibility(View.VISIBLE);
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
                if (index > 0)
                    current.startAnimation(prev_out);
                if (index == 1)
                    prev.setVisibility(View.GONE);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
