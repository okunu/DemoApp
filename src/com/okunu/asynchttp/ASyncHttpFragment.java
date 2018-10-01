package com.okunu.asynchttp;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
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

public class ASyncHttpFragment extends Fragment implements OnClickListener{

    Button open;
    
    private AsyncHttpClient mClient = new AsyncHttpClient();
    private String URL = "http://httpbin.org/get";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.async_http_layout, container, false);
        open = (Button) view.findViewById(R.id.async_http_btn);
        open.setOnClickListener(this);
        return view;
    }

    private AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers, byte[] response) {
			if (response != null) {
				Log.i("okunu", "onSuccess  response = " + new String(response));
			}else {
				Log.i("okunu", "onSuccess");
			}
		}
		
		@Override
		public void onFailure(int statusCode, Header[] headers, byte[] response, Throwable e) {
			if (response != null) {
				Log.i("okunu", "onFailure  response = " + new String(response));
			}else {
				Log.i("okunu", "onFailure");
			}
		}
	};

	@Override
	public void onClick(View v) {
		mClient.get(getActivity(), URL, handler);
	}
}
