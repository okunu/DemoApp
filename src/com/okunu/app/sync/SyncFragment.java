package com.okunu.app.sync;

import com.okunu.app.R;

import android.app.Fragment;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class SyncFragment extends Fragment implements OnClickListener{

    Button block;
    Button open;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sync_layout, container, false);
        block = (Button) view.findViewById(R.id.sync_btn_block);
        open = (Button) view.findViewById(R.id.sync_btn_open);
        block.setOnClickListener(this);
        open.setOnClickListener(this);
        return view;
    }

    int tag = 0;
    @Override
    public void onResume() {
        super.onResume();
        block.postDelayed(count, 1000);
    }

    public Runnable count = new Runnable() {
        
        @Override
        public void run() {
            Log.i("okunu", "tag = " + tag);
            tag++;
            block.postDelayed(count, 1000);
        }
    };

    ConditionVariable cv = new ConditionVariable();
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sync_btn_block) {
            Log.i("okunu", "block the thread*********");
            cv.block(2000);
        }else if (v.getId() == R.id.sync_btn_open) {
            Log.i("okunu", "open the thread--------------");
            cv.open();
        }
    }
}
