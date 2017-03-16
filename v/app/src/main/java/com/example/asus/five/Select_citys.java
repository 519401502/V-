package com.example.asus.five;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 徐会闯 on 2016/9/12.
 * 相信自己，超越自己。
 */
public class Select_citys extends AppCompatActivity {


    //界面全部组件
    private ListView listView1;
    private ListView listView2;
    private ListView listView3;
    private ImageView imageView;
    private SearchView searchView;
    private String sheng_shi;
    private List<String> xian_name;
    private List<String> xian_id;
    private String xian_tag;
    private String xian_weather;
    private List<String> xian_weather_id;
    private LinearLayout back;


    private MySQLite mySQLite;
    private SQLiteDatabase sqLiteDatabase_write;
    private SQLiteDatabase sqLiteDatabase_read;
    private List<String> sheng_id;
    private List<String> sheng_name;
    private List<String> shi_id;
    private List<String> shi_name;


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 1:
                    listView1.setAdapter(new MyListView1());
                    break;
                case 2:
                    listView3.setAdapter(new MyListView3());
                    break;
                case 3:
                    Toast.makeText(Select_citys.this,"获取数据失败!",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citys);

        //初始化全部组件
        listView1= (ListView) findViewById(R.id.listView1);
        listView2= (ListView) findViewById(R.id.listView2);
        listView3= (ListView) findViewById(R.id.listView3);
        imageView= (ImageView) findViewById(R.id.imageView);
        back= (LinearLayout) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Select_citys.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });




        mySQLite = new MySQLite(this, "mySQLiteDatabase_1", null, MySQLite.VERSION);
        sqLiteDatabase_read=mySQLite.getReadableDatabase();
        sqLiteDatabase_write=mySQLite.getWritableDatabase();
        sheng_id=new ArrayList<>();
        sheng_name=new ArrayList<>();



        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor=sqLiteDatabase_read.rawQuery("select sheng_id,sheng_name from sheng",null);
                cursor.moveToFirst();
                do {
                    sheng_id.add(cursor.getString(cursor.getColumnIndex("sheng_id")));
                    sheng_name.add(cursor.getString(cursor.getColumnIndex("sheng_name")));
                }while(cursor.moveToNext());
                Message message=new Message();
                message.what=1;
                handler.sendMessage(message);
            }
        }).start();

        //省的选择实现
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String string;
                position=position+1;
                if (position<10){
                    string="0"+position;
                }else {
                    string=String.valueOf(position);
                }
                xuanze_shi(string);

            }
        });

        //市的选择实现
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                xian_id=new ArrayList<>();
                xian_name=new ArrayList<>();
                xian_tag=shi_id.get(position);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url=new URL("http://www.weather.com.cn/data/list3/city"+xian_tag+".xml");
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
                                    xian_id.add(strings1[0]);
                                    xian_name.add(strings1[1]);

                                }
                            }
                            Message message=new Message();
                            message.what=2;
                            handler.sendMessage(message);

                        } catch (Exception e) {
                            Message message=new Message();
                            message.what=3;
                            handler.sendMessage(message);
                        }
                    }
                }).start();

            }
        });


        listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                xian_weather=xian_id.get(position);
                xian_weather_id=new ArrayList<>();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url=new URL("http://www.weather.com.cn/data/list3/city"+xian_weather+".xml");
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
//                                String[] strings = (stringBuilder.toString()).split(",");
//                                for (String string_item : strings) {
                                    String[] strings1 = (stringBuilder.toString()).split("\\|");
                                    xian_weather_id.add(strings1[1]);
//                                }
                            }

                            SharedPreferences sharedPreferences=getSharedPreferences("qidong",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putBoolean("isqidong",true);
                            editor.putString("qidong",xian_weather_id.get(0));
                            editor.commit();


                            Bundle bundler=new Bundle();
                            bundler.putString("weather_id",xian_weather_id.get(0));
                            Intent intent=new Intent(Select_citys.this,MainActivity.class);
                            intent.putExtras(bundler);
                            startActivity(intent);
                        } catch (Exception e) {
                        }
                    }
                }).start();
            }
        });
    }

    //在省列表中选择之后，实现市的加载
    public void xuanze_shi(String string){
        sheng_shi=string;
        shi_id=new ArrayList<>();
        shi_name=new ArrayList<>();
        Cursor cursor= sqLiteDatabase_read.rawQuery("select shi_id,shi_name from shi where sheng_id=?", new String[]{string});
        cursor.moveToFirst();
        do {
            shi_id.add(cursor.getString(cursor.getColumnIndex("shi_id")));
            shi_name.add(cursor.getString(cursor.getColumnIndex("shi_name")));

        }while(cursor.moveToNext());
        cursor.close();
        listView2.setAdapter(new MyListView2());
    }



    class MyListView1 extends BaseAdapter{

        @Override
        public int getCount() {
            return sheng_name.size();
        }

        @Override
        public Object getItem(int position) {
            return sheng_name.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=LayoutInflater.from(Select_citys.this).inflate(R.layout.city_item1,null);
            TextView textView= (TextView) view.findViewById(R.id.textView);
            textView.setText(sheng_name.get(position));
            return view;
        }
    }



    class MyListView2 extends BaseAdapter{

        @Override
        public int getCount() {
            return shi_name.size();
        }

        @Override
        public Object getItem(int position) {
            return shi_name.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=LayoutInflater.from(Select_citys.this).inflate(R.layout.city_item1,null);
            TextView textView= (TextView) view.findViewById(R.id.textView);
            textView.setText(shi_name.get(position));
            return view;
        }
    }


    class MyListView3 extends BaseAdapter{

        @Override
        public int getCount() {
            return xian_name.size();
        }

        @Override
        public Object getItem(int position) {
            return xian_name.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=LayoutInflater.from(Select_citys.this).inflate(R.layout.city_item1,null);
            TextView textView= (TextView) view.findViewById(R.id.textView);
            textView.setText(xian_name.get(position));
            return view;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent(Select_citys.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
