package com.curve.nandhakishore.deltagame;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class BasicUtils {
    public static ArrayList<ScoreItem> scores;
    public static ScoreItem tmp;

    public static Handler handleScores;
    public static Handler handleLives;

    public static void sortScores(){
        ScoreItem[] scoreArray = scores.toArray(new ScoreItem[scores.size()]);
        for(int i = scores.size() - 1; i > 0; i--){
            if(scoreArray[i].score > scoreArray[i-1].score) {
                tmp = scoreArray[i - 1];
                scoreArray[i - 1] = scoreArray[i];
                scoreArray[i] = tmp;
            }
            else
                break;
        }
        scores = new ArrayList<>(Arrays.asList(scoreArray));
        try{
            scores.remove(3);
        }catch (IndexOutOfBoundsException e){
            Log.e("Scores", "No value at index 3");
        }
    }
}
