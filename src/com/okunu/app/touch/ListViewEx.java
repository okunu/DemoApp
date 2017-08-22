package com.okunu.app.touch;

import com.okunu.app.util.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class ListViewEx extends ListView{

    public HorizontalEx2 mHorizontalEx2;
    
    public ListViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public ListViewEx(Context context) {
        super(context);
    }
    
    public void setGroup(HorizontalEx2 horizontalEx2){
        mHorizontalEx2 = horizontalEx2;
    }
    
    private int mLastX;
    private int mLastY;
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Util.log("ListViewEx dispatchTouchEvent action = " + ev.getAction());
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mHorizontalEx2.requestDisallowInterceptTouchEvent(true);
            break;
        case MotionEvent.ACTION_MOVE:
            int deltax = x - mLastX;
            int deltay = y - mLastY;
            //�������ƶ�������������ƶ�����ʱ������view������ø�view������touch�¼�
            //�ۺ���˵�����ǵ���view������touch�¼�������ʱ�����Ӹ���view����
            if (Math.abs(deltax) > Math.abs(deltay)) {
                mHorizontalEx2.requestDisallowInterceptTouchEvent(false);
            }
            break;
        default:
            break;
        }
        mLastX = x;
        mLastY = y;
        return super.dispatchTouchEvent(ev);
    }

}