package com.miui.video.offline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.SparseArray;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.ImageUrlInfo;

/**
 * @author tangfuling
 * 
 */

public class OfflineMediaList extends BaseMediaInfo implements Serializable {
	private static final long serialVersionUID = 2L;

	private List<OfflineMedia> mMedias = new ArrayList<OfflineMedia>();

	private final int mMediaId;

	public OfflineMediaList(int mediaId) {
		mMediaId = mediaId;
	}

	public static List<OfflineMediaList> group(List<OfflineMedia> medias) {
		SparseArray<List<OfflineMedia>> map = new SparseArray<List<OfflineMedia>>();
		// put OfflineMedia with the same mediaId together
		for (OfflineMedia media : medias) {
			if (media != null) {
				List<OfflineMedia> group = map.get(media.mediaId,
						new ArrayList<OfflineMedia>());
				group.add(media);
				map.put(media.mediaId, group);
			}
		}
		List<OfflineMediaList> list = new ArrayList<OfflineMediaList>();
		// sort by episode and create OfflineMediaList
		for (int i = 0; i < map.size(); i++) {
			List<OfflineMedia> value = map.valueAt(i);
			if (value != null) {
				Collections.sort(value);
				OfflineMediaList mediaList = new OfflineMediaList(map.keyAt(i));
				mediaList.setAll(value);
				list.add(mediaList);
			}
		}
		return list;
	}

	public static List<OfflineMedia> ungroup(List<OfflineMediaList> mediaLists) {
		List<OfflineMedia> medias = new ArrayList<OfflineMedia>();
		for (OfflineMediaList item : mediaLists) {
			if (item != null && item.getAll() != null) {
				for (OfflineMedia media : item.getAll()) {
					if (media != null) {
						medias.add(media);
					}
				}
			}
		}
		return medias;
	}

	public int getMediaId() {
		return mMediaId;
	}
	
	public void setAll(List<OfflineMedia> medias) {
		mMedias.clear();
		if (medias != null && medias.size() > 0) {
		    for(OfflineMedia media : medias){
		        if(media != null && media.mediaId == mMediaId){
		            mMedias.add(media);
		        }
		    }
		}
	}

	public List<OfflineMedia> getAll() {
		return mMedias;
	}

	public OfflineMedia get(int position) {
		if (position < 0 || position >= size()) {
			return null;
		}
		return mMedias.get(position);
	}

	public int size() {
		return mMedias.size();
	}

	public String getName() {
		if (size() > 0) {
			OfflineMedia offlineMedia = mMedias.get(0);
			if (offlineMedia != null) {
				return offlineMedia.mediaName;
			}
		}
		return "";
	}

	public long getFileSize() {
		long fileSize = 0;
		for (OfflineMedia media : mMedias) {
			if (media != null) {
				fileSize += media.fileSize;
			}
		}
		return fileSize;
	}

	public String getStatus() {
		final int count = size();
		if (count > 1) {
			return DKApp.getAppContext().getString(R.string.count_ge_media,
					count);
		} else if (count == 1) {
			OfflineMedia offlineMedia = mMedias.get(0);
			if (offlineMedia != null) {
				return offlineMedia.getStatus();
			}
		}
		return "";
	}

	public boolean isDirType() {
		return size() > 1;
	}

    @Override
    public String getMediaStatus() {
        return "";
    }

    @Override
    public String getSubtitle() {
        return "";
    }

    @Override
    public String getDesc() {
        return "";
    }

    @Override
    public ImageUrlInfo getPosterInfo() {
        if (size() > 0) {
            OfflineMedia offlineMedia = mMedias.get(0);
            if (offlineMedia != null) {
                return offlineMedia.getPosterInfo();
            }
        }
        return null;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == null){
            return super.equals(o);
        }else{
            return hashCode() == o.hashCode();
        }
    }

    @Override
    public int hashCode() {
        return (mMediaId + "").hashCode();
    }

}
