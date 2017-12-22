package com.okunu.plugin;

import java.lang.reflect.Method;

import android.content.Context;
import android.view.View;

public class IPluginProxy implements ITail{

    public Object proxy;
    public Class clazz;
    public IPluginProxy(Class c, Object object){
        clazz = c;
        proxy= object;
    }
    
    @Override
    public int getImageId() {
        try {
            Method method = clazz.getDeclaredMethod("getImageId");
            method.setAccessible(true);
            return (int) method.invoke(proxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public View getView(Context context) {
        try {
            Method method = clazz.getDeclaredMethod("getView", Context.class);
            method.setAccessible(true);
            return (View) method.invoke(proxy, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
