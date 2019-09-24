package com.example.dictionary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceControl;

public class MainActivity extends AppCompatActivity {

    Fragment HorizontalMain = new HorizontalMain();
    Fragment VerticalMain = new VerticalMain();
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragment();

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    //return;
                }
            }
        }
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

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle(){
        return this.bundle;
    }
}
