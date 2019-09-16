package com.example.dictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.media.MediaFunction;

import java.io.File;
import java.io.PrintStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VerticalWordDetails extends AppCompatActivity implements View.OnClickListener{

    private String word;
    private String translation;
    private TextView detail_word,detail_translation;
    private Button btn_us,btn_uk;
    private MediaPlayer mediaPlayer;
    private MediaFunction mediaFunction = new MediaFunction();
    private String dirPath = Environment.getExternalStorageDirectory().toString();

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
        btn_us.setOnClickListener(this);
        btn_uk.setOnClickListener(this);

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
}
