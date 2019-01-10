package mshkliai.com.ft_hangouts.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    public Database(Context context) {
        super(context, "communication.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists users(id INTEGER, name VARCHAR(200)," +
                "mobile_num VARCHAR(200), mail VARCHAR(200), photo blob)");

        db.execSQL("create table if not exists messages(name VARCHAR(100), "
        + "body VARCHAR(1000), photo blob)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists users");
        db.execSQL("drop table if exists dialogs");
    }
}