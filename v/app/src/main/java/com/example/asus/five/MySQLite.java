package com.example.asus.five;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 徐会闯 on 2016/9/13.
 * 相信自己，超越自己。
 */
public class MySQLite extends SQLiteOpenHelper {
    public static int VERSION=1;

    private String sheng="create table sheng(id integer primary key autoincrement,sheng_id text,sheng_name text)";
    private String shi="create table shi(id integer primary key autoincrement,shi_id text,shi_name text,sheng_id text,sheng_name text)";
    private String xian="create table xian(id integer primary key autoincrement,xian_id text,xian_name text,shi_id text,shi_name text,sheng_id text,sheng_name text)";

    public MySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sheng);
        db.execSQL(shi);
        db.execSQL(xian);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
