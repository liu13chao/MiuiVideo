package com.xiaomi.miui.pushads.sdk;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.miui.video.util.DKLog;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.miui.pushads.sdk.common.IOuterMsgListener;
import com.xiaomi.miui.pushads.sdk.common.MiuiAdsCell;
import com.xiaomi.miui.pushads.sdk.common.MiuiAdsTraceCell;
import com.xiaomi.miui.pushads.sdk.trace.AdsLogSender;



/**
 * 用于广告推送消息的接受， 或者非广告消息的接受。 如果是广告消息，那么SDK 将会进行广告策略的过滤和限制。
 * 对于非广告消息， SDK 不会进行过滤和限制。 用于非广告消息时，需要在服务器端使用广告的密钥进行下发。
 *
 * 默认使用小米账户作为唯一标识，用户可以注册外部ID， 也可以使用IMEI 替代。 支持TOPIC 广播功能
 *
 * @author liuwei
 *
 */
 

public class NotifyAdsManagerNew  implements INotifyAdsListener {

    //单例模式，
    public  static final String AdSPREFIX =    "com.xiaomi.miui.pushads.sdk";
    public  static final String TAG = "ads-notify-fd5dfce4";
    public  static final int  PREFER_NONE    =  0;
    public  static final int  PREFER_IMEI    =  1;
    public  static final int  PREFER_XIAOMI_ACCOUNT =  2;
    
    private final static String DEFAULT_TOPIC_ID = ":MiuiVideo"; 

    private static final String CATEGORY_UUID = "fd5dfce4-64df-4434-aa66-2a70ff84a9c4";
    private static  final String CACHE_FILE_NAME = AdSPREFIX + ":adscache";
    static final int BUBBLE_LIMIT_TIMES = 4;
    private static  NotifyAdsManagerNew sInstance;

    private Context mContext;
    private boolean mReceiverRegistered;
    private BroadcastReceiver mNetChangeReceiver;
    private NotifyAdsCache mAdsCache;

    private String mIp;
    private String mImei;
    private String mUserId;

    //为了接入新的push 系统，我们使用别名，规则是，如果有userid，那么就使用userid，如果没有userid，那么使用imei
    //alias 的目的是让server 定位一个接入的用户， 以便发送push 消息给该用户
    private String mAlias;

    //这个是app 监听
    private MiuiAdsListener mAdsListener;

    //把file 里面的缓存调入内存并进行发送
    private ArrayList<NotifyAdsCacheCell> mFileCacheCells;

    //用于进行分批发送cache 的广告，如果广告没有过期的话
    private Handler mHandler;

    //用于统计广告下载的次数
    private int mSuccessCount  = 0;
    private int mCacheCount    = 0;


    //app 想要优先使用 account 或者 imei
    private int mPreferFlag    = 0;

    // maximum 10 trace task to send log each time
    private static final int VALUE_MAX_TRACETASK = 10;
    private static final int MESSAGE_SEND_TRACELOG = 1;
    private static final int MESSAGE_RESET_ALIAS   = 2;
    private static final int MESSAGE_SET_ALIAS     = 3;
    private static final int MESSAGE_INIT_CALLBACK = 4;
    private static final int MESSAGE_CHANNEL_SUCCESS = 5;
    private static final int MESSAGE_SET_TOPIC       = 6;

    private static final long MAX_CACHE_SIZE       = 20 * 1024 * 1024;

    //version 2.0 为了接入新的push server，我们需要 appPackageName, appId 和 appToken 三个参数
    private String mAppPackageName;
    private String mAppId;
    private String mAppToken;
    private SharedPreferences mPrefer;

    //这个用于在APP 不想使用米聊号或者IMEI 时， 自行传入的ID， 这样，APP 需要在server 端自行输入这个ID
    private String mOuterId;
    private String mTopic;
    private AdsLogSender logSender;
    private boolean mInitialSuccess = false;

    //这个是后来加上，用户给第三方的APP 返回该APP 接受到的外部消息，非广告消息
    private IOuterMsgListener outerListener;

    //imei 获取的重试次数，超过3次放弃， 3分钟足够启动了
    private int mImeiRetryCount = 0;

    public enum NetState {
        NONE,
        Wifi, // Wifi 状态
        MN2G, // 移动 2G
        MN3G, // 移动 3G
        MN4G  // 移动 4G
    }

    private NotifyAdsManagerNew(Context context, MiuiAdsListener listener) {
        mAdsListener = listener;
        mContext = context;
        initReceiver();
        initMembers();

        //我们先进行cache 里面过期图片的删除
        doCleanCacheFolder();

        //当我们第一次open 的时候，如果有 cache 的广告，那么进行下载
        if (NetUtils.canDownloadAds(mContext)) {
            LogUtils.logProcess("有 cache 文件，开始下载cache");
            pushCellsInCacheFile();
        }
    }

    private NotifyAdsManagerNew(Context context, MiuiAdsListener listener, String appPackageName, String appId, String appToken,
            String outerId, String topicId, int flag) {
        this(context, listener);

        //虽然外面判断过...
        if (!NetUtils.isEmptyString(outerId)) {
            mOuterId = outerId;
        }

        if (!NetUtils.isEmptyString(topicId)) {
            mTopic = AdSPREFIX + topicId;
        } else {
        	mTopic = AdSPREFIX + DEFAULT_TOPIC_ID;
        }

        mAppPackageName = appPackageName;
        mAppId = appId;
        mAppToken = appToken;
        mPreferFlag = flag;

        logSender = AdsLogSender.getInstance(mContext, appId, appToken);
        showLog("logSender: " + logSender);

        //在这里，我们把 notifyManager 注册成为 新的push channel 的听众，接受来自push channel 的各种消息
        showLog(mAppPackageName + "--->init channel");
//        MiPushClient.initialize(mContext, mAppId, mAppToken,this);
    }

    private void initMembers() {
        mIp = NetUtils.getLocalIPAddress();
        //为0 的情况已经在api 里面处理了
        getAccountInfo();

        File cacheFolder = mContext.getCacheDir();
        mAdsCache = new NotifyAdsCache(cacheFolder.getAbsolutePath()+ "/" + CACHE_FILE_NAME);
        mFileCacheCells = new ArrayList<NotifyAdsCacheCell>();
        mPrefer = PreferenceManager.getDefaultSharedPreferences(mContext);


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                switch(what) {
                case MESSAGE_SEND_TRACELOG:
                    downloadCacheAds();
                    break;

                case MESSAGE_RESET_ALIAS:
                    mAlias = "";   // no break here
                case MESSAGE_SET_ALIAS:
                    setAlias();
                    break;

                case MESSAGE_SET_TOPIC:
                    setTopic();
                    break;

                case MESSAGE_INIT_CALLBACK:
                    int resultCode = msg.arg1;
                    String regID = (String)msg.obj;
                    if (null != mAdsListener) {
                        mAdsListener.onChannelInitialized(resultCode, regID);
                    }
                    break;

                case MESSAGE_CHANNEL_SUCCESS:
                    mInitialSuccess = true;
                    break;
                }
            }
        };
    }

    public void setOuterListener(IOuterMsgListener outerListener) {
        this.outerListener = outerListener;
    }

    /**
     * 如果返回true, 说明是外部的message 或者说明出错， 否则返回false
     * @param content
     * @return
     */

    //这个函数可能在主线程里面调用，也可能在次线程里面调用，所以使用handler 切换到主线程
//    @Override
    public void onInitializeResult(long resultCode, String reason, String regID) {
        // TODO Auto-generated method stub
        if (null != mAdsListener) {
            Message msg = mHandler.obtainMessage();
            msg.what = MESSAGE_INIT_CALLBACK;
            msg.arg1 = (int)resultCode;
            msg.obj = regID;
            mHandler.sendMessage(msg);
        }

        if (ErrorCode.SUCCESS == resultCode) {
            showLog(mAppPackageName + "--->cahnel OK");
            mHandler.sendEmptyMessage(MESSAGE_SET_ALIAS);

            if (!NetUtils.isEmptyString(mTopic))
                mHandler.sendEmptyMessage(MESSAGE_SET_TOPIC);

            mHandler.sendEmptyMessage(MESSAGE_CHANNEL_SUCCESS);
        } else {
            showLog(mAppPackageName + "--->chanle failed， need app reopen");
        }

    }

    /**
     * 我们设置该设备注册的topic ，支持广播
     */
    private void setTopic() {
        if (!NetUtils.isEmptyString(mTopic)) {
            showLog(mAppPackageName + "--->set topic " + mTopic);
            MiPushClient.subscribe(mContext, mTopic, getNamedCategory());
        }
    }

    /**
     *我们最优先使用外部的id， 然后在用户没有指定的情况下，默认使用小米账户，指定IMEI 使用IMEI
     *对于IMEI ，如果发现是空需要重新设置，因为某些APP在系统开机时，拿不到IMEI。 重试3次， 不行就停止
     */
    private void setAlias() {
        String newAlias = "";
        getAccountInfo();
        //如果用户没有指定私有的 id，那么就使用米聊号或者IMEI号
        if (!NetUtils.isEmptyString(mOuterId)) {
            newAlias = new String(mOuterId);
        } else {
            //如果使用IMEI，那么如果没有的话，60秒后再重试一次，保证系统启动完成
            if (mPreferFlag == PREFER_IMEI && NetUtils.isEmptyString(mImei)) {
                //可能系统还没有准备好
                if (mImeiRetryCount < 3) {
                    mHandler.sendEmptyMessageDelayed(MESSAGE_RESET_ALIAS, 60*1000);
                    mImeiRetryCount ++;
                } else {
                    showLog("can't get imei, system contains error, we can't get msg");
                }

                return;
            }

            //默认情况下，使用user id
            if (mPreferFlag == PREFER_IMEI) {
                newAlias = new String(mImei);
            } else {
                //使用小米账户，但是没有账户，那么直接返回，因为在账户切换的时候会自动的使用小米账户
                if (NetUtils.isEmptyString(mUserId))  return;
                else {
                    newAlias = new String(mUserId);
                }
            }
        }

        assert(!NetUtils.isEmptyString(newAlias));

        if (NetUtils.isEmptyString(newAlias)) {
            showLog(mAppPackageName + " ---> how could this happen? return");
        }

        //这个是push 广告sdk 统一的前缀名称
        newAlias = AdSPREFIX + newAlias;

        if (NetUtils.isEmptyString(mAlias) || !newAlias.equals(mAlias)) {
            boolean accountChange = false;
            if (!NetUtils.isEmptyString(mAlias)) {
                showLog(mAppPackageName + "--->unset old account: " + mAlias);
                MiPushClient.unsetAlias(mContext, mAlias, getNamedCategory());
                accountChange = true;
            }

            showLog(mAppPackageName + "-->set alias: " + mAlias + " thread: " + Thread.currentThread().getName());
            MiPushClient.setAlias(mContext, newAlias, getNamedCategory());
            mAlias = newAlias;
            //如果是账户切换，那么就重新设置上限值
            if (accountChange) resetUpperBound();
        }
    }

    private void resetUpperBound() {
        //账户改变， 设置客户端接受上限base 为0
        mPrefer.edit().putLong(NotifyAdsDef.PREFER_KEY_STARTTIME, 0).commit();
        mPrefer.edit().putInt(NotifyAdsDef.PREFER_KEY_NOTIFY_SUCCESS_COUNT, 0).commit();
        mPrefer.edit().putInt(NotifyAdsDef.PREFER_KEY_BUBBLE_SUCCESS_COUNT, 0).commit();
    }

    private String getNamedCategory() {
        String [] splits = CATEGORY_UUID.split("-");
        String result = AdSPREFIX;
        for (int i=0; i<splits.length; i++) {
            result = result + splits[i];
            result = DownloadUtils.getMd5Digest(result);
        }
        return result;
    }

//    @Override
    public void onSubscribeResult(long resultCode, String reason,
            String topic) {
        showLog(mAppPackageName + "--->topic resultCode: " + resultCode + " reason: " + reason+ " topic: " + topic);
        if (resultCode != ErrorCode.SUCCESS) {
            //如果调用失败，1个小时后再去进行尝试
            mHandler.sendEmptyMessageDelayed(MESSAGE_SET_TOPIC, 3600*1000);
        }
    }

//    @Override
    public void onUnsubscribeResult(long resultCode, String reason,
            String topic) {
        showLog(mAppPackageName + "--->unsuscribe topic resultCode: " + resultCode + " reason: " + reason+ " topic: " + topic);
    }

    private void initReceiver() {
        mNetChangeReceiver = new NotifyAdsNetReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION);
        mContext.registerReceiver(mNetChangeReceiver, filter);
        mReceiverRegistered = true;
    }

    public synchronized int getCurrentSuccess(int showType) {
        int ret = 0;
        if (showType == NotifyAdsDef.ADS_TYPE_NOTIFY) {
            ret = mPrefer.getInt(NotifyAdsDef.PREFER_KEY_NOTIFY_SUCCESS_COUNT, 0);
        } else if(showType == NotifyAdsDef.ADS_TYPE_BUBBLE) {
            ret = mPrefer.getInt(NotifyAdsDef.PREFER_KEY_BUBBLE_SUCCESS_COUNT, 0);
        }

        return ret;
    }

    public synchronized void increaseSuccessAds(int showType) {
        int ret = 0;

        if (showType == NotifyAdsDef.ADS_TYPE_NOTIFY) {
            ret = mPrefer.getInt(NotifyAdsDef.PREFER_KEY_NOTIFY_SUCCESS_COUNT, 0);
            ret ++;
            mPrefer.edit().putInt(NotifyAdsDef.PREFER_KEY_NOTIFY_SUCCESS_COUNT, ret).commit();
        } else if(showType == NotifyAdsDef.ADS_TYPE_BUBBLE) {
            ret = mPrefer.getInt(NotifyAdsDef.PREFER_KEY_BUBBLE_SUCCESS_COUNT, 0);
            ret++;
            mPrefer.edit().putInt(NotifyAdsDef.PREFER_KEY_BUBBLE_SUCCESS_COUNT, ret).commit();
        }
    }

    public static synchronized NotifyAdsManagerNew getInstance() {
        return sInstance;
    }

    /**
     * 这个接口默认使用小米账户作为alias
     * @param context
     * @param listener
     * @param appPackageName
     * @param appId
     * @param appToken
     * @return
     */
    public static synchronized NotifyAdsManagerNew open(Context context, MiuiAdsListener listener, String appPackageName, String appId, String appToken) {
        showLog("app: " + appPackageName);
        if (sInstance == null) {
            sInstance = new NotifyAdsManagerNew(context, listener, appPackageName, appId, appToken, null, null, PREFER_NONE);
        }

        return sInstance;
    }

    /**
     * 这个接口根据用户输入的flag 设置默认的alias 如果是小米账户，设置 PREFER_XIAOMI_ACCOUNT IMEI 使用 PREFER_IMEI
     * @param context
     * @param listener
     * @param appPackageName
     * @param appId
     * @param appToken
     * @param flag
     * @return
     */
    public static synchronized NotifyAdsManagerNew open(Context context, MiuiAdsListener listener, String appPackageName, String appId, String appToken, int flag) {
        showLog("app: " + appPackageName);
        if (sInstance == null) {
            sInstance = new NotifyAdsManagerNew(context, listener, appPackageName, appId, appToken, null, null, flag);
        }

        return sInstance;
    }


    /**
     * 这个API 用于外部APP 不使用IMEI 也不使用小米账户时， 注册一个outerId, 作为最优先使用的alias，
     * 如果APP 确实需要使用 IMEI 或者 小米账户， 那么不要使用这个接口， 因为在 rom 上面，如果APP 获得IMEI 失败，那么SDK 无法恢复。
     * 比如，APP 自行获得 IMEI 作为OUTERID，但是获得失败， SDK 丢弃这个ID， 然后会使用小米账户作为默认账户。产生问题
     * @param context
     * @param listener
     * @param appPackageName
     * @param appId
     * @param appToken
     * @param outerId
     * @return
     */
    public static synchronized NotifyAdsManagerNew open(Context context, MiuiAdsListener listener, String appPackageName,
            String appId, String appToken, String outerId) {
        showLog("app: " + appPackageName);
        if (sInstance == null) {
            outerId = getTrimString(outerId);
            sInstance = new NotifyAdsManagerNew(context, listener, appPackageName, appId, appToken, outerId, null, PREFER_NONE);
        }

        return sInstance;
    }

    /**
     * 同时支持内部账户 和 广播的 TOPIC， 都可以为 null
     * @param context
     * @param listener
     * @param appPackageName
     * @param appId
     * @param appToken
     * @param outerId
     * @param topicId
     * @return
     */
    public static synchronized NotifyAdsManagerNew open2(Context context, MiuiAdsListener listener, String appPackageName,
            String appId, String appToken, int flag, String topicId) {
        showLog("app: " + appPackageName);
        if (sInstance == null) {
            topicId = getTrimString(topicId);
            sInstance = new NotifyAdsManagerNew(context, listener, appPackageName, appId, appToken, null, topicId, flag);
        }

        return sInstance;
    }


    /**
     * 同时支持OUTERID 和 广播的 TOPIC， 都可以为 null
     * @param context
     * @param listener
     * @param appPackageName
     * @param appId
     * @param appToken
     * @param outerId
     * @param topicId
     * @return
     */
    public static synchronized NotifyAdsManagerNew open2(Context context, MiuiAdsListener listener, String appPackageName,
            String appId, String appToken, String outerId, String topicId) {
        showLog("app: " + appPackageName);
        if (sInstance == null) {
            outerId = getTrimString(outerId);
            topicId = getTrimString(topicId);

            sInstance = new NotifyAdsManagerNew(context, listener, appPackageName, appId, appToken, outerId, topicId, PREFER_NONE);
        }

        return sInstance;
    }

    private static String getTrimString(String str) {
        if (null != str) str = str.trim();
        return str;
    }

    /**
     * 重新打开， 替换原先的 instance，默认使用小米账户
     * @param context
     * @param listener
     * @param appPackageName
     * @param appId
     * @param appToken
     * @return
     */
    public static synchronized NotifyAdsManagerNew reopen(Context context, MiuiAdsListener listener, String appPackageName, String appId, String appToken) {
        sInstance = new NotifyAdsManagerNew(context, listener,appPackageName, appId, appToken, null, null, PREFER_NONE);
        return sInstance;
    }

    /**
     * 重新打开，并指定默认的账户，小米账户还是IMEI
     * @param context
     * @param listener
     * @param appPackageName
     * @param appId
     * @param appToken
     * @param flag
     * @return
     */
    public static synchronized NotifyAdsManagerNew reopen(Context context, MiuiAdsListener listener, String appPackageName, String appId, String appToken, int flag) {
        sInstance = new NotifyAdsManagerNew(context, listener,appPackageName, appId, appToken, null, null, flag);
        return sInstance;
    }

    /**
     * 使用外部的ID 重新打开
     * @param context
     * @param listener
     * @param appPackageName
     * @param appId
     * @param appToken
     * @param outerId
     * @return
     */
    public static synchronized NotifyAdsManagerNew reopen(Context context, MiuiAdsListener listener,
            String appPackageName, String appId, String appToken, String outerId) {
        outerId = getTrimString(outerId);
        sInstance = new NotifyAdsManagerNew(context, listener,appPackageName, appId, appToken, outerId, null, PREFER_NONE);
        return sInstance;
    }

    /**
     * 使用指定的默认账户和 TOPICID 重新打开
     * @param context
     * @param listener
     * @param appPackageName
     * @param appId
     * @param appToken
     * @param flag
     * @param topicId
     * @return
     */
    public static synchronized NotifyAdsManagerNew reopen2(Context context, MiuiAdsListener listener,
            String appPackageName, String appId, String appToken, int flag, String topicId) {
        topicId = getTrimString(topicId);
        sInstance = new NotifyAdsManagerNew(context, listener,appPackageName, appId, appToken, null, topicId, flag);
        return sInstance;
    }

    /**
     * 使用外部ID 和 TOPICID 重新打开
     * @param context
     * @param listener
     * @param appPackageName
     * @param appId
     * @param appToken
     * @param outerId
     * @param topicId
     * @return
     */
    public static synchronized NotifyAdsManagerNew reopen2(Context context, MiuiAdsListener listener,
            String appPackageName, String appId, String appToken, String outerId, String topicId) {
        outerId = getTrimString(outerId);
        topicId = getTrimString(topicId);
        sInstance = new NotifyAdsManagerNew(context, listener,appPackageName, appId, appToken, outerId, topicId, PREFER_NONE);
        return sInstance;
    }


    public void pushOneAdsRequest(String adsJsonString, String appPackageName) {
        createAndExcuteDownloader(adsJsonString, 0, appPackageName);
    }

    /*这里的 failedCount 代表该 item 已经下载失败过多少次，而不是最大的失败下载次数*/
    private void createAndExcuteDownloader(String adsJsonString, int failedCount, String appPackageName) {
        NotifyAdsDownloader oneDownloader = new NotifyAdsDownloader(mContext,mPrefer,
                adsJsonString, failedCount, appPackageName, this);
        oneDownloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 在close 后， downloader 会结束运行，最终会调用clear() 进行清空
     */
    public synchronized  void close() {

        if (mReceiverRegistered) {
            mContext.unregisterReceiver(mNetChangeReceiver);
            mReceiverRegistered = false;
        }

        //这里，我们把 sInstance 弄成作废的，这样，下次open 的时候，就会是一个新的 adsManager
        //否则，下次再次进行open 的时候，使用的可能是就的context，导致 Receiver 出现问题
        sInstance = null;
    }

    @Override
    public void onAdsReceived(int status, MiuiAdsCell cell, NotifyAdsDownloader downloader) {
    	DKLog.d(TAG, "on ads received");
        if(NotifyAdsManagerNew.getInstance() == null){
            return;
        }
        //如果是因为 nosense 或者是 upperbound 造成的广告下载失败，我们不进行重新下载
        if (cell == null) {
            NotifyAdsManagerNew.showLog(mAppPackageName + "--->cell is null");
            return;
        }

        if (status == NotifyAdsDef.RET_ERROR) {
            NotifyAdsManagerNew.showLog(mAppPackageName + "--->download failed: " + cell.id);
            cell.failedCount ++;

            if (cell.failedCount < NotifyAdsDef.VALUE_MAX_FAILEDCOUNT) {
                LogUtils.logProcess("下载失败写入缓存" + " " + cell.adsJsonString + "  " + cell.lastShowTime + "  " + cell.failedCount);
                cache2File(cell.adsJsonString, cell.lastShowTime, cell.failedCount);
            } else {
                LogUtils.logProcess("下载失败次数超过 " + NotifyAdsDef.VALUE_MAX_FAILEDCOUNT + " 不写入缓存");
            }
        } else if (status == NotifyAdsDef.RET_OK){
            //如果是白名单的话，那么我们就不增加count 的值了
            if (cell.receiveUpperBound > 0) {
                mSuccessCount++;
                NotifyAdsManagerNew.getInstance().increaseSuccessAds(cell.showType);
            }
            NotifyAdsManagerNew.showLog(mAppPackageName + "--->download sucess: " + "id: " + cell.id + " type: "
                    + cell.showType + " count: "
                    + NotifyAdsManagerNew.getInstance().getCurrentSuccess(cell.showType));
        } else {
            //如果是无效的，那么我们就不存在cache 里面了
            Log.w("com .miui.ads", "广告无效或者超过限制 " + status);
            LogUtils.logProcess("广告无效或者超过限制");
        }

        // 如果下载成功了，那么就返回给上层的APP，这里，在上报给app 的时候，需要再次检查一边限制
        if (mAdsListener != null && status == NotifyAdsDef.RET_OK) {
            if (passReceiveLimit(cell)) {
                handleAdsCellBySDK(cell);
            } else {
                 NotifyAdsManagerNew.showLog(mAppPackageName + "--->reach limit, no return to app");
            }
        }
     }

    private void sendReceiveLog(MiuiAdsCell cell) {
        if (logSender != null) {
            showLog(mAppPackageName + "--->receivedT " + cell.id);
            logSender.receiveTrace(new MiuiAdsTraceCell(cell));
        }
    }

    private void handleAdsCellBySDK(MiuiAdsCell cell) {
    	DKLog.d(TAG, "handle ads cell by sdk");
        sendReceiveLog(cell);

        if (cell.showType == NotifyAdsDef.ADS_TYPE_BUBBLE) {
            BubbleAdsCell bcell = (BubbleAdsCell)cell;
            if (mAdsListener != null) {
                mAdsListener.onBubbleReceived(bcell);
            }
        } else if(cell.showType == NotifyAdsDef.ADS_TYPE_NOTIFY) {
            NotifyAdsCell ncell = (NotifyAdsCell)cell;
            try {
                showLog(mAppPackageName + "--->get notify");
                if (mAdsListener != null) {
                    NotifyAdsCell callBackCell = new NotifyAdsCell(ncell);
                    if (!mAdsListener.onNotifyReceived(callBackCell)) {
                        handleNotifyCell(ncell);
                    }
                }

            } catch(Exception e) {
                Log.e(TAG, "SDK 发出notification 失败");
            }
        }
    }

    /*通过cell 得到一个click 的 relay pending intent*/
    private PendingIntent getPendingIntentByType(NotifyAdsCell cell, int intentType) {
        Intent intent = new Intent(mContext, MiPushRelayTraceService.class);

        Bundle bundle = new Bundle();

        bundle.putAll(cell.toBundle());
        bundle.putInt(NotifyAdsDef.INTENT_FLAG_TYPE, intentType);

        //把pending intent 传递给接受的 service
        if (intentType == NotifyAdsDef.INTENT_TYPE_CLICK && null != mAdsListener) {
            NotifyAdsCell callBackCell = new NotifyAdsCell(cell);
            PendingIntent clickIntent = mAdsListener.getClickPendingIntent(callBackCell);
            if (clickIntent != null) {
                bundle.putParcelable(NotifyAdsDef.INTENT_FLAG_PENDING, clickIntent);
            }else{
            	return null;
            }
        }

        intent.putExtras(bundle);

        int intid = (int)cell.id;
        int hashRequestCode = intid * intid + intentType;

        //这里，需要进行处理，否则第二个发出去的 pending intent 会覆盖第一个
        PendingIntent pendingIntent = PendingIntent.getService(mContext, hashRequestCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    private void setActionButton(NotifyAdsCell cell, int notifyId, NotificationBaseRemoteView view) {


        PendingIntent actionIntent = null;
        NotifyAdsCell callBackCell = new NotifyAdsCell(cell);

        if (cell.actionText != null) {
            if (!TextUtils.isEmpty(cell.actionText.trim())) {
                if (null != mAdsListener) {
                    actionIntent = mAdsListener.getActionPendingIntent(callBackCell);
                }
            }
        }

        if (actionIntent == null) return;

        Intent intent = new Intent(mContext, MiPushRelayTraceService.class);
        Bundle bundle = new Bundle();

        bundle.putAll(cell.toBundle());
        bundle.putInt(NotifyAdsDef.INTENT_FLAG_TYPE,
                NotifyAdsDef.INTENT_TYPE_CLICK);

        //我们把需要发出去的notification id 记录在这里，当我们的action 被点击的时候，我们需要把原先的notification remove
        bundle.putInt(NotifyAdsDef.INTENT_FLAG_NOTIFYID,
                notifyId);

        bundle.putParcelable(NotifyAdsDef.INTENT_FLAG_PENDING, actionIntent);
        intent.putExtras(bundle);

        int intid = (int) cell.id;
        int hashRequestCode = intid * intid + NotifyAdsDef.INTENT_TYPE_ACTIONCLICK;

        PendingIntent pendingIntent = PendingIntent.getService(mContext,
                hashRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        view.setActionButton(cell.actionText, pendingIntent);
    }

    private void handleNotifyCell(NotifyAdsCell cell) {
    	DKLog.d(TAG, "handler notify cell");
        Bitmap showPicture = null;
        int    smallIcon = 0;

        showLog("sdk handle notify");

        int notifyId = cell.actionUrl.hashCode() + cell.priText.hashCode();
        smallIcon = mAdsListener.getSmallIconID();
        Notification.Builder builder = new Notification.Builder(mContext);

        if (smallIcon != 0) builder.setSmallIcon(smallIcon);

        //以前设置的RemoteView无法显示
        builder.setAutoCancel(true);
		builder.setTicker(cell.titText);
		builder.setContentTitle(cell.priText);
		builder.setContentText(cell.secText);
		builder.setWhen(System.currentTimeMillis());

        /*
        NotificationBaseRemoteView remoteViews = new NotificationBaseRemoteView(mContext);

        remoteViews.setTitles(cell.priText,cell.secText);
        remoteViews.setIcon(smallIcon);
        setActionButton(cell, notifyId, remoteViews);

        builder.setContent(remoteViews);
        builder.setTicker(cell.titText)
                .setAutoCancel(true);
        */

        //设置点击和删除的 pending intent
        PendingIntent clickPendingIntent = getPendingIntentByType(cell, NotifyAdsDef.INTENT_TYPE_CLICK);
        if(clickPendingIntent == null){
        	return;
        }
        builder.setContentIntent(clickPendingIntent);

        PendingIntent deletePendingIntent = getPendingIntentByType(cell, NotifyAdsDef.INTENT_TYPE_DELETE);
        builder.setDeleteIntent(deletePendingIntent);

        Notification notification = builder.build();

        //如果有大图，那么我们把大图进行设置
        if (!TextUtils.isEmpty(cell.getDownloadedImagePath())) {
            showPicture = BitmapFactory.decodeFile(cell.getDownloadedImagePath());
            if (showPicture != null) {
                showLog("big picture");
                NotificationBigRemoteView bigRemoteViews = new NotificationBigRemoteView(mContext);
                bigRemoteViews.setTitles(cell.priText,cell.secText);
                bigRemoteViews.setIcon(smallIcon);
                bigRemoteViews.setBigPicture(showPicture);
                setActionButton(cell, notifyId, bigRemoteViews);
                notification.bigContentView = bigRemoteViews;
            }
        }

        NotificationManager manager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(notifyId, notification);
    }

    //这个是在给APP 发送时的次数检查
    private boolean passReceiveLimit(MiuiAdsCell cell) {
        int successCount = 0;

        //修复白名单测试的bug
        if (cell.receiveUpperBound <= 0) {
            NotifyAdsManagerNew.showLog("white user");
            return true;
        }

        int upperBound = 0;

        switch(cell.showType) {
        case NotifyAdsDef.ADS_TYPE_BUBBLE:
            upperBound = cell.receiveUpperBound * BUBBLE_LIMIT_TIMES;
            NotifyAdsManagerNew.showLog("bubble uplimit: " + upperBound);
            successCount = mPrefer.getInt(NotifyAdsDef.PREFER_KEY_BUBBLE_SUCCESS_COUNT, 0);
            break;
        case NotifyAdsDef.ADS_TYPE_NOTIFY:
            upperBound = cell.receiveUpperBound;
            NotifyAdsManagerNew.showLog("notify uplimit: " + upperBound);
            successCount = mPrefer.getInt(NotifyAdsDef.PREFER_KEY_NOTIFY_SUCCESS_COUNT, 0);
            break;
        }

        if (successCount <= upperBound) {
            return true;
        } else {
            NotifyAdsManagerNew.showLog("reach up limit---already count： " + successCount + " 上限: " + upperBound);
        }

        return false;
    }

    private void cache2File(String adsJsonString, long lastShowTime, int failedCount) {
        mCacheCount++;
        LogUtils.logProcess("存入cache 的数量: " + mCacheCount);
        mAdsCache.appendInfo(adsJsonString, lastShowTime, failedCount);
        mAdsCache.flushFile();
    }

    @Override
    public void onNetChanged() {

        if (!NetUtils.canDownloadAds(mContext)) return;

        //否则，我们开始下载cache 的广告
        mIp = NetUtils.getLocalIPAddress();
        pushCellsInCacheFile();
    }

    //这个 api 开放给那些需要自定义alias的使用，设置新的alias
    public void setNewAlias(String outerId) {
        if (NetUtils.isEmptyString(outerId)) {
            return;
        }

        mOuterId = outerId;

        //如果通道没有注册成功，直接返回，此时不应该setAlias()
        if (!this.mInitialSuccess) {
            return;
        }

        mHandler.sendEmptyMessage(MESSAGE_SET_ALIAS);
    }

    @Override
    public void onAccountChanged() {
        // TODO Auto-generated method stub
        showLog(mAppPackageName + "--->account change: " + mAlias + " thread: " + Thread.currentThread().getName());

        //如果通道没有注册成功，直接返回，此时不应该setAlias(),而是应该保留，时序问题
        if (!this.mInitialSuccess) {
            return;
        }

        mHandler.sendEmptyMessage(MESSAGE_SET_ALIAS);
    }

    private void getAccountInfo() {
        mUserId = NetUtils.getXiaomiUserId(mContext);
        mImei = NetUtils.getIMEI(mContext);
    }

    private void pushCellsInCacheFile() {
        mCacheCount = 0;
        ArrayList<NotifyAdsCacheCell> list = mAdsCache.getAdsCacheCellFromCacheFile();
        mFileCacheCells.addAll(list);
        downloadCacheAds();
    }

    //首先，我们主动删除原先已经存在了很久的广告， 这样，就可以节约我们的空间
    private void doCleanCacheFolder() {

        File folder = mContext.getDir(NotifyAdsDef.ADS_FOLDER, Context.MODE_PRIVATE);
        File []fileList = folder.listFiles();
        long sumSize  = 0;

        ArrayList<File> fileArray = new ArrayList<File>();

        //加强错误判断
        if (fileList == null) return;

        for (File file : fileList) {
            if (!file.isDirectory()) {
                fileArray.add(file);
                sumSize += file.length();
            }
        }

        LogUtils.logProcess("docleancache  " + folder.getAbsolutePath() +
                "  " + (folder == null) + "   " + fileArray.size());

        //按照广告时间进行广告的删除
        if (sumSize >= MAX_CACHE_SIZE) {
            TreeSet<File> sortedSet = new TreeSet<File>(
                    new FileComparatorByLastModifier());

            sortedSet.addAll(fileArray);
            fileArray.clear();
            Iterator<File> itor = sortedSet.iterator();
            while (itor.hasNext()) {
                File file = itor.next();
                fileArray.add(file);
            }

            //我们删除掉前面一半的广告
            for (int i=0; i<fileArray.size()/2; i++) {
                File file = fileArray.get(i);
                file.delete();
                LogUtils.logProcess("delet4e  " + file.getAbsolutePath());
            }
        }
    }


    // the function is used to send trace tasks one time
    private void downloadCacheAds() {

        if (!NetUtils.canDownloadAds(mContext))
            return;

        LogUtils.logProcess("cache 个数: " + mFileCacheCells.size());
        Iterator<NotifyAdsCacheCell> itor = mFileCacheCells.iterator();

        int sum = 0;

        while (itor.hasNext() && sum < VALUE_MAX_TRACETASK) {
            NotifyAdsCacheCell cell = itor.next();

            //如果该广告的过期时间在当前时间之前，那么直接忽略，debug 模式忽略
            if (cell.lastShowTime != 0L && cell.lastShowTime < System.currentTimeMillis()) {
                LogUtils.logProcess("过期，所以跳过, lastShow: " + cell.lastShowTime + " " + System.currentTimeMillis());
                itor.remove();
                continue;
            }

            //如果下载失败次数超过界限，那么就不再下载了
            if (cell.failedCount >= NotifyAdsDef.VALUE_MAX_FAILEDCOUNT) {
                LogUtils.logProcess("cache 的失败次数超过上限，不正常");
                itor.remove();
                continue;
            }


            sum ++;
            createAndExcuteDownloader(cell.adsJsonString, cell.failedCount, mAppPackageName);
            itor.remove();
        }


        //如果还有没有发送的cache 广告，那么下次发送
        if (mFileCacheCells.size() > 0) {
            LogUtils.logProcess("cache 太多，分批进行下载");
            Message msg = mHandler.obtainMessage();
            msg.what = MESSAGE_SEND_TRACELOG;
            mHandler.sendMessageDelayed(msg, 3000);
        }
    }

    public static void showLog(String info) {
        if (NotifyAdsDef.LOG_OPEN) {
            Log.d(TAG, info);
        }
    }
}
