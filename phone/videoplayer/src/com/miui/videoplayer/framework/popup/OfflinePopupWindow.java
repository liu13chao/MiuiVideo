package com.miui.videoplayer.framework.popup;

import java.util.ArrayList;
import java.util.List;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.offline.OfflineMediaManager;
import com.miui.video.offline.OfflineMediaManager.OfflineMediasChangeListener;
import com.miui.videoplayer.adapter.OfflinePopupAdapter;
import com.miui.videoplayer.adapter.OfflinePopupAdapter.OfflineMediaChangedListener;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class OfflinePopupWindow extends BasePopupWindow {

	private ArrayList<OfflineMedia> mMedias = new ArrayList<OfflineMedia>();
	private OfflinePopupAdapter mAdapter;
	private OfflineMediaManager mOfflineMediaManager;
	
	public OfflinePopupWindow(Context context, View anchor) {
		super(context, anchor, context.getResources().getDimensionPixelSize(R.dimen.vp_menu_offline_item_width));
		init();
	}

	public void setOfflineMedias(List<OfflineMedia> medias){
		mMedias.clear();
		mMedias.addAll(medias);
		mAdapter.setGroup(medias);
		mAdapter.setOfflineMediaChangedListener(mOfflineMediaListener);
	}
	
	OfflineMediaChangedListener mOfflineMediaListener = new OfflineMediaChangedListener() {
		
		@Override
		public void onOfflineMediaChanged(OfflineMedia media) {
			Log.d("test", "onOfflineMediaChanged");
			if(mOfflineMediaManager != null){
				if (media == null || media.isUnrecovrableError()) {
					Log.d("test", "deleteMedia");
					mOfflineMediaManager.deleteMedia(media);
				} else if (media.isLoading() || media.isWaiting()) {
					Log.d("test", "pauseDownloader");
					mOfflineMediaManager.pauseDownloader(media);
				} else if (media.isNone()){
					Log.d("test", "addDownloader");
					mOfflineMediaManager.addMedia(media);
				} else if (!media.isFinished()){
					Log.d("test", "startDownloader");
					mOfflineMediaManager.startDownloader(media);
				}
			}
		}
	}; 
	
	public boolean enable(){
		return mAdapter.getCount() != 0;
	}
	
	private void init() {
		setTitle(R.string.vp_offline_title);
		mAdapter = new OfflinePopupAdapter(mContext);
		setAdapter(mAdapter);
		setOnItemClickListener(mOnItemClickListener);
		mOfflineMediaManager = DKApp.getSingleton(OfflineMediaManager.class);
	}
	
	public void show() {
		super.show();
		mOfflineMediaManager.registerUnfinishedMediasChangeListener(mMediasChangeListener);
		mOfflineMediaManager.registerFinishedMediasChangeListener(mMediasChangeListener);
	}
	
	private OfflineMediasChangeListener mMediasChangeListener = new OfflineMediasChangeListener() {
		
		@Override
		public void onOfflineMediasChange(List<OfflineMedia> medias) {
			if(mMedias == null){
				return;
			}
			for (int i = 0; i < mMedias.size(); i++) {
				final OfflineMedia item = mMedias.get(i);
				for (OfflineMedia media : medias) {
					if (item != null && media != null && item.mediaId == media.mediaId && item.episode == media.episode) {
						mMedias.set(i, media);
					}
				}
			}
			mAdapter.setGroup(mMedias);
		}
	};
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
		}
	};
	
	@Override
	public int getGravity() {
		return Gravity.RIGHT;
	}

	@Override
	public int getAnimationStyle() {
		return R.style.rightmenu_popup_anim_style;
	}

	@Override
	public void onDismiss() {
		mOfflineMediaManager.unregisterUnfinishedMediasChangeListener(mMediasChangeListener);
		mOfflineMediaManager.unregisterFinishedMediasChangeListener(mMediasChangeListener);
	}

}
