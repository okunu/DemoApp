package com.okunu.app.optimize;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

public class MyListView extends ListView{

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public MyListView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i("okunu", "mylistview  onMeasure");
    }

}
