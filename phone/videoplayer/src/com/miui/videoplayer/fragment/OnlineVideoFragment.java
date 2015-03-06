/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OnlineVideoFragment.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-23
 */

package com.miui.videoplayer.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import com.miui.video.R;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.controller.MediaConfig;
import com.miui.videoplayer.common.Constants;
import com.miui.videoplayer.menu.MenuFactory;
import com.miui.videoplayer.menu.MenuIds;
import com.miui.videoplayer.menu.MenuItem;
import com.miui.videoplayer.menu.popup.SeriesEpListPopup;
import com.miui.videoplayer.model.BaseUri;
import com.miui.videoplayer.model.OnlineLoader;
import com.miui.videoplayer.model.OnlineUri;
import com.miui.videoplayer.model.UriLoader;

/**
 * @author tianli
 *
 */
public class OnlineVideoFragment extends OnlinePlayFragment {

    public static final String TAG = "OnlineVideoFragment";
    private boolean alreadyPreLoadNext = false;

	public OnlineVideoFragment(Context context, FrameLayout anchor) {
        super(context, anchor);
    }

//	private SelectSourceView mSelectSource = null;
	
	@Override
    public void launch(Intent intent) {
        init(mContext, intent);
    }

    public boolean isOfflineDownload(BaseUri baseUri) {
		Uri uri = baseUri.getUri();
        if(MediaConfig.isOfflineUri(uri)){
            return true;
        }else{
            return false;
        }
	}
	
	private void init(Context context, Intent intent){
		int mediaId = intent.getIntExtra(Constants.MEDIA_ID, -1);
		int ci = intent.getIntExtra(Constants.CURRENT_EPISODE, 1);
		int source = intent.getIntExtra(Constants.MEDIA_SOURCE, -1);
		String html5 = intent.getStringExtra(Constants.MEDIA_HTML5_URL);
		String title = intent.getStringExtra(Constants.INTENT_KEY_STRING_MEDIA_TITLE);
		String suffix = "";
	    boolean isMultiSet = intent.getBooleanExtra(Constants.MULTI_SET, false) ;
		int mediaSetStyle = intent.getIntExtra(Constants.MEDIA_SET_STYLE, Constants.MEDIA_TYPE_VARIETY);
		if(mediaSetStyle == Constants.MEDIA_TYPE_SERIES && isMultiSet){
			String text = context.getResources().getString(R.string.episode_suffix);
			text = String.format(text, ci);
			suffix = text;			
		}else if(!TextUtils.isEmpty(intent.getStringExtra(Constants.MEDIA_SET_NAME))){
		    title = intent.getStringExtra(Constants.MEDIA_SET_NAME);
		}
		String sdkinfo = intent.getStringExtra(Constants.INTENT_KEY_STRING_MEDIA_SDKINFO);
		boolean sdkdisable = intent.getBooleanExtra(Constants.INTENT_KEY_STRING_MEDIA_SDKDISABLE, true);
		int videoType = intent.getIntExtra(Constants.VIDEO_TYPE, Constants.MEDIA_TYPE_LONG);
		String posterUrl = intent.getStringExtra(Constants.MEDIA_POSTER_URL);
		OnlineUri onlineUri = new OnlineUri(mediaId, ci, html5, 
				title + suffix, source, MediaConstantsDef.CLARITY_NORMAL, sdkinfo, sdkdisable, intent.getData(), videoType, posterUrl);
		mUri = onlineUri;
		mUriLoader = new OnlineLoader(context, mediaId, mediaSetStyle, onlineUri);
	}

	@Override
	public UriLoader getUriLoader() {
		return mUriLoader;
	}

//	private void addSelectSource(){
//		mSelectSource = new SelectSourceView(mActivity);
//		mSelectSource.attachOnlineLoader((OnlineLoader)mUriLoader);
//		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
//				FrameLayout.LayoutParams.WRAP_CONTENT);
//		params.gravity = Gravity.RIGHT | Gravity.TOP;
//		mSelectSource.setLayoutParams(params);
//		mParent.addView(mSelectSource);
//	}
//	private void removeSelectSource(){
//		if(mSelectSource != null){
//			mParent.removeView(mSelectSource);
//			mSelectSource = null;
//		}
//	}

	@Override
	protected void onBufferingUpdate(int percent) {
		Log.d(TAG, "onBufferingUpdate " + percent);
		if(mUriLoader.hasNext() && !alreadyPreLoadNext && percent == 90 ){
		    alreadyPreLoadNext = true;
		    ((OnlineLoader)mUriLoader).preloadNext();
		}
	}

    @Override
    public CharSequence getVideoTitle() {
        if(mUri != null){
            return mUri.getTitle();
        }
        return "";
    }

    @Override
    public CharSequence getVideoSubtitle() {
        if(mUri != null && MediaConfig.isOfflineUri(mUri.getUri())){
            return mContext.getResources().getString(R.string.top_status_offline_media);
        }else{
            return mContext.getResources().getString(R.string.top_status_online_media);
        }
    }

    @Override
    public List<MenuItem> getMenu() {
        List<MenuItem> items = new ArrayList<MenuItem>();
        if(mUriLoader != null && mUriLoader.canSelectCi()){
            items.add(MenuFactory.createEp());
        }
        items.addAll(super.getMenu());
        return items;
    }

    @Override
    public void onMenuClick(MenuItem menuItem) {
        Log.d(TAG, "onMenuClick " + menuItem.getId());
        if(menuItem.getId() == MenuIds.MENU_ID_ONLINE_EP){
            SeriesEpListPopup window = new SeriesEpListPopup(mContext, getUriLoader(),
                    mVideoProxy);
            window.show(mAnchor);
            window.setShowHideListener(mMenuShowHideListener);
        }else {
            super.onMenuClick(menuItem);
        }
    }

}
