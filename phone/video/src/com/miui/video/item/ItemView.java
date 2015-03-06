package com.miui.video.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.miui.video.R;
import com.miui.video.offline.OfflineMediaList;
import com.miui.video.type.BaseMediaInfo;
import com.xiaomi.common.util.Strings;

public abstract class ItemView extends FrameLayout {

	public ItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ItemView(Context context) {
		super(context);
		init();
	}

	private void init() {
	}

	public abstract void setPoster(Bitmap bitmap);

	public abstract void setPosterUrl(String url, String md5);

	public abstract void setTitle(String title);

	public abstract void setSubtitle(String subtitle);

	public abstract void setSouthExtra(String extra);

	public abstract void setSouthwestExtra(String extra);

	public abstract void setSoutheastExtra(String extra);

	// TODO: 
	public void setMediaInfo(int channelId, BaseMediaInfo info) {
		if (info == null) {
			return;
		}
//		if (!TextUtils.isEmpty(info.po)) {
//		setPosterUrl(info.posterurl, info.md5);
//		} else if (info.smallImageURL != null && !TextUtils.isEmpty(info.smallImageURL.url)) {
//			setPosterUrl(info.smallImageURL.url, info.smallImageURL.md5);
//		}
//		setTitle(info.medianame);
//		setSubtitle(info.subtitle);
//		if (Channel.CHANNEL_TYPE_ZONGYI == channelId) {
//			setSouthExtra(getContext().getString(R.string.count_qi, info.lastissuedate));
////			setSoutheastExtra(String.valueOf(info.playcount));
////			setSouthwestExtra(info.lastissuedate);
//		}else if (Channel.CHANNEL_TYPE_DIANYING == channelId) {
//			setSouthExtra(getContext().getString(R.string.score_by, String.format(Locale.US, "%.1f", info.score)));
//		} else {
//			if (info.setnow >= info.setcount && info.setcount != 0) {
//				setSouthExtra(getContext().getString(R.string.count_ji_quan, info.setcount));
//			} else {
//				setSouthExtra(getContext().getString(R.string.update_to_count_ji, info.setnow));
//			}
//		}
	}
	
	public void setOfflineMediaList(OfflineMediaList medias) {
		if (medias == null || medias.size() <= 0) {
			return;
		}
//		setPosterUrl(medias.getPosterUrl(), medias.getPosterMd5());
		setTitle(medias.getName());
		setSubtitle(Strings.formatSize(medias.getFileSize()));
		if (medias.size() > 1) {
			setSoutheastExtra(getResources().getString(R.string.count_ge_media, medias.size()));
		} else{
			setSoutheastExtra(null);
		}
	}

}
