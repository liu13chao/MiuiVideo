package com.miui.video.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import com.miui.video.DKApp;
import com.miui.video.api.DKApi;
import com.miui.video.model.MediaUrlForPlayerUtil;
import com.miui.video.model.MediaUrlForPlayerUtil.PlayUrlObserver;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.request.TvServiceRequest;
import com.miui.video.response.MediaDetailInfoResponse;
import com.miui.video.statistic.MediaFeeDef;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.PlayerMediaSetInfo;
import com.miui.video.util.DKLog;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class MediaInfoForPlayerProvider extends ContentProvider implements PlayUrlObserver, Observer {
	private final static String TAG = "MediaInfoForPlayerProvider";

	private OfflineMediaSqliteOpenHelper mOfflineSqlOpenHelper;
		
	private MediaUrlForPlayerUtil mMediaUrlForPlayerUtil;
	
	private static Object lock = new Object();
	
	private static final UriMatcher URI_MATCHER;
	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(DBUtil.AUTHORITY, DBUtil.MEDIAINFO_TABLE_NAME, DBUtil.CODE_MEDIA_INFO_URL);
		URI_MATCHER.addURI(DBUtil.AUTHORITY, DBUtil.MEDIAINFO_TABLE_NAME + "/*", DBUtil.CODE_MEDIA_INFO_URL_ALL);
	}

	@Override
	public boolean onCreate() {
		if (mOfflineSqlOpenHelper == null) {
			mOfflineSqlOpenHelper = new OfflineMediaSqliteOpenHelper(getContext());
		}

		if (mMediaUrlForPlayerUtil == null) {
			mMediaUrlForPlayerUtil = new MediaUrlForPlayerUtil(getContext());
			mMediaUrlForPlayerUtil.setObserver(this);
		}
		return true;
	}
	
	private static final String[] MEDIA_INFO_URL_ONE = { DBUtil.REMOTE_URL, DBUtil.MEDIA_HTML5_URL };
	private static final String[] MEDIA_INFO_URL_ALL = {DBUtil.MEDIA_TYPE, DBUtil.CURRENT_EPISODE, DBUtil.MEDIA_DATE,
		DBUtil.MEDIA_NAME, DBUtil.OFFLINE_STATUS, DBUtil.OFFLINE_SOURCE};
	
	private static int getIntParam(Uri uri, String key) {
		if (uri == null || key == null) {
			return -1;
		}
		try {
			return Integer.parseInt(uri.getQueryParameter(key));
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		synchronized (lock) {
			lock.notify();
		}
		// ensure MiuiVideo is alive
		if (DKApp.getAppContext() == null) {
			return null;
		}
		final int mediaId = getIntParam(uri, DBUtil.MEDIA_ID);
		final int mediaCi = getIntParam(uri, DBUtil.CURRENT_EPISODE);
		final int mediaSource = getIntParam(uri, DBUtil.MEDIA_SOURCE);
		final int mediaClarity = getIntParam(uri, DBUtil.MEDIA_CLARITY);
		switch(URI_MATCHER.match(uri)) {
			case DBUtil.CODE_MEDIA_INFO_URL:
				synchronized (lock) {
					if (mOfflineSqlOpenHelper.isFinished(mediaId, mediaCi)) {
						DKLog.d(TAG, "get from offline");
						OfflineMedia media = mOfflineSqlOpenHelper.getRecord(mediaId, mediaCi);
						if (media != null) {
							MediaInfoForPlayerUtil.getInstance().updateUrl(mediaId, mediaCi, media.localPath, media.remoteUrl);
						}
					} else {
						mMediaUrlForPlayerUtil.getMediaUrlForPlayer(mediaId, mediaCi, mediaSource, mediaClarity, "");
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					PlayerMediaSetInfo info = MediaInfoForPlayerUtil.getInstance().get(mediaId, mediaCi);
					MatrixCursor matrixCursor = new MatrixCursor(MEDIA_INFO_URL_ONE);
					matrixCursor.addRow(new Object[]{ info.playUrl, info.html5Url });
					return matrixCursor;
				}
			case DBUtil.CODE_MEDIA_INFO_URL_ALL:
				DKLog.d(TAG, "get ci infos");
				synchronized (lock) {
					if (MediaInfoForPlayerUtil.getInstance().getCount(mediaId) <= 0 && mediaId > 0) {
						TvServiceRequest detailInfoRequest = DKApi.getMediaDetailInfo(mediaId, true, MediaFeeDef.MEDIA_ALL, null, this);
						detailInfoRequest.setShowResultDesc(false);
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					MatrixCursor matrixCursor = new MatrixCursor(MEDIA_INFO_URL_ALL);
					for (int i = 0; i < MediaInfoForPlayerUtil.getInstance().getCount(mediaId); i++) {
						PlayerMediaSetInfo info = MediaInfoForPlayerUtil.getInstance().getAt(mediaId, i);
						if (info != null) {
							matrixCursor.addRow(new Object[]{MediaInfoForPlayerUtil.getInstance().getStyle(), 
									info.ci, info.date, info.videoname, info.offlineStatus,
									intArray2String(info.ci_available_download_source) });
						}
					}
					return matrixCursor;
				}
			default:
				DKLog.d(TAG, "Unknown URI" +uri);
				return null;
		}
	}
	
	private static String intArray2String(int[] array) {
		String ret = "";
		if (array != null) {
			for (int i=0; i<array.length; i++) {
				ret += array[i] + "#";
			}
		}
		return ret;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
//
	@Override
	public void onUrlUpdate(int mediaId, int ci, String playUrl, String html5Url) {
		MediaInfoForPlayerUtil.getInstance().updateUrl(mediaId, ci, playUrl, html5Url);
		DKLog.d(TAG, "playUrl: " +playUrl);
		DKLog.d(TAG, "html5Url: " +html5Url);
		synchronized (lock) {
			lock.notify();
		}
	}

	@Override
	public void onError() {
		synchronized (lock) {
			lock.notify();
		}
	}

	@Override
	public void onReleaseLock() {
		synchronized (lock) {
			lock.notify();
		}
	}

	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	}

	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if (response instanceof MediaDetailInfoResponse) {
			MediaDetailInfo2 detailInfo2 = null;
			if (response.isSuccessful()) {
				detailInfo2 = (MediaDetailInfo2)(((MediaDetailInfoResponse) response).data);
			}
			MediaInfoForPlayerUtil.getInstance().set(detailInfo2);
			synchronized (lock) {
				lock.notify();
			}
		}
	}
}
