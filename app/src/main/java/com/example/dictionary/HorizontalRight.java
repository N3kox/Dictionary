package com.example.dictionary;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.XMLParser.Parser;
import com.example.XMLParser.dict;
import com.example.media.MediaFunction;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class HorizontalRight extends Fragment implements View.OnClickListener {

    public LayoutInflater layoutInflater;
    public static final String TAG = "RightFragment";
    private TextView detail_word,detail_translation;
    private String word,translation;
    private Button btn_us,btn_uk;
    private Button btn_add_vocabulary;
    private MediaPlayer mediaPlayer;
    private String dirPath = Environment.getExternalStorageDirectory().toString();
    private TextView net_ps,net_pos,net_acceptation,net_sent;
    private TextView horizontal_hide_1,horizontal_hide_2;

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
        btn_add_vocabulary = view.findViewById(R.id.horizontal_right_add_vocabulary);
        net_ps = view.findViewById(R.id.horizontal_net_ps);
        net_pos = view.findViewById(R.id.horizontal_net_pos);
        net_acceptation = view.findViewById(R.id.horizontal_net_acceptation);
        net_sent = view.findViewById(R.id.horizontal_net_sent);
        horizontal_hide_1 = view.findViewById(R.id.horizontal_hide_1);
        horizontal_hide_2 = view.findViewById(R.id.horizontal_hide_2);
        mediaPlayer = new MediaPlayer();
        btn_uk.setOnClickListener(this);
        btn_us.setOnClickListener(this);
        btn_add_vocabulary.setOnClickListener(this);
        linearLayout = view.findViewById(R.id.horizontal_tts_btn_ground);
        linearLayout.setVisibility(View.INVISIBLE);
        btn_uk.setVisibility(View.INVISIBLE);
        btn_us.setVisibility(View.INVISIBLE);
        btn_add_vocabulary.setVisibility(View.INVISIBLE);
        horizontal_hide_1.setVisibility(View.INVISIBLE);
        horizontal_hide_2.setVisibility(View.INVISIBLE);

        if(textToSpeech == null)
            textToSpeech = new TextToSpeech(view.getContext(),new TTSListener());

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
            case R.id.horizontal_right_add_vocabulary:{
                if(checkExistenceInVocabulary(word)){
                    insertIntoVocabulary(word,translation);
                }else{
                    Toast.makeText(getActivity(),"生词本中已有此单词",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }

    }

    private boolean checkExistenceInVocabulary(String word){
        Uri uri = Uri.parse(Configuration.URI_VOCABULARY);
        ContentResolver resolver =  getActivity().getContentResolver();
        Cursor cursor2 = resolver.query(uri, new String[]{"word"}, "word = ?", new String[]{word}, null);
        if(cursor2.getCount() > 0){
            return false;
        }
        return true;
    }

    private void insertIntoVocabulary(String word,String translation){
        Uri uri = Uri.parse(Configuration.URI_VOCABULARY);
        ContentResolver resolver = getActivity().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put("word",word);
        contentValues.put("translation",translation);
        resolver.insert(uri,contentValues);
        Toast.makeText(getActivity(),"加入生词表成功",Toast.LENGTH_SHORT).show();
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
        btn_add_vocabulary.setVisibility(View.VISIBLE);
        horizontal_hide_1.setVisibility(View.VISIBLE);
        horizontal_hide_2.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);

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
        dict d = null;
        try {
            InputStream inputStream = new FileInputStream(filePath);
            d = Parser.parseXML(inputStream);
            Log.d("#TEST",d.toString());
            net_ps.setText("发音:"+d.getPs());
            net_pos.setText("词性:"+d.getPos());
            net_acceptation.setText("网络词义:"+d.getAcceptation());
            String showList = "";
            for(int i = 0;i<d.getSents().size();i++){
                final String orig = d.getSents().get(i).getOrig();
                showList += "例句"+(i+1)+":"+d.getSents().get(i).getOrig()+"\n" +
                        "翻译"+(i+1)+":"+d.getSents().get(i).getTrans()+"\n\n";

                Button btnTTS = new Button(getContext());
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

}
