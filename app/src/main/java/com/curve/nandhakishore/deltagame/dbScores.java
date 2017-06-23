package com.curve.nandhakishore.deltagame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class dbScores {

    private static final String DB_NAME = "SPD";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "HIGH_SCORES";
    private static final String C_ID = "ID";
    private static final String C_RANK = "RANK";
    private static final String C_SCORE = "SCORE";
    private static final String C_NAME = "NAME";
    private String[] allColumns = {C_ID, C_RANK, C_NAME, C_SCORE};

    private static final String CREATE_DB = "CREATE TABLE " + TABLE_NAME + "( " + C_ID + " INTEGER PRIMARY KEY, "
            + C_RANK + " INTEGER, " + C_NAME + " TEXT, " + C_SCORE + " TEXT);";

    private dbHelper myHelper;
    private final Context myContext;
    private SQLiteDatabase myDatabase;

    public dbScores (Context c) {
        myContext = c;
    }

    public class dbHelper extends SQLiteOpenHelper {

        public dbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DB);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    public dbScores open() {
        myHelper = new dbHelper(myContext);
        myDatabase = myHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        myHelper.close();
    }

    public long createEntry (scoreItem c) {
        ContentValues cv = new ContentValues();
        cv.put(C_ID, c.sno);
        cv.put(C_RANK, c.rank);
        cv.put(C_NAME, c.name);
        cv.put(C_SCORE, String.valueOf(c.score));
        return myDatabase.insert(TABLE_NAME, null, cv);
    }


    public ArrayList<scoreItem> getData() {
        ArrayList<scoreItem> list = new ArrayList<>();
        Cursor c = myDatabase.query(TABLE_NAME, allColumns, null, null, null, null, null);
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            scoreItem row = new scoreItem(c.getInt(0), c.getString(2), c.getLong(3));
            row.setRank(c.getInt(1));
            list.add(row);
        }
        c.close();
        return list;
    }

    public void removeRows() {
        myDatabase.delete(TABLE_NAME, null, null);
    }

}

