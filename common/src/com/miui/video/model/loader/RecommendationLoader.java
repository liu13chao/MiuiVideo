/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  RecommendationLoader.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-11-25
 */
package com.miui.video.model.loader;

import java.util.ArrayList;
import java.util.List;

import android.os.Build;
import android.util.Log;

import com.miui.video.DKApp;
import com.miui.video.api.DKApi;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.model.DataStore;
import com.miui.video.response.ChannelRecommendationResponse;
import com.miui.video.statistic.ChannelMediaInfoListTypeDef;
import com.miui.video.statistic.GetChannelMediaListStatisticInfo;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.Channel;
import com.miui.video.type.ChannelRecommendation;
import com.miui.video.type.ChannelRecommendationTab;
import com.miui.video.type.MediaInfoQuery;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.util.DKLog;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 * @author tianli
 *
 */
public class RecommendationLoader extends DataLoader {
	
	public static final String TAG = "RecommendationLoader";
	
	/* Data */
//	private Channel[] mChannels = null;
//	private ArrayList<Channel> mOrderedChannels = new ArrayList<Channel>(); 
	private ChannelRecommendation[] mRecommendations;
//	private HashMap<Channel, ChannelRecommendation> mGroupData = new HashMap<Channel, ChannelRecommendation>();
//	private TelevisionInfo[] mRecommendationTvInfos;
	
	private int mChannelId = -1;
	 private ArrayList<ChannelRecommendation> mOuterRecommendations = new ArrayList<ChannelRecommendation>(); 
	
	private DataStore mDataStore;
	
//	public RecommendationLoader(){
//	    this(-1);
//	}
	
	public RecommendationLoader(int channelId){
	    mChannelId = channelId;
        mDataStore = DataStore.getInstance();
        DKApp.getSingleton(ChannelInfoStore.class);
    }

	@Override
	public void load() {
		/*  check channel list. */
//		mChannels = mDataStore.loadChannelList();
//		if(mChannels != null && mChannels.length > 0){
//			if(mDataStore.isChannelsExpired()) {
//				getChannelList();
//			} else {
				loadRecommendations();
//			}
//		} else{
//			getChannelList();
//		}
	}
	
//	public BaseMediaInfo[] getRecommendation(Channel channel){
//		ChannelRecommendation recommendation = getChannelRecommendation(channel);
//		if(recommendation != null){
//			return recommendation.getRecommendMedias();
//		}
//		return null;
//	}

//	public BaseMediaInfo[] getRecommendation(Channel channel, int tabIndex){
//	    ChannelRecommendation recommendation = getChannelRecommendation(channel);
//	    if(recommendation != null && recommendation.data != null){
//	        if(tabIndex >= 0 && tabIndex < recommendation.data.length){
//                return recommendation.data[tabIndex].getRecommendMedias(); 
//	        }
//	    }
//	    return null;
//	}

//    public List<String> getChannelRecommendTabs(Channel channel){
//        List<String> names = new ArrayList<String>();
//        if(channel != null){
//            ChannelRecommendation recommend = getChannelRecommendation(channel);
//            if(recommend != null && recommend.data != null){
//                for(ChannelRecommendationTab tab : recommend.data){
//                    names.add(tab.getTabName());
//                }
//            }
//        }
//        return names;
//    }
	
//	public List<String> getChannelRecommendTabs(int channelId){
//	    return getChannelRecommendTabs(getChannel(channelId));
//	}
	
//	synchronized private ChannelRecommendation getChannelRecommendation(Channel channel){
//		if(mGroupData.containsKey(channel)){
//			return mGroupData.get(channel);
//		}
//		return null;
//	}
	
	public ArrayList<ChannelRecommendation> getRecommendations(){
	    return mOuterRecommendations;
	}
	
	public List<String> getChannelRecommendTabs(ChannelRecommendation recommend){
	    List<String> names = new ArrayList<String>();
	    if(recommend != null && recommend.data != null){
	        for(ChannelRecommendationTab tab : recommend.data){
	            names.add(tab.getTabName());
	        }
	    }
	    return names;
	}
	
	public BaseMediaInfo[] getRecommendationMedia(ChannelRecommendation recommend, int tabIndex){
	    if(recommend != null && recommend.data != null){
	        if(tabIndex >= 0 && tabIndex < recommend.data.length){
	            return recommend.data[tabIndex].getRecommendMedias(); 
	        }
	    }
	    return null;
	}
	
//	synchronized public ArrayList<Channel> getChannels(){
//		ArrayList<Channel> channels = new ArrayList<Channel>();
//		channels.addAll(mOrderedChannels);
//		return channels;
//	}
	
//	public boolean isManual(Channel channel){
//		ChannelRecommendation recommendation = getChannelRecommendation(channel);
//		if(recommendation != null){
//			return recommendation.manual == 1;
//		}
//		return false;
//	}
	
	public TelevisionInfo[] getTvRecommendation(){
	    if(mRecommendations != null){
	        try{
	            for(ChannelRecommendation recommend : mRecommendations){
	                if(recommend != null && Channel.isTvChannel(recommend.id)){
	                    if(recommend.data != null && recommend.data.length > 0){
	                        return (TelevisionInfo[])recommend.data[0].getRecommendMedias();
	                    }
	                }
	            }
	        }catch(Exception e){
	        }
	    }
	    return null;
	}
	
	private void loadRecommendations(){
		new AsyncLoadTask().start();
	}
	
//	private void getChannelList(){
//		DKApi.getChannelList(-1, mChannelObserver);
//	}
	
	private void requestChannelRecommendation() {
		MediaInfoQuery q = new MediaInfoQuery();
		q.ids = new int[1];
		q.ids[0] = mChannelId;
		if(mChannelId > 0){
		      q.statisticInfo = prepareChoiceStatisticInfo();
		}
		DKApi.getChannelRecommendation(q, false, mRecommendObserver);
	}
	
	@Override
    public void onPreStorageLoad() {
    }

    @Override
    public void doStorageLoad() {
        Log.d(TAG, "load recommendation, channel id = " + mChannelId);
        mRecommendations = mDataStore.loadChannelRecommendationList(mChannelId);
    }

    @Override
    public void onPostStorageLoad() {
        if(mRecommendations != null) {
            if(mDataStore.isRecommendationsExpired(mChannelId)){
                requestChannelRecommendation();
            }else{
                prepareRecommendationOfChannels();
                notifyDataReady();
            }
        } else {
            requestChannelRecommendation();
        }
    }

//	public Observer mChannelObserver = new Observer() {
//		@Override
//		public void onRequestCompleted(ServiceRequest request, ServiceResponse response) {
//			if(response.isSuccessful()){
//				ChannelListResponse myResponse = (ChannelListResponse) response;
//				mChannels = myResponse.data;
//				if(mChannels != null && mChannels.length > 0){
//					mDataStore.saveChannelList(mChannels);
//					requestChannelRecommendation();
//				}
//			}
//		}
//		@Override
//		public void onProgressUpdate(ServiceRequest arg0, int arg1) {
//		}
//	};
	
	public Observer mRecommendObserver = new Observer() {
		@Override
		public void onRequestCompleted(ServiceRequest request, ServiceResponse response) {
			if(response.isSuccessful()){
				ChannelRecommendationResponse myResponse = (ChannelRecommendationResponse) response;
				mRecommendations = myResponse.data;
				if(mRecommendations != null){
					prepareRecommendationOfChannels();
					new Thread(new Runnable() {
						@Override
						public void run() {
							mDataStore.saveChannelRecommendationList(mChannelId, mRecommendations);
						}
					}).start();
					notifyDataReady();
				}
			}else{
				notifyDataFail();
			}
		}
		@Override
		public void onProgressUpdate(ServiceRequest request, int progress) {
		}
	};
	
//	private Channel getChannel(int channelId){
//	    if(mChannels != null){
//	        for(Channel channel : mChannels){
//	            if(channel != null && channel.id == channelId){
//	                return channel;
//	            }
//	        }
//	    }
//	    return null;
//	}
	
	private  void prepareRecommendationOfChannels() {
		DKLog.d(TAG, "prepare channel recommendation start");
//		mOrderedChannels.clear();
//		mGroupData.clear();
		ArrayList<ChannelRecommendation> list = new ArrayList<ChannelRecommendation>();
		if(mRecommendations != null) {
			for (int i = 0; i < mRecommendations.length; i++) {
				if (mRecommendations[i] != null && mRecommendations[i].getRecommendTabCount() > 0){
				    if(!mRecommendations[i].isTvChannel() || !("ferrari".equalsIgnoreCase(Build.MODEL))){
	                    list.add(mRecommendations[i]);
				    }
				}
			}
			mOuterRecommendations = list;
		}
		DKLog.d(TAG, "prepare channel recommendation end");
	}
	
	//statistic
    private String prepareChoiceStatisticInfo() {
         GetChannelMediaListStatisticInfo  getChannelMediaListStatisticInfo = new GetChannelMediaListStatisticInfo();
         getChannelMediaListStatisticInfo.categoryId = getCategoryId();
         getChannelMediaListStatisticInfo.listType = ChannelMediaInfoListTypeDef.LIST_HOT_TYPE_CODE;
        return getChannelMediaListStatisticInfo.formatToJson();
    }
    
    private String getCategoryId() {
        Channel channel = DKApp.getSingleton(ChannelInfoStore.class).getChannel(mChannelId);
        if(channel != null) {
            StringBuilder categoryId = new StringBuilder();
            categoryId.append(channel.name);
            categoryId.append("(");
            categoryId.append(channel.id);
            categoryId.append(")");
            return categoryId.toString();
        }
        return "";
    }

}