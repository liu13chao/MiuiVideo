/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  BannerLoader.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-11-25
 */
package com.miui.video.model.loader;

import java.util.ArrayList;

import com.miui.video.api.DKApi;
import com.miui.video.model.DataStore;
import com.miui.video.response.BannerListResponse;
import com.miui.video.statistic.BannerListStatisticInfo;
import com.miui.video.type.Banner;
import com.miui.video.type.BannerList;
import com.miui.video.type.TelevisionInfo;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;


/**
 * @author tianli
 *
 */
public class BannerLoader extends DataLoader implements Observer {

	private int mChannelId = 0;
	private DataStore mDataStore;
	private Banner[] mBanners;
	private TelevisionInfo[] mTvBanners;	
	private String mEntry;
	private String mSearchRecommend[];
	
	public BannerLoader(int channelId, String entry) {
		mDataStore = DataStore.getInstance();
		mChannelId = channelId;
		mEntry = entry;
	}

	@Override
	public void load() {
		(new AsyncLoadTask()).start();
	}
	
	public ArrayList<Object> getBanners() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		if(mBanners != null) {
			for(int i = 0; i < mBanners.length; i++) {
				if(mBanners[i] != null) {
					arrayList.add(mBanners[i]);
				}
			}
		}
		if(mTvBanners != null) {
			for(int i = 0; i < mTvBanners.length; i++) {
				if(mTvBanners[i] != null) {
					arrayList.add(mTvBanners[i]);
				}
			}
		}
		return arrayList;
	}
	
	public Banner[] getMediaBanners() {
		return mBanners;
	}
	
	public TelevisionInfo[] getTvBanners() {
		return mTvBanners;
	}
	
	public int getTotalCount(){
		int count = 0;
		if(mBanners != null){
			count += mBanners.length;
		}
		if(mTvBanners != null){
			count += mTvBanners.length;
		}
		return count;
	}
	
	public String[] getSearchKeywords() {
		return mSearchRecommend;
	}
	
	private void getBannerList() {
		BannerListStatisticInfo statisticInfo = new BannerListStatisticInfo();
		statisticInfo.cateogry = mEntry;
		DKApi.getBannerList(mChannelId, statisticInfo.formatToJson(), this);
	}
	
    @Override
    public void doStorageLoad() {
        BannerList bannerList = mDataStore.loadBannerList(mChannelId);
        if(bannerList != null) {
            mBanners = bannerList.banners;
            mTvBanners = bannerList.tvInfos;
            mSearchRecommend = bannerList.searchKeyWords;
        }
    }

    @Override
    public void onPostStorageLoad() {
        if(mBanners != null) {
            if(mDataStore.isBannerListExpired(mChannelId)) {
                getBannerList();
            }else{
                notifyDataReady();
            }
        } else {
            getBannerList();
        }
    }

	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	}

	@Override
	public void onRequestCompleted(ServiceRequest request, ServiceResponse response) {
		if(response.isSuccessful()) {
			BannerListResponse myResponse = (BannerListResponse) response;
			final BannerList list = myResponse.bannerList;
			mSearchRecommend = list.searchKeyWords;
			mBanners = list.banners;
			mTvBanners = list.tvInfos;
			new Thread(new Runnable() {
				@Override
				public void run() {
					mDataStore.saveBannerList(mChannelId, list);
				}
			}).start();
			notifyDataReady();
		} else {
			notifyDataFail();
		}
	}
}
