package com.miui.video.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.miui.video.DKApp;
import com.miui.video.controller.CancelableRequestor;
import com.miui.video.controller.MediaConfig;
import com.miui.video.datasupply.InformationListSupply;
import com.miui.video.datasupply.InformationListSupply.InfoRecommendListListener;
import com.miui.video.datasupply.InformationListSupply.InformationDataListDetail;
import com.miui.video.datasupply.InformationListSupply.InformationDataListListener;
import com.miui.video.datasupply.InformationListSupply.InformationDataRecommendedList;
import com.miui.video.type.InformationData;
import com.miui.video.util.PlayUrlLoader;
import com.miui.video.util.Util;
import com.miui.videoplayer.model.Episode;
import com.miui.videoplayer.model.UriLoader;

public class InfoChannelDataManager extends UriLoader{
	// data from network
    
	private HashMap<Integer, InformationDataListDetail> mInformationDataListMap;
	private InformationDataRecommendedList mInfoRecommendList;
	private int mCurChannelId;
//	private List<InfoEpisode> mInfoEpisodeList = new ArrayList<InfoEpisode>();
	private int mEntryMediaId;
	private int mCurrentPlayPosition = 0;
	private InformationData mEntryInfo;

	// data supply
	private InformationListSupply mInformationListSupply;
	
	private ArrayList<InfoDataChangeListener> mInfoDataListener = new ArrayList<InfoChannelDataManager.InfoDataChangeListener>();

	private SparseArray<String> mPlayUrlMap = new SparseArray<String>();
	
	private InfoPlayListener mInfoPlayListener;
	
	public InfoChannelDataManager(){
		mInformationListSupply = new InformationListSupply();
		mInformationListSupply.addListener(mInformationListListener);
		mInformationListSupply.addListener(mInfoRecommendListListener);
	}
	
	public void addInfoDataChangeListener(InfoDataChangeListener listener){
		if(listener != null && !mInfoDataListener.contains(listener)){
			mInfoDataListener.add(listener);
		}
	}
	
	public void removeDataChangeListener(InfoDataChangeListener listener){
		if(listener != null){
			mInfoDataListener.remove(listener);
		}
	}
	
	public void setInfoPlayListener(InfoPlayListener listener){
		mInfoPlayListener = listener;
	}
	
	public void setChannelId(int channelId){
	    mCurChannelId = channelId;
	}
	
	public void setInfoDataPlay(InformationData infoData){
		mCurrentPlayPosition = 0;
		mEntryInfo = infoData;
		if(mEntryInfo != null){
		    mEntryMediaId = mEntryInfo.mediaid;
		}
	}
	
	public void getInformationList() {
		InformationDataListDetail curInformationDataList = getCurInformationDataList();
		if (curInformationDataList != null
				&& curInformationDataList.medialist.size() > 0) {
			for(InfoDataChangeListener listener : mInfoDataListener){
				listener.refreshListView(false);
			}
			if(curInformationDataList.canLoadMore){
				doGetInfoList();
			}
		} else {
			if (curInformationDataList == null
					|| curInformationDataList.medialist.size() == 0) {
				for(InfoDataChangeListener listener : mInfoDataListener){
					listener.showLoading();
				}
			}
			doGetInfoList();
		}
	}
	
	public void doGetInfoList(){
		mInformationListSupply.getInformationList(mCurChannelId, "");
	}
	
	public void doGetInfoRecommendList(){
		mInformationListSupply.getInfoRecommendList(mEntryMediaId, mCurChannelId, "");
	}
	
	public InformationDataRecommendedList getRecommendedInfoList() {
		return mInfoRecommendList;
	}
	
	public InformationDataListDetail getCurInformationDataList() {
		if (mInformationDataListMap != null) {
			return mInformationDataListMap.get(mCurChannelId);
		}
		return null;
	}
	
	private InformationDataListListener mInformationListListener = new InformationDataListListener() {

		@Override
		public void onInformationDataListDone(
				HashMap<Integer, InformationDataListDetail> informationDataListMap,
				boolean isError) {
			mInformationDataListMap = informationDataListMap;
			for(InfoDataChangeListener listener : mInfoDataListener){
				listener.refreshListView(isError);
			}
		}
	};
	
	private InfoRecommendListListener mInfoRecommendListListener = new InfoRecommendListListener() {
		@Override
		public void onInfoRecommendListDone(InformationDataListDetail infoDetails,
				boolean isError) {
			mInfoRecommendList = new InformationDataRecommendedList();
			mInfoRecommendList.mInfoData = mEntryInfo;
			mInfoRecommendList.mRecommendedList = infoDetails;
			for(InfoDataChangeListener listener : mInfoDataListener){
				listener.refreshListView(isError);
			}
		}
	};

	
	public interface InfoDataChangeListener{
		public void refreshListView(boolean isError);
		public void showLoading();
		public void setSelection(int position);
	}
	
	public int getSelectionPosition(){
		return mCurrentPlayPosition;
	}
	
//	public void playNext(){
//		mAutoPlayP++;
//		for(InfoDataChangeListener listener : mInfoDataListener){
//			listener.setSelection(mAutoPlayP);
//		}
//	}
	
//	public void playVideo(int position, boolean autoPlay){
//		if(mInfoChannelPlayListener != null){
//			mAutoPlayP = autoPlay ? -1 : position;
//			mInfoChannelPlayListener.play(generateItemIntent(position, autoPlay), autoPlay);
//			for(InfoDataChangeListener listener : mInfoDataListener){
//				listener.setSelection(mAutoPlayP);
//			}
//		}
//	}
	
//	public void playVideo(InformationData infodata){
//		if(mInfoChannelPlayListener != null){
//			ArrayList<Intent> intents = new ArrayList<Intent>();
//			intents.add(generateItemIntent(infodata));
//			intents.addAll(generateItemIntent(0));
//			mInfoChannelPlayListener.play(intents, true);
////			for(InfoDataChangeListener listener : mInfoDataListener){
////				listener.setSelection(mAutoPlayP);
////			}
//		}
//	}
	
//	private List<Intent> generateItemIntent(int position){
//		ArrayList<Intent> intents = new ArrayList<Intent>();
//		if(mInfoRecommendList != null && mInfoRecommendList.medialist != null){
//			Object[] informations = mInfoRecommendList.medialist.toArray();
//			for(int i = position; i < informations.length; i ++){
//				if (informations[i] instanceof InformationData) {
//					InformationData infodata = (InformationData) informations[i];
//					if(Util.playBySdk(infodata.sdkinfo2, infodata.sdkdisable, 
//							infodata.source, MediaConfig.MEDIA_TYPE_SHORT)){
//						intents.add(generateItemIntent(infodata));
//					}
//				}
//			}
//		}
//		return intents;
//	}
//	
//	private List<Intent> generateItemIntent(int position, boolean autoPlay){
//		ArrayList<Intent> intents = new ArrayList<Intent>();
//		if(autoPlay){
//			InformationDataListDetail curInformationDataList = mInformationDataListMap.get(mCurChannelId);
//			if(curInformationDataList != null && curInformationDataList.medialist != null){
//				Object[] informations = curInformationDataList.medialist.toArray();
//				if (informations[position] instanceof InformationData) {
//					InformationData infodata = (InformationData) informations[position];
//					if(Util.playBySdk(infodata.sdkinfo2, infodata.sdkdisable, 
//							infodata.source, MediaConfig.MEDIA_TYPE_SHORT)){
//						intents.add(generateItemIntent(infodata));
//					}
//				}
//			}
//		}
//		intents.addAll(generateItemIntent(position));
//		return intents;
//	}
//	
//	private Intent generateItemIntent(InformationData infodata){
//		Intent intent = new Intent();
////		Uri uri = Uri.parse(infodata.playurl);
//		intent.putExtra(DBUtil.KEY_MEDIA_SDKINFO, infodata.sdkinfo2);
//		intent.putExtra(DBUtil.KEY_MEDIA_SDKDISABLE, infodata.sdkdisable);
////		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		if (infodata != null) {
//			intent.putExtra(DBUtil.MEDIA_ID, infodata.mediaid);
//			intent.putExtra(DBUtil.MULTI_SET, false);
//			intent.putExtra(DBUtil.MEDIA_POSTER_URL, infodata.posterurl);
//			intent.putExtra("mediaTitle", infodata.medianame);
//		}
//		intent.putExtra(DBUtil.CURRENT_EPISODE, 0);
//		intent.putExtra(DBUtil.PLAY_INDEX, -1);
//		intent.putExtra(DBUtil.MEDIA_CLARITY, infodata.resolution);
//		intent.putExtra(DBUtil.MEDIA_SOURCE, infodata.source);
//		intent.putExtra(DBUtil.MEDIA_HTML5_URL, infodata.playurl);
//		intent.putExtra(DBUtil.MEDIA_SET_STYLE, MediaConstantsDef.MEDIA_TYPE_VARIETY);
//		intent.putExtra(DBUtil.VIDEO_TYPE, MediaConfig.MEDIA_TYPE_SHORT);
//		return intent;
//	}

    @Override
    public String getTitle() {
        InformationData info = getInfoDataByCi(mCurrentPlayPosition);
        if(info != null){
            return info.medianame;
        }
        return "";
    }
    
    private InformationData getInfoDataByCi(int ci){
        if(ci == 0){
            return mEntryInfo;
        }else{
            if(mInfoRecommendList != null && mInfoRecommendList.mRecommendedList != null){
                List<InformationData> list = mInfoRecommendList.mRecommendedList.medialist;
                if(list != null && ci >= 1 && ci <= list.size()){
                    return list.get(ci - 1);
                }
            }
        }
        return null;
    }

    @Override
    public void loadEpisode(int episode, OnUriLoadedListener uriListener) {
        InformationData infoData = getInfoDataByCi(episode);
        if(infoData != null){
            mCurrentPlayPosition = episode;
            getPlayUrl(infoData, episode, uriListener);
            notifyPlayPositionChanged();
        }
    }

    @Override
    public boolean hasNext() {
        return getInfoDataByCi(mCurrentPlayPosition + 1) != null;
    }

    @Override
    public boolean canSelectCi() {
        return getInfoDataByCi(1) != null;
    }

    @Override
    public int next(OnUriLoadedListener uriListener) {
        mCurrentPlayPosition =  mCurrentPlayPosition+ 1;
        InformationData infoData = getInfoDataByCi(mCurrentPlayPosition);
        getPlayUrl(infoData, mCurrentPlayPosition, uriListener);
        notifyPlayPositionChanged();
        return mCurrentPlayPosition;
    }

    @Override
    public void cancel() {
    }

    @Override
    public List<Episode> getEpisodeList() {
        return null;
    }
    
    private void notifyPlayPositionChanged(){
        if(mInfoPlayListener != null){
            InformationData infoData = getInfoDataByCi(mCurrentPlayPosition);
            if(infoData != null){
                mInfoPlayListener.onPlayInfoData(mCurrentPlayPosition, infoData);
            }
        }
    }
    
    private void getPlayUrl(InformationData infoData, int ci, OnUriLoadedListener uriListener){
        if(infoData != null){
            if(Util.playBySdk(infoData.sdkinfo2, infoData.sdkdisable, infoData.source,
                    MediaConfig.MEDIA_TYPE_SHORT)){
                if(uriListener != null){
                    uriListener.onUriLoaded(ci, InfoPlayUtil.buildUri(infoData, ci, infoData.playurl));
                }
            }else{
                String playUrl = mPlayUrlMap.get(infoData.mediaid, null);
                if(TextUtils.isEmpty(playUrl)){
                    new PlayUrlRequest(ci, infoData, uriListener).start();
                }else{
                    if(uriListener != null){
                        uriListener.onUriLoaded(ci, InfoPlayUtil.buildUri(infoData, ci, 
                                mPlayUrlMap.get(infoData.mediaid)));
                    }
                }
            }
        }
    }

    @Override
    public String getVideoNameOfCi(int ci) {
        InformationData infoData = getInfoDataByCi(ci);
        if(infoData != null){
            return infoData.medianame;
        }
        return "";
    }
    
    private void onUrlReady(int ci, InformationData infoData, String playUrl, 
            OnUriLoadedListener listener){
        if(infoData != null){
            InformationData curInfoData = getInfoDataByCi(mCurrentPlayPosition);
            if(listener != null && curInfoData != null && curInfoData.mediaid == infoData.mediaid){
                if(!TextUtils.isEmpty(playUrl)){
                    if(mPlayUrlMap.get(infoData.mediaid, null) == null){
                        listener.onUriLoaded(ci, InfoPlayUtil.buildUri(infoData, ci, playUrl));
                        mPlayUrlMap.put(infoData.mediaid, playUrl);
                    }
                }else{
                    listener.onUriLoadError(-1);
                }
            }else{
                if(!TextUtils.isEmpty(playUrl)){
                    mPlayUrlMap.put(infoData.mediaid, playUrl);
                }
            }
        }
    }

    public interface InfoPlayListener{
        public void onPlayInfoData(int position, InformationData infoData);
    }
    
    private class PlayUrlRequest extends CancelableRequestor{
        
        private InformationData mInfoData;
        private int mCi;
        private String mPlayUrl;
        private OnUriLoadedListener mUriLoadedListener;
        
        public PlayUrlRequest(int ci, InformationData infoData, OnUriLoadedListener listener){
            mInfoData = infoData;
            mUriLoadedListener = listener;
            mCi = ci;
        }
        
        @Override
        protected void onDoRequest() {
            Context context = DKApp.getAppContext();
            if(context != null && mInfoData != null){
                if(mInfoData.playType == MediaConfig.PLAY_TYPE_DIRECT){
                    mPlayUrl = mInfoData.playurl;
                }else{
                    PlayUrlLoader mUrlLoader = new PlayUrlLoader(context, mInfoData.playurl, 
                            mInfoData.source);
                    mPlayUrl = mUrlLoader.get(30000);
                }
            }
        }

        @Override
        protected void onPostRequest() {
            super.onPostRequest();
            onUrlReady(mCi, mInfoData, mPlayUrl, mUriLoadedListener);
        }
        
    }
    
}
