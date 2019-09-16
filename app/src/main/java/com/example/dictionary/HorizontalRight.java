package com.example.dictionary;


import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.media.MediaFunction;

import org.w3c.dom.Text;

import java.io.File;
import java.io.PrintStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class HorizontalRight extends Fragment implements View.OnClickListener {

    TextView tv;
    public LayoutInflater layoutInflater;
    public static final String TAG = "RightFragment";
    private TextView detail_word,detail_translation;
    private String word,translation;
    private Button btn_us,btn_uk;
    private MediaPlayer mediaPlayer;
    private String dirPath = Environment.getExternalStorageDirectory().toString();



    public HorizontalRight() {
        // Required empty public constructor
    }


    public static HorizontalRight newInstance(String str){
        HorizontalRight horizontalRight = new HorizontalRight();
        Bundle bundle = new Bundle();
        bundle.putString(TAG,str);
        horizontalRight.setArguments(bundle);
        return horizontalRight;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_horizontal_right, container, false);
        layoutInflater = getActivity().getLayoutInflater();
        init(view);
        return view;
    }

    private void init(View view){
        detail_word = view.findViewById(R.id.horizontal_right_word);
        detail_translation = view.findViewById(R.id.horizontal_right_translation);
        btn_uk = view.findViewById(R.id.horizontal_pronunciation_uk);
        btn_us = view.findViewById(R.id.horizontal_pronunciation_us);
        mediaPlayer = new MediaPlayer();
        btn_uk.setOnClickListener(this);
        btn_us.setOnClickListener(this);
        btn_uk.setVisibility(View.INVISIBLE);
        btn_us.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.horizontal_pronunciation_uk:{
                fetchSoundTrack("us");
                break;
            }
            case R.id.horizontal_pronunciation_us:{
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

    public void setWord(String w, String t){
        word = w;
        translation = t;
        detail_word.setText(word);
        detail_translation.setText(translation);
        btn_uk.setVisibility(View.VISIBLE);
        btn_us.setVisibility(View.VISIBLE);

    }

}
