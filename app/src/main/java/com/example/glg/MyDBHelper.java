package com.example.glg;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.glg.MainActivity.KEY_NAME;
import static com.example.glg.MainActivity.KEY_PRICE;
import static com.example.glg.MainActivity.TABLE_NAME;


class MyDBHelper extends SQLiteOpenHelper {
    public MyDBHelper(Context context) {
        super(context, "MyData.db", null, 2);
    }

    public void onCreate(SQLiteDatabase db) {
        String query = String.format("CREATE TABLE %s (" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "%s TEXT," + "%s TEXT);", TABLE_NAME, KEY_NAME, KEY_PRICE);
        db.execSQL(query);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(query);
        onCreate(db);
    }
}

