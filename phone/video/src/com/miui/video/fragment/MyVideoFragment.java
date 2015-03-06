package com.miui.video.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import miui.content.ExtraIntent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.miui.video.DKApp;
import com.miui.video.FavoriteActivity;
import com.miui.video.HistoryActivity;
import com.miui.video.LocalMediaActivity;
import com.miui.video.OfflineMediaActivity;
import com.miui.video.R;
import com.miui.video.SettingActivity;
import com.miui.video.ShareDeviceActivity;
import com.miui.video.adapter.MyVideoAdapter;
import com.miui.video.adapter.MyVideoItem;
import com.miui.video.addon.AddonActivity;
import com.miui.video.base.BaseFragment;
import com.miui.video.datasupply.NickNameInfoSupply;
import com.miui.video.datasupply.NickNameInfoSupply.NickNameInfoListener;
import com.miui.video.local.Favorite;
import com.miui.video.local.FavoriteManager;
import com.miui.video.local.FavoriteManager.OnFavoriteChangedListener;
import com.miui.video.local.PlayHistory;
import com.miui.video.local.PlayHistoryManager;
import com.miui.video.local.PlayHistoryManager.OnHistoryChangedListener;
import com.miui.video.model.AppConfig;
import com.miui.video.model.AuthenticateAccountManager;
import com.miui.video.model.LoginManager;
import com.miui.video.model.UserManager.UserAccount;
import com.miui.video.model.loader.LocalMediaLoader;
import com.miui.video.model.loader.LocalMediaLoader.OnLocalMediaLoadListener;
import com.miui.video.offline.OfflineMediaManager;
import com.miui.video.offline.OfflineMediaManager.OfflineMediasCountChangeListener;
import com.miui.video.storage.BaseDevice;
import com.miui.video.storage.DLNAMediaManager;
import com.miui.video.storage.DeviceManager;
import com.miui.video.storage.DeviceManager.DeviceObserver;
import com.miui.video.type.LocalMedia;
import com.miui.video.type.LocalMediaList;
import com.miui.video.type.UserNickNameInfo;
import com.miui.video.util.StringUtils;
import com.miui.video.widget.UserHeadView;
import com.miui.video.widget.media.MyVideoView;
import com.miui.video.widget.media.MyVideoView.OnMyVideoClickListener;


/**
 *@author tangfuling
 *
 */

public class MyVideoFragment extends BaseFragment {
	
	private Activity mContext;
	
	public static final int TAG_LOCAL_MEDIA = 0;
	public static final int TAG_MY_FAVORITE = 1;
	public static final int TAG_MY_OFFLINE = 2;
	public static final int TAG_PLAY_HIS = 3;
	public static final int TAG_SHARE_DEVICE = 4;
	public static final int TAG_ADDON = 5;
	public static final int TAG_SETTING = 6;
	
	//UI
	private View mContentView;
	private UserHeadView mUserHeadView;
//	private LoadingListView mLoadingListView;
	private ListView mListView;
	private MyVideoAdapter mAdapter;
	
	private List<MyVideoItem> mMyVideoItems = new LinkedList<MyVideoItem>();
	private MyVideoItem mLocalMediaItem;
	private MyVideoItem mMyFavoriteItem;
	private MyVideoItem mMyOfflineItem;
	private MyVideoItem mPlayHistoryItem;
	private MyVideoItem mShareDeviceItem;
	private MyVideoItem mAddonItem;
	private MyVideoItem mSettingItem;
	
	//data from net
	private UserAccount mUserAccount;
	private HashMap<String, UserNickNameInfo> mNickNameInfoMap;
	private List<Object> mAllDevices = new ArrayList<Object>();
//	private int mAddonCount;
//	private int mAddonInstalledCount;
	
	//data from local
	private List<Object> mLocalMedias = new ArrayList<Object>();
	private List<Favorite> mFavoriteMedias = new ArrayList<Favorite>();
//	private List<OfflineMedia> mFinishedOfflineMedias = new ArrayList<OfflineMedia>();
//	private List<OfflineMedia> mUnfinishedOfflineMedias = new ArrayList<OfflineMedia>();
	private int mOfflineMediasCount = 0;
	private List<PlayHistory> mPlayHistorys = new ArrayList<PlayHistory>();
	
	//manager
	private AuthenticateAccountManager mAuthenticateAccountManager;
	private LoginManager mLoginManager;

	//data supply
    private NickNameInfoSupply mNickNameInfoSupply;
    
    private LocalMediaLoader mLocalMediaLoader;
    private FavoriteManager mFavoriteManager;
    private OfflineMediaManager mOfflineMediaManager;
    private PlayHistoryManager mPlayHisManager;
    private DeviceManager mDeviceManager;
    private DLNAMediaManager mDLNAMediaManager;
//  private AddonManager mAddonManager;
//  private AddonListSupply mAddonListSupply;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.my_video, container, false);
		return mContentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = (Activity) getActivity();
		init();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refreshData();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mOfflineMediaManager.unregisterMediasCountChangeListener(mOfflineMediasCountListener);
		mFavoriteManager.removeListener(mOnFavoriteChangedListener);
		mPlayHisManager.removeListener(mOnHistoryChangedListener);
		mDeviceManager.removeObserver(mDeviceObserver);
//		mAddonManager.removeListener(mOnAddonChangedListener);
	}
	
	@Override
	public void onSelected() {
		super.onSelected();
	}
	
	//init
	private void init() {
		initManager();
		initDataSupply();
		initUI();
		loadData();
	}
	
	private void initDataSupply() {
		mNickNameInfoSupply = new NickNameInfoSupply();
		mNickNameInfoSupply.addListener(mNickNameInfoListener);
		
		mLocalMediaLoader = LocalMediaLoader.getInstance();
		
		mFavoriteManager = DKApp.getSingleton(FavoriteManager.class);
		mFavoriteManager.addListener(mOnFavoriteChangedListener);
		
		mOfflineMediaManager = DKApp.getSingleton(OfflineMediaManager.class);
		mOfflineMediaManager.registerMediasCountChangeListener(mOfflineMediasCountListener);
		mOfflineMediasCount = mOfflineMediaManager.getOfflineMediaCount();
		
		mPlayHisManager = DKApp.getSingleton(PlayHistoryManager.class);
		mPlayHisManager.addListener(mOnHistoryChangedListener);
		
		mDeviceManager = DKApp.getSingleton(DeviceManager.class);
		mDeviceManager.addObserver(mDeviceObserver);
		mDLNAMediaManager = DKApp.getSingleton(DLNAMediaManager.class);
		
//		mAddonManager = AddonManager.getInstance();
//		mAddonListSupply = new AddonListSupply();
//		mAddonManager.addListener(mOnAddonChangedListener);
//		mAddonListSupply.addListener(mAddonListListener);
	}
	
	private void initUI() {
		initHeadView();
		initMyVideoItems();
		initListView();
		refresh();
	}
	
	private void initHeadView() {
	    mUserHeadView = new UserHeadView(getActivity());
	    mUserHeadView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.my_video_list_margin_top),
	            0, getResources().getDimensionPixelSize(R.dimen.my_video_avatar_padding_bottom));
		mUserHeadView.setOnClickListener(mOnClickListener);
	}
	
	private void initMyVideoItems() {
		mLocalMediaItem = new MyVideoItem();
		mLocalMediaItem.itemName = mContext.getResources().getString(R.string.local_video);
		mLocalMediaItem.itemIconResId = R.drawable.icon_my_video_local;
		mLocalMediaItem.tag = TAG_LOCAL_MEDIA;

		mMyFavoriteItem = new MyVideoItem();
		mMyFavoriteItem.itemName = mContext.getResources().getString(R.string.my_favorite);
		mMyFavoriteItem.itemIconResId = R.drawable.icon_my_video_favorite;
		mMyFavoriteItem.tag = TAG_MY_FAVORITE;
		
		mMyOfflineItem = new MyVideoItem();
		mMyOfflineItem.itemName = mContext.getResources().getString(R.string.my_offline);
		mMyOfflineItem.itemIconResId = R.drawable.icon_my_video_offline;
		mMyOfflineItem.tag = TAG_MY_OFFLINE;
		
		mPlayHistoryItem = new MyVideoItem();
		mPlayHistoryItem.itemName = mContext.getResources().getString(R.string.play_history);
		mPlayHistoryItem.itemIconResId = R.drawable.icon_my_video_play_his;
		mPlayHistoryItem.tag = TAG_PLAY_HIS;
		
		mShareDeviceItem = new MyVideoItem();
		mShareDeviceItem.itemName = mContext.getResources().getString(R.string.share_device);
		mShareDeviceItem.itemIconResId = R.drawable.icon_my_video_share_device;
		mShareDeviceItem.tag = TAG_SHARE_DEVICE;
		
		mAddonItem = new MyVideoItem();
		mAddonItem.itemName = mContext.getResources().getString(R.string.addon);
		mAddonItem.itemIconResId = R.drawable.icon_my_video_addon;
		mAddonItem.tag = TAG_ADDON;
	    
		mSettingItem = new MyVideoItem();
		mSettingItem.itemName = mContext.getResources().getString(R.string.setting);
		mSettingItem.itemIconResId = R.drawable.icon_my_set_up;
		mSettingItem.tag = TAG_SETTING;
	}
	
	private void initListView() {
	    mListView = (ListView) mContentView.findViewById(R.id.my_video_list);
		mAdapter = new MyVideoAdapter(mContext);
		mAdapter.setOnMyVideoClickListener(mOnMyVideoClickListener);
		mListView.setSelector(R.drawable.transparent);
		mListView.setAdapter(mAdapter);
		mListView.addHeaderView(mUserHeadView);
	}
	
	private void refreshData(){
	    refreshAccountInfo();
	    if(mLocalMediaLoader != null){
	          mLocalMediaLoader.getLocalMedias(mOnLocalMediaLoadListener, true);
	    }
	}
	
	private void loadData() {
	    getMyVideoInfo();
    }
	
	private void refreshAccountInfo() {
	    if(mLoginManager != null){
	        mUserAccount = mLoginManager.getUserAccount();
	    }
	    if(mAuthenticateAccountManager == null ||
	            mAuthenticateAccountManager.needAuthenticate()) {
	        mUserAccount = null;
	    } else {
	        if(!hasNickNameInfo()) {
	            if(mUserAccount != null) {
	                List<String> userIds = new ArrayList<String>();
	                userIds.add(mUserAccount.accountName);
	                mNickNameInfoSupply.getNickNameInfo(userIds);
	            }
	        }
	    }
	    refreshUserHeadView();
	}
	
	private void tryAuthAccount(){
	       if(mAuthenticateAccountManager != null && 
	               mAuthenticateAccountManager.needAuthenticate()) {
	           mAuthenticateAccountManager.authAccount();
	       }
	}
	
	private void getMyVideoInfo() {
		mFavoriteManager.loadFavorite();
		mPlayHisManager.loadPlayHistory();
		getDevices();
		refreshAccountInfo();
//		getAddons();
	}
	
	private void getDevices() {
		mAllDevices.clear();
		List<BaseDevice> devices = mDeviceManager.getDevices();
		if(devices != null) {
			mAllDevices.addAll(devices);
		}
		refresh();
		mDeviceManager.scan();
	}
	
//	private void getAddons() {
//		mAddonListSupply.getAddonList(1, "");
//		mAddonManager.load();
//	}
	
	//packaged method
	private void refresh() {
		refreshUserHeadView();
		refreshListView();
	}
	
	private void refreshUserHeadView() {
		UserNickNameInfo userNickNameInfo = getUserAccountInfo();
		mUserHeadView.setUserNickNameInfo(userNickNameInfo);
	}
	
	private void refreshListView() {
	    if(!isAdded()){
            return;
        }
		refreshMyVideoItems();
		mAdapter.setGroup(mMyVideoItems);
	}
	
	private String getCountDesc(int count){
	    String str = mContext.getResources().getString(R.string.gong_count_ge);
	    str = StringUtils.formatString(str, count);
	    return str;
	}
	
	private void refreshMyVideoItems() {
	    mLocalMediaItem.mDesc  = getCountDesc(getMediaTotalCount());
	    mMyFavoriteItem.mDesc = getCountDesc(mFavoriteMedias.size());
	    mMyOfflineItem.mDesc = getCountDesc(getOfflineTotalCount());
	    mPlayHistoryItem.mDesc = getCountDesc(mPlayHistorys.size());
	    mShareDeviceItem.mDesc = getCountDesc(mAllDevices.size());
	    mSettingItem.mDesc = getResources().getString(R.string.system_setting);
		
		mMyVideoItems.clear();
		mMyVideoItems.add(mLocalMediaItem);
		
		AppConfig config = DKApp.getSingleton(AppConfig.class);
		if(config == null || config.isOnlineVideoOn()){
		      mMyVideoItems.add(mMyFavoriteItem);
		      mMyVideoItems.add(mMyOfflineItem);
		}
		mMyVideoItems.add(mPlayHistoryItem);
		if(config == null || config.isOnlineVideoOn()){
	        if(mAllDevices.size() > 0) {
	            mMyVideoItems.add(mShareDeviceItem);
	        }
	        mMyVideoItems.add(mSettingItem);
		}
	}
	
//	private UserNickNameInfo getCurUserNickNameInfo() {
//		if(mNickNameInfoMap != null && mUserAccount != null) {
//			return mNickNameInfoMap.get(mUserAccount.accountName);
//		}
//		return null;
//	}
	
	private boolean hasNickNameInfo() {
	    if(mNickNameInfoMap != null && mUserAccount != null) {
	        return mNickNameInfoMap.get(mUserAccount.accountName) != null;
	    }
	    return false;
	}
	
	private UserNickNameInfo getUserAccountInfo() {
	    if(mNickNameInfoMap != null && mUserAccount != null) {
	        return mNickNameInfoMap.get(mUserAccount.accountName);
	    }
	    try{
	        UserNickNameInfo info = new UserNickNameInfo();
	        if(mUserAccount != null){
	            info.userId = Integer.parseInt(mUserAccount.accountName);  
	            return info;
	        }
	    }catch(Exception e){
	    }
	    return null;
	}
	
	private int getMediaTotalCount() {
		int mediaCount = 0;
		for(int i = 0; i < mLocalMedias.size(); i++) {
			Object obj = mLocalMedias.get(i);
			if(obj instanceof LocalMediaList) {
				LocalMediaList localMediaList = (LocalMediaList) obj;
				mediaCount += localMediaList.size();
			} else if(obj instanceof LocalMedia) {
				mediaCount++;
			}
		}
		return mediaCount;
	}
	
	private int getOfflineTotalCount() {
		return mOfflineMediasCount;
	}
	
	private void startLocalMediaActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, LocalMediaActivity.class);
		startActivity(intent);
	}
	
	private void startMyFavoriteActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, FavoriteActivity.class);
		startActivity(intent);
	}
	
	private void startMyOfflineActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, OfflineMediaActivity.class);
		startActivity(intent);
	}
	
	private void startPlayHistoryActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, HistoryActivity.class);
		startActivity(intent);
	}
	
	private void startShareDeviceActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, ShareDeviceActivity.class);
		startActivity(intent);
	}
	
	private void startAddonActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, AddonActivity.class);
		startActivity(intent);
	}
	
    private void startSettingActivity() {
        Intent intent = new Intent();
        intent.setClass(mContext, SettingActivity.class);
        mContext.startActivity(intent);
    }

	//UI callback
	private OnMyVideoClickListener mOnMyVideoClickListener = new OnMyVideoClickListener() {
		
		@Override
		public void onMyVideoClick(MyVideoView view, MyVideoItem myVideoItem) {
			int tag = myVideoItem.tag;
			if(tag == TAG_LOCAL_MEDIA) {
				startLocalMediaActivity();
			} else if(tag == TAG_MY_FAVORITE) {
				startMyFavoriteActivity();
			} else if(tag == TAG_MY_OFFLINE) {
				startMyOfflineActivity();
			} else if(tag == TAG_PLAY_HIS) {
				startPlayHistoryActivity();
			} else if(tag == TAG_SHARE_DEVICE) {
				startShareDeviceActivity();
			} else if(tag == TAG_ADDON) {
				startAddonActivity();
			} else if(tag == TAG_SETTING) {
			    startSettingActivity();
            }
		}
	};
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mUserHeadView) {
			    if(mAuthenticateAccountManager.needAuthenticate()){
		             tryAuthAccount();
			    }else{
	                  Intent intent = new Intent(ExtraIntent.ACTION_XIAOMI_ACCOUNT_SETTING);
	                     mContext.startActivity(intent);
			    }
			}
		}
	};
	
	//auth callback
	private void initManager() {
		mLoginManager = DKApp.getSingleton(LoginManager.class);
		mAuthenticateAccountManager = new AuthenticateAccountManager(mContext) {
			@Override
			protected void onAuthSuccess() {
			}
			
			@Override
			protected void onAuthNoAccount() {
			}
			
			@Override
			protected void onAuthFailed(String failedReason) {
			}
		};
	}
	
	//data callback
	private NickNameInfoListener mNickNameInfoListener = new NickNameInfoListener() {
		
		@Override
		public void onNickNameInfoDone(
				HashMap<String, UserNickNameInfo> nickNameInfoMap, boolean isError) {
			mNickNameInfoMap = nickNameInfoMap;
			refresh();
		}
	};
	
	private OnLocalMediaLoadListener mOnLocalMediaLoadListener = new OnLocalMediaLoadListener() {
		
		@Override
		public void onLocalMediaDone(ArrayList<LocalMediaList> localMedias) {
			mLocalMedias.clear();
			if(localMedias != null) {
				mLocalMedias.addAll(localMedias);
			}
			refresh();
		}
	};
	
	private OnFavoriteChangedListener mOnFavoriteChangedListener = new OnFavoriteChangedListener() {
		
		@Override
		public void onFavoritesChanged(List<Favorite> favList) {
			mFavoriteMedias.clear();
			if(favList != null) {
				mFavoriteMedias.addAll(favList);
			}
			refresh();
		}
	};
	
	private OfflineMediasCountChangeListener mOfflineMediasCountListener = new OfflineMediasCountChangeListener() {
		@Override
		public void onOfflineMediasCountChange(int num) {
			mOfflineMediasCount = num;
			refresh();
		}
	};
	
	private OnHistoryChangedListener mOnHistoryChangedListener = new OnHistoryChangedListener() {
		
		@Override
		public void onHistoryChanged(List<PlayHistory> historyList) {
			mPlayHistorys.clear();
			if(historyList != null) {
				mPlayHistorys.addAll(historyList);
			}
			refresh();
		}
	};
	
	private DeviceObserver mDeviceObserver = new DeviceObserver() {
		
		@Override
		public void onDeviceRemoved(BaseDevice device) {
			mAllDevices.clear();
			List<BaseDevice> devices = mDeviceManager.getDevices();
			if(devices != null) {
				mAllDevices.addAll(devices);
			}
			refresh();
		}
		
		@Override
		public void onDeviceAdded(BaseDevice device) {
			mDLNAMediaManager.browseDevice(device);
			mAllDevices.clear();
			List<BaseDevice> devices = mDeviceManager.getDevices();
			if(devices != null) {
				mAllDevices.addAll(devices);
			}
			refresh();
		}
	};
	
//	private AddonListListener mAddonListListener = new AddonListListener() {
//		
//		@Override
//		public void onAddonListDone(List<BaseMediaInfo> addonList, int totalCount,
//				boolean isError, boolean canLoadMore) {
//			mAddonCount = totalCount;
//			refresh();
//		}
//	};
//	
//	private OnAddonChangedListener mOnAddonChangedListener = new OnAddonChangedListener() {
//		
//		@Override
//		public void onAddonChanged() {
//			List<AddonInfo> addonInfos = mAddonManager.getAddonList();
//			if(addonInfos != null) {
//				mAddonInstalledCount = addonInfos.size();
//			} else {
//				mAddonInstalledCount = 0;
//			}
//			refresh();
//		}
//	};
}
