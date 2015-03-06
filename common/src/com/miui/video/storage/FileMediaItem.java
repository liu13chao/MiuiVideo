package com.miui.video.storage;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class FileMediaItem extends MediaItem {
	
    private static final long serialVersionUID = 1L;

    public static final String TAG = "FileMediaItem";
//	private final File mFile;
	protected String mMediaMetadata = null;
	protected String mFileMetadata = null;
	
	//for apk file
	private String mAppName;
	private Drawable mAppIcon;
	private String mPackageName;
	private String[] mRequestedPermissions;

	public FileMediaItem(Context context, String path, String name) {
		super(context);
		mPath = path;
		mName = name;
		mIsDirectory = false;
		
		mMediaType = getTypeByFilenameExt(mName);
	}
	
//	private void parseAppInfo() {
//		Context context = App.getInstance().getApplicationContext();
//		PackageManager pm = context.getPackageManager();
//		PackageInfo pi = pm.getPackageArchiveInfo(getMediaUrl(),
//				PackageManager.GET_ACTIVITIES | PackageManager.GET_PERMISSIONS);
//		
//		if(pi == null)
//			return;
//		
//		pi.applicationInfo.sourceDir = getMediaUrl();
//		pi.applicationInfo.publicSourceDir = getMediaUrl();
//		mAppIcon = pi.applicationInfo.loadIcon(pm);
//		mAppName = (String) pi.applicationInfo.loadLabel(pm);
//		mPackageName = pi.applicationInfo.packageName;
//		mRequestedPermissions = pi.requestedPermissions;
//	}
	
	public String getAppName() {
		return mAppName;
	}

	public Drawable getAppIcon() {
		return mAppIcon;
	}

	public String[] getRequestedPermissions() {
		return mRequestedPermissions;
	}

	public String getPackageName() {
		return mPackageName;
	}
}
