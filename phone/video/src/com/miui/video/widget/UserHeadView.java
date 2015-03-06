package com.miui.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.model.ImageManager;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.type.UserNickNameInfo;
import com.miui.video.util.Util;

public class UserHeadView extends FrameLayout {

	private Context mContext;
	
	//UI
	private View mContentView;
	private UserHeadImageView mUserHeadIv;
	private TextView mUserHeadName;
	private TextView mUserHeadIdentity;
	
	//data
	private UserNickNameInfo mUserNickNameInfo;
	
	public UserHeadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	public UserHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public UserHeadView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public void setUserNickNameInfo(UserNickNameInfo userNickNameInfo) {
		this.mUserNickNameInfo = userNickNameInfo;
		refresh();
	}
	
	//init
	private void init() {
		mContentView = View.inflate(mContext, R.layout.user_head, null);
		addView(mContentView);
		
		mUserHeadIv = (UserHeadImageView) mContentView.findViewById(R.id.user_head_iv);
		mUserHeadName = (TextView) mContentView.findViewById(R.id.user_head_name);
		mUserHeadIdentity = (TextView) mContentView.findViewById(R.id.user_head_identity);
	}
	
	//packaged method
	private void refresh() {
		if(mUserNickNameInfo == null) {
			showNotLogin();
		} else {
			showLogin();
		}
	}
	
	private void showLogin() {
		mUserHeadName.setText(String.valueOf(mUserNickNameInfo.userId));
		mUserHeadIdentity.setText(mUserNickNameInfo.aliasNick);
		fetchImage();
	}
	
	private void showNotLogin() {
		mUserHeadName.setText(R.string.not_login);
		mUserHeadIdentity.setText(R.string.click_to_login);
		fetchImage();
	}
	
	private void fetchImage() {
		if(mUserNickNameInfo == null || Util.isEmpty(mUserNickNameInfo.miliaoIcon)) {
			mUserHeadIv.setImageResource(R.drawable.user_head_default);
		} else {
			String miliaoIcon = mUserNickNameInfo.miliaoIcon;
			int index = miliaoIcon.lastIndexOf(".");
			if(index >= 0) {
				String name = miliaoIcon.substring(0, index);
				String postfix = miliaoIcon.substring(index);
				String constent = "_orig";
				StringBuilder sb = new StringBuilder();
				sb.append(name);
				sb.append(constent);
				sb.append(postfix);
				
				ImageUrlInfo imageUrlInfo = new ImageUrlInfo(sb.toString(), null, null);
				if(!ImageManager.isUrlDone(imageUrlInfo, mUserHeadIv)) {
					mUserHeadIv.setImageResource(R.drawable.user_head_default);
		               ImageManager.getInstance().fetchImage(ImageManager.createTask(imageUrlInfo, null),
		                       mUserHeadIv);
				}
			}
		}
	}
}
