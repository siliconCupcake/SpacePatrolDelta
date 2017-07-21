package com.curve.nandhakishore.deltagame;

import android.content.Context;
import android.media.MediaPlayer;
import android.provider.MediaStore;


public class AudioUtils {

    static MediaPlayer bgm = new MediaPlayer();
    static MediaPlayer coin = new MediaPlayer();
    static MediaPlayer meteor = new MediaPlayer();
    static MediaPlayer explosion = new MediaPlayer();
    static int music_volume = 1;
    static int sound_volume = 1;

    public static void init(Context con) {
        bgm = MediaPlayer.create(con, R.raw.splash_bgm);
        bgm.setLooping(true);
        bgm.setVolume(music_volume, music_volume);
        music_volume = 1;

        coin = MediaPlayer.create(con, R.raw.coin);
        meteor = MediaPlayer.create(con, R.raw.meteor);
        explosion = MediaPlayer.create(con, R.raw.explosion);
    }

    public static void setSoundVolume() {
        coin.setVolume(sound_volume, sound_volume);
        meteor.setVolume(sound_volume, sound_volume);
        explosion.setVolume(sound_volume, sound_volume);
    }

    public static void setMusicVolume() {
        bgm.setVolume(music_volume, music_volume);
    }
}
