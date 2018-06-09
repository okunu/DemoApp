package com.okunu.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.okunu.app.R;

import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Editor;
import libcore.io.DiskLruCache.Snapshot;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PhotoWallAdapter extends ArrayAdapter<String> {

	public static final int MSG_SHOW_BITMAP = 1000;
	
	private GridView mPhotoWall;
	private LruCache<String, Bitmap> mMemoryCache;
	private DiskLruCache mDiskLruCache;
	private int mItemHeight;
	private ExecutorService mPools;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_SHOW_BITMAP) {
				Object obj = msg.obj;
				if (obj != null) {
					Result result = (Result) obj;
					if (result.iv.getTag().equals(result.url)) {
						result.iv.setImageBitmap(result.bitmap);
					}
				}
			}
		}
	};

	public PhotoWallAdapter(Context context, int textViewResourceId,
			String[] objects, GridView gridView) {
		super(context, textViewResourceId, objects);
		mPhotoWall = gridView;
		mPools = Executors.newFixedThreadPool(8);
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			}
		};
		try {
			File cacheDir = getDiskCacheDir(context, "thumb");
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1, 50 * 1024 * 1024);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final String url = getItem(position);
		View view;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(
					R.layout.cache_gridview_item_layout, null);
		} else {
			view = convertView;
		}
		final ImageView imageView = (ImageView) view.findViewById(R.id.photo);
		if (imageView.getLayoutParams().height != mItemHeight) {
			imageView.getLayoutParams().height = mItemHeight;
		}
		imageView.setTag(url);
		imageView.setImageResource(R.drawable.ic_launcher);
		loadBitmaps(imageView, url);
		return view;
	}

	private void loadBitmaps(ImageView imageView, String url) {
		try {
			Bitmap bitmap = getBitmapFromMemoryCache(url);
			if (bitmap == null) {
				Task task = new Task(imageView, url);
				mPools.submit(task);
			} else {
				if (imageView != null && bitmap != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void cancelAllTasks() {
		mPools.shutdown();
	}

	public void setItemHeight(int height) {
		if (height == mItemHeight) {
			return;
		}
		mItemHeight = height;
		notifyDataSetChanged();
	}

	public void fluchCache() {
		if (mDiskLruCache != null) {
			try {
				mDiskLruCache.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	class Task implements Runnable {
		ImageView iv;
		String imageUrl;

		Task(ImageView view, String url) {
			iv = view;
			imageUrl = url;
		}

		@Override
		public void run() {
			FileDescriptor fileDescriptor = null;
			FileInputStream fileInputStream = null;
			Snapshot snapshot = null;
			try {
				final String key = hashKeyForDisk(imageUrl);
				snapshot = mDiskLruCache.get(key);
				if (snapshot == null) {
					Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						if (downloadUrlToStream(imageUrl, outputStream)) {
							editor.commit();
						} else {
							editor.abort();
						}
					}
					snapshot = mDiskLruCache.get(key);
				}
				if (snapshot != null) {
					fileInputStream = (FileInputStream) snapshot
							.getInputStream(0);
					fileDescriptor = fileInputStream.getFD();
				}
				Bitmap bitmap = null;
				if (fileDescriptor != null) {
					bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
				}
				if (bitmap != null) {
					addBitmapToMemoryCache(key, bitmap);
					Result result = new Result(iv, bitmap, imageUrl);
					Message msg = Message.obtain(mHandler, MSG_SHOW_BITMAP, result);
					msg.sendToTarget();
				}
			} catch (Exception e) {
				Log.e("okunu", "run", e);
			}
		}
	}
	
	class Result{
		ImageView iv;
		Bitmap bitmap;
		String url;
		
		Result(ImageView view, Bitmap b, String url){
			this.iv = view;
			this.bitmap = b;
			this.url = url;
		}
	}

	public Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	private boolean downloadUrlToStream(String urlString,
			OutputStream outputStream) {
		HttpURLConnection connection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(connection.getInputStream(), 8 * 1024);
			out = new BufferedOutputStream(outputStream, 8 * 1024);
			int b = -1;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			out.flush();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			try {
				if (in != null) {
					in.close();
					;
				}
				if (out != null) {
					out.close();
				}
			} catch (Exception e2) {
			}
		}
		return false;
	}

	public String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
			Log.i("okunu", "cacheKey = " + cacheKey);
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
}
