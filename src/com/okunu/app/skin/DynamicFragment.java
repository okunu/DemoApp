package com.okunu.app.skin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import com.okunu.app.R;
import com.okunu.plugin.ITail;

import dalvik.system.DexClassLoader;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class DynamicFragment extends Fragment implements OnClickListener {

    ImageView mImage;
    Button mOriBtn, mThemeBtn;
    ITail mTail;
    String mPluginDir;
    Resources mResources;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dynamic_layout, container, false);
        mImage = (ImageView) view.findViewById(R.id.dynamic_img);
        mOriBtn = (Button) view.findViewById(R.id.dynamic_origin_btn);
        mThemeBtn = (Button) view.findViewById(R.id.dynamic_theme_btn);
        mOriBtn.setOnClickListener(this);
        mThemeBtn.setOnClickListener(this);

        getTail();
        loadRes();

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dynamic_origin_btn) {
            int id = R.drawable.man;
            mImage.setImageResource(id);
            /*
             * addAssetPath.invoke(assetManager,
             * getActivity().getApplicationInfo().sourceDir);
             * 需要将本地的apk路径也添加，否则无法获取本地的资源 如果不添加下面这句话将要报错
             * mResources是可以直接使用系统资源的，因为assetManager在构造的时候就添加了系统资源路径了
             */
            // mImage.setImageDrawable(mResources.getDrawable(id));
            // mImage.setImageDrawable(mResources.getDrawable(android.R.drawable.dark_header));
        } else if (v.getId() == R.id.dynamic_theme_btn) {
            if (mTail != null) {
                int id = mTail.getImageId();
                mImage.setImageDrawable(mResources.getDrawable(id));
            }
        }
    }

    public void loadRes() {
        AssetManager assetManager = null;
        try {
            assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", new Class[] { String.class });
            addAssetPath.invoke(assetManager, mPluginDir);
            Log.i("okunu", "dir = " + getActivity().getApplicationInfo().sourceDir);
            addAssetPath.invoke(assetManager, getActivity().getApplicationInfo().sourceDir);
        } catch (Exception e) {
            Log.i("okunu", "e", e);
            e.printStackTrace();
        }
        mResources = new Resources(assetManager, super.getResources().getDisplayMetrics(), super.getResources().getConfiguration());
    }

    public void getTail() {
        getPluginDir();
        try {
            DexClassLoader loader = new DexClassLoader(mPluginDir, getActivity().getApplicationInfo().dataDir, null, getClass().getClassLoader());
            String dex = getActivity().getDir("dex", 0).getAbsolutePath();
            String data = getActivity().getApplicationInfo().dataDir;
            Log.i("okunu", "dex = " + dex + "  data = " + data);
            Class<?> clazz = loader.loadClass("com.okunu.demoplugin.TailImpl");
            Constructor<?> constructor = clazz.getConstructor(new Class[] {});
            // 两种构造方式都记录下，可通过构造函数也可以直接newInstance，前提是这个类有无参构造函数
            // Object instance = constructor.newInstance(new Object[]{});
            // mTail = (ITail) instance;
            mTail = (ITail) clazz.newInstance();
            // int id = mTail.getImageId();
            // Log.i("okunu", "id = " + id);
        } catch (Exception e) {
            Log.i("okunu", "e", e);
            e.printStackTrace();
        }
    }

    public void getPluginDir() {
        PackageManager pm = getActivity().getPackageManager();
        Intent intent = new Intent("android.intent.plugin");
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        if (infos.size() > 0) {
            mPluginDir = infos.get(0).activityInfo.applicationInfo.sourceDir;
            Log.i("okunu", "mPluginDir = " + mPluginDir);
        }
    }

}
