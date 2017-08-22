package com.okunu.app;

import com.okunu.app.skin.DynamicFragment;
import com.okunu.app.sync.SyncFragment;
import com.okunu.app.touch.Touch2Fragment;
import com.okunu.app.touch.TouchFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class FirstActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_first);
        
        //camera示例
        //setDefaultFragment(new CameraFragment());
        
        //外部拦截法
        //setDefaultFragment(new TouchFragment());
        
        //内部拦截法
        //setDefaultFragment(new Touch2Fragment());
        
        //ConditionVariable用法
        //setDefaultFragment(new SyncFragment());
        
        //动态加载相关
        setDefaultFragment(new DynamicFragment());
    }
    
    public void setDefaultFragment(Fragment fragment){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.main_content, fragment);
        transaction.commit();
    }

}
