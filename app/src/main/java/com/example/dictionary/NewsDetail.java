package com.example.dictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.XMLParser.Parser;
import com.example.XMLParser.dict;
import com.example.newsFetcher.HtmlParser;
import com.example.newsFetcher.HttpTools;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsDetail extends AppCompatActivity implements View.OnClickListener {


    public static final String DetailUrl = "detailUrl";//键
    private static String newsDetailUrl;//值
    public static final String DetailTitle = "title";//键
    private static String newsDetailTitle;//值
    public static final String NewsDate = "date"; //键
    private static String newsDate; //值
    private final static int GET_DATA_SUCCEED = 1;
    //final View inflate = View.inflate(this,R.layout.activity_news_detail,null);

    public TextView newsDetail;
    public List<String> list;
    private String word = null;
    private String translation = null;
    private LinearLayout thisLayout;

    private PopupWindow popupWindow;
    private TextView wordTextView,translationTextView;

    private String dirPath = Environment.getExternalStorageDirectory().toString();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                String filePath = dirPath + "/" +word + ".xml";
                func(filePath);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Intent intent = getIntent();
        newsDetailTitle = intent.getStringExtra(DetailTitle);
        newsDetailUrl = intent.getStringExtra(DetailUrl);
        newsDate = intent.getStringExtra(NewsDate);
        thisLayout = findViewById(R.id.layout_news_detail);
        initView();
        initData();
        initPopupWindow();

    }

    public void initView() {
        list = new ArrayList<String>();
        newsDetail = (TextView) findViewById(R.id.txt_news_detail);
        newsDetail.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                final int selStart = Selection.getSelectionStart(getText());
                final int selEnd = Selection.getSelectionEnd(getText());
                int min = Math.max(0, Math.min(selStart, selEnd));
                int max = Math.max(0, Math.max(selStart, selEnd));
                Log.d("#DETAIL",getText().toString().substring(min,max));
                final String selectedWord = getText().toString().substring(min,max).toLowerCase();
                word = selectedWord;
                //数据库尝试小写变换查询
                if(searchForResult(selectedWord)){
                    longTapWordDetail();
                }
                else{
                    Log.d("#TEST","selectedWord:"+selectedWord);
                    wordTextView.setText(selectedWord);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String filePath = dirPath + "/" +word + ".xml";
                            File file = new File(filePath);
                            if(file.exists()){
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }
                            else{
                                try{
                                    OkHttpClient client = new OkHttpClient();
                                    String url = "https://dict-co.iciba.com/api/dictionary.php?w="+word+"&key=49B6F1EBB26292D8988A833149904BC3";
                                    Request request = new Request.Builder()
                                            .url(url)
                                            .build();
                                    Response response = client.newCall(request).execute();
                                    if(response.isSuccessful()){
                                        PrintStream ps = new PrintStream(file);
                                        byte[] bytes = response.body().bytes();
                                        ps.write(bytes,0,bytes.length);
                                        ps.close();
                                        Message msg = new Message();
                                        msg.what = 1;
                                        handler.sendMessage(msg);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                        }
                    }).start();
                }

                //Toast.makeText(NewsDetail.this,"暂无单词数据",Toast.LENGTH_SHORT).show();
                /*
                MenuInflater menuInflater = mode.getMenuInflater();
                if(menuInflater != null)
                    menuInflater.inflate(R.menu.text_selected_menu,menu);
                */
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menu_search_vocabulary:{
                        //长按menu
                        /*
                        final int selStart = Selection.getSelectionStart(getText());
                        final int selEnd = Selection.getSelectionEnd(getText());
                        int min = Math.max(0, Math.min(selStart, selEnd));
                        int max = Math.max(0, Math.max(selStart, selEnd));
                        Log.d("#DETAIL",getText().toString().substring(min,max));
                        mode.finish();
                        */
                        break;
                    }
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }


    public void func(String filePath){
        dict d = null;
        try {
            InputStream inputStream = new FileInputStream(filePath);
            d = Parser.parseXML(inputStream);
            Log.d("#TEST",d.toString());
            if(d.getPs() == null){
                Toast.makeText(this,"暂无资料",Toast.LENGTH_SHORT).show();
                return;
            }
            String net_pos = "词性:"+d.getPos()+"\n";
            String net_acceptation = "网络词义:"+d.getAcceptation();

            translation = net_pos + net_acceptation;
            longTapWordDetail();

        }catch (Exception e){
            Log.d("#TEST","failed");
            e.printStackTrace();
        }
    }

    public void initPopupWindow(){
        View contentView = LayoutInflater.from(NewsDetail.this).inflate(R.layout.activity_translation_popup,null);
        popupWindow = new PopupWindow(contentView);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        wordTextView = contentView.findViewById(R.id.pop_word);
        translationTextView = contentView.findViewById(R.id.pop_translation);
        Button addVocabulary = contentView.findViewById(R.id.pop_add_vocabulary);
        Button navigateToDetail = contentView.findViewById(R.id.pop_navigate_to_detail);
        addVocabulary.setOnClickListener(NewsDetail.this);
        navigateToDetail.setOnClickListener(NewsDetail.this);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                thisLayout.setBackgroundColor(Color.WHITE);
                thisLayout.setAlpha(1);
            }
        });

    }
    public void longTapWordDetail(){
        wordTextView.setText(word);
        translationTextView.setText(translation);
        popupWindow.showAtLocation(newsDetail,Gravity.CENTER,0,0);
        thisLayout.setBackgroundColor(Color.GRAY);
        thisLayout.setAlpha((float)0.7);

    }

    public CharSequence getText() {
        return newsDetail.getText();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.pop_add_vocabulary:{
                if(popupWindow.isShowing())
                    popupWindow.dismiss();
                if(checkExistenceInVocabulary(word)){
                    insertIntoVocabulary(word,translation);
                }else{
                    Toast.makeText(this,"生词本已有此单词",Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.pop_navigate_to_detail:{
                Toast.makeText(this,"跳转",Toast.LENGTH_SHORT).show();
                if(popupWindow.isShowing())
                    popupWindow.dismiss();
                Intent intent = new Intent(NewsDetail.this,VerticalWordDetails.class);
                intent.putExtra("word",word);
                intent.putExtra("translation",translation);
                startActivity(intent);
                break;
            }
        }
    }

    private boolean checkExistenceInVocabulary(String word){
        Uri uri = Uri.parse(Configuration.URI_VOCABULARY);
        ContentResolver resolver =  this.getContentResolver();
        Cursor cursor2 = resolver.query(uri, new String[]{"word"}, "word = ?", new String[]{word}, null);
        if(cursor2.getCount() > 0){
            return false;
        }
        return true;
    }
    private void insertIntoVocabulary(String word,String translation){
        Uri uri = Uri.parse(Configuration.URI_VOCABULARY);
        ContentResolver resolver = this.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put("word",word);
        contentValues.put("translation",translation);
        resolver.insert(uri,contentValues);
        Toast.makeText(this,"加入生词表成功",Toast.LENGTH_SHORT).show();
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
                    newsDetail.setText(sp,TextView.BufferType.SPANNABLE);
                    break;
            }
        }
    };

    private boolean searchForResult(String keyWord){
        try{
            DBHelper dbHelper = new DBHelper(this,"userTest",null,1);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select word,translation from EnWords where word = '"+keyWord+"'",null);
            if(cursor != null && cursor.getCount() > 0) {
                cursor.moveToNext();
                word = cursor.getString(0);
                translation = cursor.getString(1);
            }
            else
                return false;
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
