/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   AsyncRequestor.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-10 
 */
package com.xiaomi.mitv.common.webservice;

import android.os.AsyncTask;

/**
 * @author tianli
 * 
 */
public abstract class AsyncRequest {

	private final Task mAsyncTask = new Task();

	public void sendRequest() {
		mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	final public void cancelRequest() {
		mAsyncTask.cancel(true);
	}

	protected void onPreRequest() {
	}

	protected abstract void onRequestInBackground();

	protected void onPostRequest() {
	}

	protected void onCancelReuqest() {
	}

	private class Task extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			onRequestInBackground();
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			onCancelReuqest();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			onPostRequest();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			onPreRequest();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}
	}

}
