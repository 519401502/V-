package com.example.asus.five;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 徐会闯 on 2016/9/13.
 * 相信自己，超越自己。
 */
public class MyServer extends Service {

    private MySQLite mySQLite;
    private SQLiteDatabase sqLiteDatabase;
    private SQLiteDatabase sqLiteDatabase_read;

    private List<String> sheng_id;
    private List<String> sheng_name;
    private List<String> shi_id;
    private List<String> shi_name;
    private List<String> shi_sheng_id;
    private List<String> shi_sheng_name;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences.Editor editor2=getSharedPreferences("service_ok",MODE_PRIVATE).edit();
        editor2.putBoolean("service_ok",true);
        editor2.commit();

        sheng_id = new ArrayList<>();
        sheng_name = new ArrayList<>();
        shi_id=new ArrayList<>();
        shi_name=new ArrayList<>();
        shi_sheng_id=new ArrayList<>();
        shi_sheng_name=new ArrayList<>();

        mySQLite = new MySQLite(this, "mySQLiteDatabase_1", null, MySQLite.VERSION);
        sqLiteDatabase = mySQLite.getWritableDatabase();
        sqLiteDatabase_read = mySQLite.getReadableDatabase();
        //获得所有的省
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.weather.com.cn/data/list3/city.xml");
                    HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                    httpUrlConnection.setRequestMethod("GET");
                    httpUrlConnection.setReadTimeout(5000);
                    httpUrlConnection.setConnectTimeout(5000);
                    InputStream inputStream = httpUrlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    if (stringBuilder != null) {

                        String[] strings = (stringBuilder.toString()).split(",");
                        for (String string_item : strings) {
                            String[] strings1 = string_item.split("\\|");
                            sqLiteDatabase.execSQL("insert into sheng(sheng_id,sheng_name) values(?,?)", new String[]{strings1[0], strings1[1]});

                        }
                    }


                    //获取省
                    Cursor cursor = sqLiteDatabase_read.rawQuery("select sheng_id,sheng_name from sheng", null);
                    cursor.moveToFirst();
                    do {
                        String id = cursor.getString(cursor.getColumnIndex("sheng_id"));
                        String name = cursor.getString(cursor.getColumnIndex("sheng_name"));
                        sheng_id.add(id);
                        sheng_name.add(name);
                    } while (cursor.moveToNext());
                    cursor.close();
                    init_shi();

                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (ProtocolException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void init_shi() {

        for (int i = 0; i < sheng_id.size(); i++) {
            URL url_2;
            try {
                url_2 = new URL("http://www.weather.com.cn/data/list3/city" + sheng_id.get(i) + ".xml");
                HttpURLConnection httpUrlConnection_2 = (HttpURLConnection) url_2.openConnection();
                httpUrlConnection_2.setRequestMethod("GET");
                httpUrlConnection_2.setReadTimeout(5000);
                httpUrlConnection_2.setConnectTimeout(5000);
                InputStream inputStream_2 = httpUrlConnection_2.getInputStream();
                BufferedReader bufferedReader_2 = new BufferedReader(new InputStreamReader(inputStream_2));
                StringBuilder stringBuilder_2 = new StringBuilder();
                String line_2;
                while ((line_2 = bufferedReader_2.readLine()) != null) {
                    stringBuilder_2.append(line_2);
                }
                if (stringBuilder_2 != null) {
                    String[] strings = (stringBuilder_2.toString()).split(",");
                    for (String string_item : strings) {
                        String[] strings2 = string_item.split("\\|");
                        sqLiteDatabase.execSQL("insert into shi(shi_id,shi_name,sheng_id,sheng_name) values(?,?,?,?)",new String[]{strings2[0], strings2[1], sheng_id.get(i), sheng_name.get(i)});
                    }
                }
                SharedPreferences sharedPreferences=getSharedPreferences("is_ok",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("is_ok",true);
                editor.commit();

//                //获取市
//                Cursor cursor = sqLiteDatabase_read.rawQuery("select shi_id,shi_name,sheng_id,sheng_name from shi", null);
//                cursor.moveToFirst();
//                do {
//                    String id = cursor.getString(cursor.getColumnIndex("shi_id"));
//                    String name = cursor.getString(cursor.getColumnIndex("shi_name"));
//                    String id_shi=cursor.getString(cursor.getColumnIndex("sheng_id"));
//                    String name_shi=cursor.getString(cursor.getColumnIndex("sheng_name"));
//                    shi_id.add(id);
//                    shi_name.add(name);
//                    shi_sheng_id.add(id_shi);
//                    shi_sheng_name.add(name_shi);
//                } while (cursor.moveToNext());
//                cursor.close();


            }
             catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
