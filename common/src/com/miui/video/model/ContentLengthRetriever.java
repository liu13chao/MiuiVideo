/**
 *  Copyright(C) 2014 XiaoMi TV Group
 * 
 *  ContentLengthRetriever.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2014-1-7
 */
package com.miui.video.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import com.miui.video.util.DKLog;

/**
 * @author tianli
 *
 */
public class ContentLengthRetriever {
	
	private static final String TAG = "ContentLengthRetriever";
	
	private String mUrl;
	private long mTimeout;
	
	public ContentLengthRetriever(String url){
		mUrl = url;
	}
	
	public int get(long timeout){
		mTimeout = timeout;
		FutureTask<Integer> future = new FutureTask<Integer>(mRunner);
		new Thread(future).start();
		int size = 0;
		try {
			size = future.get(mTimeout, TimeUnit.MILLISECONDS);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}
	
	private Callable<Integer> mRunner = new Callable<Integer>() {
		@Override
		public Integer call() throws Exception {
			int retry = 3;
			for(int i = 0; i < retry; i++){
				try {
					DKLog.d(TAG, "retry count " + i);
					int size = doGet();
					return size;
				} catch (Exception e) {
				}
			}
			return 0;
		}
		
		private int doGet() throws IOException{
			HttpURLConnection connection = null;
			int size = 0;
			try{
				URL url = new URL(mUrl);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout((int)mTimeout);
				size = connection.getContentLength();
				DKLog.d(TAG, "url: " + mUrl + ", size is " + size);
			}finally{
				if(connection != null){
					connection.disconnect();
				}
			}
			return size;
		}
	};
}
