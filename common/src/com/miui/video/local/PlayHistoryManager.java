/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  PlayHistoryManager.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-23
 */
package com.miui.video.local;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.miui.video.offline.OfflineMedia;
import com.miui.video.type.LocalMedia;
import com.miui.video.type.LocalMediaList;

/**
 * @author tianli
 *
 */
public class PlayHistoryManager extends SyncManager {

    private PlayHistoryStore mStore;
    private List<PlayHistory> mHistoryList = new ArrayList<PlayHistory>();
    private List<PlayHistory> mPlayerHistoryList = new ArrayList<PlayHistory>();

    private ArrayList<String> mPlayHisDateList = new ArrayList<String>();
    private HashMap<String, List<PlayHistory>> mPlayHisListMap = new HashMap<String, List<PlayHistory>>();

    // Listeners
    private List<SoftReference<OnHistoryChangedListener>> mListeners = new ArrayList<
            SoftReference<OnHistoryChangedListener>>();

    private static HandlerThread mSyncThread;
    private static Handler mHandler;

    static {
        mSyncThread = new HandlerThread("PlayHisThread");
        mSyncThread.start();
        mHandler = new Handler(mSyncThread.getLooper());
    }

    @Override
    public void init(Context context) {
        super.init(context);
        mStore = new PlayHistoryStore(mContext);
        loadPlayHistory();
    }

    @Override
    protected void scheduleBackgroundTask(Runnable task) {
        mHandler.post(task);
    }

    //public method
    public void loadPlayHistory() {
        scheduleBackgroundTask(mLoadAllTask);
    }

    public void reloadPlayerHistory(){
        scheduleBackgroundTask(mLoadPlayerTask);
    }

    public PlayHistory getPlayHistoryById(int mediaId) {
        if(mediaId <= 0) {
            return null;
        }
        for(int i = 0; i < mHistoryList.size(); i++) {
            PlayHistory playHistory = mHistoryList.get(i);
            if(playHistory != null && playHistory.mediaId == mediaId) {
                return playHistory;
            }
        }
        return null;
    }

    public PlayHistory getPlayPositionByMeidaUrl(String mediaUrl) {
        if( mediaUrl == null || mediaUrl.trim().length() == 0)
            return null;
        for(int i = 0; i < mPlayerHistoryList.size(); i++) {
            PlayHistory playHistory = mPlayerHistoryList.get(i);
            if(playHistory != null && mediaUrl.equals(playHistory.mediaUrl)) {
                return playHistory;
            }
        }
        return null;
    }

    public List<String> getPlayHisDateList() {
        return mPlayHisDateList;
    }

    public List<PlayHistory> getPlayHisList(String playHisDate) {
        return mPlayHisListMap.get(playHisDate);
    }

    public List<PlayHistory> getPlayHisList() {
        return mHistoryList;
    }

    public HashMap<String, List<PlayHistory>> getHistoryListMap() {
        return mPlayHisListMap;
    }

    public void addListener(OnHistoryChangedListener listener){
        if(listener != null){
            for(SoftReference<OnHistoryChangedListener> ref : mListeners){
                if(ref != null && ref.get() == listener){
                    return;
                }
            }
            mListeners.add(new SoftReference<OnHistoryChangedListener>(listener));
        }
    }

    public void removeListener(OnHistoryChangedListener listener){
        if(listener != null && mListeners.contains(listener)){
            for(SoftReference<OnHistoryChangedListener> ref : mListeners){
                if(ref != null && ref.get() == listener){
                    mListeners.remove(ref);
                    break;
                }
            }
        }
    }

    public void addPlayHistory(PlayHistory playHistory){
        if(playHistory != null){
            playHistory.playDate = System.currentTimeMillis();
            Runnable savePlayHisTask = new SavePlayHisRunnable(playHistory);
            scheduleBackgroundTask(savePlayHisTask);
        }
    }
    
    public void addPlayHistory(OfflineMedia offlineMedia){
        if(offlineMedia != null){
            PlayHistory playHistory = null;
            if(offlineMedia.mediaInfo != null){
                playHistory = new OnlinePlayHistory(offlineMedia);
            }else{
                playHistory = new LocalPlayHistory(offlineMedia);
            }
            Runnable savePlayHisTask = new SavePlayHisRunnable(playHistory);
            scheduleBackgroundTask(savePlayHisTask);
        }
    }
    
    public void delPlayHistoryList(List<PlayHistory> playHistoryList){
        if(playHistoryList == null || playHistoryList.size() == 0) {
            return;
        }
        List<PlayHistory> list = new ArrayList<PlayHistory>();
        list.addAll(playHistoryList);
        Runnable delPlayHisListTask = new DelPlayHisListRunnable(list);
        scheduleBackgroundTask(delPlayHisListTask);
    }

    public void delLocalMediaLists(List<LocalMediaList> localMediaList){
        if(localMediaList == null || localMediaList.size() == 0) {
            return;
        }
        List<PlayHistory> list = new ArrayList<PlayHistory>();
        for(int i = 0; i < localMediaList.size(); i++){
            LocalMediaList mediaList = localMediaList.get(i);
            List<LocalMedia> medias = mediaList.getLocalMediaList();
            for(int j = 0; medias != null && j < medias.size(); j++){
                PlayHistory his = new LocalPlayHistory(medias.get(j));
                list.add(his);
            }
        }
        Runnable delPlayHisListTask = new DelPlayHisListRunnable(list);
        scheduleBackgroundTask(delPlayHisListTask);
    }

    public void delLocalMedias(List<LocalMedia> localMediaList){
        if(localMediaList == null || localMediaList.size() == 0) {
            return;
        }
        List<PlayHistory> list = new ArrayList<PlayHistory>();
        for(int i = 0; i < localMediaList.size(); i++){
            PlayHistory his = new LocalPlayHistory(localMediaList.get(i));
            list.add(his);
        }
        Runnable delPlayHisListTask = new DelPlayHisListRunnable(list);
        scheduleBackgroundTask(delPlayHisListTask);
    }

    //load play his task
    private Runnable mLoadAllTask = new Runnable() {
        @Override
        public void run() {
            mStore.loadPlayHistory(getAccount());
            notifyDataReady();
        }
    };

    private Runnable mLoadPlayerTask = new Runnable() {
        @Override
        public void run() {
            mStore.reloadPlayerHistory();
            notifyDataReady();
        }
    };

    //notify history loaded
    private Runnable mNotifyRunnable = new Runnable(){
        @Override
        public void run() {
            notifyHistoryLoaded();
        }
    };

    private void notifyHistoryLoaded(){

        generatePlayHisData();
        for(SoftReference<OnHistoryChangedListener> ref : mListeners){
            if(ref != null && ref.get() != null){
                ref.get().onHistoryChanged(mHistoryList);
            }
        }
    }

    private void generatePlayHisData() {
        mPlayHisDateList.clear();
        mPlayHisListMap.clear();
        for(int i = 0; i < mHistoryList.size(); i++) {
            PlayHistory playHistory = mHistoryList.get(i);
            if(playHistory != null) {
                String key = playHistory.formatPlayDate();
                List<PlayHistory> playHistoryList = mPlayHisListMap.get(key);
                if(playHistoryList == null) {
                    playHistoryList = new ArrayList<PlayHistory>();
                }
                playHistoryList.add(playHistory);
                if(!mPlayHisListMap.containsKey(key)) {
                    mPlayHisListMap.put(key, playHistoryList);
                    mPlayHisDateList.add(key);
                }
            }
        }
    }
    
    private void notifyDataReady(){
        mHistoryList = mStore.getHistoryList();
        mPlayerHistoryList = mStore.getPlayerHistoryList();
        scheduleUITask(mNotifyRunnable);
    }

    //save play his
    private void delPlayHisList(List<PlayHistory> delPlayHisList) {
        mStore.delHistoryList(getAccount(), delPlayHisList);
        notifyDataReady();
    }

    private void saveVideoPlayHis(PlayHistory playHistory) {
        mStore.saveVideoHistory(getAccount(), playHistory);
        notifyDataReady();
    }

    //save play his runnable
    public class SavePlayHisRunnable implements Runnable {

        PlayHistory playHistory;

        public SavePlayHisRunnable(PlayHistory playHistory) {
            this.playHistory = playHistory;
        }

        @Override
        public void run() {
            saveVideoPlayHis(playHistory);
        }
    }

    public class DelPlayHisListRunnable implements Runnable {

        List<PlayHistory> delPlayHisList;

        public DelPlayHisListRunnable(List<PlayHistory> delPlayHisList) {
            this.delPlayHisList = delPlayHisList;
        }

        @Override
        public void run() {
            delPlayHisList(delPlayHisList);
        }
    }

    //self def class
    public static interface OnHistoryChangedListener{
        public void onHistoryChanged(List<PlayHistory> historyList);
    }
}
