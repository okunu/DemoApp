package com.okunu.app.touch;

import java.util.ArrayList;

import com.okunu.app.R;

//import android.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 使用内部拦截法处理滑动冲突
 * @author oukun.ok
 *
 */
public class Touch2Fragment extends Fragment{

    ArrayList<String> mData1 = new ArrayList<String>();
    ArrayList<String> mData2 = new ArrayList<String>();
    ArrayList<String> mData3 = new ArrayList<String>();
    public void prepareData(){
        for (int i = 0; i < 20; i++) {
            mData1.add("第一页  " + i);
            mData2.add("第二页  " + i);
            mData3.add("第三页  " + i);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prepareData();
        
        HorizontalEx2 root = new HorizontalEx2(getActivity());
        
        ListViewEx listView1 = new ListViewEx(getActivity());
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mData1);
        listView1.setAdapter(adapter1);
        listView1.setGroup(root);
        
        ListViewEx listView2 = new ListViewEx(getActivity());
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mData2);
        listView2.setAdapter(adapter2);
        listView2.setGroup(root);
        
        ListViewEx listView3 = new ListViewEx(getActivity());
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mData3);
        listView3.setAdapter(adapter3);
        listView3.setGroup(root);
        
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.MATCH_PARENT);
        root.addView(listView1, params);
        root.addView(listView2, params);
        root.addView(listView3, params);
        
        root.setLayoutParams(params);
        return root;
    }

}
