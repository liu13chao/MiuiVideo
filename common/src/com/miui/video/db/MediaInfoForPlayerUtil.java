package com.miui.video.db;


import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaSetInfo;
import com.miui.video.type.PlayerMediaSetInfo;
import com.miui.video.util.DKLog;

import android.os.AsyncTask;
import android.util.SparseArray;
/**
 * @author dz
 *
 */
public class MediaInfoForPlayerUtil {
	private final static String TAG = MediaInfoForPlayerUtil.class.getSimpleName();
	
	private static MediaInfoForPlayerUtil sMediaInfoForPlayerUtil = new MediaInfoForPlayerUtil();
	
	private int mMediaId;
	private int mStyle;
	
	private SparseArray<PlayerMediaSetInfo> mMediaSetInfos;
		
	private AsyncSetMediaSetInfoDatabaseTask mSetInfoDatabaseTask = null;
	
	private MediaInfoForPlayerUtil() {
		mMediaSetInfos = new SparseArray<PlayerMediaSetInfo>();
	}
	
	public static MediaInfoForPlayerUtil getInstance() {
		return sMediaInfoForPlayerUtil;
	}
	
	public void asyncSet(MediaDetailInfo2 detailInfo2) {
		if (mSetInfoDatabaseTask != null) {
			mSetInfoDatabaseTask.cancel(true);
		}
		mSetInfoDatabaseTask = new AsyncSetMediaSetInfoDatabaseTask();
		mSetInfoDatabaseTask.execute(detailInfo2);
	}
	
	public void set(MediaDetailInfo2 detailInfo2) {
		if (detailInfo2 == null || detailInfo2.mediainfo == null
				|| detailInfo2.mediaciinfo == null
				|| detailInfo2.mediaciinfo.videos == null) {
			return;
		}
		mMediaId = detailInfo2.mediainfo.mediaid;
		mStyle = detailInfo2.mediaciinfo.style;
		DKLog.d(TAG, "set, mediaId: " + mMediaId + ", style: " + mStyle);

		for (MediaSetInfo info : detailInfo2.mediaciinfo.videos) {
			if (info != null) {
				PlayerMediaSetInfo playerMediaSetInfo = new PlayerMediaSetInfo();
				playerMediaSetInfo.ci = info.ci;
				playerMediaSetInfo.date = info.date;
				playerMediaSetInfo.offlineStatus = MediaConstantsDef.OFFLINE_NONE;
				playerMediaSetInfo.videoname = info.videoname;
				playerMediaSetInfo.ci_available_download_source = info.ci_available_download_source;
				playerMediaSetInfo.playUrl = "";
				mMediaSetInfos.put(playerMediaSetInfo.ci, playerMediaSetInfo);
			}
		}
		DKLog.d(TAG, "set, size: " + mMediaSetInfos.size());
	}
	
	public int getStyle() {
		return mStyle;
	}

	public int getCount(int mediaId) {
		if (mMediaId == mediaId && mMediaSetInfos != null)
			return mMediaSetInfos.size();
		return 0;
	}
	
	public PlayerMediaSetInfo get(int mediaId, int ci) {
		if (mMediaId == mediaId && mMediaSetInfos != null)
			return mMediaSetInfos.get(ci);
		return null;
	}
	
	public PlayerMediaSetInfo getAt(int mediaId, int index) {
		if (mMediaId == mediaId && mMediaSetInfos != null && index >= 0 && index < mMediaSetInfos.size())
			return mMediaSetInfos.valueAt(index);
		return null;
	}

	public void updateUrl(int mediaId, int ci, String playUrl, String html5Url) {
		PlayerMediaSetInfo playerMediaSetInfo = null;
		if (mMediaId != mediaId) {
			mMediaSetInfos.clear();
			mMediaId = mediaId;
		} else {
			playerMediaSetInfo = mMediaSetInfos.get(ci);
		}
		if (playerMediaSetInfo == null) {
			playerMediaSetInfo = new PlayerMediaSetInfo();
		}
		playerMediaSetInfo.ci = ci;
		playerMediaSetInfo.playUrl = playUrl;
		playerMediaSetInfo.html5Url = html5Url;
		mMediaSetInfos.put(ci, playerMediaSetInfo);
	}

	public void updateOfflineStatus(int mediaId, int ci, int offlineStatus) {
		PlayerMediaSetInfo playerMediaSetInfo = null;
		if (mMediaId != mediaId) {
			mMediaSetInfos.clear();
			mMediaId = mediaId;
		} else {
			playerMediaSetInfo = mMediaSetInfos.get(ci);
		}
		if (playerMediaSetInfo == null) {
			playerMediaSetInfo = new PlayerMediaSetInfo();
		}
		playerMediaSetInfo.ci = ci;
		playerMediaSetInfo.offlineStatus = offlineStatus;
		mMediaSetInfos.put(ci, playerMediaSetInfo);
	}
	
	private class AsyncSetMediaSetInfoDatabaseTask extends AsyncTask<MediaDetailInfo2, Void, Void> {
		@Override
		protected Void doInBackground(MediaDetailInfo2... params) {
			set(params[0]);
			return null;
		}
	}
}
