package com.example.dictionary;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ContentProvider extends android.content.ContentProvider {
    private Context mContext;
    DBHelper mDbHelper = null;
    SQLiteDatabase db = null;

    public static final String AUTOHORITY = "example.dictionary.provider";
    // 设置ContentProvider的唯一标识


    public static final int SEARCH_CODE = 1;
    public static final int VOCABULARY_CODE = 2;
    public static final int HISTORY = 3;

    // UriMatcher类使用:在ContentProvider 中注册URI
    private static final UriMatcher mMatcher;
    static{
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // 初始化
        mMatcher.addURI(AUTOHORITY,"search", SEARCH_CODE);
        mMatcher.addURI(AUTOHORITY, "vocabulary", VOCABULARY_CODE);
        mMatcher.addURI(AUTOHORITY, "history", HISTORY);
        // 若URI资源路径 = content://example.dictionary.provider/search ，则返回注册码SEARCH_CODE
        // 若URI资源路径 = content://example.dictionary.provider/vocabulary ，则返回注册码VOCABULARY_CODE
    }

    // 以下是ContentProvider的6个方法

    /**
     * 初始化ContentProvider
     */
    @Override
    public boolean onCreate() {
        //初始化获取数据库
        mContext = getContext();
        mDbHelper = new DBHelper(getContext(),"userTest",null,1);
        db = mDbHelper.getWritableDatabase();
        return true;
    }

    /**
     * 添加数据
     */

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        String table = getTableName(uri);
        db.insert(table, null, values);

        // 当该URI的ContentProvider数据发生变化时，通知外界（即访问该ContentProvider数据的访问者）
        mContext.getContentResolver().notifyChange(uri, null);
        return uri;
    }

    /**
     * 查询数据
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        String table = getTableName(uri);

        // 查询数据
        return db.query(table,projection,selection,selectionArgs,null,null,sortOrder,null);
    }

    /**
     * 更新数据
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return db.update(getTableName(uri),values,selection,selectionArgs);
    }

    /**
     * 删除数据
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return db.delete(getTableName(uri),selection,selectionArgs);
    }

    @Override
    public String getType(Uri uri) {

        return null;
    }

    /**
     * 根据URI匹配 URI_CODE，从而匹配ContentProvider中相应的表名
     */
    private String getTableName(Uri uri){
        String tableName = null;
        switch (mMatcher.match(uri)) {
            case SEARCH_CODE:
                tableName = DBHelper.WORDS_TABLE_NAME;
                break;
            case VOCABULARY_CODE:
                tableName = DBHelper.MY_VOCABULARY_TABLE_NAME;
                break;
            case HISTORY:
                tableName = DBHelper.HISTORY_TABLE_NAME;
                break;
        }
        return tableName;
    }
}
