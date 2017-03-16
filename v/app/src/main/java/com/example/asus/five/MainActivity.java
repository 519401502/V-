package com.example.asus.five;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private String bundle_string;
    private int i=30;

    private TextView month_textView;
    private String month_textView_string;

    private String richu;
    private String riluo;
    private String jingdu;
    private String weidu;
    private String haiba;

    private TextView textView1_tianqiqingkuang;
    private TextView textView1_dushu;
    private TextView textView1_city;
    private TextView textView1_gengxinshijian;
    private Switch switch_weixingdingwei;
    private Switch switch_tixing;

    private String zuidiwendu="30";
    private String zuigaowendu="25";
    private String wendu="28";
    private String fengsu="3级";
    private String fengxiang="南风";
    private List<String> lists;
    private String time_string;
    private Message message_time;
    private List<Integer> heights;
    private Calendar c;

    private int hour,minute,time_day,time_month;
    private TextView main_textView;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AppBarLayout appBarLayout;
    private FloatingActionButton floatingActionButton;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 1:

                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this,"刷新成功！",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    main_textView.setText((String) message_time.obj);
                    break;
                case 3:

                case 4:
                    Bundle bundle_weather2=msg.getData();
                    richu=bundle_weather2.getString("city_richu");
                    riluo=bundle_weather2.getString("city_riluo");
                    jingdu=bundle_weather2.getString("city_jingdu");
                    weidu=bundle_weather2.getString("city_weidu");
                    haiba=bundle_weather2.getString("city_haiba");
                    zuidiwendu=bundle_weather2.getString("city_zuidiwendu");
                    zuigaowendu=bundle_weather2.getString("city_zuigaowendu");
                    fengsu=bundle_weather2.getString("city_fengsu");
                    fengxiang=bundle_weather2.getString("city_fengxiang");
                    wendu=bundle_weather2.getString("city_wendu");
                    month_textView_string=bundle_weather2.getString("city_date");
                    String tring_dushu=bundle_weather2.getString("city_wendu");
                    String string_gengxinshijian=bundle_weather2.getString("city_gengxinshijian");
                    textView1_city.setText(bundle_weather2.getString("city_name"));
                    textView1_dushu.setText(tring_dushu+"°");
                    textView1_gengxinshijian.setText(string_gengxinshijian);
                    month_textView.setText(month_textView_string);
                    textView1_tianqiqingkuang.setText(bundle_weather2.getString("city_tianqiqingkuang"));
                    recyclerView.setAdapter(new MyAdapter());
                    if(swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this,"刷新成功！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 5:
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this,"刷新失败！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Start.start == 0) {
            Intent intent = new Intent(MainActivity.this, Start.class);
            startActivity(intent);
            finish();
            Start.start = 1;
        }

        setContentView(R.layout.activity_main);
        bundle_string="101180101";






        month_textView= (TextView) findViewById(R.id.month_textView);
        textView1_city = (TextView) findViewById(R.id.city);
        textView1_dushu = (TextView) findViewById(R.id.dushu);
        textView1_gengxinshijian = (TextView) findViewById(R.id.gengxinshijian);
        textView1_tianqiqingkuang = (TextView) findViewById(R.id.tianqiqingkuang);


        init();
        getRandomHeight();
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingButtonActivity);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        main_textView = (TextView) findViewById(R.id.main_time);

        //启动时默认的城市
        SharedPreferences sharedPreferences_qidong=getSharedPreferences("qidong",MODE_PRIVATE);
        boolean isqidong=sharedPreferences_qidong.getBoolean("isqidong",false);
        if(isqidong){
            bundle_string=sharedPreferences_qidong.getString("qidong","0");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                         try{
                            URL url = new URL("http://apis.baidu.com/apistore/weatherservice/cityid?cityid=" + bundle_string);
                            HttpURLConnection httpUrlConnection_3 = (HttpURLConnection) url.openConnection();
                            httpUrlConnection_3.setRequestMethod("GET");
                            httpUrlConnection_3.setReadTimeout(5000);
                            httpUrlConnection_3.setRequestProperty("apikey", "4b6f55340baa10c0423827738435ce48");
                            httpUrlConnection_3.setConnectTimeout(5000);
                            InputStream inputStream_3 = httpUrlConnection_3.getInputStream();
                            BufferedReader bufferedReader_3 = new BufferedReader(new InputStreamReader(inputStream_3));
                            StringBuilder stringBuilder_3 = new StringBuilder();
                            String line_3;
                            while ((line_3 = bufferedReader_3.readLine()) != null) {
                                stringBuilder_3.append(line_3);
                            }
                            JSONObject jsonObject = new JSONObject(stringBuilder_3.toString());
                            JSONObject jsonObject1 = jsonObject.getJSONObject("retData");
                            String city_name = jsonObject1.getString("city");
                            String city_id = jsonObject1.getString("citycode");
                            String city_wendu = jsonObject1.getString("temp");
                            i = Integer.parseInt(city_wendu);
                            if (i < -5) {
                                i = -5;
                            } else if (i >= -5 && i < 0) {
                                i = 0;
                            } else if (i >= 0 && i < 5) {
                                i = 5;
                            } else if (i >= 5 && i < 10) {
                                i = 10;
                            } else if (i >= 10 && i < 15) {
                                i = 15;
                            } else if (i >= 15 && i < 20) {
                                i = 20;
                            } else if (i >= 20 && i < 25) {
                                i = 25;
                            } else if (i >= 25 && i < 30) {
                                i = 30;
                            } else if (i >= 30 && i < 35) {
                                i = 35;
                            } else if (i >= 35) {
                                i = 40;
                            }
                            String city_fengxiang = jsonObject1.getString("WD");
                            String city_fengsu = jsonObject1.getString("WS");
                             String city_zuidiwendu=jsonObject1.getString("l_tmp");
                             String city_zuigaowendu=jsonObject1.getString("h_tmp");

                             String city_richu=jsonObject1.getString("sunrise");
                             String city_riluo=jsonObject1.getString("sunset");
                             String city_jingdu=jsonObject1.getString("longitude");
                             String city_weidu=jsonObject1.getString("latitude");
                             String city_haiba=jsonObject1.getString("altitude");

                             String city_date=jsonObject1.getString("date");
                             String city_tianqiqingkuang=jsonObject1.getString("weather");
                            String city_gengxinshijian = jsonObject1.getString("time");
                            Message message = new Message();
                            message.what = 4;
                            Bundle bundler_weather = new Bundle();
                            bundler_weather.putString("city_name", city_name);
                            bundler_weather.putString("city_id", city_id);
                            bundler_weather.putString("city_wendu", city_wendu);
                             bundler_weather.putString("city_zuidiwendu",city_zuidiwendu);
                             bundler_weather.putString("city_zuigaowendu",city_zuigaowendu);

                             bundler_weather.putString("city_richu",city_richu);
                             bundler_weather.putString("city_riluo",city_riluo);
                             bundler_weather.putString("city_jingdu",city_jingdu);
                             bundler_weather.putString("city_weidu",city_weidu);
                             bundler_weather.putString("city_haiba",city_haiba);

                             bundler_weather.putString("city_date",city_date);
                             bundler_weather.putString("city_tianqiqingkuang", city_tianqiqingkuang);
                            bundler_weather.putString("city_fengxiang", city_fengxiang);
                            bundler_weather.putString("city_fengsu", city_fengsu);
                            bundler_weather.putString("city_gengxinshijian", city_gengxinshijian);
                            message.setData(bundler_weather);
                            handler.sendMessage(message);
                        }catch (Exception e){

                         }

                    }
                }).start();
        }


        //标题底下的时间功能实现
        main_time_function();

        //为主菜单RecyclerView设置Adapter
        recyclerView.setAdapter(new MyAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        //刷新组件功能实现
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_blue_light);
        swipeRefreshLayout.setProgressViewOffset(true, -20, 100);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://apis.baidu.com/apistore/weatherservice/cityid?cityid=" +bundle_string);
                            HttpURLConnection httpUrlConnection_3 = (HttpURLConnection) url.openConnection();
                            httpUrlConnection_3.setRequestMethod("GET");
                            httpUrlConnection_3.setReadTimeout(5000);
                            httpUrlConnection_3.setConnectTimeout(5000);
                            httpUrlConnection_3.setRequestProperty("apikey", "4b6f55340baa10c0423827738435ce48");
                            InputStream inputStream_3 = httpUrlConnection_3.getInputStream();
                            BufferedReader bufferedReader_3 = new BufferedReader(new InputStreamReader(inputStream_3));
                            StringBuilder stringBuilder_3 = new StringBuilder();
                            String line_3;
                            while ((line_3 = bufferedReader_3.readLine()) != null) {
                                stringBuilder_3.append(line_3);
                            }
                            JSONObject jsonObject = new JSONObject(stringBuilder_3.toString());
                            JSONObject jsonObject1 = jsonObject.getJSONObject("retData");
                            String city_name = jsonObject1.getString("city");
                            String city_id = jsonObject1.getString("citycode");
                            String city_wendu = jsonObject1.getString("temp");
                            i = Integer.parseInt(city_wendu);
                            if (i < -5) {
                                i = -5;
                            } else if (i >= -5 && i < 0) {
                                i = 0;
                            } else if (i >= 0 && i < 5) {
                                i = 5;
                            } else if (i >= 5 && i < 10) {
                                i = 10;
                            } else if (i >= 10 && i < 15) {
                                i = 15;
                            } else if (i >= 15 && i < 20) {
                                i = 20;
                            } else if (i >= 20 && i < 25) {
                                i = 25;
                            } else if (i >= 25 && i < 30) {
                                i = 30;
                            } else if (i >= 30 && i < 35) {
                                i = 35;
                            } else if (i >= 35) {
                                i = 40;
                            }
                            String city_fengxiang = jsonObject1.getString("WD");
                            String city_fengsu = jsonObject1.getString("WS");
                            String city_date=jsonObject1.getString("date");
                            String city_gengxinshijian = jsonObject1.getString("time");
                            String city_tianqiqingkuang=jsonObject1.getString("weather");

                            String city_richu=jsonObject1.getString("sunrise");
                            String city_riluo=jsonObject1.getString("sunset");
                            String city_jingdu=jsonObject1.getString("longitude");
                            String city_weidu=jsonObject1.getString("latitude");
                            String city_haiba=jsonObject1.getString("altitude");

                            String city_zuidiwendu=jsonObject1.getString("l_tmp");
                            String city_zuigaowendu=jsonObject1.getString("h_tmp");
                            Message message = new Message();
                            message.what = 4;
                            Bundle bundler_weather = new Bundle();
                            bundler_weather.putString("city_name", city_name);
                            bundler_weather.putString("city_date",city_date);
                            bundler_weather.putString("city_zuidiwendu",city_zuidiwendu);
                            bundler_weather.putString("city_zuigaowendu",city_zuigaowendu);

                            bundler_weather.putString("city_richu",city_richu);
                            bundler_weather.putString("city_riluo",city_riluo);
                            bundler_weather.putString("city_jingdu",city_jingdu);
                            bundler_weather.putString("city_weidu",city_weidu);
                            bundler_weather.putString("city_haiba",city_haiba);

                            bundler_weather.putString("city_tianqiqingkuang",city_tianqiqingkuang);
                            bundler_weather.putString("city_id", city_id);
                            bundler_weather.putString("city_wendu", city_wendu);
                            bundler_weather.putString("city_fengxiang", city_fengxiang);
                            bundler_weather.putString("city_fengsu", city_fengsu);
                            bundler_weather.putString("city_gengxinshijian", city_gengxinshijian);
                            message.setData(bundler_weather);
                            handler.sendMessage(message);
                        } catch (Exception e) {
                            Message message = new Message();
                            message.what = 5;
                            handler.sendMessage(message);
                        }
                    }
                }).start();

            }
        });

        //为FloatingActionButton添加点击事件
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("is_ok", MODE_PRIVATE);
                boolean is_ok = sharedPreferences.getBoolean("is_ok", false);
                if (is_ok) {
                    Intent intent = new Intent(MainActivity.this, Select_citys.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    Intent intent_service = new Intent(MainActivity.this, MyServer.class);
                    stopService(intent_service);
                } else {
                    Toast.makeText(MainActivity.this, "初次加载，正在初始化...", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //解决SwipeRefreshLayout与AppBarLayout冲突
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >= 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });


        Intent intent_weather_id = getIntent();
        Bundle bundle = intent_weather_id.getExtras();
        if (bundle != null) {
            bundle_string = bundle.getString("weather_id");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://apis.baidu.com/apistore/weatherservice/cityid?cityid=" + bundle_string);
                        HttpURLConnection httpUrlConnection_3 = (HttpURLConnection) url.openConnection();
                        httpUrlConnection_3.setRequestMethod("GET");
                        httpUrlConnection_3.setReadTimeout(5000);
                        httpUrlConnection_3.setRequestProperty("apikey", "4b6f55340baa10c0423827738435ce48");
                        httpUrlConnection_3.setConnectTimeout(5000);
                        InputStream inputStream_3 = httpUrlConnection_3.getInputStream();
                        BufferedReader bufferedReader_3 = new BufferedReader(new InputStreamReader(inputStream_3));
                        StringBuilder stringBuilder_3 = new StringBuilder();
                        String line_3;
                        while ((line_3 = bufferedReader_3.readLine()) != null) {
                            stringBuilder_3.append(line_3);
                        }

                        JSONObject jsonObject = new JSONObject(stringBuilder_3.toString());
                        JSONObject jsonObject1 = jsonObject.getJSONObject("retData");
                        String city_name = jsonObject1.getString("city");
                        String city_zuidiwendu=jsonObject1.getString("l_tmp");
                        String city_zuigaowendu=jsonObject1.getString("h_tmp");
                        String city_id = jsonObject1.getString("citycode");

                        String city_richu=jsonObject1.getString("sunrise");
                        String city_riluo=jsonObject1.getString("sunset");
                        String city_jingdu=jsonObject1.getString("longitude");
                        String city_weidu=jsonObject1.getString("latitude");
                        String city_haiba=jsonObject1.getString("altitude");

                        String city_wendu = jsonObject1.getString("temp");
                        String city_fengxiang = jsonObject1.getString("WD");
                        String city_fengsu = jsonObject1.getString("WS");
                        String city_date=jsonObject1.getString("date");
                        String city_gengxinshijian = jsonObject1.getString("time");
                        String city_tianqiqingkuang=jsonObject1.getString("weather");
                        Message message = new Message();
                        message.what = 4;
                        Bundle bundler_weather = new Bundle();
                        bundler_weather.putString("city_zuidiwendu",city_zuidiwendu);

                        bundler_weather.putString("city_richu",city_richu);
                        bundler_weather.putString("city_riluo",city_riluo);
                        bundler_weather.putString("city_jingdu",city_jingdu);
                        bundler_weather.putString("city_weidu",city_weidu);
                        bundler_weather.putString("city_haiba",city_haiba);

                        bundler_weather.putString("city_zuigaowendu",city_zuigaowendu);
                        bundler_weather.putString("city_name", city_name);
                        bundler_weather.putString("city_tianqiqingkuang",city_tianqiqingkuang);
                        bundler_weather.putString("city_date",city_date);
                        bundler_weather.putString("city_id", city_id);
                        bundler_weather.putString("city_wendu", city_wendu);
                        bundler_weather.putString("city_fengxiang", city_fengxiang);
                        bundler_weather.putString("city_fengsu", city_fengsu);
//                        bundler_weather.putString("city_xiangduishidu",city_xiangduishidu);
//                        bundler_weather.putString("city_fengli",city_fengli);
                        bundler_weather.putString("city_gengxinshijian", city_gengxinshijian);
                        message.setData(bundler_weather);
                        handler.sendMessage(message);
                    } catch (Exception e) {

                    }
                }
            }).start();
        }
    }



    //月份和时间的实现
    public void main_time_function(){

        Timer timer=new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
                if(minute<10){
                    time_string=hour + ":" +"0"+minute;
                }else{
                    time_string=hour + ":"+minute;
                }

                message_time=new Message();
                message_time.what=2;
                message_time.obj=time_string;

                handler.sendMessage(message_time);
            }
        },0,100);
    }

    //为Arraylist填充数据
    public void init() {
        lists = new ArrayList<>();
        lists.add("天气详情");
        lists.add("小贴士");

    }

    private void getRandomHeight() {//得到随机item的高度
        heights = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            heights.add((int) (200 + Math.random() * 400));
        }
    }

    //监听事件
    @Override
    public void onClick(View v) {

    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHandler> {
        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            } else if (position == 1) {
                return 1;
            } else if (position == 2) {
                return 2;
            } else if (position == 3) {
                return 3;
            } else if(position ==4){
                return 4;
            }else {
                return 5;
            }
        }

        @Override
        public MyHandler onCreateViewHolder(ViewGroup parent, int viewType) {
            MyHandler myHandler = null;
            View view = null;
            switch (viewType) {
                case 0:
                    view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item, null);
                    myHandler = new MyHandler(view);
                    myHandler.textView = (TextView) view.findViewById(R.id.tianqixiangqing);
                    myHandler.cardView = (CardView) view.findViewById(R.id.tianqixiangqing_cardView);

                    myHandler.zuidiwendu_textView= (TextView) view.findViewById(R.id.zuidiwendu);
                    myHandler.zuidiwendu_textView.setText(zuidiwendu+"°");

                    myHandler.zuigaowendu_textView= (TextView) view.findViewById(R.id.zuigaowendu);
                    myHandler.zuigaowendu_textView.setText(zuigaowendu+"°");

                    myHandler.textView1_fengsu= (TextView) view.findViewById(R.id.fengsu);
                    myHandler.textView1_fengsu.setText(fengsu);

                    myHandler.textView1_fengxiang= (TextView) view.findViewById(R.id.fengxiang);
                    myHandler.textView1_fengxiang.setText(fengxiang);

                    myHandler.textView.setText("天气详情");
                    break;

                case 1:
                    view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item5, null);
                    myHandler = new MyHandler(view);
                    myHandler.cardView= (CardView) view.findViewById(R.id.tianqixiangqing_cardView);
                    myHandler.diquxiangqing= (TextView) view.findViewById(R.id.diquxiangqing);
                    myHandler.diquxiangqing.setText("地区详情");

                    myHandler.richu_textView= (TextView) view.findViewById(R.id.richu);
                    myHandler.richu_textView.setText(richu);

                    myHandler.riluo_textView= (TextView) view.findViewById(R.id.riluo);
                    myHandler.riluo_textView.setText(riluo);

                    myHandler.jingdu_textView= (TextView) view.findViewById(R.id.jingdu);
                    myHandler.jingdu_textView.setText(jingdu);

                    myHandler.weidu_textView= (TextView) view.findViewById(R.id.weidu);
                    myHandler.weidu_textView.setText(weidu);
                    myHandler.haiba_textView= (TextView) view.findViewById(R.id.haiba);
                    myHandler.haiba_textView.setText(haiba);
                    break;

                case 2:
                    view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item2, null);
                    myHandler = new MyHandler(view);
                    myHandler.textView = (TextView) view.findViewById(R.id.xiaotieshi);
                    myHandler.textView_vdongni= (TextView) view.findViewById(R.id.textView);
                    String vdongni = null;
                    switch (i){
                        case -5:
                            vdongni="寒风“呼呼”地咆哮着，行人万般无奈，只得将冬衣扣得严严实实的，把手揣在衣兜里，缩着脖子，疾步前行。而大路两旁的松柏，却精神抖擞地挺立着，傲迎风霜雨雪，激励着人们勇敢地前进。";
                            break;
                        case 0:
                            vdongni="天刚见明，我背着书包，徒步走在上学路上。天和地的界限是那么朦胧：山是白的，天是白的，水上也飘着白雾。我想摸摸这奇怪的雾，可它像个调皮的孩子，一会儿逃向东，一会儿逃向西……";
                            break;
                        case 5:
                            vdongni="云冷江空岁暮时，竹阴梅影月参差。鸡催梦枕司晨早，更咽寒城报点迟。";
                            break;
                        case 10:
                            vdongni="摘来一颗星星用思念写上：吉祥如意；拂去一丝寒意用关怀写上：天凉要爱惜自己。";
                            break;
                        case 15:
                            vdongni="沐浴着清新的阳光和雨露，看着生命正在蓬勃的生长，我的诗文也开始放射着春天幽香。";
                            break;
                        case 20:
                            vdongni="越来越浓重的云,从身边紧紧划过去的风,在繁华而又奚落的街道里,那蓄积污水的坑死死的拽着我沉重的脚,我只不过失去了你,却为何像失去了世界。";
                            break;
                        case 25:
                            vdongni="晴天里有阳光，阳光总是充满温馨，相信有这么多朋友的厚爱和鼓励，晴天会永远阳光灿烂。";
                            break;
                        case 30:
                            vdongni="我祈望，在阳光明媚的街角遇见你，然后遇见我自己。";
                            break;
                        case 35:
                            vdongni="盛夏，天热得连蜻蜓都只敢贴着树荫处飞，好象怕阳光伤了他们的翅膀。";
                            break;
                        case 40:
                            vdongni="特别提示：后羿因回家抱孩子所以不能按时出来射日，请大家做好避暑准备。";
                            break;
                    }
                    myHandler.textView_vdongni.setText(vdongni);
                    myHandler.cardView = (CardView) view.findViewById(R.id.xiaotieshi_cardView);
                    myHandler.textView.setText("v懂你");
                    break;
                case 3:
                    view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item3, null);
                    myHandler = new MyHandler(view);
                    switch_weixingdingwei= (Switch) view.findViewById(R.id.switch1);
                    switch_weixingdingwei.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Snackbar.make(v,"还未完善，敬请期待！",Snackbar.LENGTH_SHORT).show();
                        }
                    });
                    myHandler.cardView = (CardView) view.findViewById(R.id.item3_cardView);
                    break;
                case 4:
                    view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item4, null);
                    myHandler = new MyHandler(view);
                    switch_tixing= (Switch) view.findViewById(R.id.switch1);

                    SharedPreferences tixing=getSharedPreferences("tixing",MODE_PRIVATE);
                    boolean istixing=tixing.getBoolean("tixing",false);
                    if(istixing){
                        switch_tixing.setChecked(true);
                    }

                    switch_tixing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                SharedPreferences.Editor editor=getSharedPreferences("tixing",MODE_PRIVATE).edit();
                                editor.putBoolean("tixing",true);
                                editor.commit();
                                Intent intent=new Intent(MainActivity.this,tixingService.class);
                                startService(intent);
                            }else{
                                SharedPreferences.Editor editor=getSharedPreferences("tixing",MODE_PRIVATE).edit();
                                editor.putBoolean("tixing",false);
                                editor.commit();
                                Intent intent=new Intent(MainActivity.this,tixingService.class);
                                stopService(intent);
                            }
                        }
                    });
                    myHandler.cardView = (CardView) view.findViewById(R.id.item4_cardView);
                    break;
            }
            return myHandler;
        }

        @Override
        public void onBindViewHolder(MyHandler holder, int position) {
            int i = getItemViewType(position);
            if (i == 0) {
                ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();//得到item的LayoutParams布局参数
//            ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(null);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                params.height=(displayMetrics.heightPixels)/3;
                params.width = (displayMetrics.widthPixels) - 8;
                holder.cardView.setLayoutParams(params);
//            params.height = heights.get(position);//把随机的高度赋予itemView布局
//            holder.textView.setLayoutParams(params);//把params设置给itemView布局
            } else if(i==1){
                ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();//得到item的LayoutParams布局参数
//            ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(null);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                params.height=(displayMetrics.heightPixels)/3;
                params.width = (displayMetrics.widthPixels) - 8;
                holder.cardView.setLayoutParams(params);


            } else if (i == 2) {

                ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();//得到item的LayoutParams布局参数
//            ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(null);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                params.height=(displayMetrics.heightPixels)/3;
                params.width = (displayMetrics.widthPixels) - 8;
                holder.cardView.setLayoutParams(params);
            } else if (i == 3) {

            } else if(i == 4) {

            }
        }

        @Override
        public int getItemCount() {
            return 5;
        }

        class MyHandler extends RecyclerView.ViewHolder {
            private TextView textView;
            private CardView cardView;
            private Switch switch_1;
            private TextView zuidiwendu_textView;
            private TextView zuigaowendu_textView;
            private Switch switch_2;
            private TextView textView1_fengsu;
            private TextView textView1_fengxiang;
            private TextView textView1_fengli;
            private TextView textView1_xiangduishidu;
            private TextView textView1_wendu_2;
            private TextView textView_vdongni;

            private TextView richu_textView;
            private TextView riluo_textView;
            private TextView jingdu_textView;
            private TextView weidu_textView;
            private TextView haiba_textView;
            private TextView diquxiangqing;

            public MyHandler(View itemView) {
                super(itemView);
            }
        }
    }


}










