package com.example.dictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TranslationPopup extends AppCompatActivity{

    private String word,translation;
    private TextView wordText,translationText;
    private Button addVocabulary,navigateToDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation_popup);
        //init();
    }
    private void init(){
        /*
        wordText = findViewById(R.id.pop_word);
        translationText = findViewById(R.id.pop_translation);
        addVocabulary = findViewById(R.id.pop_add_vocabulary);
        navigateToDetail = findViewById(R.id.pop_navigate_to_detail);

        wordText.setText(word);
        translationText.setText(translation);
        */
    }
    /*
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.pop_add_vocabulary:{
                Log.d("#pop","addvocabulary");
                break;
            }
            case R.id.pop_navigate_to_detail:{
                Log.d("#pop","navigate");
                break;
            }
        }
    }
    */
}
