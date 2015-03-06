/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OnlineLoader.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-24
 */

package com.miui.videoplayer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

import com.miui.video.R;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.datasupply.MediaUrlInfoListSupply;
import com.miui.video.datasupply.MediaUrlInfoListSupply.MediaUrlInfoListListener;
import com.miui.video.model.MediaUrlForPlayerUtil;
import com.miui.video.model.MediaUrlForPlayerUtil.PlayUrlObserver;
import com.miui.video.model.loader.DataLoader;
import com.miui.video.model.loader.DataLoader.LoadListener;
import com.miui.video.model.loader.DetailInfoLoader;
import com.miui.video.type.MediaDetailInfo;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaSetInfo;
import com.miui.video.type.MediaSetInfoList;
import com.miui.video.type.MediaUrlInfo;
import com.miui.video.type.MediaUrlInfoList;
import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.common.Constants;

/**
 * @author tianli
 *
 */
@SuppressLint("UseSparseArrays")
public class OnlineLoader extends UriLoader {

	public static final String TAG = "OnlineLoader";

	private Context mContext;
	private int mMediaId;
	private int mCurCi = -1;
	private int mCurSource = -1;
	private int mCurClarity = -1;
	private int mMediaSetStyle = -1;
	
	//data supply
	private DetailInfoLoader mDetailLoader;
	private MediaUrlInfoListSupply mMediaUrlInfoListSupply;
	private MediaUrlForPlayerUtil mMediaUrlForPlayerUtil;
	
	//data from net
	private MediaDetailInfo2 mMediaDetailInfo2;
	private MediaSetInfoList mMediaSetInfoList;
	
	private MediaUrlInfoList mMediaUrlInfoList;
	
	private OnlineUri mOnlineUri;

	private OnUriLoadedListener mListener;
	private NotifyBuildSourcesListener mNotifyBuildSourcesListener;
	private List<Episode> mEpisodeList = new ArrayList<Episode>();
	
	public OnlineLoader(Context context, int mediaId, int ci, int source, int clarity, int mediaSetStyle){
		mContext = context.getApplicationContext();
		mMediaId = mediaId;
		mCurCi = ci;
		mCurSource = source;
		mCurClarity = clarity;
		mMediaSetStyle = mediaSetStyle;
		init();
	}
	
	public OnlineLoader(Context context, int mediaId, int mediaSetStyle, OnlineUri onlineUri){
		mContext = context.getApplicationContext();
		mMediaId = mediaId;
		mMediaSetStyle = mediaSetStyle;
		mOnlineUri = onlineUri;
		if(mOnlineUri != null) {
			mCurCi = mOnlineUri.getCi();
			mCurSource = mOnlineUri.getSource();
			mCurClarity = mOnlineUri.getResolution();
		}
		init();
	}
	
	public void setNotifyBuildSourcesListener(NotifyBuildSourcesListener listener){
		mNotifyBuildSourcesListener = listener;
	}
	
	@Override
	public String getTitle() {
		if(mMediaDetailInfo2 != null) {
			MediaDetailInfo mediaDetailInfo =  mMediaDetailInfo2.mediainfo;
			if(mediaDetailInfo != null) {
				String title = mediaDetailInfo.medianame;
				if(mediaDetailInfo.isMultiSetType()) {
					String suffix = mContext.getResources().getString(R.string.episode_suffix);
					suffix = String.format(suffix, mCurCi);
					title = title +" " +suffix;
					return title;
				}
			}
		}
		
		OnlineEpisode episode = getCurEpisode();
		if(episode != null) {
			return episode.getName();
		}
		
		return mPlayingUri.getTitle();
	}
	
	public int getMediaStyle() {
		return mMediaSetStyle;
	}
	
	public void preloadNext() {
		next(null);
	}
	
	public MediaUrlInfoList getMediaUrlInfoList() {
		return mMediaUrlInfoList;
	}

	@Override
	public List<Episode> getEpisodeList() {
		return mEpisodeList;
	}

	@Override
	public boolean canSelectCi() {
		if(mEpisodeList.size() > 1){
			return true;
		}
		return false;
	}

	public MediaUrlInfo[] getAll() {
		if(mMediaUrlInfoList != null){
			MediaUrlInfo[] temp = AndroidUtils.concat(mMediaUrlInfoList.urlNormal, mMediaUrlInfoList.urlHigh);
			return AndroidUtils.concat(temp, mMediaUrlInfoList.urlSuper);
		}
		return null;
	}
	
	public boolean canSelectSource(int episode) {
		final MediaUrlInfo[] all = getAll();
		return all != null && all.length > 1;
	}
	
	public List<OnlineEpisodeSource> buildSources(MediaUrlInfo[] mediaUrlInfos, int resolution){
		List<OnlineEpisodeSource> list = new ArrayList<OnlineEpisodeSource>();
		if(mediaUrlInfos != null && mediaUrlInfos.length > 0){
			for(MediaUrlInfo info : mediaUrlInfos){
				if(info != null){
					OnlineEpisodeSource s = new OnlineEpisodeSource();
					s.setSource(info.mediaSource);
					s.setResolution(resolution);
					list.add(s);				
				}
			}
		}
		return list;
	}
	
	public List<OnlineEpisodeSource> buildSources(int episode) {
		if(mMediaUrlInfoList == null){
			return null;
		}
		List<OnlineEpisodeSource> list = new ArrayList<OnlineEpisodeSource>();
		list.addAll(buildSources(mMediaUrlInfoList.urlNormal, MediaConstantsDef.CLARITY_NORMAL));
		list.addAll(buildSources(mMediaUrlInfoList.urlHigh, MediaConstantsDef.CLARITY_HIGH));
		list.addAll(buildSources(mMediaUrlInfoList.urlSuper, MediaConstantsDef.CLARITY_SUPPER));
		return list;
	}
	
	@Override
	public void cancel() {
		
	}
	
	@Override
	public int next(OnUriLoadedListener uriListener) {
		if(mPlayingUri != null){
			int ci = mPlayingUri.getCi();
			for(int i = 0; mEpisodeList != null && i < mEpisodeList.size(); i++){
				if(mEpisodeList.get(i).getCi() == ci){
					if(i < mEpisodeList.size() - 1){
						loadEpisode(mEpisodeList.get(i+1).getCi(), uriListener);
						return mEpisodeList.get(i+1).getCi();
					}
				}
			}
		}
		onError(ERROR_NO_EPISODE, uriListener);
		return -1;
	}
	
	private void onError(int error, OnUriLoadedListener uriLoadedListener){
		if(uriLoadedListener != null){
			uriLoadedListener.onUriLoadError(error);
		}
	}
	
	@Override
	public boolean hasNext() {
		return mCurCi < mEpisodeList.size();
	}
	
	@Override
	public void loadEpisode(int episode, OnUriLoadedListener uriListener) {
		loadEpisode(episode, mCurSource, mCurClarity, uriListener);
	}
	
	public void switchClarity(int source, int clarity, OnUriLoadedListener uriListener) {
		loadEpisode(mCurCi, source, clarity, uriListener);
	}
	
	public void loadEpisode(int episode, int source, int clarity, OnUriLoadedListener uriListener) {
		this.mListener = uriListener;
		if(mCurCi != episode){
			mCurCi = episode;
			mCurSource = source;
			mCurClarity = clarity;
			resetData();
		}else if(mCurSource != source || mCurClarity != clarity){
			mCurSource = source;
			mCurClarity = clarity;
			resetPlayData();
		}
		getPlayUrl();
	}
	
	//init
	private void init() {
	    mDetailLoader = new DetailInfoLoader(mMediaId, "");
	    mDetailLoader.addListener(mLoadListener);
		mMediaUrlInfoListSupply = new MediaUrlInfoListSupply();
		mMediaUrlForPlayerUtil = new MediaUrlForPlayerUtil(mContext);
		mMediaUrlInfoListSupply.addListener(mMediaUrlInfoListListener);
		mMediaUrlForPlayerUtil.setObserver(mPlayUrlObserver);
		getPlayUrl();
	}
	
	//get data
	private void getPlayUrl() {
		if(mMediaDetailInfo2 == null) {
			getMediaDetailInfo();
		} else if(mMediaUrlInfoList == null) {
			getMediaUrl();
		} else if(mOnlineUri == null) {
			retrievePlayUrl();
		} else {
			notifyPlayUrlDone(false, mOnlineUri);
		}
	}
	
	private void getMediaDetailInfo() {
	    if(mDetailLoader != null){
	          mDetailLoader.load();
	    }
	}
	
	private void getMediaUrl(){
		if(mCurCi > 0) {
			mMediaUrlInfoListSupply.getMediaUrlInfoList(mMediaId, mCurCi, -1);
		}
	}
	
	private void retrievePlayUrl() {
		if(mMediaUrlInfoList != null) {
			MediaUrlInfo mediaUrlInfo = MediaUrlInfoListSupply.filterMediaUrlInfoList(mMediaUrlInfoList, mCurSource, mCurClarity);
			if(mediaUrlInfo != null) {
				mCurSource = mediaUrlInfo.mediaSource;
				mCurClarity = mediaUrlInfo.clarity;
				if(MediaUrlInfoListSupply.isNeedSDK(mediaUrlInfo)) {
					mOnlineUri = buildOnlineUri(mediaUrlInfo.mediaUrl, mediaUrlInfo.mediaUrl, mediaUrlInfo.sdkinfo2);
					mOnlineUri.setPlayType(com.miui.video.controller.MediaConfig.PLAY_TYPE_SDK);
					notifyPlayUrlDone(false, mOnlineUri);
				} else {
					if(mediaUrlInfo.isHtml()) {
						mMediaUrlForPlayerUtil.getMediaUrlForPlayer(mediaUrlInfo.mediaUrl);
					} else {
						mOnlineUri = buildOnlineUri(mediaUrlInfo.mediaUrl, mediaUrlInfo.mediaUrl, mediaUrlInfo.sdkinfo2);
						mOnlineUri.setPlayType(com.miui.video.controller.MediaConfig.PLAY_TYPE_DIRECT);
						notifyPlayUrlDone(false, mOnlineUri);
					}
				}
			}
		}
	}
	
	private void buildEpisodeList() {
		mEpisodeList.clear();
		if(mMediaSetInfoList != null && mMediaSetInfoList.videos != null) {
			MediaSetInfo[]  mediaSetInfos = mMediaSetInfoList.videos;
			for(int i = 0; i < mediaSetInfos.length; i++) {
				MediaSetInfo mediaSetInfo = mediaSetInfos[i];
				if(mediaSetInfo != null) {
					OnlineEpisode episode = new OnlineEpisode();
					episode.setMediaStyle(mMediaSetInfoList.style);
					episode.setCi(mediaSetInfo.ci);
					episode.setName(mediaSetInfo.videoname);
					episode.setDate(mediaSetInfo.date);
					mEpisodeList.add(episode);
				}
			}
			if(mMediaSetStyle == Constants.MEDIA_TYPE_VARIETY) {
				Collections.reverse(mEpisodeList);
			}
		}
	}
	
	//packaged method
	private void resetData() {
		mMediaUrlInfoList = null;
		mOnlineUri = null;
	}
	
	private void resetPlayData() {
		mOnlineUri = null;
	}
	
	private void notifyPlayUrlDone(boolean isError, OnlineUri onlineUri) {
		if(mListener != null) {
			if(isError) {
				mListener.onUriLoadError(-1);
			} else {
				mListener.onUriLoaded(mCurCi, onlineUri);
			}
		}
	}
	
	private OnlineUri buildOnlineUri(String html5, String playUrl, String sdkinfo) {
		if(mMediaDetailInfo2 != null) {
			MediaDetailInfo mediaDetailInfo =  mMediaDetailInfo2.mediainfo;
			MediaSetInfoList mediaSetInfoList = mMediaDetailInfo2.mediaciinfo;
			if(mediaDetailInfo != null) {
				int mediaId = mediaDetailInfo.mediaid;
				String title = mediaDetailInfo.medianame;
				if(mediaDetailInfo.isMultiSetType() && mediaSetInfoList != null 
						&& !mediaSetInfoList.isVariety()) {
					String suffix = mContext.getResources().getString(R.string.episode_suffix);
					suffix = String.format(suffix, mCurCi);
					title = title +" " +suffix;
				}
				Uri uri = Uri.parse(playUrl);
				OnlineUri onlineUri = new OnlineUri(mediaId, mCurCi, html5, title, 
						mCurSource, mCurClarity, sdkinfo, uri);
				return onlineUri;
			}
		}
		return null;
	}
	
	private OnlineEpisode getCurEpisode() {
		for(int i = 0; i < mEpisodeList.size(); i++) {
			Episode episode = mEpisodeList.get(i);
			if(episode instanceof OnlineEpisode && episode.getCi() == mCurCi) {
				return (OnlineEpisode) episode;
			}
		}
		return null;
	}
	
	private MediaUrlInfoListListener mMediaUrlInfoListListener = new MediaUrlInfoListListener() {
		@Override
		public void onMediaUrlInfoListDone(MediaUrlInfoList mediaUrlInfoList,
				boolean isError) {
			if(!isError) {
				mMediaUrlInfoList = mediaUrlInfoList;
				if(mNotifyBuildSourcesListener != null){
					mNotifyBuildSourcesListener.OnBuildSourcesDown();
				}
				buildEpisodeList();
				getPlayUrl();
			} else {
				notifyPlayUrlDone(true, null);
			}
		}
	};
	
	private PlayUrlObserver mPlayUrlObserver = new PlayUrlObserver() {
		@Override
		public void onUrlUpdate(int mediaId, int ci, String playUrl, String html5Url) {
			mOnlineUri = buildOnlineUri(html5Url, playUrl, "");
			mOnlineUri.setPlayType(com.miui.video.controller.MediaConfig.PLAY_TYPE_HTML5);
			notifyPlayUrlDone(false, mOnlineUri);
		}
		
		@Override
		public void onReleaseLock() {
		}
		
		@Override
		public void onError() {
			notifyPlayUrlDone(true, null);
		}
	};
	
	public String getVideoNameOfCi(int ci){
		for(int i = 0; i < mEpisodeList.size(); i++) {
			Episode episode = mEpisodeList.get(i);
			if(episode instanceof OnlineEpisode && episode.getCi() == ci) {
				return episode.getName();
			}
		}
		return "";
	}
	
	public interface NotifyBuildSourcesListener{
		public void OnBuildSourcesDown();
	}
	
	private LoadListener mLoadListener = new LoadListener() {
        @Override
        public void onLoadFinish(DataLoader loader) {
            mMediaDetailInfo2 = mDetailLoader.getDetailInfo();
            if(mMediaDetailInfo2 != null) {
                mMediaSetInfoList = mMediaDetailInfo2.mediaciinfo;
                if(mMediaSetInfoList != null) {
                    mMediaSetStyle = mMediaSetInfoList.style;
                }
                getPlayUrl();
            }
        }
        
        @Override
        public void onLoadFail(DataLoader loader) {
            notifyPlayUrlDone(true, null);
        }
    };
    
}
