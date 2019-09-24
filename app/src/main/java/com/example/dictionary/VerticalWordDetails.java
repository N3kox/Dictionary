package com.example.dictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.media.MediaFunction;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.example.XMLParser.*;

public class VerticalWordDetails extends AppCompatActivity implements View.OnClickListener{

    private String word;
    private String translation;
    private TextView detail_word,detail_translation;
    private Button btn_us,btn_uk;
    private Button btn_add_vocabulary;
    private MediaPlayer mediaPlayer;
    private MediaFunction mediaFunction = new MediaFunction();
    private String dirPath = Environment.getExternalStorageDirectory().toString();
    private TextView net_ps,net_pos,net_acceptation,net_sent;
    private LinearLayout linearLayout;
    private TextToSpeech textToSpeech = null;

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
        setContentView(R.layout.activity_vertical_word_details);
        init();
    }



    private void init(){
        Intent intent = getIntent();
        word = intent.getStringExtra("word");
        translation = intent.getStringExtra("translation");
        detail_word = findViewById(R.id.detail_word);
        detail_translation = findViewById(R.id.detail_translation);
        detail_word.setText(word);
        detail_translation.setText(translation);
        mediaPlayer = new MediaPlayer();
        btn_us = findViewById(R.id.vertical_Pronunciation_US);
        btn_uk = findViewById(R.id.vertical_Pronunciation_UK);
        btn_add_vocabulary = findViewById(R.id.vertical_add_vocabulary);
        net_ps = findViewById(R.id.vertical_net_ps);
        net_pos = findViewById(R.id.vertical_net_pos);
        net_acceptation = findViewById(R.id.vertical_net_acceptation);
        net_sent = findViewById(R.id.vertical_net_sent);
        btn_us.setOnClickListener(this);
        btn_uk.setOnClickListener(this);
        btn_add_vocabulary.setOnClickListener(this);
        linearLayout = findViewById(R.id.vertical_tts_btn_ground);
        if(textToSpeech == null)
            textToSpeech = new TextToSpeech(this,new TTSListener());


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
                            //播放
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

    public void func(String filePath){
        try {
            InputStream inputStream = new FileInputStream(filePath);
            dict d = Parser.parseXML(inputStream);
            Log.d("#TEST",d.toString());
            net_ps.setText("发音:"+d.getPs());
            net_pos.setText("词性:"+d.getPos());
            net_acceptation.setText("网络词义:"+d.getAcceptation());
            String showList = "";

            for(int i = 0;i<d.getSents().size();i++){
                final String orig = d.getSents().get(i).getOrig();
                showList += "例句"+(i+1)+":"+d.getSents().get(i).getOrig()+"\n" +
                        "翻译"+(i+1)+":"+d.getSents().get(i).getTrans()+"\n\n";

                Button btnTTS = new Button(this);
                btnTTS.setText("语句发音"+(i+1));
                btnTTS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(textToSpeech.isSpeaking())
                            textToSpeech.stop();
                        textToSpeech.speak(orig,TextToSpeech.QUEUE_FLUSH,null);
                    }
                });
                linearLayout.addView(btnTTS);
            }
            net_sent.setText(showList);
        }catch (Exception e){
            Log.d("#TEST","failed");
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.vertical_Pronunciation_US:{
                //String fileName = "pronunciation_"+word+"_us.mp3";
                fetchSoundTrack("us");
                break;
            }
            case R.id.vertical_Pronunciation_UK:{
                //String fileName = "pronunciation_"+word+"_uk.mp3";
                fetchSoundTrack("uk");
                break;
            }
            case R.id.vertical_add_vocabulary:{
                if(checkExistenceInVocabulary(word)){
                    insertIntoVocabulary(word,translation);
                }else{
                    Toast.makeText(this,"生词本中已有此单词",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
    private void fetchSoundTrack(final String type){
        final String fileName = type.equals("us")?"pronunciation_"+word+"_us.mp3":"pronunciation_"+word+"_uk.mp3";
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = dirPath + "/" + fileName;
                File file = new File(filePath);
                if(file.exists()){
                    //播放
                    play(filePath);
                }
                else{
                    try{
                        OkHttpClient client = new OkHttpClient();
                        String url = "https://media.shanbay.com/audio/"+type+"/"+word+".mp3";
                        Request request = new Request.Builder()
                                .url(url)
                                .build();
                        Response response = client.newCall(request).execute();
                        if(response.isSuccessful()){
                            PrintStream ps = new PrintStream(file);
                            byte[] bytes = response.body().bytes();
                            ps.write(bytes,0,bytes.length);
                            ps.close();
                            //播放
                            play(filePath);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public void play(String filePath){
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepareAsync();
            //Log.d(TAG,"read wanle");
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
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


}
