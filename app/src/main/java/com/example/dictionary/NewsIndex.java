package com.example.dictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.newsFetcher.HtmlParser;
import com.example.newsFetcher.HttpTools;
import com.example.newsFetcher.NewsOverview;

import java.util.ArrayList;
import java.util.List;

public class NewsIndex extends AppCompatActivity {

    public static final String DetailUrl = "detailUrl";//键
    public static final String DetailTitle = "title";//键
    public static final String NewsDate = "date"; //键

    private ListView mListView;
    private List<NewsOverview> list;
    private NewsIndexAdapter adapter;
    private final static int GET_DATA_SUCCEED = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_index);

        //初始化视图
        initView();
        //初始化数据
        initData();
    }

    public void initView() {
        list = new ArrayList<NewsOverview>();
        mListView =findViewById(R.id.list_view_news);
    }

    public void initData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取网络数据
                //翻页 --- page=n
                String result = HttpTools.myRequest("https://so.cntv.cn/language/english/index.php?qtext=new&type=-1&sort=date&page=1&vtime=-1&datepid=1&history=yes","utf-8");
                List<NewsOverview> list = HtmlParser.parseHtmlNews(result);

                mHandler.sendMessage(mHandler.obtainMessage(GET_DATA_SUCCEED, list));
            }
        }).start();

    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA_SUCCEED:
                    List<NewsOverview> list = (List<NewsOverview>) msg.obj;
                    //新闻列表适配器
                    adapter = new NewsIndexAdapter(getBaseContext(), list, R.layout.activity_news_index_adapter);
                    mListView.setAdapter(adapter);
                    //设置点击事件
                    mListView.setOnItemClickListener(new ItemClickListener());
                    //Toast.makeText(getApplicationContext(), String.valueOf(list.size()), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    /**
     * 新闻列表点击事件
     */
    public class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            NewsOverview temp =(NewsOverview) adapter.getItem(i);
            Intent intent= new Intent(NewsIndex.this,NewsDetail.class);
            intent.putExtra(DetailUrl,temp.getNewsUrl());
            intent.putExtra(DetailTitle,temp.getNewsTitle());
            intent.putExtra(NewsDate,temp.getNewsDate());
            startActivity(intent);

        }
    }

}
