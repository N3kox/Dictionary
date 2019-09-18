package com.example.dictionary;


import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class VerticalMain extends ListFragment {
    private int EDIT_OK = 1;
    private String TAG = "VerticalMain";


    private ArrayAdapter<String> adapter;
    private ListView wordListView;
    private EditText inputSearch;
    public LayoutInflater layoutInflater;
    private String[] words = new String[]{};
    private String[] historyWords = new String[]{};
    private String[] translations = new String[]{};
    private String[] historyTranslations = new String[]{};
    private boolean bundleNotGetMark = false;
    private int number;



    public VerticalMain() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        wordListView = getListView();
        this.registerForContextMenu(wordListView);
        if(!((MainActivity)getActivity()).getBundle().isEmpty() && !bundleNotGetMark){
            buildBundle();
        }
        Log.d("#RESUME","ok");
        bundleNotGetMark = false;
        if(words.length > 0)
            rebuildListView();
    }


    public void buildBundle(){
        Bundle bundle = ((MainActivity)getActivity()).getBundle();
        inputSearch.setText(bundle.getString("input"));
        words = bundle.getStringArray("words");
        translations = bundle.getStringArray("translations");
        historyWords = bundle.getStringArray("historyWords");
        historyTranslations = bundle.getStringArray("historyTranslations");
    }

    public Bundle saveBundle(){
        Bundle bundle = new Bundle();
        bundle.putString("input",inputSearch.getText().toString());
        bundle.putStringArray("words",words);
        bundle.putStringArray("translations",translations);
        bundle.putStringArray("historyWords",historyWords);
        bundle.putStringArray("historyTranslations",historyTranslations);
        return bundle;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).setBundle(saveBundle());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity)getActivity()).setBundle(saveBundle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vertical_main, container, false);
        init(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_enter_vocabulary:{
                Intent intent = new Intent(getActivity(),MyVocabularyList.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_delete_history:{
                Toast.makeText(getActivity(),"删除历史",Toast.LENGTH_SHORT).show();
                deleteHistory();
                break;
            }
            case R.id.menu_news:{
                Toast.makeText(getActivity(),"新闻阅读",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),NewsIndex.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_help:{
                Toast.makeText(getActivity(),"帮助toast",Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(View view){
        inputSearch = view.findViewById(R.id.vertical_input_search);
        layoutInflater = getActivity().getLayoutInflater();
        selectHistory();
        words = historyWords;
        translations = historyTranslations;

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //延迟回调，防止递归监听内存爆炸
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable,500);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Log.d(TAG,"what?"+msg.what);
            if (EDIT_OK == msg.what) {
                Log.d(TAG, "handleMessage() returned:输入完成 " );
                //do something
                searchForResult(inputSearch.getText().toString());
            }

        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(EDIT_OK);
        }
    };

    private boolean searchForResult(String keyWord){
        ArrayList<String> wordList = new ArrayList<>();
        ArrayList<String> translationList = new ArrayList<>();
        if(keyWord.equals("")){
            words = historyWords;
            translations = historyTranslations;
            rebuildListView();
            //this.setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,words));
            return true;
        }
        try{

            Uri uri = Uri.parse(Configuration.URI_SEARCH);
            ContentResolver resolver =  getActivity().getContentResolver();
            String[] k = {keyWord+"%"};
            Cursor cursor2 = resolver.query(uri, new String[]{"word","translation"}, "word like ?", k, null);

            if(cursor2 != null && cursor2.getCount() > 0){
                while(cursor2.moveToNext()){
                    wordList.add(cursor2.getString(0));
                    translationList.add(cursor2.getString(1));
                }
            }

            int len = wordList.size();
            words = new String[len];
            translations = new String[len];

            for(int i = 0 ;i<len;i++){
                words[i] = wordList.get(i);
                translations[i] = translationList.get(i);
            }
            //this.setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,words));
            rebuildListView();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }



    private void rebuildListView(){
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,words);
        wordListView.setAdapter(adapter);
    }

    private boolean selectHistory(){
        try{
            Uri uri = Uri.parse(Configuration.URI_HISTORY);
            ContentResolver resolver =  getActivity().getContentResolver();
            Cursor cursor2 = resolver.query(uri, new String[]{"word","translation"},null, null, null);

            ArrayList<String> historyWordList = new ArrayList<>();
            ArrayList<String> historyTranslationList = new ArrayList<>();

            if(cursor2 != null && cursor2.getCount() > 0){
                while(cursor2.moveToNext()){
                    historyWordList.add(cursor2.getString(0));
                    historyTranslationList.add(cursor2.getString(1));
                }
            }

            int len = historyWordList.size();
            historyWords = new String[len];
            historyTranslations = new String[len];

            for(int i = 0 ;i<len;i++){
                historyWords[len-1-i] = historyWordList.get(i);
                historyTranslations[len-1-i] = historyTranslationList.get(i);
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        try{
            //Toast.makeText(getActivity(),translations[position],Toast.LENGTH_SHORT).show();
            //竖屏点击跳转至详情
            insertHistory(words[position],translations[position]);
            //处理从activity返回后fragment获取保存bundle从而无法正确显示历史记录与原输入问题
            bundleNotGetMark = true;
            Intent intent = new Intent(getActivity(),VerticalWordDetails.class);
            intent.putExtra("word",words[position]);
            intent.putExtra("translation",translations[position]);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private boolean insertHistory(String word,String translation){
        try{
            //检查是否已经在历史记录中（如果查询到则提至首位操作未做）
            int posi = 0;
            for(;posi<historyWords.length;posi++)
                if(word.equals(historyWords[posi]))
                    return true;

            Uri uri = Uri.parse(Configuration.URI_HISTORY);
            ContentResolver resolver =  getActivity().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put("word",word);
            contentValues.put("translation",translation);
            Uri newUri= resolver.insert(uri,contentValues);
            Log.d("#URI","before"+uri.toString());
            Log.d("#URI","after"+newUri.toString());


            String[] newHistoryWords = new String[historyWords.length+1];
            String[] newHistoryTranslations = new String[historyTranslations.length+1];
            newHistoryWords[0] = word;
            newHistoryTranslations[0] = translation;
            for(int i = 0 ;i<historyWords.length;i++){
                newHistoryWords[i+1] = historyWords[i];
                newHistoryTranslations[i+1] = historyTranslations[i];
            }
            historyWords = newHistoryWords;
            historyTranslations = newHistoryTranslations;

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean deleteHistory(){
        try{
            Uri uri = Uri.parse(Configuration.URI_HISTORY);
            ContentResolver resolver = getActivity().getContentResolver();
            resolver.delete(uri,null,null);
            historyWords = new String[]{};
            historyTranslations = new String[]{};
            //重绘
            if(inputSearch.getText().toString().equals("")){
                words = new String[]{};
                translations = new String[]{};
                rebuildListView();
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
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
        getActivity().getMenuInflater().inflate(R.menu.long_tap_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_item1) {
            Toast.makeText(getActivity(), "加入生词本 with number:"+number, Toast.LENGTH_SHORT).show();
            if(checkExistenceInVocabulary(words[number])){
                insertIntoVocabulary(words[number],translations[number]);
            }else{
                //已在生词本中
                Toast.makeText(getActivity(),"生词表已有此单词",Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.menu_item2) {
            Toast.makeText(getActivity(), "选项2 with number:"+number, Toast.LENGTH_SHORT).show();
        }

        return super.onContextItemSelected(item);
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

}
