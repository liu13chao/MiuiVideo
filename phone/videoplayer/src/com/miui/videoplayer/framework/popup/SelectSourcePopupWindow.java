package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.miui.video.R;
import com.miui.video.type.MediaUrlInfo;
import com.miui.videoplayer.adapter.SelectSourceAdapter;
import com.miui.videoplayer.fragment.UIConfig;
import com.miui.videoplayer.model.BaseUri;
import com.miui.videoplayer.model.OnlineLoader;
import com.miui.videoplayer.model.OnlineUri;
import com.miui.videoplayer.model.UriLoader;

public class SelectSourcePopupWindow extends BasePopupWindow {

	private SelectSourceAdapter mSelectSourceAdapter;
	private OnlineLoader mOnlineLoader;
	
	public SelectSourcePopupWindow(Context context, View anchor) {
		super(context, anchor);
		init();
	}
	
	public void attachUriLoader(UriLoader uriLoader) {
		if(uriLoader instanceof OnlineLoader) {
			this.mOnlineLoader = (OnlineLoader) uriLoader;
		}
	}
	
	@Override
	public void show() {
		super.show();
		if(mOnlineLoader != null) {
			mSelectSourceAdapter.setData(mOnlineLoader);
		}
	}

	//init
	private void init() {
		setTitle(R.string.vp_select_video_source);
		mSelectSourceAdapter = new SelectSourceAdapter(mContext);
		setAdapter(mSelectSourceAdapter);
		setOnItemClickListener(mOnItemClickListener);
	}
	
	//UI callback
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			dismiss();
			MediaUrlInfo mediaUrlInfo = mSelectSourceAdapter.getItem(position);
			if(mediaUrlInfo != null) {
				int source = mediaUrlInfo.mediaSource;
				int clarity = mediaUrlInfo.clarity;
				if(mOnlineLoader != null) {
					BaseUri baseUri = mOnlineLoader.getPlayingUri();
					if(baseUri instanceof OnlineUri) {
						OnlineUri onlineUri = (OnlineUri) baseUri;
						if(source == onlineUri.getSource() && clarity == onlineUri.getResolution()) {
							return;
						}
					}
				}
				Message msg = Message.obtain();
				msg.what = UIConfig.MSG_WHAT_SWITCH_CLARITY;
				msg.arg1 = source;
				msg.arg2 = clarity;
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
