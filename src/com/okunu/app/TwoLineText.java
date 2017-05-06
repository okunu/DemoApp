package com.okunu.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View.MeasureSpec;
import android.widget.TextView;

public class TwoLineText extends TextView{

    public int mFirstColor;
    public int mSecondColor;
    public int mFirstSize;
    public int mSecondSize;
    public Typeface mFirsTypeface;
    public Typeface mSecondTypeface;
    public String mFirstText;
    public String mSecondText;
    
    public int mFirstTextWidth;
    public int mFirstTextHeight;
    public int mSecondTextWidth;
    public int mSecondTextHeight;
    public int mWidth;
    public int mHeight;
    public int mGap = 7;
    
    public TwoLineText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.two_line_textview);
        mFirstColor = array.getColor(R.styleable.two_line_textview_first_line_textColor, Color.YELLOW);
        mSecondColor = array.getColor(R.styleable.two_line_textview_second_line_textColor, Color.YELLOW);
        mFirstSize = (int) array.getDimension(R.styleable.two_line_textview_first_line_textSize, 20);
        mSecondSize = (int) array.getDimension(R.styleable.two_line_textview_second_line_textSize, 20);
        array.recycle();
        setShowText(getText().toString());
        mFirsTypeface = Typeface.DEFAULT;
        mSecondTypeface = Typeface.DEFAULT;
    }

    public void setShowText(String text){
        if (text == null) {
            mFirstText = "";
            mSecondText = "";
            return;
        }
        String[] strs = text.split("\\n");
        if (strs.length == 0) {
            mFirstText = "";
            mSecondText = "";
        }else if (strs.length == 1) {
            mFirstText = strs[0];
            mSecondText = "";
        }else if (strs.length >= 2) {
            mFirstText = strs[0];
            mSecondText = strs[1];
        }
    }
    
    
    public TwoLineText(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int max = getMaxWidth();
        TextPaint paint = new TextPaint();
        paint.setTextSize(mFirstSize);
        paint.setColor(mFirstColor);
        paint.setTypeface(mFirsTypeface);
        mFirstTextWidth = (int) paint.measureText(mFirstText);
        mFirstTextHeight = (int) (paint.descent() - paint.ascent());
        
        paint.setTextSize(mSecondSize);
        paint.setColor(mSecondColor);
        paint.setTypeface(mSecondTypeface);
        mSecondTextWidth = (int) paint.measureText(mSecondText);
        mSecondTextHeight = (int) (paint.descent() - paint.ascent());
//        Log.i("okunu", " * measure w1 = " + mFirstTextWidth + "  h1 = " + mFirstTextHeight + "  w2 = " + mSecondTextWidth 
//                + "  h2 = " + mSecondTextHeight + " w = " + mWidth + " h = " + mHeight + "  firsttext = " + mFirstText
//                + "  msecond = " + mSecondText + "  widthMode = " + widthMode + " heightMode = " + heightMode);
        mWidth = (widthMode == MeasureSpec.EXACTLY) ? widthSize : ((mFirstTextWidth > mSecondTextWidth) ? mFirstTextWidth : mSecondTextWidth);
        mHeight = (heightMode == MeasureSpec.EXACTLY) ? heightSize : (mFirstTextHeight + mSecondTextHeight + mGap);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    public void draw(Canvas canvas){
      //Log.i("okunu"," firsttext = " + mFirstText + "  msecond = " + mSecondText);
      int totalTextHeight = mFirstTextHeight + mGap + mSecondTextHeight;
      
      TextPaint paint = getPaint();
      paint.setTextSize(mFirstSize);
      paint.setColor(mFirstColor);
      paint.setTypeface(mFirsTypeface);
      float x1 = (mWidth - mFirstTextWidth)/2;
      float y1 = (mHeight - totalTextHeight) - paint.ascent();
//      float y1 = (mHeight - totalTextHeight);
      canvas.drawText(mFirstText, 0, mFirstText.length(), x1, y1, paint);
      
      paint.setTextSize(mSecondSize);
      paint.setColor(mSecondColor);
      paint.setTypeface(mSecondTypeface);
      float x2 = (mWidth - mSecondTextWidth)/2;
      float y2 = (mHeight - totalTextHeight) + mFirstTextHeight + mGap - paint.ascent();
      canvas.drawText(mSecondText, 0, mSecondText.length(), x2, y2, paint);
    }
    
    public Typeface getFirsTypeface() {
        return mFirsTypeface;
    }

    public void setFirsTypeface(Typeface mFirsTypeface) {
        this.mFirsTypeface = mFirsTypeface;
    }

    public Typeface getSecondTypeface() {
        return mSecondTypeface;
    }

    public void setSecondTypeface(Typeface mSecondTypeface) {
        this.mSecondTypeface = mSecondTypeface;
    }

    public String getFirstText() {
        return mFirstText;
    }

    public void setFirstText(String text) {
        if (text == null) {
            this.mFirstText = "";
        }else {
            if (getMaxWidth() == Integer.MAX_VALUE) {
                mFirstText = text;
            }else {
                TextPaint paint = new TextPaint();
                paint.setTextSize(mFirstSize);
                paint.setColor(mFirstColor);
                paint.setTypeface(mFirsTypeface);
                this.mFirstText = TextUtils.ellipsize(text, paint, getMaxWidth(), TruncateAt.END).toString();
            }
        }
        requestLayout();
    }
    
    public void setFirstText(int id) {
        setFirstText(getResources().getString(id));
    }
    
    public void setFirstText(CharSequence charSequence) {
        setFirstText(charSequence.toString());
    }

    public String getSecondText() {
        return mSecondText;
    }

    public void setSecondText(String text) {
        if (text == null) {
            this.mSecondText = "";
        }else {
            if (getMaxWidth() == Integer.MAX_VALUE) {
                mSecondText = text;
            }else {
                TextPaint paint = new TextPaint();
                paint.setTextSize(mSecondSize);
                paint.setColor(mSecondColor);
                paint.setTypeface(mSecondTypeface);
                this.mSecondText = TextUtils.ellipsize(text, paint, getMaxWidth(), TruncateAt.END).toString();
            }
        }
        requestLayout();
    }
    
    public void setSecondText(int id) {
        setSecondText(getResources().getString(id));
    }
    
    public void setSecondText(CharSequence charSequence) {
        setSecondText(charSequence.toString());
    }
}
