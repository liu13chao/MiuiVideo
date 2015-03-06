package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.miui.video.R;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.videoplayer.adapter.OnlineEpisodeAdapter;
import com.miui.videoplayer.fragment.UIConfig;
import com.miui.videoplayer.model.Episode;
import com.miui.videoplayer.model.OnlineLoader;
import com.miui.videoplayer.model.UriLoader;


public class EpisodeListPopupWindow extends BasePopupWindow {

	private UriLoader mUriLoader;
	private OnlineEpisodeAdapter mOnlineEpisodeAdapter;
	
	public EpisodeListPopupWindow(Context context, View anchor) {
		super(context, anchor);
		init();
	}
	
	public void attachUriLoader(UriLoader uriLoader) {
		this.mUriLoader = uriLoader;
		if(mUriLoader instanceof OnlineLoader) {
			int mediaSetStyle = ((OnlineLoader) mUriLoader).getMediaStyle();
			if(mediaSetStyle == MediaConstantsDef.MEDIA_TYPE_VARIETY) {
				setWidth(mContext.getResources().getDimensionPixelSize(
				        R.dimen.vp_variety_popup_width));
			}
		}
	}
	
	@Override
	public void show() {
		super.show();
		if(mUriLoader.getPlayingUri() != null) {
			mOnlineEpisodeAdapter.setSelectedEpisode(mUriLoader.getPlayingUri().getCi());
		}
		mOnlineEpisodeAdapter.setGroup(mUriLoader.getEpisodeList());
	}

	//init
	private void init() {
		setTitle(R.string.vp_select_ci);
		mOnlineEpisodeAdapter = new OnlineEpisodeAdapter(mContext);
		setAdapter(mOnlineEpisodeAdapter);
		setOnItemClickListener(mOnItemClickListener);
	}
	
	//UI callback
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			dismiss();
			Episode episode = mOnlineEpisodeAdapter.getItem(position);
			if(episode != null) {
				if(mUriLoader.getPlayingUri() != null
						&& mUriLoader.getPlayingUri().getCi() == episode.getCi()) {
					return;
				}
				Message msg = Message.obtain();
				msg.what = UIConfig.MSG_WHAT_PLAY_CI;
				msg.arg1 = episode.getCi();
//				Controller.sendMessage(msg);
			}
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
}
