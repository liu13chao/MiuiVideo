package com.miui.video.type;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.util.Util;

/**
 *@author tangfuling
 *
 */

public class LocalMediaList extends BaseMediaInfo implements Serializable {
	
	private static final long serialVersionUID = 2L;
	
	private ArrayList<LocalMedia> localMediaList = new ArrayList<LocalMedia>();
	private boolean mIsCamera = false;
	
	public void setIsCamera() {
		mIsCamera = true;
	}
	public int size() {
		return localMediaList.size();
	}
	
	public void add(LocalMedia localMedia) {
		if(localMedia != null && !localMediaList.contains(localMedia)) {
			localMediaList.add(localMedia);
		}
	}
	
	public void removeAll(List<LocalMedia> localMedias) {
		if(localMedias != null) {
			localMediaList.removeAll(localMedias);
		}
	}
	
	public LocalMedia get(int index) {
		return localMediaList.get(index);
	}
	
	public List<LocalMedia> getLocalMediaList() {
		return localMediaList;
	}
	
	public boolean isDirType() {
		if(localMediaList.size() > 1 || mIsCamera) {
			return true;
		}
		return false;
	}
	
	public String getPath() {
		if(localMediaList.size() > 0) {
			LocalMedia localMedia = localMediaList.get(0);
			if(localMedia != null) {
				if(!isDirType()) {
					return localMedia.mediaPath;
				} else {
					String path = localMedia.mediaPath;
					if(!Util.isEmpty(path)) {
						int index = path.lastIndexOf(File.separatorChar);
						if(index != -1) {
							path = path.substring(0, index);
							return path;
						}
					}
				}
			}
		}
		return "";
	}
	
	@Override
	public String getName() {
		if(localMediaList.size() > 0) {
			LocalMedia localMedia = localMediaList.get(0);
			if(localMedia != null) {
				if (mIsCamera) {
					return DKApp.getAppContext().getResources().getString(R.string.mycamera);
				} else if(localMediaList.size() == 1) {
					return localMedia.displayName;
				} else {
					return localMedia.bucketName;
				}
			}
		}
		return "";
	}
	
	@Override
	public String getDesc() {
		return getDescSouth();
	}
	
	@Override
	public String getDescSouth() {
		if(localMediaList.size() > 0) {
			LocalMedia localMedia = localMediaList.get(0);
			if(localMedia != null) {
				return localMedia.getDescSouth();
			}
		}
		return "";
	}
	
	@Override
	public String getDescSouthEast() {
		if(localMediaList.size() > 1) {
			String str = DKApp.getAppContext().getString(R.string.count_ge_media);
			str = String.format(str, localMediaList.size());
			return str;
		}
		return "";
	}
	
	@Override
	public ImageUrlInfo getPosterInfo() {
		return null;
	}
    @Override
    public String getMediaStatus() {
        return "";
    }
    
    @Override
    public String getSubtitle() {
        return "";
    }
}
