package com.xiaomi.miui.pushads.sdk.trace;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.xiaomi.miui.pushads.sdk.NetUtils;
import com.xiaomi.miui.pushads.sdk.NotifyAdsManager;
import com.xiaomi.miui.pushads.sdk.common.IMiuiAdsLogSender;
import com.xiaomi.miui.pushads.sdk.common.MiuiAdsTraceCell;

/**
 * main class the notification bar calls to send ads log to server
 *
 * @author liuwei
 *
 */

public class AdsLogSender implements IMiuiAdsLogSender, IAdsTraceListener {

    public static class SendingCell {
        public String mMd5;
        public String mBase64;
        public int mAdsType;
    }

    // singleton mode
    private static AdsLogSender sInstance;
    private static final String mCacheFileName = "logcache";

    // for cache cells
    private AdsLogCache mLogCache;

    // the net status receiver
    private BroadcastReceiver mNetChangeReceiver;

    // the map contains the log that is sending, if failed, we save to cache
    // the key is md5, the value is base64
    private HashMap<String, AdsCacheCell> mSendingMap;

    // the memory we need to re-send the logs. when the net is available for
    // trace log
    // we will read the cache file and read all items into memory and send them
    private ArrayList<AdsCacheCell> mFileCacheCells;

    // the main loop handler to send trace log task with interval
    private Handler mHandler;

    // some fix value, reload when net changes
//    private String mIp;
    private String mImei;
//    private String mUserId;
    private Context mContext;
    private String mAppId;
    private String mAppToken;

    // maximum 10 trace task to send log each time
    private static final int VALUE_MAX_TRACETASK = 10;

    private static final int MESSAGE_SEND_TRACELOG = 1;

    private int mSuccCount = 0;
    private int mSendCount = 0;
    private int mFailCount = 0;
    private int mCacheCount = 0;

    private AdsLogSender(Context context, String appId, String appToken) {
        mContext = context;
//        mIp = NetUtils.getLocalIPAddress();
        mImei = NetUtils.getIMEI(context);
//        mUserId = NetUtils.getXiaomiUserId(context);
        mAppId = appId;
        mAppToken = appToken;

        File cacheFolder = mContext.getCacheDir();
        mLogCache = new AdsLogCache(cacheFolder.getAbsolutePath() + "/"
                + NotifyAdsManager.AdSPREFIX + ":" + mCacheFileName);
        mSendingMap = new HashMap<String, AdsCacheCell>(100);
        mFileCacheCells = new ArrayList<AdsCacheCell>(100);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                switch (what) {
                case MESSAGE_SEND_TRACELOG:
                    break;
                }
            }
        };

        initReceiver();

        // when get the instance, if we could send trace logs, we get them from
        // cache file and send
        // them to server
        if (AdsNetUtil.canUploadLogs(mContext)) {
            pushCellsInCacheFile();
        }
    }

    private void initReceiver() {
        mNetChangeReceiver = new AdsNetReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION);
        mContext.registerReceiver(mNetChangeReceiver, filter);
    }

    private void pushCellsInCacheFile() {
        mCacheCount = 0;
        ArrayList<AdsCacheCell> list = mLogCache.getAdsCacheCellFromCacheFile();
        AdsSaltUtil.show("获取cache并发送" + "  " + NetUtils.getNetState(mContext)
                + list.size());
        mFileCacheCells.addAll(list);
        pushTraceTasks();
    }

    public static synchronized AdsLogSender getInstance(Context context,
            String appId, String appToken) {
        if (sInstance == null) {
            sInstance = new AdsLogSender(context, appId, appToken);
        }

        replaceContext(sInstance, context);
        return sInstance;
    }

    public static synchronized AdsLogSender getInstance() {
        return sInstance;
    }

    @Override
    public void onNetStateChanged() {
        // TODO Auto-generated method stub
        AdsSaltUtil.show("网络改变" + NetUtils.getNetState(mContext));
        if (!AdsNetUtil.canUploadLogs(mContext))
            return;

        // when net state changed, we need to reload the net-info
//        mIp = NetUtils.getLocalIPAddress();

        // else, we get all the cache into memory and send them
        pushCellsInCacheFile();
    }

    @Override
    public void onAccountChanged() {
//        mUserId = NetUtils.getXiaomiUserId(mContext);
    }

    @Override
    public void clickTrace(MiuiAdsTraceCell cell) {
        if (cell.adId <= 0)
            return;
        ArrayList<MiuiAdsTraceCell> cellList = new ArrayList<MiuiAdsTraceCell>();
        cellList.add(cell);
        sendTask(cellList, LogDef.ACTIONTYPE_CLICK, cell.showType);
    }

    @Override
    public void removeTrace(MiuiAdsTraceCell cell) {
        if (cell.adId <= 0)
            return;
        ArrayList<MiuiAdsTraceCell> cellList = new ArrayList<MiuiAdsTraceCell>();
        cellList.add(cell);
        sendTask(cellList, LogDef.ACTIONTYPE_REMOVE, cell.showType);
    }

    @Override
    public void receiveTrace(MiuiAdsTraceCell cell) {
        if (cell.adId <= 0)
            return;
        ArrayList<MiuiAdsTraceCell> cellList = new ArrayList<MiuiAdsTraceCell>();
        cellList.add(cell);
        sendTask(cellList, LogDef.ACTIONTYPE_RECEIVED, cell.showType);
    }

    private void sendTask(ArrayList<MiuiAdsTraceCell> cellList,
            String actionType, int showType) {
        try {
            String base64 = getBase64NotifyJsonString(cellList, actionType);
            String md5 = AdsSaltUtil.getMd5Digest(base64);
            if (checkNetAndCacheIfNeed(new AdsCacheCell(showType, base64, md5))) {
                excuteTrackTask(new AdsCacheCell(showType, base64, md5));
            }
        } catch (JSONException e) {

        }
    }

    /**
     * release of the log sender, will log cache if not send
     */
    @Override
    public void release() {
        mContext.unregisterReceiver(mNetChangeReceiver);
        mHandler.removeMessages(MESSAGE_SEND_TRACELOG);
        Iterator<Entry<String, AdsCacheCell>> itor = mSendingMap.entrySet()
                .iterator();
        while (itor.hasNext()) {
            Entry<String, AdsCacheCell> entry = itor.next();
            AdsCacheCell cell = entry.getValue();
            mLogCache.appendInfo(cell);
        }

        mSendingMap.clear();
        sInstance = null;
    }

    // the function is used to send trace tasks one time
    private void pushTraceTasks() {

        if (!AdsNetUtil.canUploadLogs(mContext))
            return;

        AdsSaltUtil.show("cache 个数: " + mFileCacheCells.size());
        Iterator<AdsCacheCell> itor = mFileCacheCells.iterator();
        int i = 0;
        while (itor.hasNext() && i < VALUE_MAX_TRACETASK) {
            AdsCacheCell cell = itor.next();
            i++;
            excuteTrackTask(cell);
            itor.remove();
        }

        if (mFileCacheCells.size() > 0) {
            AdsSaltUtil.show("cache 太多，下次发送 left: " + mFileCacheCells.size());
            Message msg = mHandler.obtainMessage();
            msg.what = MESSAGE_SEND_TRACELOG;
            mHandler.sendMessageDelayed(msg, 3000);
        }
    }

    // called this when we could upload track log. means the network is ok for
    // log
    private void excuteTrackTask(AdsCacheCell cell) {
        if (mSendingMap.containsKey(cell.mMd5))
            return;

        // else, put in sending map
        mSendCount++;
        AdsSaltUtil.show("send: " + mSendCount);
        AdsLogTraceTask logTask = new AdsLogTraceTask(this, mAppId, mAppToken,
                cell);
        mSendingMap.put(cell.mMd5, cell);
        logTask.execute();
    }

    /**
     * callback from async task, if send failed, we put it into cache log file
     * and re-send in future
     */
    @Override
    public void onTraceTaskFinished(Integer status, AdsCacheCell cell) {
        // TODO Auto-generated method stub
        if (mSendingMap.containsKey(cell.mMd5)) {
            if (status != LogDef.RET_OK) {
                mFailCount++;
                AdsSaltUtil.show("faild: " + mFailCount + " " + cell.mMd5
                        + "  " + mSendingMap.size());
                cache2LogFile(cell);
            } else {
                mSuccCount++;
                AdsSaltUtil.show("success: " + mSuccCount);
            }

            mSendingMap.remove(cell.mMd5);
        }
    }

    private String getBase64NotifyJsonString(
            ArrayList<MiuiAdsTraceCell> cellList, String actionType)
            throws JSONException {
        JSONObject log = new JSONObject();
//        log.put(LogDef.JSON_TAG_USERID, mUserId);
        log.put(LogDef.JSON_TAG_IMEI, AdsSaltUtil.getMd5Digest(mImei));
//        log.put(LogDef.JSON_TAG_IP, mIp);
        log.put(LogDef.JSON_TAG_ACTIONTYPE, actionType);
        log.put(LogDef.JSON_TAG_ACTIONTIME, System.currentTimeMillis());

        JSONArray jsonArray;
        ArrayList<JSONObject> adList = new ArrayList<JSONObject>();

        for (int i = 0; i < cellList.size(); i++) {
            // 这里，我们认为， 从 MiuiAdsTraceCell.content 是一个带有特定信息的json 串
            JSONObject cell = null;
            if (TextUtils.isEmpty(cellList.get(i).content)) {
                cell = new JSONObject();
            } else {
                try {
                    cell = new JSONObject(cellList.get(i).content);
                } catch (Exception e) {
                    Log.e("com.xiaomi.miui.ads.pushsdk", "content 不是json串");
                }
            }

            if (cell == null) {
                cell = new JSONObject();
            }

            cell.put(LogDef.JSON_TAG_ADID, cellList.get(i).adId);
            adList.add(cell);
        }

        jsonArray = new JSONArray(adList);
        log.put(LogDef.JSON_TAG_ADLIST, jsonArray);

        String base64 = Base64.encodeToString(log.toString().getBytes(),
                Base64.DEFAULT | Base64.NO_WRAP);
        return base64;
    }

    /**
     *
     * @param cellList
     * @return true: can start the trace task false: save to the cache and don't
     *         start task
     */
    private boolean checkNetAndCacheIfNeed(AdsCacheCell cell) {
        if (AdsNetUtil.canUploadLogs(mContext))
            return true;

        // we need to cache the info into cache file
        cache2LogFile(cell);
        return false;
    }

    private void cache2LogFile(AdsCacheCell cell) {
        mCacheCount++;
        AdsSaltUtil.show("cacheCount: " + mCacheCount);
        mLogCache.appendInfo(cell);
        mLogCache.flushFile();
    }

    // 如果在app oncreate 里面调用，是不会出现这种情况的，不过以防万一
    private static void replaceContext(AdsLogSender logSender, Context context) {
        if (logSender != null && context != null) {
            if (logSender.mContext != context) {
                logSender.mContext = context;
            }
        }
    }
}
