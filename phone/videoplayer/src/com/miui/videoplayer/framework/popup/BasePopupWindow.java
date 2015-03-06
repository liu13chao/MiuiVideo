package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.miui.video.R;

public abstract class BasePopupWindow {
	
	protected Context mContext;
	protected View mAnchor;
	
	protected boolean mCanStartVideo = true;
	protected boolean mCanHideFullscreenBg = true;
	
	protected PopupWindow mPopupWindow;
	private View mContentView;
	private ListView mListView;
	
	private TextView mTitle;
	
	private int mWidth;
	
	public BasePopupWindow(Context context, View anchor) {
		this.mContext = context;
		this.mAnchor = anchor;
		mWidth = getWidth();
		init();
	}
	
	public BasePopupWindow(Context context, View anchor, int width) {
		this.mContext = context;
		this.mAnchor = anchor;
		mWidth = width;
		init();
	}
	
	public void show() {
		mPopupWindow.showAtLocation(mAnchor, getGravity(), 0, 0);
//		Controller.sendMessage(UIConfig.MSG_WHAT_VIDEO_PAUSE);
	}
	
	public void dismiss() {
		mPopupWindow.dismiss();
	}
	
	public void setTitle(int resid) {
		mTitle.setText(resid);
	}
	
	public void setTitle(String title) {
		mTitle.setText(title);
	}
	
	public void setAdapter(BaseAdapter adapter) {
		mListView.setAdapter(adapter);
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		mListView.setOnItemClickListener(listener);
	}
	
	public void addListHeader(View v){
		mListView.addHeaderView(v);
	}
	
	public void setWidth(int width) {
		mPopupWindow.setWidth(width);
	}
	
	private void init() {
		mPopupWindow = new PopupWindow(mWidth, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setOnDismissListener(mOnDismissListener);
		mPopupWindow.setAnimationStyle(getAnimationStyle());
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.full_translucent)));
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mContentView = View.inflate(mContext, R.layout.vp_popup_base, null);
		mPopupWindow.setContentView(mContentView);
		
		mTitle = (TextView) mContentView.findViewById(R.id.vp_popup_base_title_name);
		mListView = (ListView) mContentView.findViewById(R.id.vp_popup_base_listview);
		mListView.setDivider(mContext.getResources().getDrawable(R.drawable.vp_divider_bg_30));
	}
	
	private OnDismissListener mOnDismissListener = new OnDismissListener() {
		@Override
		public void onDismiss() {
			if(mCanStartVideo) {
//				Controller.sendMessage(UIConfig.MSG_WHAT_VIDEO_START);
			}
			BasePopupWindow.this.onDismiss();
		}
	};
	
	public void onDismiss(){
	};
	
	public abstract int getGravity();
	public abstract int getAnimationStyle();
	
	public int getWidth(){
	    return mContext.getResources().getDimensionPixelSize(R.dimen.vp_base_popup_width);
	}
	
}
