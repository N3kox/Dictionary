package com.example.dictionary;

import android.speech.tts.TextToSpeech;
import android.util.Log;

public class TTSListener implements TextToSpeech.OnInitListener {
    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            Log.d("#TTS","init success");
        }else{
            Log.d("#TTS","init failed");
        }
    }
}
