/**
 * 
 */
package com.miui.video.addon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import com.miui.video.DKApp;
import com.miui.video.helper.JobRunner;
import com.miui.video.model.DataStore;
import com.miui.video.type.AddonInfo;
import com.miui.video.util.Util;

/**
 * @author dz
 *
 */
public class AddonManager {
	
	private static AddonManager sAddonManager = new AddonManager();

	private SparseArray<AddonInfo> mAddonInfos;

	private List<OnAddonChangedListener> mListeners = new ArrayList<OnAddonChangedListener>();
	
	private int totalCount = 0;

	Handler mHandler = new Handler(Looper.getMainLooper());
	
	private AddonManager() {
		mAddonInfos = new SparseArray<AddonInfo>();
	}
	
	public static AddonManager getInstance() {
		return sAddonManager;
	}
	
	public void addListener(OnAddonChangedListener listener) {
		if(listener != null && !mListeners.contains(listener)) {
			mListeners.add(listener);
		}
	}
	
	public void removeListener(OnAddonChangedListener listener) {
		if(listener != null) {
			mListeners.remove(listener);
		}
	}
	
	public void setTotalCount(int count) {
		totalCount = count;
	}
	
	public int getTotalCount() {
		return totalCount;
	}
	
	public int getAddonCount() {
		return mAddonInfos.size();
	}
	
	public ArrayList<AddonInfo> getAddonList() {
		ArrayList<AddonInfo> addonList = new ArrayList<AddonInfo>();
		for (int i=0; i<mAddonInfos.size(); i++) {
			addonList.add(mAddonInfos.valueAt(i));
		}
		return addonList;
	}	
	
	public AddonInfo getById(int id) {
		if (mAddonInfos != null) {
			return mAddonInfos.get(id);
		}
		return null;
	}
	
	public void add(AddonInfo info) {
		mAddonInfos.put(info.id, info);
		save();
	}
	
	public void delete(int Id) {
		mAddonInfos.remove(Id);
		save();
		try {
			JobRunner.postJob(new DeleteAddonRunnable(mAddonInfos.get(Id).getLocalPath()));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void load(){
		JobRunner.postJob(new Runnable() {
			@Override
			public void run() {
				final AddonInfo[] infos = DataStore.getInstance().loadAddonList();
				mHandler.post(new Runnable() {					
					@Override
					public void run() {
						if (infos != null) {
							for (int i=0; i<infos.length; i++) {
								mAddonInfos.put(infos[i].id, infos[i]);
							}
							refresh();
						} else {
							notifyAddonChanged();
						}
					}
				});
			}
		});
	}
	
	public void save(){
		int n = mAddonInfos.size();
		final AddonInfo[] addonInfos = new AddonInfo[n];
		for (int i=0; i<n; i++) {
			addonInfos[i] = mAddonInfos.valueAt(i);
		}
		
		JobRunner.postJob(new Runnable() {
			@Override
			public void run() {
				DataStore.getInstance().saveAddonList(addonInfos);
			}
		});
		notifyAddonChanged();
	}
	
	public void deleteAddons(int[] addonIds) {
		if (addonIds != null) {
			try {
				for (int i=0; i<addonIds.length; i++) {
					JobRunner.postJob(new DeleteAddonRunnable(mAddonInfos.get(addonIds[i]).getLocalPath()));
					mAddonInfos.remove(addonIds[i]);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			save();
		}
	}
	
	private class DeleteAddonRunnable implements Runnable {
		
		private String addonPath;
		
		public DeleteAddonRunnable(String addonPath) {
			this.addonPath = addonPath;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				File file = new File(addonPath);
				if (file.isFile()) {
					file.delete();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	public void refresh() {
		PackageManager pm = DKApp.getAppContext().getPackageManager();
		AddonInfo addonInfo;
		for (int i=0; i<mAddonInfos.size(); i++) {
			addonInfo = mAddonInfos.valueAt(i);
			if (!Util.isEmpty(addonInfo.getPackageName())) {
				try {
					pm.getPackageInfo(addonInfo.getPackageName(), PackageManager.GET_PERMISSIONS);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					//mAddonInfos.removeAt(i);
					delete(mAddonInfos.keyAt(i));
					i--;
				}
			}
		}
		notifyAddonChanged();
	}
	
	private void notifyAddonChanged() {
		for(int i = 0; i < mListeners.size(); i++) {
			OnAddonChangedListener listener = mListeners.get(i);
			if(listener != null) {
				listener.onAddonChanged();
			}
		}
	}
	
	public  interface OnAddonChangedListener{
		public void onAddonChanged();
	}
	
	public static class AddonList {
		AddonInfo[] list;
	}
}
