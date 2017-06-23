package com.curve.nandhakishore.deltagame;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;


public class audioUtils {

    static MediaPlayer bgm = new MediaPlayer();
    static int volume = 1;

    public static void init(Context con) {
            bgm = MediaPlayer.create(con, R.raw.splash_bgm);
            bgm.setLooping(true);
            bgm.setVolume(volume, volume);
            volume = 1;
    }

    public static void setBgmVolume() {
        bgm.setVolume(volume, volume);
    }
}
