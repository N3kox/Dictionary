package com.example.dictionary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class MainActivity extends AppCompatActivity {

    Fragment HorizontalMain = new HorizontalMain();
    Fragment VerticalMain = new VerticalMain();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragment();
    }
    private void initFragment(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        if (dm.widthPixels > dm.heightPixels) {
            //横屏
            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, HorizontalMain,"HorizontalMain").commit();
        } else {
            //竖屏
            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, VerticalMain,"VerticalMain").commit();
        }
    }

    //处理转屏主activity多次oncreate问题
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            //Toast.makeText(getApplicationContext(),"横屏",Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, HorizontalMain,"HorizontalMain").commit();
        }
        else{
            //Toast.makeText(getApplicationContext(),"竖屏",Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, VerticalMain,"VerticalMain").commit();
        }
    }
}
