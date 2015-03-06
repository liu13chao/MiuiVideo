package com.miui.video.db;

import android.os.AsyncTask;

public abstract class DBTask<T> extends AsyncTask<Void, Void, T> {

	private final DBOperationCallback<T> mDBOperationCallback;

	public DBTask(DBOperationCallback<T> callback) {
		mDBOperationCallback = callback;
	}

	@Override
	protected T doInBackground(Void... params) {
		return doDBOperation();
	}

	@Override
	protected void onPostExecute(T result) {
		super.onPostExecute(result);
		if (mDBOperationCallback != null) {
			mDBOperationCallback.onResult(result);
		}
	}

	protected abstract T doDBOperation();

}
