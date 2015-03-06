/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  MediaViewType.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-11-26
 */
package com.miui.video.util;

import android.content.res.Resources;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.local.Favorite;
import com.miui.video.local.PlayHistory;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.offline.OfflineMediaList;
import com.miui.video.screenfit.ScreenFitHelper;
import com.miui.video.storage.BaseDevice;
import com.miui.video.storage.MediaItem;
import com.miui.video.type.LocalMedia;
import com.miui.video.type.LocalMediaList;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.SpecialSubject;
import com.miui.video.type.TelevisionInfo;

/**
 * @author tianli
 * 
 */
public class MediaViewHelper {
	//online page
	public final static int UI_COVER_TYPE = 0;
	public final static int UI_SMALL_COVER_TYPE = 1;
	public final static int UI_BANNER_TYPE = 2;
	//local page
	public final static int UI_LOCAL_COVER_TYPE = 10;//history, bookmark
	public final static int UI_LOCAL_NORMAL_TYPE = 11;
	public final static int UI_LOCAL_PADDING_TYPE = 12;
	public final static int UI_LOCAL_DIR_TYPE = 13;
		
	public static String getMediaStatus(MediaInfo mediaInfo) {
		StringBuilder status = new StringBuilder();
		Resources res = DKApp.getAppContext().getResources();
		if (mediaInfo.isMultiSetType()) {
			if (!Util.isEmpty(mediaInfo.lastissuedate)){
				String[] array = mediaInfo.lastissuedate.split("-");
				if (array.length >= 3) {
					status.append(array[1] + res.getString(R.string.month));
					status.append(array[2] + res.getString(R.string.day));
					status.append(res.getString(R.string.update));
				}		
			} else if (mediaInfo.setnow == mediaInfo.setcount) {
				String str = res.getString(R.string.count_ji_quan);
				str = String.format(str, mediaInfo.setnow);
				status.append(str);
			} else {
				String str = "";
				str = res.getString(R.string.update_to_count_ji);
		    	str = String.format(str, mediaInfo.setnow);
			    status.append(str);
			}
		} else {
			if (mediaInfo.playlength > 0) {
				status.append(mediaInfo.playlength / 60);
				int second = mediaInfo.playlength % 60;
				status.append(":");
				if (second == 0) {
					status.append("00");
				} else {
					if (second < 10)
						status.append("0");
					status.append(second);
				}
			}
		}
		return status.toString();
	}
	
	public static int getSizePerRow(Object object) {
		Object contentInfo = object;
		if (contentInfo instanceof PlayHistory
				|| contentInfo instanceof Favorite
				|| contentInfo instanceof LocalMediaList
				|| contentInfo instanceof LocalMedia
				|| contentInfo instanceof OfflineMediaList
				|| contentInfo instanceof OfflineMedia
				|| contentInfo instanceof BaseDevice) {
			return ScreenFitHelper.getLocalSizePerRow();
		} else if (contentInfo instanceof SpecialSubject) {
			return ScreenFitHelper.getBannerSizePerRow();
		}
		return ScreenFitHelper.getVideoSizePerRow();
	}
	
	public static int getTypeOf(Object object, boolean mixed) {
		Object contentInfo = object;
		boolean isLocal = false;
		if (contentInfo instanceof PlayHistory) {
			contentInfo = ((PlayHistory)contentInfo).getPlayItem();
			isLocal = true;
		} else if (contentInfo instanceof Favorite) {
			contentInfo = ((Favorite)contentInfo).getFavoriteItem();
			isLocal = true;
		}
		
		if (contentInfo instanceof MediaInfo) {
			if (isLocal) {
				return UI_LOCAL_COVER_TYPE;
			} else {
				return UI_COVER_TYPE;
			}
		} else if (contentInfo instanceof SpecialSubject) {
			return UI_BANNER_TYPE;
		} else if (contentInfo instanceof TelevisionInfo) {
			return UI_SMALL_COVER_TYPE;
		} else if (contentInfo instanceof BaseDevice) {
			return UI_LOCAL_NORMAL_TYPE;
		} else if (contentInfo instanceof MediaItem) {
			return UI_LOCAL_NORMAL_TYPE;
		} else if (contentInfo instanceof LocalMedia) {
			if (mixed) {
				return UI_LOCAL_PADDING_TYPE;
			} else {
				return UI_LOCAL_NORMAL_TYPE;
			}
		} else if (contentInfo instanceof LocalMediaList) {
			if (((LocalMediaList)contentInfo).isDirType()) {
				return UI_LOCAL_DIR_TYPE;
			} else {
				if (mixed) {
					return UI_LOCAL_PADDING_TYPE;
				} else {
					return UI_LOCAL_NORMAL_TYPE;
				}
			}
		} else if (contentInfo instanceof OfflineMedia) {
			if (mixed) {
				return UI_LOCAL_PADDING_TYPE;
			} else {
				return UI_LOCAL_NORMAL_TYPE;
			}
		} else if (contentInfo instanceof OfflineMediaList) {
			if (((OfflineMediaList)contentInfo).isDirType()) {
				return UI_LOCAL_DIR_TYPE;
			} else {
				if (mixed) {
					return UI_LOCAL_PADDING_TYPE;
				} else {
					return UI_LOCAL_NORMAL_TYPE;
				}
			}
		} 
		return UI_COVER_TYPE;
	}
	
	public static boolean isMediaContentMixed(Object[] mediaViewContents) {
		if (mediaViewContents == null || mediaViewContents.length == 0) {
			return false;
		}
		boolean containsMedia = false;
		boolean containsLocal = false;
		boolean isMediaContentMixed = false;
		for (int i = 0; i < mediaViewContents.length; i++) { 
			Object obj = mediaViewContents[i];
			
			if (obj instanceof PlayHistory) {
				obj = ((PlayHistory)obj).getPlayItem();
			} else if (obj instanceof Favorite) {
				obj = ((Favorite)obj).getFavoriteItem();
			}
			
			if (obj instanceof MediaInfo) {
				containsMedia = true;
			} else if (obj instanceof LocalMediaList) {
				if (((LocalMediaList)obj).isDirType()) {
					containsMedia = true;
				} else {
					containsLocal = true;
				}
			} else if (obj instanceof TelevisionInfo || obj instanceof LocalMedia
					|| obj instanceof OfflineMedia) {
				containsLocal = true;
			} else if (obj instanceof OfflineMediaList) {
				if (((OfflineMediaList)obj).isDirType()) {
					containsMedia = true;
				} else {
					containsLocal = true;
				}
			}
			if (containsMedia && containsLocal) {
				isMediaContentMixed = true;
			}
		}
		return isMediaContentMixed;
	}
}
