package com.example.media;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.PrintStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MediaFunction {

    private String dirPath = Environment.getExternalStorageDirectory().toString();


    //Thread 获取音频资源
    public void fetchSoundTrack(final MediaPlayer mediaPlayer, final String fileName,final String word){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = dirPath + "/" + fileName;
                File file = new File(filePath);
                if(file.exists()){
                    //播放
                    play(mediaPlayer,filePath);
                }
                else{
                    try{
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("https://media.shanbay.com/audio/us/"+word+".mp3")
                                .build();
                        Response response = client.newCall(request).execute();
                        if(response.isSuccessful()){
                            PrintStream ps = new PrintStream(file);
                            byte[] bytes = response.body().bytes();
                            ps.write(bytes,0,bytes.length);
                            ps.close();
                            //播放
                            play(mediaPlayer,filePath);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }


    //获取音频地址，播放音频
    public void play(MediaPlayer mediaPlayer, String filePath){
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepareAsync();
            //Log.d(TAG,"read wanliao");
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
}
