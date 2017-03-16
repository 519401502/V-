package com.example.asus.five;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 徐会闯 on 2016/9/17.
 * 相信自己，超越自己。
 */
public class Start extends AppCompatActivity {
    static int start=0;
    private LinearLayout linearLayout;
    private ImageView imageView;
    private Handler handler=new Handler(){
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if(msg.what==0){
                Intent intent=new Intent(Start.this,MainActivity.class);
                startActivity(intent);
                finish();
            }else if (msg.what==2){
                    linearLayout.setBackground((Drawable)msg.obj);
            }else if(msg.what==3){
                linearLayout.setBackgroundResource(R.drawable.zhongqiu);
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        SharedPreferences sharedPreferences3 = getSharedPreferences("service_ok", MODE_PRIVATE);
        boolean service_ok = sharedPreferences3.getBoolean("service_ok", false);
        if (service_ok == false) {
            Intent intent_service = new Intent(Start.this, MyServer.class);
            startService(intent_service);
        }

//        imageView= (ImageView) findViewById(R.id.start_imageView);
        linearLayout= (LinearLayout) findViewById(R.id.linearLayout);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url=new URL("http://115.159.78.127:8080/docs/start.png");
                    HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                    httpUrlConnection.setRequestMethod("GET");
                    httpUrlConnection.setReadTimeout(1000);
                    httpUrlConnection.setConnectTimeout(1000);
                    InputStream inputStream = httpUrlConnection.getInputStream();
                    Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                    Drawable drawable=new BitmapDrawable(bitmap);
                    Message message1=new Message();
                    message1.what=2;
                    message1.obj=drawable;
                    handler.sendMessage(message1);

                } catch (Exception e) {
//                    Message message2=new Message();
//                    message2.what=3;
//                    handler.sendMessage(message2);
                }
            }
        }).start();


        start=1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    Message message=new Message();
                    message.what=0;
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
