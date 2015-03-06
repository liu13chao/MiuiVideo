package com.miui.video;

import com.miui.video.base.BasePreferenceActivity;
import com.miui.video.model.DataStore;
import com.miui.video.thumbnail.ThumbnailManager;
import com.miui.video.util.AlertMessage;
import com.miui.video.util.DKLog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

public class SettingActivity extends BasePreferenceActivity {
	
	private final String TAG = SettingActivity.class.getName();
	
	private DataStore mDataStore;
	private ThumbnailManager mThumbnailManager;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.miui_video_setting);
		Preference clearCahcePreference = findPreference("clear_cache");
		clearCahcePreference.setOnPreferenceClickListener(mOnPreferenceClickListener);
		
		mDataStore = DataStore.getInstance();
		mThumbnailManager = DKApp.getSingleton(ThumbnailManager.class);
	}
	
	//UI callback
	private OnPreferenceClickListener mOnPreferenceClickListener = new OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			String preKey = preference.getKey();
			if(preKey.equals("clear_cache")) {
				clearCache();
				return true;
			}
			return false;
		}
	};
	
	//packaged method
    private void clearCache(){
    	new AsyncClearCacheTask().execute();
    }
    
    private class AsyncClearCacheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				mDataStore.clearCache();
		    	mThumbnailManager.clearCache();
			} catch (Exception e) {
				DKLog.e(TAG, e.getLocalizedMessage());
			}
			return null;
		}
    	
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
	    	AlertMessage.show(SettingActivity.this, R.string.clear_cache_success);
		}
    }
}
