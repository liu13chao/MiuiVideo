/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   DetailInfoLoader.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-12-17
 */
package com.miui.video.model.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.text.TextUtils;
import android.util.LruCache;

import com.miui.video.api.DKApi;
import com.miui.video.model.CacheConfig;
import com.miui.video.response.MediaDetailInfoResponse;
import com.miui.video.statistic.MediaFeeDef;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.util.ObjectStore;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 * @author tianli
 *
 */
public class DetailInfoLoader extends DataLoader {

    final static int MAX_MEM_CACHE_COUNT = 5;
    final static int MAX_DISK_CACHE_COUNT = 10;
    
    static LruCache<Integer, MediaDetailInfo2> mDetailCache = 
            new LruCache<Integer, MediaDetailInfo2>(MAX_MEM_CACHE_COUNT);
    
    private int mMediaId;
    private String mStatisticInfo;
    
    private MediaDetailInfo2 mDetailInfo;
    
    private final static String DETAIL_CACHE_DIR = "/detailcache";
    private final static long EXPIRED_TIME = 3600000;
    
    private ServiceRequest mRequest;
    
    public DetailInfoLoader(int mediaId, String statisticInfo){
        mMediaId = mediaId;
        mStatisticInfo = statisticInfo;
    }
    
    @Override
    public void load() {
        mDetailInfo = mDetailCache.get(mMediaId);
        if(mDetailInfo != null){
//            Log.d(TAG, "in memory");
            notifyDataReady();
            return;
        }
        new AsyncLoadTask().start();
    }
    
    public MediaDetailInfo2 getDetailInfo(){
        return mDetailInfo;
    }
    
    private String prepareDir(){
        try{
            String cacheDir = CacheConfig.getCacheRootDir();
            String detailDir = cacheDir + DETAIL_CACHE_DIR;
            File file = new File(detailDir);
            if(!file.exists()){
                file.mkdir();
            }
            return detailDir;
        }catch(Exception e){
        }
        return null;
    }

    @Override
    public void doStorageLoad() {
        super.doStorageLoad();
        try{
            String dir = prepareDir();
            if(TextUtils.isEmpty(dir)){
                return;
            }
            File cacheDirFile = new File(dir);
            File[] filelist = cacheDirFile.listFiles();
            for(int i = 0; filelist != null && filelist.length > 0; i++){
                File file = filelist[i];
                String filename = file.getName();
                if(filename == null){
                    file.delete();
                }else if(filename.equals(mMediaId + "")){
                    long time = file.lastModified();
                    if(System.currentTimeMillis() - time > EXPIRED_TIME){
                        file.delete();
                    }else{
                        Object bean = ObjectStore.readObject(file.getAbsolutePath());
                        if(bean instanceof MediaDetailInfo2){
                            mDetailInfo = (MediaDetailInfo2)bean;
                            file.setLastModified(System.currentTimeMillis());
//                            Log.d(TAG, "in disk");
                        }else{
                            file.delete();
                        }
                    }
                }
            }
        }catch(Exception e){
        }
    }

    @Override
    public void onPostStorageLoad() {
        super.onPostStorageLoad();
        if(mDetailInfo != null){
            mDetailCache.put(mMediaId, mDetailInfo);
            notifyDataReady();
        }else{
            getDetailInfoFromNet();
        }
    }
    
    private void getDetailInfoFromNet(){
        if(mRequest != null) {
            mRequest.cancelRequest();
        }
//        Log.d(TAG, "mMediaId = " + mMediaId);
        mRequest = DKApi.getMediaDetailInfo(mMediaId, true, MediaFeeDef.MEDIA_ALL, mStatisticInfo, mRequestObserver);
    }
    
    private void saveDetailInfo(final MediaDetailInfo2 detailInfo){
        new Thread(new Runnable() {
            @Override
            public void run() {
                doSave(detailInfo);
            }
        }).start();
    }
    
    private void doSave(MediaDetailInfo2 detailInfo){
        if(detailInfo != null){
            try{
                String dir = prepareDir();
                if(TextUtils.isEmpty(dir)){
                    return;
                }
                ObjectStore.writeObject(dir + "/" + mMediaId, detailInfo);
                File cacheDirFile = new File(dir);
                File[] filelist = cacheDirFile.listFiles();
                ArrayList<File> list = new ArrayList<File>();
                for (File f : filelist) {
                    list.add(f);
                }
                Collections.sort(list, new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        return Long.valueOf(lhs.lastModified()).compareTo(
                                Long.valueOf(rhs.lastModified()));
                    }
                });
                for (int i = 0; i <  list.size() - MAX_DISK_CACHE_COUNT; i++) {
                    File f = list.get(i);
                    f.delete();
                }
            }catch(Exception e){
            }
        }
    }
    
    private Observer mRequestObserver = new Observer() {
        @Override
        public void onRequestCompleted(ServiceRequest request,
                ServiceResponse response) {
            if(response.isSuccessful() && response instanceof MediaDetailInfoResponse) {
                MediaDetailInfoResponse mediaDetailInfoResponse = (MediaDetailInfoResponse) response;
//                Log.d(TAG, "from web");
                mDetailInfo = mediaDetailInfoResponse.data;
                if(mDetailInfo != null){
                    mDetailCache.put(mMediaId, mDetailInfo);
                    saveDetailInfo(mDetailInfo);
                    notifyDataReady();
                }else{
                    notifyDataFail();
                }
            } else {
                notifyDataFail();
            }
        }
        
        @Override
        public void onProgressUpdate(ServiceRequest request, int progress) {
        }
    };
}
