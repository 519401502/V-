package com.example.asus.five;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 徐会闯 on 2016/9/16.
 * 相信自己，超越自己。
 */
public class tixingService extends Service {

    private Calendar c;
    int hour;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                if(hour==8){
                    NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification.Builder builder= new Notification.Builder(tixingService.this);
                    builder.setContentText("查看今日天气状况");
                    builder.setContentTitle("v天气");
                    builder.setAutoCancel(true);
                    Intent intent=new Intent(tixingService.this,MainActivity.class);
                    builder.setContentIntent(PendingIntent.getActivity(tixingService.this,0,intent,0));
                    Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.vlogo);
                    builder.setLargeIcon(bitmap);
                    Notification notification=builder.build();
                    manager.notify(1,notification);

                }
            }
        },0,1000*30);
    }


}
