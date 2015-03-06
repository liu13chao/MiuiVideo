/**
 * 
 */
package com.miui.video.controller;

import android.os.AsyncTask;

/**
 * @author tianli
 *
 */
public abstract class CancelableRequestor extends AsyncTask<Void, Void, Void> {

	private OnRequestListener mListener;
	
	@Override
	final protected Void doInBackground(Void... arg0) {
	    try{
	        onDoRequest();
	    }catch(Throwable t){
	    }
		return null;
	}
	
	@Override
	final protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	final protected void onCancelled(Void result) {
		super.onCancelled(result);
	}

	@Override
	final protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		onPostRequest();
		if(mListener != null){
			mListener.onRequestDone();
		}
	}

	@Override
	final protected void onPreExecute() {
		super.onPreExecute();
		onPreRequest();
	}

	@Override
	final protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

    protected  void onPreRequest(){
    }

    protected  void onPostRequest(){
    }
    
    protected abstract void onDoRequest();
    
    final public void setRequestListener(OnRequestListener listener){
    	mListener = listener;
    }
    
	final public void cancel(){
		cancel(true);
		mListener = null;
	}
	
	final public void start(){
		executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public static interface OnRequestListener{
		public void onRequestDone();
	}
}
