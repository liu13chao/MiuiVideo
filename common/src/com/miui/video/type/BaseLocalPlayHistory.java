/**
 *   Copyright(c) 2013 DuoKan TV Group
 *    
 *   BaseLocalPlayHistory.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2013-6-19
 */

package com.miui.video.type;

import java.io.Serializable;

import com.miui.video.util.DKLog;

/**
 * @author xuanmingliu
 * 
 */

public class BaseLocalPlayHistory implements Comparable<BaseLocalPlayHistory>,
		Serializable {
	private static final long serialVersionUID = 2L;
	private static final String TAG = BaseLocalPlayHistory.class
			.getSimpleName();

	public static final class PlayHistoryType {
		public static final int PLAYHISTORY_TYPE_UNKNOWN = -1;
		public static final int PLAYHISTORY_TYPE_MEDIA = 0;
		public static final int PLAYHISTORY_TYPE_TV = 1;
	}

	public String playDate; // ms play date
	public int nType = PlayHistoryType.PLAYHISTORY_TYPE_UNKNOWN;

	public boolean isTVPlayHistory() {
		return nType == PlayHistoryType.PLAYHISTORY_TYPE_TV;
	}

	public boolean isMediaPlayHistory() {
		return nType == PlayHistoryType.PLAYHISTORY_TYPE_MEDIA;
	}

	public int getPlayHistoryType() {
		return nType;
	}

	public String getPlayHistoryDate() {
		return playDate;
	}

	@Override
	public int compareTo(BaseLocalPlayHistory another) {
		if (another == null)
			return -1;

		try {
			long date = Long.parseLong(playDate);
			long anotherDate = Long.parseLong(another.playDate);
			if (date > anotherDate) {
				return -1;
			} else if (date < anotherDate) {
				return 1;
			} else {
				return 0;
			}
		} catch (NumberFormatException ex) {
			DKLog.e(TAG, "" + ex);
		}

		return -1;
	}
}
