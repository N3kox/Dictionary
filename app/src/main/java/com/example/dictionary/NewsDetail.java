package com.example.dictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.TextView;

import com.example.newsFetcher.HtmlParser;
import com.example.newsFetcher.HttpTools;

import java.util.ArrayList;
import java.util.List;

public class NewsDetail extends AppCompatActivity {


    public static final String DetailUrl = "detailUrl";//键
    private static String newsDetailUrl;//值
    public static final String DetailTitle = "title";//键
    private static String newsDetailTitle;//值
    public static final String NewsDate = "date"; //键
    private static String newsDate; //值

    private final static int GET_DATA_SUCCEED = 1;

    public TextView newsDetail;
    public List<String> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Intent intent = getIntent();
        newsDetailTitle = intent.getStringExtra(DetailTitle);
        newsDetailUrl = intent.getStringExtra(DetailUrl);
        newsDate = intent.getStringExtra(NewsDate);

        initView();
        initData();


    }

    public void initView() {
        list = new ArrayList<String>();
        newsDetail = (TextView) findViewById(R.id.txt_news_detail);
    }

    public void initData() {
        //开启一个线程执行耗时操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取网络数据
                String result = HttpTools.myRequest(newsDetailUrl, "utf-8");

                List<String> list = HtmlParser.parseHtmlNewsDetailData(result);
                StringBuffer sb = new StringBuffer();
                if (list.get(0).equals("-1")) {//无内容
                    sb.append(newsDate);
                } else {
                    for (int i = 0; i < list.size(); i++) {//list.size()
                        sb.append("    "+list.get(i));
                    }
                }
                StringBuffer str = new StringBuffer();
                str.append(newsDetailTitle + "\n" + sb);
                mHandler.sendMessage(mHandler.obtainMessage(GET_DATA_SUCCEED, str));
            }
        }).start();
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA_SUCCEED:
                    Log.d("#NEWSDETAIL","succeed");
                    StringBuffer detail = (StringBuffer) msg.obj;
                    //设置不同字体
                    final SpannableStringBuilder sp = new  SpannableStringBuilder(detail);
                    sp.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, newsDetailTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //粗体
                    sp.setSpan(new AbsoluteSizeSpan(70), 0, newsDetailTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//字体大小为70像素
                    sp.setSpan(new AbsoluteSizeSpan(40), newsDetailTitle.length(), detail.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体大小为40像素
                    newsDetail.setText(sp);
                    break;
            }
        }
    };
}
