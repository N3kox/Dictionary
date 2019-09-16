package com.example.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DBHelper extends SQLiteOpenHelper {

    private Context myContext;

    //词库标识
    public static final String WORDS_TABLE_NAME = "EnWords";

    //生词本标识
    public static final String MY_VOCABULARY_TABLE_NAME = "MyVocabulary";

    //历史记录标识
    public static final String HISTORY_TABLE_NAME = "History";




    private static final String CREATE_ENWORDS = "create table EnWords(" +
            "word text," +
            "translation text)";
    private static final String CREATE_MY_VOCABULARY = "create table MyVocabulary(" +
            "word text," +
            "translation text)";
    private static final String CREATE_HISTORY = "create table History(" +
            "word text," +
            "translation text)";

    private static final String INSERT_BASIC = "insert into EnWords (word, translation) values";


    //构造
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        myContext = context;
    }


    //初始化构建库表结构
    public void onCreate(SQLiteDatabase db){
        //初始化本地词库与用户生词本
        db.execSQL(CREATE_ENWORDS);
        db.execSQL(CREATE_MY_VOCABULARY);
        db.execSQL(CREATE_HISTORY);

        //检查初始化情况
        //本地词库内数据初始化
        if(!tableExists(WORDS_TABLE_NAME,db)){
            Log.d("db","EnWords built");
            executeAssetsSQL(db,"EnWords.csv");
        }
        else{
            Log.d("db-error","EnWords exists");
        }
        if(!tableExists(MY_VOCABULARY_TABLE_NAME,db)){
            Log.d("db","Vocabulary built");
        }
        else{
            Log.d("db-error","Vocabulary exists");
        }
    }

    //csv文件导入本地词库
    public void executeAssetsSQL(SQLiteDatabase db,String schemaName){
        BufferedReader in = null;
        try{
            in = new BufferedReader(new InputStreamReader(myContext.getAssets().open(Configuration.DB_PATH+"/"+schemaName)));
            String line;
            while((line = in.readLine()) != null) {
                String executionStr = INSERT_BASIC + "(" + line +")";
                db.execSQL(executionStr);
            }
        }catch (IOException e) {
            Log.e("db-error", e.toString());
        }finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                Log.e("db-error", e.toString());
            }
        }
    }


    //校验表结构存在且表内数据为空
    public boolean tableExists(String tableName,SQLiteDatabase db){
        boolean result = false;
        if(tableName == null)
            return result;
        Cursor cursor = null;
        try{
            String sql = "select translation from "+tableName+" where word = 'ab'";
            cursor = db.rawQuery(sql,null);
            if(cursor.moveToNext()){
                String data = cursor.getString(0);
                Log.d("db",data);
                if(data != null)
                    result = true;
            }
        }catch (Exception e){

        }
        return result;
    }

    //升级
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
