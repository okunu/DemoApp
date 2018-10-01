package com.okunu.image;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.okunu.app.R;

import android.R.integer;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

public class ImageFragment extends Fragment implements OnClickListener{

    private View mContent;
    private ImageView mImageView;
    private ImageView mSecImageView;
    private Button mButton;
    private Button mButton2;
    private ExecutorService mPool = null;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_layout, container, false);
        mPool = Executors.newFixedThreadPool(3);
        mContent = view.findViewById(R.id.image_content);
        mImageView = (ImageView) view.findViewById(R.id.image_pic);
        mSecImageView = (ImageView) view.findViewById(R.id.image_pic2);
        mButton = (Button)view.findViewById(R.id.image_btn);
        mButton.setOnClickListener(this);
        mButton2 = (Button)view.findViewById(R.id.image_btn2);
        mButton2.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
    	Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.chongqi);
        if (v.getId() == R.id.image_btn) {
            submitMainColorTask(bitmap);
        }else if (v.getId() == R.id.image_btn2) {
        	submitGrayColorTask(bitmap);
		}
    }
    
    public void submitGrayColorTask(final Bitmap bitmap){
        Runnable runnable = new Runnable() {
            
            @Override
            public void run() {
                final Bitmap r = getGrayBitmap(bitmap);
                getActivity().runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        mSecImageView.setImageBitmap(r);
                    }
                });
            }
        };
        mPool.submit(runnable);
    }
    
    public void submitMainColorTask(final Bitmap bitmap){
        Runnable runnable = new Runnable() {
            
            @Override
            public void run() {
                final int r = getMainColor(bitmap);
                getActivity().runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        mContent.setBackgroundColor(r);
                    }
                });
            }
        };
        mPool.submit(runnable);
    }
    
    //提出图片主色值
    public int getMainColor(Bitmap bitmap){
        long start = System.currentTimeMillis();
        int r = 0;
        if (bitmap == null) {
            return r;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.i("okunu", "width = " + width + " height = " + height);
        int[] pixels = new int[width * height];
        int totalR = 0, totalG = 0, totalB = 0;
        int sampleColor = 0;
        int sampleCount = 0;
        int red = 0, green = 0, blue = 0;
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        Log.i("okunu", "pixels.length = " + pixels.length);
        for (int i = 0; i < height; i+=4) {
            int s = i * width;
            for (int j = 0; j < width; j+=4) {
                sampleColor = pixels[s + j];
                red = Color.red(sampleColor);
                green = Color.green(sampleColor);
                blue = Color.blue(sampleColor);
                totalR += red;
                totalG += green;
                totalB += blue;
                sampleCount++;
            }
        }
        Log.i("okunu", "sampleCount = " + sampleCount);
        totalR = (int) Math.floor(totalR/sampleCount);
        totalG = (int) Math.floor(totalG/sampleCount);
        totalB = (int) Math.floor(totalB/sampleCount);
        r = Color.rgb(totalR, totalG, totalB);
        Log.i("okunu", "time = " + (System.currentTimeMillis() - start) );
        return r;
    }

    public Bitmap getGrayBitmap(Bitmap bitmap){
        Bitmap r = null;
        long start = System.currentTimeMillis();
        if (bitmap == null) {
            return r;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.i("okunu", "width = " + width + " height = " + height);
        int[] pixels = new int[width * height];
        int[] out = new int[width * height];
        int sampleColor = 0;
        int red = 0, green = 0, blue = 0;
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        Log.i("okunu", "pixels.length = " + pixels.length);
        for (int i = 0; i < height; i+=1) {
            int s = i * width;
            for (int j = 0; j < width; j+=1) {
                sampleColor = pixels[s + j];
                red = Color.red(sampleColor);
                green = Color.green(sampleColor);
                blue = Color.blue(sampleColor);
                int modifColor = (int) (red*0.3 + green*0.59 + blue*0.11);
                out[s + j] = Color.rgb(modifColor, modifColor, modifColor);
            }
        }
        r = Bitmap.createBitmap(out, width, height, Bitmap.Config.RGB_565);
        Log.i("okunu", "time = " + (System.currentTimeMillis() - start) );
        return r;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPool != null) {
            mPool.shutdown();
            mPool = null;
        }
    }
}
