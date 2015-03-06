package com.miui.video;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;

import com.miui.video.base.BasePreferenceActivity;
import com.miui.video.model.AppEnv;
import com.miui.video.model.AppSettings;
import com.miui.video.model.DataStore;
import com.miui.video.model.DeviceInfo;
import com.miui.video.thumbnail.ThumbnailManager;
import com.miui.video.util.AlertMessage;
import com.miui.video.util.DKLog;

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
		
		if (DKApp.getSingleton(AppEnv.class).isExternalSdcardMounted() && !DeviceInfo.isH2()) {
			CheckBoxPreference checkmPriorityStorage = (CheckBoxPreference) findPreference("priority_storage");
			checkmPriorityStorage.setChecked(DKApp.getSingleton(AppEnv.class).isPriorityStorage());
			checkmPriorityStorage.setOnPreferenceClickListener(mOnPreferenceClickListener);
		} else {
			final PreferenceScreen screen = getPreferenceScreen();
			removeChildPreference(screen, "priority_storage");
		} 
		
		CheckBoxPreference playHint = (CheckBoxPreference) findPreference("use_cellular_play_hint");
		playHint.setChecked(DKApp.getSingleton(AppSettings.class).isOpenCellularPlayHint(this));
		playHint.setOnPreferenceClickListener(mOnPreferenceClickListener);
		
		CheckBoxPreference offDownloadHint = (CheckBoxPreference) findPreference("use_cellular_offlinedownload_hint");
		offDownloadHint.setChecked(DKApp.getSingleton(AppSettings.class).isOpenCellularOfflineHint(this));
		offDownloadHint.setOnPreferenceClickListener(mOnPreferenceClickListener);

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
			}else if(preKey.equals("priority_storage")){
				DKApp.getSingleton(AppEnv.class).setPriorityStorage(((CheckBoxPreference)preference).isChecked());
				return true;
			}else if(preKey.equals("use_cellular_play_hint")){
				DKApp.getSingleton(AppSettings.class).setOpenCellularPlayHint(SettingActivity.this, ((CheckBoxPreference)preference).isChecked());
				return true;
			}else if(preKey.equals("use_cellular_offlinedownload_hint")){
				DKApp.getSingleton(AppSettings.class).setOpenCellularOfflineHint(SettingActivity.this, ((CheckBoxPreference)preference).isChecked());
				return true;
			}else if(preKey.equals("mipush_setting")){
                DKApp.getSingleton(AppSettings.class).setMiPushOn( ((CheckBoxPreference)preference).isChecked());
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
    
	public  boolean removeChildPreference(PreferenceGroup preferenceGroup, String key) {
        Preference preference = preferenceGroup.findPreference(key);
        if (preference != null) {
            return removeChildPreference(preferenceGroup, preference);
        }
        return false;
    }

    public  boolean removeChildPreference(PreferenceGroup preferenceGroup, Preference preference) {
        if (preferenceGroup.removePreference(preference)) {
            return true;
        }
        final int childCount = preferenceGroup.getPreferenceCount();
        for (int i=0; i<childCount; i++) {
            final Preference childPreference = preferenceGroup.getPreference(i);
            if ((childPreference instanceof PreferenceGroup) &&
                    removeChildPreference((PreferenceGroup) childPreference, preference)){
                return true;
            }
        }
        return false;
    }
}
