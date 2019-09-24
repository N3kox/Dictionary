package com.example.dictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;

public class MyVocabularyList extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ArrayAdapter<String> adapter;
    private ListView listView;
    private String[] words = null;
    private String[] translations = null;
    private int number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vocabulary_list);
        Intent intent = getIntent();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init(){
        listView = findViewById(R.id.my_vocabulary_list_view);
        listView.setOnItemClickListener(this);
        //listView.setOnItemLongClickListener(this);
        this.registerForContextMenu(listView);

        //获取生词本，倒序显示
        ArrayList<String> vocabularyWordList = new ArrayList<>();
        ArrayList<String> vocabularyTranslationList = new ArrayList<>();

        Uri uri = Uri.parse(Configuration.URI_VOCABULARY);
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"word","translation"},null, null, null);

        if(cursor != null && cursor.getCount() > 0){
            while(cursor.moveToNext()){
                vocabularyWordList.add(cursor.getString(0));
                vocabularyTranslationList.add(cursor.getString(1));
            }
        }
        int len = vocabularyTranslationList.size();
        words = new String[len];
        translations = new String[len];

        for(int i = 0;i<len;i++){
            words[len-i-1] = vocabularyWordList.get(i);
            translations[len-i-1] = vocabularyTranslationList.get(i);
        }
        refreshListView();
    }

    @Override
    public void registerForContextMenu(@NonNull View view) {
        super.registerForContextMenu(view);
        view.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("操作");
        menu.setHeaderIcon(R.drawable.ic_launcher_background);
        number = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;//获取listview的item对象
        this.getMenuInflater().inflate(R.menu.long_tap_delete_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_item_long_tap_delete) {
            Toast.makeText(this, "删除项目:"+number, Toast.LENGTH_SHORT).show();
            //删库
            Uri uri = Uri.parse(Configuration.URI_VOCABULARY);
            ContentResolver resolver = this.getContentResolver();
            resolver.delete(uri,"word = ?",new String[]{words[number]});
            //更新String list
            String[] newWords = new String[words.length-1];
            String[] newTranslations = new String[words.length-1];
            int i,j;
            for(i = 0;i<number;i++){
                newWords[i] = words[i];
                newTranslations[i] = translations[i];
            }
            for(j = number+1;j<words.length;j++,i++){
                newWords[i] = words[j];
                newTranslations[i] = translations[j];
            }
            words = newWords;
            translations = newTranslations;
            //更新ui
            refreshListView();
            Toast.makeText(this,"删除操作成功",Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }

    private void refreshListView(){
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,words);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //Toast.makeText(this,"light tap position:"+i,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,VerticalWordDetails.class);
        intent.putExtra("word",words[i]);
        intent.putExtra("translation",translations[i]);
        startActivity(intent);
    }

    /*
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(this,"long click position"+i,Toast.LENGTH_SHORT).show();
        return true;
    }
    */
}
