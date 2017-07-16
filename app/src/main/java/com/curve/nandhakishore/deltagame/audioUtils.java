package com.curve.nandhakishore.deltagame;

import android.content.Context;
import android.media.MediaPlayer;


public class audioUtils {

    static MediaPlayer bgm = new MediaPlayer();
    static MediaPlayer coin = new MediaPlayer();
    static int bgm_volume = 1;

    public static void init(Context con) {
            bgm = MediaPlayer.create(con, R.raw.splash_bgm);
            bgm.setLooping(true);
            bgm.setVolume(bgm_volume, bgm_volume);
            bgm_volume = 1;

            coin = MediaPlayer.create(con, R.raw.coin);
    }

    public static void setBgmVolume() {
        bgm.setVolume(bgm_volume, bgm_volume);
        coin.setVolume(bgm_volume,bgm_volume);
    }
}
