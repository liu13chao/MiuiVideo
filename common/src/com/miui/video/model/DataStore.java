/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   DataStore.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-11 
 */
package com.miui.video.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.miui.video.type.AddonInfo;
import com.miui.video.type.Banner;
import com.miui.video.type.BannerList;
import com.miui.video.type.BannerUrlCahceInfo;
import com.miui.video.type.BootResponseInfo;
import com.miui.video.type.Channel;
import com.miui.video.type.ChannelList;
import com.miui.video.type.ChannelRecommendation;
import com.miui.video.type.ChannelRecommendationList;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.type.PlayHistory;
import com.miui.video.type.SourceInfo;
import com.miui.video.type.SpecialSubject;
import com.miui.video.type.SpecialSubjectList;
import com.miui.video.type.SyncResultInfo;
import com.miui.video.type.TvLiveData;
import com.miui.video.util.DKLog;
import com.miui.video.util.ObjectStore;
import com.miui.video.util.Util;
import com.xiaomi.mitv.common.net.HttpClient;
import com.xiaomi.mitv.common.net.HttpRequest;
import com.xiaomi.mitv.common.net.HttpResponse;

/**
 * @author tianli
 * 
 */
public class DataStore {

	private final static String TAG = DataStore.class.getName();

	private final static String IMAGE_PATH = "/images";
	private final static String DATA_PATH = "/data";

	private final static String TIME_CACHE = "/cachetime.cache";

	private final static String BOOT_RESPONSE_INFO_CACHE = "/boot_response_info.cache";
	private final static String SPECIAL_SUBJECT_CACHE = "/special_subject.cache";
	private final static String CHANNEL_LIST_CACHE = "/channel_list.cache";
	private final static String CHANNEL_RECOMMENDATION_LIST_CACHE = "/channel_recommendation.cache";
	private final static String BANNER_LIST_CACHE = "/banner_list.cache";
	private final static String PLAY_HISTORY_CACHE = "/play_history.cache";
	private final static String BANNER_INFO_CACHE = "/banner_info.cache";
	private final static String ADDON_LIST_CACHE = "/addon_list.cache";
	
	private final static String TVLIVE_DATA_PATH = "/tvlivedata";
	private final static String TVLIVE_DATA_CACHE = "/tvlivedata.cache";

	// only store last sync time and whether it's successful to sync or not
	private final static String LAST_SYNC_MY_FAVORITE_CACHE = "/sync_myfavorite.cache";
	private final static String LAST_SYNC_RECENT_PLAYHISTORY_CACHE = "/sync_recentplayhistory.cache";

	private final static int TEN_MINUTES = 10 * 60 * 1000;
	private final static int ONE_DAY = 24 * 3600000;

	private final static long MY_FAVORITE_SYNC_INTERVAL = 6 * TEN_MINUTES;
	private final static long RECENT_PLAYHISTORY_SYNC_INTERVAL = 6 * TEN_MINUTES;

	private static final int NUM_OF_IMAGE_SUBFOLDERS = 37;
	private static final int MAX_FILES_PER_SUBFOLDER = 20;

	// private static final String PNG = ".png";
	// private static final String JPG = ".jpg";

	private static String mImageCacheDir;
	private static String mDataCacheDir;
	private static String mCacheRootDir;
	private static String mTvLiveDataDir;

	private final static DataStore sInstance = new DataStore();
	private int sourceVersion = -1;
	
	private SpecialSubjectList specialSubjectList = new SpecialSubjectList();
	private ChannelList channelList = new ChannelList();
	// private BannerStore bannerStore = new BannerStore();

	// private RecommendationList recommendationList = new RecommendationList();
//	private ChannelRecommendationList channelRecommendationList = new ChannelRecommendationList();
	private Hashtable<String, BannerList> cache = new Hashtable<String, BannerList>();
	private Hashtable<String, ChannelRecommendationList> mRecommendCache = 
	        new Hashtable<String, ChannelRecommendationList>();
	private Hashtable<Integer, BannerUrlCahceInfo> bannerUrlCacheInfoHashtable = new Hashtable<Integer, BannerUrlCahceInfo>();

	private SyncResultInfo myFavoriteSyncResultInfo;
	private byte[] myFavoriteSyncResultMonitor = new byte[0];
	private SyncResultInfo recentPlayHistorySyncResultInfo;
	private byte[] recentPlayHistorySyncResultMonitor = new byte[0];

	private CacheTime mCacheTime = new CacheTime();
	private SourceInfo[] mSourceInfos;
	private ImageManager mImageManager = ImageManager.getInstance();

	public static DataStore getInstance() {
		return sInstance;
	}

	private DataStore() {
		prepareDirs();
		loadCacheTime();
		loadData();
	}

	private void loadCacheTime() {
		String fullPath = mDataCacheDir + TIME_CACHE;
		Object object = ObjectStore.readObject(fullPath);
		if (object != null && object instanceof CacheTime) {
			mCacheTime = (CacheTime) object;
		}
	}
	
	public boolean isSpecialSubjectExpired() {
		synchronized (mCacheTime) {
			if (System.currentTimeMillis() - mCacheTime.specialSubjectTime < ONE_DAY) {
				return false;
			}
			return true;
		}
	}
	
//	public boolean isChannelRecommendationListExpired() {
//		synchronized (mCacheTime) {
//			if (System.currentTimeMillis() - mCacheTime.homeRecommendationTime < TEN_MINUTES) {
//				return false;
//			}
//			return true;
//		}
//	}

	public boolean isChannelsExpired() {
		synchronized (mCacheTime) {
			if (System.currentTimeMillis() - mCacheTime.channelTime < ONE_DAY) {
				return false;
			}
			return true;
		}
	}

	public boolean isRecommendationsExpired(int channelId) {
		long ms = System.currentTimeMillis();
		synchronized (mCacheTime) {
			if (mCacheTime.recommendTime.containsKey(channelId)) {
				Long cacheTime = mCacheTime.recommendTime.get(channelId);
				if (cacheTime != null && ms - cacheTime < TEN_MINUTES) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * @param channelId
	 *            channel id, 0 means home page
	 * 
	 * @return the expired time is three hours.
	 */
	public boolean isBannerListExpired(int channelId) {
		long ms = System.currentTimeMillis();
		synchronized (mCacheTime) {
			if (mCacheTime.bannerTime.containsKey(channelId)) {
				Long cacheTime = mCacheTime.bannerTime.get(channelId);
				if (cacheTime != null && ms - cacheTime < TEN_MINUTES) {
					return false;
				}
			}
			return true;
		}
	}

	private void loadData() {
		// loadToken();
		loadRecentPlayHistorySyncResultInfo();
		loadMyFavoriteSyncResultInfo();
		mSourceInfos = loadSourceInfo();
	}

	// public void saveData() {
	// saveChannelList(channelList.channels);
	// }

	private void prepareDirs() {
		mCacheRootDir = CacheConfig.getCacheRootDir();
		
		mDataCacheDir = mCacheRootDir + DATA_PATH;
		File file = new File(mDataCacheDir);
		if (!file.exists()) {
			file.mkdir();
		}
		mImageCacheDir = mCacheRootDir + IMAGE_PATH;
		file = new File(mImageCacheDir);
		if (!file.exists()) {
			file.mkdir();
		}
		mTvLiveDataDir = mCacheRootDir + TVLIVE_DATA_PATH;
		file = new File(mTvLiveDataDir);
		if (!file.exists()) {
			file.mkdir();
		}
		for (int i = 0; i < NUM_OF_IMAGE_SUBFOLDERS; i++) {
			String subdir = mImageCacheDir + i;
			file = new File(subdir);
			if (!file.exists()) {
				file.mkdir();
			}
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public synchronized boolean isTvLiveDataExpired() {
		synchronized (mCacheTime) {
			long nCacheLongTime = mCacheTime.tvLiveTime;
			String nCacheday = new SimpleDateFormat("dd").format(new Date(nCacheLongTime));
			long nCurLongTime = System.currentTimeMillis();
			String nCurday = new SimpleDateFormat("dd").format(new Date(nCurLongTime));
			if (nCacheday.equalsIgnoreCase(nCurday) ) {
				return false;
			}
			return true;
		}
	}

	public synchronized TvLiveData loadTvLiveData() {
		String fullPath = mTvLiveDataDir + TVLIVE_DATA_CACHE;
		Object object = ObjectStore.readObject(fullPath);
		if (object != null && object instanceof TvLiveData) {
			return (TvLiveData)object;
		}
		return null;
	}

	public synchronized void saveTvLiveData(TvLiveData tvLiveData) {
		if (tvLiveData != null) {
			String fullPath = mTvLiveDataDir + TVLIVE_DATA_CACHE;
			ObjectStore.writeObject(fullPath, tvLiveData);
			mCacheTime.tvLiveTime = System.currentTimeMillis();
			saveCacheTime();
		}
	}
	
	public int getSoureceVersion() {
		return sourceVersion;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized Hashtable<Integer, PlayHistory> getPlayHistoryInfo() {
		Hashtable<Integer, PlayHistory> playHistoryInfoCache = null;
		String fullPath = mDataCacheDir + PLAY_HISTORY_CACHE;
		Object object = ObjectStore.readObject(fullPath);
		if (object != null && object instanceof Hashtable<?, ?>)
			playHistoryInfoCache = (Hashtable<Integer, PlayHistory>) object;
		return playHistoryInfoCache;
	}

	public synchronized void savePlayHistoryInfo(
			Hashtable<Integer, PlayHistory> playHistoryInfoCache) {
		if (playHistoryInfoCache == null)
			return;
		String fullPath = mDataCacheDir + PLAY_HISTORY_CACHE;
		ObjectStore.writeObject(fullPath, playHistoryInfoCache);
	}

	public ChannelRecommendation[] loadChannelRecommendationList(int channelId) {
	       // synchronized is used to protect cache file write and read concurrently
        synchronized (mRecommendCache) {
            if (mRecommendCache.containsKey(channelId)) {
                return mRecommendCache.get(channelId).channelRecommendations;
            } else {
                String fullPath = mDataCacheDir + CHANNEL_RECOMMENDATION_LIST_CACHE + "."
                        + channelId;
                Object object = ObjectStore.readObject(fullPath);
                if (object instanceof ChannelRecommendationList) {
                    mRecommendCache.put(channelId + "", ((ChannelRecommendationList) object));
                    return ((ChannelRecommendationList) object).channelRecommendations;
                }
            }
            return null;
        }
	}

	public void saveChannelRecommendationList(int channelId, 
	        ChannelRecommendation[] recommendations) {
		if (recommendations != null) {
			boolean saveSuccess = false;
			synchronized (mRecommendCache) {
			    ChannelRecommendationList list = new ChannelRecommendationList();
			    list.channelRecommendations = recommendations;
			    mRecommendCache.put(channelId + "", list);
			    String fullPath = mDataCacheDir + CHANNEL_RECOMMENDATION_LIST_CACHE + "."
			            + channelId;
			    saveSuccess = ObjectStore.writeObject(fullPath, list);
			}
			if(saveSuccess){
			    synchronized (mCacheTime) {
			        mCacheTime.recommendTime.put(channelId, System.currentTimeMillis());
			    }
			    saveCacheTime();
			}
		}
	}

	// public boolean ChannelListExpired() {
	// synchronized (channelList) {
	// if (channelList.channels == null) {
	// String fullPath = mDataCacheDir + CHANNEL_LIST_CACHE;
	// Object object = ObjectStore.readObject(fullPath);
	// if (object != null && object instanceof ChannelList) {
	// channelList.channels = ((ChannelList) object).channels;
	// channelList.cacheTime = ((ChannelList) object).cacheTime;
	// }
	// }
	// if (System.currentTimeMillis() - channelList.cacheTime < ONE_DAY) {
	// return false;
	// }
	// }
	// return true;
	// }
	
	public SpecialSubject[] loadSpecialSubject() {
		synchronized (specialSubjectList) {
			if (specialSubjectList.subjects == null) {
				String fullPath = mDataCacheDir + SPECIAL_SUBJECT_CACHE;
				Object object = ObjectStore.readObject(fullPath);
				if (object != null && object instanceof SpecialSubjectList) {
					specialSubjectList.subjects = ((SpecialSubjectList) object).subjects;
					specialSubjectList.cacheTime = ((SpecialSubjectList) object).cacheTime;
				}
			}
			return specialSubjectList.subjects;
		}
	}
	
	public void saveSpecialSubject(SpecialSubject[] specialSubjects) {
		boolean saveSuccess = false;
		synchronized (specialSubjectList) {
			if (specialSubjects != null) {
				this.specialSubjectList.subjects = specialSubjects;
				this.specialSubjectList.cacheTime = System.currentTimeMillis();
				String fullPath = mDataCacheDir + SPECIAL_SUBJECT_CACHE;
				saveSuccess = ObjectStore.writeObject(fullPath, specialSubjectList);
			}
		}
		
		if(saveSuccess){
			synchronized (mCacheTime) {
				mCacheTime.specialSubjectTime = System.currentTimeMillis();
			}
			saveCacheTime();
		}
	}
	
	public SourceInfo[] getSourceInfos(){
		if(mSourceInfos == null || mSourceInfos.length == 0){
			return loadSourceInfo();
		}else{
			return mSourceInfos;
		}
	}
	
	public SourceInfo[] loadSourceInfo() {
		BootResponseInfo bootResponseInfo = loadBootResponseInfo();
		if(bootResponseInfo != null){
			SourceInfo[] sourceInfos = bootResponseInfo.sourceinfolist;
			return sourceInfos;
		}
		return null;
	}
	
	public synchronized BootResponseInfo loadBootResponseInfo() {
		BootResponseInfo bootResponseInfo = null;
		String fullPath = mDataCacheDir + BOOT_RESPONSE_INFO_CACHE;
		Object object = ObjectStore.readObject(fullPath);
		if (object != null && object instanceof BootResponseInfo) {
			bootResponseInfo = (BootResponseInfo) object;
		}
		return bootResponseInfo;
	}
	
	public synchronized void saveBootResponseInfo(BootResponseInfo bootResponseInfo) {
		if (bootResponseInfo != null) {
			String fullPath = mDataCacheDir + BOOT_RESPONSE_INFO_CACHE;
			ObjectStore.writeObject(fullPath, bootResponseInfo);
			mSourceInfos = bootResponseInfo.sourceinfolist;
		}
	}

	public Channel[] loadChannelList() {
		synchronized (channelList) {
			if (channelList.channels == null) {
				String fullPath = mDataCacheDir + CHANNEL_LIST_CACHE;
				Object object = ObjectStore.readObject(fullPath);
				if (object != null && object instanceof ChannelList) {
					channelList.channels = ((ChannelList) object).channels;
					channelList.cacheTime = ((ChannelList) object).cacheTime;
				}
			}
			return channelList.channels;
		}
	}

	public void saveChannelList(Channel[] channels) {
		boolean saveSuccess = false;
		synchronized (channelList) {
			if (channels != null) {
				this.channelList.channels = channels;
				this.channelList.cacheTime = System.currentTimeMillis();
				String fullPath = mDataCacheDir + CHANNEL_LIST_CACHE;
				saveSuccess = ObjectStore.writeObject(fullPath, channelList);
			}
		}
		
		if(saveSuccess){
			synchronized (mCacheTime) {
				mCacheTime.channelTime = System.currentTimeMillis();
			}
			saveCacheTime();
		}
	}

	/**
	 * @param channelID
	 *            channel id, 0 means home page
	 * 
	 * @return the banner list of the specific channel
	 */
	public BannerList loadBannerList(int channelID) {
		// synchronized is used to protect cache file write and read concurrently
		synchronized (cache) {
			if (cache.containsKey(channelID)) {
				return cache.get(channelID);
			} else {
				String fullPath = mDataCacheDir + BANNER_LIST_CACHE + "."
						+ channelID;
				Object object = ObjectStore.readObject(fullPath);
				if (object != null && object instanceof BannerList) {
					cache.put(channelID + "", ((BannerList) object));
					return ((BannerList) object);
				}
			}
			return null;
		}
	}

	public void saveBannerList(int channelID, Banner[] banners) {
		if (banners == null) {
			return;
		}
		boolean saveSuccess = false;
		// synchronized is used to protect cache file write and read
		// concurrently
		synchronized (cache) {
			BannerList list = new BannerList();
			list.banners = banners;
			cache.put(channelID + "", list);
			String fullPath = mDataCacheDir + BANNER_LIST_CACHE + "."
					+ channelID;
			saveSuccess = ObjectStore.writeObject(fullPath, list);
		}
		if(saveSuccess){
			synchronized (mCacheTime) {
				mCacheTime.bannerTime.put(channelID, System.currentTimeMillis());
			}
			saveCacheTime();
		}
	}

	public void saveBannerList(int channelID, BannerList bannerList) {
		if (bannerList == null)
			return;
		boolean saveSuccess = false;
		synchronized (cache) {
			cache.put(channelID + "", bannerList);
			String fullPath = mDataCacheDir + BANNER_LIST_CACHE + "."
					+ channelID;
			saveSuccess = ObjectStore.writeObject(fullPath, bannerList);
		}
		if(saveSuccess){
			synchronized (mCacheTime) {
				mCacheTime.bannerTime.put(channelID, System.currentTimeMillis());
			}
			saveCacheTime();
		}
	}
	
	public AddonInfo[] loadAddonList() {
		String fullPath = mCacheRootDir + ADDON_LIST_CACHE;
		Object object = ObjectStore.readObject(fullPath);
		return (AddonInfo[]) object;
	}
	
	public void saveAddonList(AddonInfo[]addonInfos) {
		String fullPath = mCacheRootDir + ADDON_LIST_CACHE;
		ObjectStore.writeObject(fullPath, addonInfos);
	}
	
	public void saveCacheTime() {
		String fullPath = mDataCacheDir + TIME_CACHE;
		ObjectStore.writeObject(fullPath, mCacheTime);
	}
	

	@SuppressWarnings("unchecked")
	public void loadBannerUrlInfo() {
		synchronized (bannerUrlCacheInfoHashtable) {
			bannerUrlCacheInfoHashtable.clear();
			String fullPath = mDataCacheDir + BANNER_INFO_CACHE;
			Object object = ObjectStore.readObject(fullPath);
			if (object != null && object instanceof Hashtable<?, ?>) {
				bannerUrlCacheInfoHashtable = (Hashtable<Integer, BannerUrlCahceInfo>) object;
				boolean infoChanged = false;
				Set<Integer> mediaIdSet = bannerUrlCacheInfoHashtable.keySet();
				for (Iterator<Integer> itr = mediaIdSet.iterator(); itr
						.hasNext();) {
					int mediaId = itr.next();
					BannerUrlCahceInfo bannerUrlCacheInfo = bannerUrlCacheInfoHashtable
							.get(mediaId);
					if (bannerUrlCacheInfo != null) {
						if (System.currentTimeMillis()
								- bannerUrlCacheInfo.cacheTime > ONE_DAY) {
							infoChanged = true;
							itr.remove();
						}
					} else {
						infoChanged = true;
						itr.remove();
					}
				}

				if (infoChanged)
					ObjectStore.writeObject(fullPath,
							bannerUrlCacheInfoHashtable);
			}
		}
	}

	public ImageUrlInfo getBannerUrlInfo(int mediaId) {
		synchronized (bannerUrlCacheInfoHashtable) {
			BannerUrlCahceInfo bannerUrlCacheInfo = bannerUrlCacheInfoHashtable
					.get(mediaId);
			if (bannerUrlCacheInfo != null)
				return bannerUrlCacheInfo.imageUrlInfo;
			else
				return null;
		}
	}

	public void saveBannerUrlInfo(int mediaId, ImageUrlInfo imageUrlInfo) {
		if (mediaId < 0 || imageUrlInfo == null)
			return;

		synchronized (bannerUrlCacheInfoHashtable) {
			BannerUrlCahceInfo bannerUrlCacheInfo = new BannerUrlCahceInfo();
			bannerUrlCacheInfo.cacheTime = System.currentTimeMillis();
			bannerUrlCacheInfo.mediaId = mediaId;
			bannerUrlCacheInfo.imageUrlInfo = imageUrlInfo;
			bannerUrlCacheInfoHashtable.put(mediaId, bannerUrlCacheInfo);

			String fullPath = mDataCacheDir + BANNER_INFO_CACHE;
			ObjectStore.writeObject(fullPath, bannerUrlCacheInfoHashtable);
		}
	}

	public String getRandomSearchKeyword() {
		BannerList bannerList = cache.get("0");
		if (bannerList == null)
			return "";

		if (bannerList.searchKeyWords == null
				|| bannerList.searchKeyWords.length == 0)
			return "";

		int keyWordsCount = bannerList.searchKeyWords.length;
		int randomIndex = (int) (Math.random() * keyWordsCount);
		return bannerList.searchKeyWords[randomIndex];
	}

	public void writeToFile(String fileName, String content) {
		FileOutputStream stream = null;
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			stream = new FileOutputStream(file);
			stream.write(content.getBytes("utf-8"));
		} catch (Exception e) {
			DKLog.e(TAG, "can not read from file " + fileName, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					DKLog.e(TAG, e.getLocalizedMessage());
				}
			}
		}
	}

	public String readFromFile(String fileName) {
		try {
			File file = new File(fileName);
			if (file.exists()) {
				FileInputStream stream = new FileInputStream(file);
				byte[] data = Util.readInputStream(stream);
				return new String(data, "utf-8");
			}
		} catch (Exception e) {
			DKLog.e(TAG, "can not read from file " + fileName, e);
		}
		return null;
	}

	public void checkImageCache(String dir) {
		if (!Util.isEmpty(dir)) {
			dir = mImageCacheDir + dir + "/";
			File file = new File(dir);
			File[] files = file.listFiles();
			if (files != null && files.length > MAX_FILES_PER_SUBFOLDER) {
				ArrayList<File> list = new ArrayList<File>();
				for (File f : files) {
					list.add(f);
				}
				Collections.sort(list, new Comparator<File>() {
					@Override
					public int compare(File lhs, File rhs) {
						return Long.valueOf(lhs.lastModified()).compareTo(
								Long.valueOf(rhs.lastModified()));
					}
				});
				for (int i = 0; i < list.size(); i++) {
					Date date = new Date();
					date.setTime(list.get(i).lastModified());
				}
				for (int i = 0; i < MAX_FILES_PER_SUBFOLDER / 2; i++) {
					File f = list.get(i);
					f.delete();
					Date date = new Date();
					date.setTime(f.lastModified());
					DKLog.i(TAG, "delete image cache file " + dir + f.getName()
							+ ", last access time = " + date.toString());
				}
			}
		}
	}

	// private boolean isJPEGValid(byte[] data) {
	// if (data != null && data.length >= 4) {
	// if (data[0] == (byte) 0xFF && data[1] == (byte) 0xD8
	// && data[data.length - 2] == (byte) 0xFF
	// && data[data.length - 1] == (byte) 0xD9) {
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// private boolean isPNGValid(byte[] data) {
	// if (data != null && data.length >= 16) {
	// final int length = data.length;
	// return (data[0] == (byte) 0x89 && data[1] == (byte) 0x50
	// && data[2] == (byte) 0x4E && data[3] == (byte) 0x47
	// && data[4] == (byte) 0x0D && data[5] == (byte) 0x0A
	// && data[6] == (byte) 0x1A && data[7] == (byte) 0x0A
	// && data[length - 8] == (byte) 0x49
	// && data[length - 7] == (byte) 0x45
	// && data[length - 6] == (byte) 0x4E
	// && data[length - 5] == (byte) 0x44
	// && data[length - 4] == (byte) 0xAE
	// && data[length - 3] == (byte) 0x42
	// && data[length - 2] == (byte) 0x60 && data[length - 1] == (byte) 0x82);
	// }
	// return false;
	// }

	public Bitmap getImage(ImageUrlInfo imageUrlInfo, boolean needFromNet) {
		if (imageUrlInfo == null || Util.isEmpty(imageUrlInfo.getImageUrl())) {
			DKLog.i(TAG, "url is empty, get image failed");
			return null;
		}
		try {
			String url = imageUrlInfo.getImageUrl();
			String md5 = imageUrlInfo.md5;
//			DKLog.e(TAG, "getImage  md5: " + md5);
			DKLog.d(TAG, "getImage  url: " + url);
			String localName = urlLocalName(url);
			byte[] data = null;
			File file = new File(localName);
			if (file.exists()) {
				file.setLastModified(System.currentTimeMillis());
				FileInputStream stream = new FileInputStream(file);
				data = Util.readInputStream(stream);
				stream.close();
				if (!Util.isEmpty(md5) && data != null) {
					String fileContentMd5 = Util.getMD5(data);
					DKLog.i(TAG, "getImage file cache md5: " +
					        fileContentMd5);
					if (!fileContentMd5.equalsIgnoreCase(md5)) {
						file.delete();
						data = null;
					}
				}
//				 DKLog.i(TAG, "found image on local disk");
			} else if (needFromNet) {
				HttpClient httpClient = new HttpClient();
				HttpRequest request = new HttpRequest();
				request.setUrl(url);
				DKLog.i(TAG, "getImage : url = " + url + ", localName = " +
				        localName);
				HttpResponse response = httpClient.doGetRequest(request);
				if (response != null) {
					InputStream is = response.getContentStream();
					data = Util.readInputStream(is);
					is.close();
				}
				checkImageCache(urlDir(url));
			}
			if (data != null) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				if (bitmap != null) {
					if (!file.exists()) {
						file.createNewFile();
						FileOutputStream stream = new FileOutputStream(file);
						stream.write(data);
						stream.close();
					}
					return bitmap;
				}
			}
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			DKLog.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	// private boolean checkImageData(byte[] data, String format) {
	// if (PNG.compareToIgnoreCase(format) == 0) {
	// return isPNGValid(data);
	// } else if (JPG.compareToIgnoreCase(format) == 0) {
	// return isJPEGValid(data);
	// }
	// // only support PNG and JPG format
	// return false;
	// return true;
	// }

	private String getExtension(String url) {
		String extension = "";
		int pos = url.lastIndexOf(".");
		if (pos >= 0) {
			extension = url.substring(pos, url.length());
		}
		return extension;
	}

	private String urlLocalName(String url) {
		if (url == null || url.length() == 0)
			return "";

		String extension = getExtension(url);
		// Remove domain of URL, we just keep the path of URL.
		String strip = url.replaceFirst("http://", "");
		int pos = strip.indexOf("/");
		if (pos >= 0) {
			strip = strip.substring(pos, strip.length());
		}
		int dir = strip.hashCode() % NUM_OF_IMAGE_SUBFOLDERS;
		dir = (dir + NUM_OF_IMAGE_SUBFOLDERS) % NUM_OF_IMAGE_SUBFOLDERS;
		return mImageCacheDir + dir + "/" + strip.hashCode() + extension;
	}

	private String urlDir(String url) {
		if (url == null || url.length() == 0)
			return "";
		// Remove domain of URL, we just keep the path of URL.
		String strip = url.replaceFirst("http://", "");
		int pos = strip.indexOf("/");
		if (pos >= 0) {
			strip = strip.substring(pos, strip.length());
		}
		int dir = strip.hashCode() % NUM_OF_IMAGE_SUBFOLDERS;
		return dir + "";
	}

	public void clearCache() {
		try {
			mImageManager.pause();
			Util.delDir(mCacheRootDir);
		} catch (Exception ex) {
			DKLog.e(TAG, "delete cache exception.", ex);
		} finally {
			prepareDirs();
			mImageManager.resume();
		}
	}

	public boolean isMyFavoriteSyncCacheExist() {
		String fullPath = mDataCacheDir + LAST_SYNC_MY_FAVORITE_CACHE;
		File syncInfoFile = new File(fullPath);
		if (!syncInfoFile.exists())
			return false;

		return true;
	}

	public boolean refreshMyFavoriteSyncInfo(boolean bExistAccount,
			String xiaomiAccountName) {
		synchronized (myFavoriteSyncResultMonitor) {
			if (myFavoriteSyncResultInfo == null) {
				SyncResultInfo syncResultInfo = new SyncResultInfo();
				syncResultInfo.bAnonymousAccount = true;
				syncResultInfo.xiaomiAccountName = "";
				saveMyFavoriteSyncResultInfo(syncResultInfo);
				return true;
			}

			if (bExistAccount) {
				/*
				 * if( myFavoriteSyncResultInfo.bAnonymousAccount) return false;
				 * else { if(!myFavoriteSyncResultInfo.xiaomiAccountName.equals(
				 * xiaomiAccountName)) return true; else return false; }
				 */

				if (myFavoriteSyncResultInfo.bAnonymousAccount) {
					myFavoriteSyncResultInfo.bAnonymousAccount = false;
					myFavoriteSyncResultInfo.xiaomiAccountName = xiaomiAccountName;
					myFavoriteSyncResultInfo.lastSyncSuccess = false;
					myFavoriteSyncResultInfo.lastSyncTime = 0;
					saveMyFavoriteSyncResultInfo(myFavoriteSyncResultInfo);
					return true;
				} else {
					if (!myFavoriteSyncResultInfo.xiaomiAccountName
							.equals(xiaomiAccountName)) {
						myFavoriteSyncResultInfo.bAnonymousAccount = false;
						myFavoriteSyncResultInfo.xiaomiAccountName = xiaomiAccountName;
						myFavoriteSyncResultInfo.lastSyncSuccess = false;
						myFavoriteSyncResultInfo.lastSyncTime = 0;
						saveMyFavoriteSyncResultInfo(myFavoriteSyncResultInfo);
						return true;
					} else
						return false;
				}

			} else {
				if (myFavoriteSyncResultInfo.bAnonymousAccount)
					return false;
				else {
					myFavoriteSyncResultInfo.bAnonymousAccount = true;
					myFavoriteSyncResultInfo.xiaomiAccountName = "";
					myFavoriteSyncResultInfo.lastSyncSuccess = false;
					myFavoriteSyncResultInfo.lastSyncTime = 0;
					saveMyFavoriteSyncResultInfo(myFavoriteSyncResultInfo);
					return true;
				}
			}
		}
	}

	public boolean isMyFavoriteValid(String xiaomiAccountName) {
		synchronized (myFavoriteSyncResultMonitor) {
			if (myFavoriteSyncResultInfo == null)
				return false;

			if (!isMyFavoriteSyncCacheExist())
				return false;

			if (myFavoriteSyncResultInfo.bAnonymousAccount)
				return false;

			// check whether it's the same xiaomi user or not
			if (myFavoriteSyncResultInfo.xiaomiAccountName == null
					|| !myFavoriteSyncResultInfo.xiaomiAccountName
							.equals(xiaomiAccountName))
				return false;

			// check whether it's successful to sync or not last time
			if (!myFavoriteSyncResultInfo.lastSyncSuccess)
				return false;
			else {
				// check time interval
				long curTime = System.currentTimeMillis();
				if (curTime - myFavoriteSyncResultInfo.lastSyncTime > MY_FAVORITE_SYNC_INTERVAL)
					return false;
				else
					return true;
			}
		}
	}

	public boolean isMyFavoriteAnonymous() {
		synchronized (myFavoriteSyncResultMonitor) {
			if (myFavoriteSyncResultInfo != null)
				return myFavoriteSyncResultInfo.bAnonymousAccount;
			else
				return true;
		}
	}

	public void loadMyFavoriteSyncResultInfo() {
		synchronized (myFavoriteSyncResultMonitor) {
			String fullPath = mDataCacheDir + LAST_SYNC_MY_FAVORITE_CACHE;
			Object readObject = ObjectStore.readObject(fullPath);
			if (readObject != null && readObject instanceof SyncResultInfo) {
				myFavoriteSyncResultInfo = (SyncResultInfo) readObject;
			}
		}
	}

	public void saveMyFavoriteSyncResultInfo(SyncResultInfo syncResultInfo) {
		if (syncResultInfo == null)
			return;

		synchronized (myFavoriteSyncResultMonitor) {
			/*
			 * UserAccountInfo userAccountInfo = UserAccountInfo.getInstance();
			 * String xiaomiAccountName = null; boolean bExistAccount =
			 * !userAccountInfo.isNoAccount(); if( bExistAccount) {
			 * xiaomiAccountName =
			 * userAccountInfo.getAccount(UserAccountInfo.ACCOUNT_TYPE_XIAOMI
			 * ).name; }
			 * 
			 * if( bExistAccount &&
			 * xiaomiAccountName.equals(syncResultInfo.xiaomiAccountName) ||
			 * !bExistAccount && syncResultInfo.bAnonymousAccount) { String
			 * fullPath = dataCacheDir + LAST_SYNC_MY_FAVORITE_CACHE;
			 * ObjectStore.writeObject(fullPath, syncResultInfo);
			 * 
			 * if(myFavoriteSyncResultInfo == null) myFavoriteSyncResultInfo =
			 * new SyncResultInfo();
			 * 
			 * myFavoriteSyncResultInfo.bAnonymousAccount =
			 * syncResultInfo.bAnonymousAccount;
			 * myFavoriteSyncResultInfo.xiaomiAccountName =
			 * syncResultInfo.xiaomiAccountName;
			 * myFavoriteSyncResultInfo.lastSyncTime =
			 * syncResultInfo.lastSyncTime;
			 * myFavoriteSyncResultInfo.lastSyncSuccess =
			 * syncResultInfo.lastSyncSuccess;
			 * 
			 * DKLog.e(TAG,
			 * "*************************************************");
			 * DKLog.e(TAG, " anonymous : " + syncResultInfo.bAnonymousAccount +
			 * " name : " + myFavoriteSyncResultInfo.xiaomiAccountName); }
			 */

			String fullPath = mDataCacheDir + LAST_SYNC_MY_FAVORITE_CACHE;
			ObjectStore.writeObject(fullPath, syncResultInfo);

			if (myFavoriteSyncResultInfo == null)
				myFavoriteSyncResultInfo = new SyncResultInfo();

			myFavoriteSyncResultInfo.bAnonymousAccount = syncResultInfo.bAnonymousAccount;
			myFavoriteSyncResultInfo.xiaomiAccountName = syncResultInfo.xiaomiAccountName;
			myFavoriteSyncResultInfo.lastSyncTime = syncResultInfo.lastSyncTime;
			myFavoriteSyncResultInfo.lastSyncSuccess = syncResultInfo.lastSyncSuccess;
		}
	}

	public boolean isRecentPlayHistoryValid(String xiaomiAccountName) {
		synchronized (recentPlayHistorySyncResultMonitor) {
			if (recentPlayHistorySyncResultInfo == null)
				return false;

			String fullPath = mDataCacheDir
					+ LAST_SYNC_RECENT_PLAYHISTORY_CACHE;
			File syncInfoFile = new File(fullPath);
			if (!syncInfoFile.exists())
				return false;

			// check whether it's the same xiaomi user or not
			if (recentPlayHistorySyncResultInfo.xiaomiAccountName == null
					|| !recentPlayHistorySyncResultInfo.xiaomiAccountName
							.equals(xiaomiAccountName))
				return false;

			// check whether it's successful to sync or not last time
			if (!recentPlayHistorySyncResultInfo.lastSyncSuccess)
				return false;
			else {
				// check time interval
				long curTime = System.currentTimeMillis();
				if (curTime - recentPlayHistorySyncResultInfo.lastSyncTime > RECENT_PLAYHISTORY_SYNC_INTERVAL)
					return false;
				else
					return true;
			}
		}
	}

	public void loadRecentPlayHistorySyncResultInfo() {
		synchronized (recentPlayHistorySyncResultMonitor) {
			String fullPath = mDataCacheDir
					+ LAST_SYNC_RECENT_PLAYHISTORY_CACHE;
			Object readObject = ObjectStore.readObject(fullPath);
			if (readObject != null && readObject instanceof SyncResultInfo) {
				recentPlayHistorySyncResultInfo = (SyncResultInfo) readObject;
			}
		}
	}

	public void saveRecentPlayHistorySyncResultInfo(
			SyncResultInfo syncResultInfo) {
		if (syncResultInfo == null)
			return;

		synchronized (recentPlayHistorySyncResultMonitor) {
			String fullPath = mDataCacheDir
					+ LAST_SYNC_RECENT_PLAYHISTORY_CACHE;
			ObjectStore.writeObject(fullPath, syncResultInfo);

			if (recentPlayHistorySyncResultInfo == null)
				recentPlayHistorySyncResultInfo = new SyncResultInfo();

			recentPlayHistorySyncResultInfo.bAnonymousAccount = syncResultInfo.bAnonymousAccount;
			recentPlayHistorySyncResultInfo.xiaomiAccountName = syncResultInfo.xiaomiAccountName;
			recentPlayHistorySyncResultInfo.lastSyncTime = syncResultInfo.lastSyncTime;
			recentPlayHistorySyncResultInfo.lastSyncSuccess = syncResultInfo.lastSyncSuccess;
		}
	}

	@SuppressLint("UseSparseArrays")
	public static class CacheTime implements Serializable {
		private static final long serialVersionUID = 2L;

		public long tvLiveTime;
		public long channelTime;
		public long specialSubjectTime;
		public long homeRecommendationTime;
		public HashMap<Integer, Long> bannerTime = new HashMap<Integer, Long>();
		public HashMap<Integer, Long> recommendTime = new HashMap<Integer, Long>();
	}

}
