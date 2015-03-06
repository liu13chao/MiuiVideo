package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.miui.video.R;
import com.miui.videoplayer.adapter.ScaleScreenAdapter;
import com.miui.videoplayer.fragment.UIConfig;

public class ScaleScreenPopupWindow extends BasePopupWindow {

	private ScaleScreenAdapter mScaleScreenAdapter;
	private String[] mValues;
	private int mSelectedIndex;
	
	
	public ScaleScreenPopupWindow(Context context, View anchor) {
		super(context, anchor);
		init();
	}
	
	@Override
	public void show() {
		super.show();
		refreshListView();
	}

	//init
	private void init() {
		initValues();
		setTitle(R.string.vp_scale_screen);
		mScaleScreenAdapter = new ScaleScreenAdapter(mContext);
		setAdapter(mScaleScreenAdapter);
		setOnItemClickListener(mOnItemClickListener);
	}
	
	private void initValues() {
		mValues = new String[]{mContext.getResources().getString(R.string.vp_auto_select),
				mContext.getResources().getString(R.string.vp_full_screen),
				mContext.getResources().getString(R.string.vp_adapt_width),
				mContext.getResources().getString(R.string.vp_adapt_height),
				mContext.getResources().getString(R.string.vp_using_16_9),
				mContext.getResources().getString(R.string.vp_using_4_3)};
	}
	
	//private method
	private void refreshListView() {
		mScaleScreenAdapter.setSelectedIndex(mSelectedIndex);
		mScaleScreenAdapter.setGroup(mValues);
	}
	
	private void updateVideoPlayerSize(int position) {
		int videoSizeStyle = UIConfig.VIDEO_SIZE_STYLE_AUTO;
		switch (position) {
		case 0:
			videoSizeStyle = UIConfig.VIDEO_SIZE_STYLE_AUTO;
			break;		
		case 1:
			videoSizeStyle = UIConfig.VIDEO_SIZE_STYLE_FULL_SCREEN;
			break;
		case 2:
			videoSizeStyle = UIConfig.VIDEO_SIZE_STYLE_ADAPT_WIDTH;
			break;
		case 3:
			videoSizeStyle = UIConfig.VIDEO_SIZE_STYLE_ADAPT_HEIGHT;
			break;
		case 4:
			videoSizeStyle = UIConfig.VIDEO_SIZE_STYLE_16_9;
			break;
		case 5:
			videoSizeStyle = UIConfig.VIDEO_SIZE_STYLE_4_3;
			break;			
		default:
			break;
		}
		Message msg = Message.obtain();
		msg.what = UIConfig.MSG_WHAT_SCALE_SCREEN;
		msg.arg1 = videoSizeStyle;
//		Controller.sendMessage(msg);		
	}
	
	//UI callback
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			dismiss();
			mSelectedIndex = position;
			updateVideoPlayerSize(position);
		}
	};
	
	@Override
	public int getGravity() {
		return Gravity.RIGHT;
	}

	@Override
	public int getAnimationStyle() {
		return R.style.leftmenu_popup_anim_style;
	}
}
