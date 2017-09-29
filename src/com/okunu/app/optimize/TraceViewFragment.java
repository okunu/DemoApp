package com.okunu.app.optimize;

import java.util.ArrayList;
import java.util.List;

import com.okunu.app.R;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TraceViewFragment extends Fragment{
    
    MyListView mListView;
    List<Integer> resData;
    List<String> strData;
    MyAdapter mAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trace_view_layout, container, false);
        mListView = (MyListView) view.findViewById(R.id.trace_view_list);
        mAdapter = new MyAdapter();
        initRes();
        mAdapter.setData(resData, strData);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(new OnScrollListener() {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                Log.i("okunu", "onScrollStateChanged  scrollState  = "+ scrollState);
//                resData.add(R.drawable.app_icon);
//                strData.add(0, "改变第1个");
//                mAdapter.setData(resData, strData);
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                Log.i("okunu", "onScroll  firstVisibleItem  = "+ firstVisibleItem + "  visibleItemCount = " + visibleItemCount);
            }
        });
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        getActivity().registerReceiver(mReceiver, filter);
        return view;
    }
    
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            
        }
    };
    
    public void initRes(){
        resData = new ArrayList<>();
        strData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            resData.add(R.drawable.ic_launcher);
            strData.add("" + i);
        }
//        resData.add(R.drawable.ic_launcher);
//        resData.add(R.drawable.ic_launcher);
//        resData.add(R.drawable.ic_launcher);
//        resData.add(R.drawable.ic_launcher);
//        resData.add(R.drawable.ic_launcher);
//        resData.add(R.drawable.ic_launcher);
//        
//        strData.add("第2个");
//        strData.add("第3个");
//        strData.add("第4个");
//        strData.add("第5个");
//        strData.add("第6个");
//        strData.add("第7个");
    }
    
    class MyAdapter extends BaseAdapter{
        
        public List<Integer> data = new ArrayList<>();
        public List<String> strings = new ArrayList<>();
        
        public void setData(List<Integer> data, List<String> strs){
            this.data = data;
            strings = strs;
            notifyDataSetChanged();
        }
        
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trace_view_layout_item, null);
                viewHolder = new ViewHolder();
                viewHolder.img = (ImageView) convertView.findViewById(R.id.trace_item_img);
                viewHolder.tv = (TextView) convertView.findViewById(R.id.trace_item_txt);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (data.size() > position) {
                viewHolder.img.setImageResource(data.get(position));
                viewHolder.tv.setText(strings.get(position));
            }
//            try {
//                Thread.sleep(50);
//            } catch (Exception e) {
//            }
            return convertView;
        }
        
    }
    
    class ViewHolder{
        public ImageView img;
        public TextView tv;
    }
}
