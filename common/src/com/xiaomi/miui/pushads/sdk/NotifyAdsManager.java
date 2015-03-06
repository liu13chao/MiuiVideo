package com.xiaomi.miui.pushads.sdk;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushClient.MiPushClientCallback;
import com.xiaomi.miui.pushads.sdk.common.MiuiAdsCell;
import com.xiaomi.miui.pushads.sdk.common.MiuiAdsReceivedListener;

/**
 * 这个类负责打开后台的广告下载功能，用户调用open 后会自动进行下载，下载完一个后会调用通过listener 回调通知者
 * 如果广告下载失败，那么会放入到本地的 cache 文件里面， 等到网络恢复的时候， 从cache 文件读出后，再次进行下载
 * 网络监听器作为一个动态注册的机制
 *
 * version2.0 / 2013.7.8  需要连接上新的push server 进行消息的推送
 * http://wiki.n.miliao.com/xmg/2.%E5%AE%A2%E6%88%B7%E7%AB%AFSDK%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97
 * 在 NotifyAdsManagerNew 里面，可以直接发出trace ，不需要APP 的参与，更加简洁，这个接口作废
 * 这个接口作废
 *
 * @author liuwei
 *
 */

public class NotifyAdsManager extends MiPushClientCallback implements INotifyAdsListener {

    //单例模式，
    public  static final String AdSPREFIX =    "com.xiaomi.miui.pushads.sdk";
    public  static final String TAG = "ads-notify-fd5dfce4";
    private static final String CATEGORY_UUID = "fd5dfce4-64df-4434-aa66-2a70ff84a9c4";
    private static  final String CACHE_FILE_NAME = AdSPREFIX + ":adscache";
    static final int BUBBLE_LIMIT_TIMES = 4;
    private static  NotifyAdsManager sInstance;

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
    private MiuiAdsReceivedListener mAdsListener;

    //把file 里面的缓存调入内存并进行发送
    private ArrayList<NotifyAdsCacheCell> mFileCacheCells;

    //用于进行分批发送cache 的广告，如果广告没有过期的话
    private Handler mHandler;

    //用于统计广告下载的次数
    private int mSuccessCount  = 0;
    private int mCacheCount    = 0;

    // maximum 10 trace task to send log each time
    private static final int VALUE_MAX_TRACETASK = 10;
    private static final int MESSAGE_SEND_TRACELOG = 1;
    private static final int MESSAGE_RESET_ALIAS   = 2;
    private static final int MESSAGE_SET_ALIAS     = 3;
    private static final int MESSAGE_INIT_CALLBACK = 4;
    private static final int MESSAGE_CHANNEL_SUCCESS = 5;

    private static final long MAX_CACHE_SIZE       = 20 * 1024 * 1024;

    //version 2.0 为了接入新的push server，我们需要 appPackageName, appId 和 appToken 三个参数
    private String mAppPackageName;
    private String mAppId;
    private String mAppToken;
    private SharedPreferences mPrefer;

    //这个用于在APP 不想使用米聊号或者IMEI 时， 自行传入的ID， 这样，APP 需要在server 端自行输入这个ID
    private String mOuterId;
    private boolean mInitialSuccess = false;

    public enum NetState {
        NONE,
        Wifi, // Wifi 状态
        MN2G, // 移动 2G
        MN3G, // 移动 3G
        MN4G  // 移动 4G
    }

    private NotifyAdsManager(Context context, MiuiAdsReceivedListener listener) {
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

    private NotifyAdsManager(Context context, MiuiAdsReceivedListener listener, String appPackageName, String appId, String appToken, String outerId) {
        this(context, listener);

        if (!NetUtils.isEmptyString(outerId)) {
            mOuterId = outerId;
        }

        mAppPackageName = appPackageName;
        mAppId = appId;
        mAppToken = appToken;

        //set category
        String category = getNamedCategory();
        setCategory(category);

        //在这里，我们把 notifyManager 注册成为 新的push channel 的听众，接受来自push channel 的各种消息
        showLog("通道进行初始化");
        MiPushClient.initialize(mContext, mAppId, mAppToken,this);
    }

    private void initMembers() {
        mIp = NetUtils.getLocalIPAddress();
        mImei = NetUtils.getIMEI(mContext);
        mUserId = NetUtils.getXiaomiUserId(mContext);

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
                case MESSAGE_SET_ALIAS:
                    setAlias();
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

    @Override
    public void onReceiveMessage(String content, String alias, String topic, boolean flag) {
        showLog("接受到消息 " + content + "##" + topic + "##" + alias);
        //如果接受到了不是自己 alias 的消息，那么
        //这样做的前提是，如果同时使用 ads sdk 和 mi push sdk,那么必须保证两个的category 不一样
        //@liuwei, @wuxiaojun 先记下
        if (NetUtils.isEmptyString(mAlias)) {
            showLog("没有有效alias，忽略消息 " + content + "##" + topic + "##" + alias);
            return;
        }

        if (!NetUtils.isEmptyString(alias) && !NetUtils.isEmptyString(mAlias)) {
            if (!TextUtils.equals(mAlias, alias)) {
                showLog("接受到不同alias 的消息，注销旧的 " + content + "##" + topic + "##" + alias);
                MiPushClient.unsetAlias(mContext, alias, getCategory());
                return;
            }
        }

        pushOneAdsRequest(content, mAppPackageName);
    }

    //这个函数可能在主线程里面调用，也可能在次线程里面调用，所以使用handler 切换到主线程
    @Override
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
            showLog("通道进行初始化OK");
            mHandler.sendEmptyMessage(MESSAGE_SET_ALIAS);
            mHandler.sendEmptyMessage(MESSAGE_CHANNEL_SUCCESS);
        } else {
            showLog("通道初始化失败， 已经通知了app，需要重新 open 通道");
        }

    }

    /**
     *我们设置该设备的别名为该设备的userid (如果存在的话)，否则设置为该设备的imei，
     *如果用户有自定义的id，那么就使用自定义的id 进行
     */
    private void setAlias() {
        //如果用户没有指定私有的 id，那么就使用米聊号或者IMEI号
        if (!NetUtils.isEmptyString(mOuterId)) {
            mAlias = new String(mOuterId);
        } else {
            if (!NetUtils.isEmptyString(mUserId)) {
                mAlias = new String(mUserId);
            } else if(!NetUtils.isEmptyString(mImei)) {
                mAlias = new String(mImei);
            } else {
                Log.e(TAG, "没有 outerId, userId, imei, 设置别名失败");
                return;
            }
        }

        //这个是push 广告sdk 统一的前缀名称
        mAlias = AdSPREFIX + mAlias;
        showLog("设置别名: " + mAlias + " thread: " + Thread.currentThread().getName());
        MiPushClient.setAlias(mContext, mAlias, getCategory());
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

    @Override
    public void onSubscribeResult(long resultCode, String reason,
            String topic) {
    }

    @Override
    public void onUnsubscribeResult(long resultCode, String reason,
            String topic) {
    }

    @Override
    public void onCommandResult(String command, long resultCode, String reason, List<String> params) {
        boolean setAliasFlag = false;

        if (resultCode != 0) {
            showLog("命令失败: " + command + " code: " + resultCode + " reason: " + reason);
            for (int i=0; i<params.size(); i++) {
                showLog("param: " + params.get(i));
            }
        }

        if (TextUtils.equals(MiPushClient.COMMAND_SET_ALIAS, command)) {
            for (int i=0; i<params.size(); i++) {
                String alias = params.get(i);
                if (TextUtils.equals(mAlias, alias)) {
                    setAliasFlag = true;
                    showLog("设置别名成功: " + mAlias);
                }
            }

            if (!setAliasFlag) {
                showLog("设置别名失败，重新设置: " + mAlias);
                mHandler.sendEmptyMessage(MESSAGE_RESET_ALIAS);
            }
        }
    }

    private void initReceiver() {
        mNetChangeReceiver = new NotifyAdsNetReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION);
        mContext.registerReceiver(mNetChangeReceiver, filter);
        mReceiverRegistered = true;
    }

    //如果在app oncreate 里面调用，是不会出现这种情况的，不过以防万一
    private static void replaceContext(NotifyAdsManager manager, Context context) {
        if (manager != null && context != null) {
            if (manager.mContext != context) {
                manager.mContext = context;
            }
        }
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

    public static synchronized NotifyAdsManager getInstance() {
        return sInstance;
    }

    //这个函数应该被 app 调用，在 application 那里进行统一的注册和监听
    //在receiver 那里， 就只进行 push 操作。 每次广播到来的时候，很可能会重新创建一个 receiver 来进行处理
    public static synchronized NotifyAdsManager open(Context context, MiuiAdsReceivedListener listener, String appPackageName, String appId, String appToken) {
        showLog("打开的 app 是: " + appPackageName);
        if (sInstance == null) {
            sInstance = new NotifyAdsManager(context, listener, appPackageName, appId, appToken, "");
        }

        replaceContext(sInstance, context);
        return sInstance;
    }

    //这个API 用于第三方的APP 不想使用米聊号或者IMEI 号时，进行调用，但是如果客户端调用这个接口，那么
    //服务器端在发送的时候，需要在输入的target 里面，使用这个id
    public static synchronized NotifyAdsManager open(Context context, MiuiAdsReceivedListener listener, String appPackageName,
            String appId, String appToken, String outerId) {
        if (sInstance == null) {
            //封住外部传递过来的为0 的情况
            if (NetUtils.isEmptyString(outerId))
                outerId = "";

            sInstance = new NotifyAdsManager(context, listener, appPackageName, appId, appToken, outerId);
        }

        replaceContext(sInstance, context);
        return sInstance;
    }

    //让用户重新进行参数设置，替换原先的失效的 sInstance
    public static synchronized NotifyAdsManager reopen(Context context, MiuiAdsReceivedListener listener, String appPackageName, String appId, String appToken) {
        sInstance = new NotifyAdsManager(context, listener,appPackageName, appId, appToken, "");
        return sInstance;
    }

    //让用户重新进行参数设置，替换原先的失效的 sInstance
    public static synchronized NotifyAdsManager reopen(Context context, MiuiAdsReceivedListener listener,
            String appPackageName, String appId, String appToken, String outerId) {
        sInstance = new NotifyAdsManager(context, listener,appPackageName, appId, appToken, outerId);
        return sInstance;
    }


    private void pushOneAdsRequest(String adsJsonString, String appPackageName) {
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

        //如果是因为 nosense 或者是 upperbound 造成的广告下载失败，我们不进行重新下载
        if (cell == null) {
            NotifyAdsManager.showLog("返回广告为null");
            return;
        }

        if (status == NotifyAdsDef.RET_ERROR) {
            NotifyAdsManager.showLog("广告下载失败: " + cell.id);
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
                NotifyAdsManager.getInstance().increaseSuccessAds(cell.showType);
            }
            NotifyAdsManager.showLog("广告下载成功: " + "id: " + cell.id + " 类型: "
                    + cell.showType + " 成功次数: "
                    + NotifyAdsManager.getInstance().getCurrentSuccess(cell.showType));
        } else {
            //如果是无效的，那么我们就不存在cache 里面了
            Log.w("com.miui.ads", "广告无效或者超过限制 " + status);
            LogUtils.logProcess("广告无效或者超过限制");
        }

        // 如果下载成功了，那么就返回给上层的APP，这里，在上报给app 的时候，需要再次检查一边限制
        if (mAdsListener != null && status == NotifyAdsDef.RET_OK) {
            if (passReceiveLimit(cell)) {
                NotifyAdsManager.showLog("===========给APP 发送广告信息");
                mAdsListener.onReceived(cell);
            } else {
                 NotifyAdsManager.showLog("广告数量超过限制，不返回给APP");
            }
        }
     }

    //这个是在给APP 发送时的次数检查
    private boolean passReceiveLimit(MiuiAdsCell cell) {
        int successCount = 0;

        //修复白名单测试的bug
        if (cell.receiveUpperBound <= 0) {
            NotifyAdsManager.showLog("白名单用户");
            return true;
        }

        int upperBound = 0;

        switch(cell.showType) {
        case NotifyAdsDef.ADS_TYPE_BUBBLE:
            upperBound = cell.receiveUpperBound * BUBBLE_LIMIT_TIMES;
            NotifyAdsManager.showLog("冒泡上限: " + upperBound);
            successCount = mPrefer.getInt(NotifyAdsDef.PREFER_KEY_BUBBLE_SUCCESS_COUNT, 0);
            break;
        case NotifyAdsDef.ADS_TYPE_NOTIFY:
            upperBound = cell.receiveUpperBound;
            NotifyAdsManager.showLog("通知上限: " + upperBound);
            successCount = mPrefer.getInt(NotifyAdsDef.PREFER_KEY_NOTIFY_SUCCESS_COUNT, 0);
            break;
        }

        if (successCount <= upperBound) {
            return true;
        } else {
            NotifyAdsManager.showLog("广告次数超过上限---已经获得次数： " + successCount + " 上限: " + upperBound);
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

        String newAlias = AdSPREFIX + outerId;

        mOuterId = outerId;
        //如果是相同的 alias，那么就返回，不进行处理了
        if (TextUtils.equals(newAlias, mAlias)) {
            return;
        }

        if (!NetUtils.isEmptyString(mAlias)) {
            MiPushClient.unsetAlias(mContext, mAlias, getCategory());
        }

        MiPushClient.setAlias(mContext, newAlias, getCategory());
        mAlias = newAlias;
    }

    @Override
    public void onAccountChanged() {
        // TODO Auto-generated method stub
        showLog("小米账户发生了改变: " + mAlias + " thread: " + Thread.currentThread().getName());
        //如果通道没有注册成功，直接返回，此时不应该setAlias()
        if (!this.mInitialSuccess) {
            return;
        }

        mUserId = NetUtils.getXiaomiUserId(mContext);
        String newAlias = "";

        //如果还没有启动好，那么就返回，现在的onInitailize() 在次线程返回，所以主线程处理可能还没有完毕
        //而且push sdk 分起线程，如果APP 使用APP 作为notifyAdsManager 的容器，那么可能一个alias 好了，另外一个好没有
        //好，所以不能单单从LOG 上面来看。 建议APP 使用 service 作为 NotifyAdsManager 的container()
        //如果用户自定义了alias 帐号，那么手机切换米聊号不会产生影响
        if (NetUtils.isEmptyString(mAlias) || !NetUtils.isEmptyString(mOuterId)) {
            if (!NetUtils.isEmptyString(mOuterId)) {
                showLog("用户使用的是自定义的账户: " + mOuterId);
            }
            return;
        }

        //如果原先设置的alias 和新的不一样，那么我们需要重新设置别名
        if (!NetUtils.isEmptyString(mUserId)) {
            newAlias = AdSPREFIX + mUserId;
        } else if (!NetUtils.isEmptyString(mImei)){
            newAlias = AdSPREFIX + mImei;
        } else {
            Log.e(TAG, "没有 userid, imei onAccountChanged 重设alias 失败");
            return;
        }

        if (!newAlias.equals(mAlias)) {
            showLog("取消旧的账户: " + mAlias);
            MiPushClient.unsetAlias(mContext, mAlias, getCategory());
            showLog("设置新的账户: " + newAlias);
            MiPushClient.setAlias(mContext, newAlias, getCategory());
            mAlias = newAlias;

            //账户改变， 设置客户端接受上限base 为0
            mPrefer.edit().putLong(NotifyAdsDef.PREFER_KEY_STARTTIME, 0).commit();
            mPrefer.edit().putInt(NotifyAdsDef.PREFER_KEY_NOTIFY_SUCCESS_COUNT, 0).commit();
            mPrefer.edit().putInt(NotifyAdsDef.PREFER_KEY_BUBBLE_SUCCESS_COUNT, 0).commit();
        }
    }

    private void pushCellsInCacheFile() {
        mCacheCount = 0;
        ArrayList<NotifyAdsCacheCell> list = mAdsCache.getAdsCacheCellFromCacheFile();
//        LogUtils.logProcess("获取cache并发送" + "  " + NetUtils.getNetState(mContext)
//                + "  " + list.size());
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
