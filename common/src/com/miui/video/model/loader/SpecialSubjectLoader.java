package com.miui.video.model.loader;

import java.util.ArrayList;

import android.os.AsyncTask;
import com.miui.video.api.DKApi;
import com.miui.video.model.DataStore;
import com.miui.video.response.SpecialSubjectListResponse;
import com.miui.video.type.SpecialSubject;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceResponse;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;

public class SpecialSubjectLoader extends DataLoader implements Observer {

	private DataStore mDataStore;
	private SpecialSubject[] mSpecialSubjects;
	
	private int PAGE_NO = 1;
	private int PAGE_SIZE = 10;
	
	public SpecialSubjectLoader() {
		mDataStore = DataStore.getInstance();
	}
	
	@Override
	public void load() {
		(new LocalTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public ArrayList<Object> getSpecialSubjectList() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		if(mSpecialSubjects != null) {
			for(int i = 0; i < mSpecialSubjects.length; i++) {
				if(mSpecialSubjects[i] != null) {
					arrayList.add(mSpecialSubjects[i]);
				}
			}
		}
		return arrayList;
	}
	
	private void getSpecialSubject() {
		DKApi.getSpecialSubjectList(PAGE_NO, PAGE_SIZE, "", this);
	}
	
	private class LocalTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			mSpecialSubjects = mDataStore.loadSpecialSubject();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(mSpecialSubjects != null) {
				if(mDataStore.isSpecialSubjectExpired()) {
					getSpecialSubject();
				} else {
					notifyDataReady();
				}
			} else {
				getSpecialSubject();
			}
		}
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	}

	@Override
	public void onRequestCompleted(ServiceRequest request, ServiceResponse response) {
		if(response.isSuccessful()) {
			SpecialSubjectListResponse specialSubjectListResponse = (SpecialSubjectListResponse) response;
			this.mSpecialSubjects = specialSubjectListResponse.data;
			new Thread(new Runnable() {
				@Override
				public void run() {
					mDataStore.saveSpecialSubject(mSpecialSubjects);
				}
			}).start();
			notifyDataReady();
		} else {
			notifyDataFail();
		}
	}
}
