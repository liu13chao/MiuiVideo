package com.xiaomi.miui.pushads.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.miui.video.util.DKLog;
import com.xiaomi.miui.pushads.sdk.NotifyAdsManager.NetState;
import com.xiaomi.miui.pushads.sdk.common.MiuiAdsCell;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * 异步广告下载类，现在的广告下载，支持候选广告方案
 * @author liuwei
 *
 */
class NotifyAdsDownloader extends AsyncTask<String, Integer, Integer> {
    private static final String ADS_DOWNLOAD = "ADS_DOWNLOAD";
    private Context mContext;
    private INotifyAdsListener mAdsListener;
    private MiuiAdsCell mAdsCell;
    private String mAdsJsonString;
    private int mFailedCount;
    private SharedPreferences mPrefer;
    private String mAppPackageName;

    //一天的毫秒数
    private static final long ONE_DAY_MISECONDS = 24 * 3600 * 1000;

    public NotifyAdsDownloader(Context context, SharedPreferences pre, String adsJsonString, int failedCount, String appPackageName, INotifyAdsListener listener) {
        mContext = context;
        mAdsListener = listener;
        mAdsJsonString = adsJsonString;
        mPrefer = pre;
        mAppPackageName = appPackageName;
    }

    @Override
    protected Integer doInBackground(String... params) {
        //可能会受到 nosense 或者是超过限制的影响
        int ret = parseAdsResponse(mAdsJsonString);


        if (ret != NotifyAdsDef.RET_OK) {
            LogUtils.logProcess("广告解析失败 " + ret);
            return ret;
        }

        if (mAdsCell.showType == NotifyAdsDef.ADS_TYPE_NOTIFY) {
            ret = downloadXiaomiAds(mContext.getDir(NotifyAdsDef.ADS_FOLDER, Context.MODE_PRIVATE));
        }

        return ret;
    }


    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (null != mAdsListener) {
            LogUtils.logProcess("下载 post 的结果是: " + integer);
            mAdsListener.onAdsReceived(integer, mAdsCell, this);
        }
    }

    //如果是因为调用了 asyncTask.cancel() 让task 中断的话，虽然doInBackground 返回，但是onPostExecute 是不会被调用的
    //而是调用 onCancelled
    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(ADS_DOWNLOAD, "onCancelled");
    }


    private String getResonByCode(int ret) {
        String str = "";
        switch(ret) {
        case NotifyAdsDef.RET_OK:
            str = "成功";
            break;
        case NotifyAdsDef.RET_ERROR:
            str = "未知原因";
            break;
        case NotifyAdsDef.RET_EXPIRED:
            str = "过期";
            break;
        case NotifyAdsDef.RET_LIMITREACH:
            str = "到达上限";
            break;
        case NotifyAdsDef.RET_NOSENSE:
            str = "广告失效";
            break;
        case NotifyAdsDef.RET_NOTMATCHED:
            str = "消息不匹配";
            break;

        }

        return str;
    }

    /** 通过NetUtils.getHttpAds()得到一个httpResponse, 是一个json字符串，解析这个字符串
     * @param httpResponse
     * @return 返回XiaomiAds的数组，有几个广告，数组就有几个元素.
     **/
    private int parseAdsResponse(String adsJsonString) {

        JSONObject adsJson = null;
        try {
            adsJson = new JSONObject(adsJsonString);
        } catch(JSONException e) {
            return NotifyAdsDef.RET_ERROR;
        }

        //我们先对主广告进行判断
        int ret = passFilterRules(adsJson);
        NotifyAdsManager.showLog("解析参数并检查, 返回结果: " + getResonByCode(ret));

        if (ret != NotifyAdsDef.RET_OK) return ret;

        //然后， 经过转发策略， 看是否使用候选广告
        ret = passAdsDistributePolicy(adsJson);

        if (mAdsCell != null) {
            NotifyAdsManager.showLog("广告获取最终结果： " + ret + " 类型: " + mAdsCell.showType);
        }

        return ret;
    }

    //这里的upper Bound 计算有问题，需要进行持久化
    /**
     * 匹配广告客户端的过滤策略
     * @param adsJsonObject
     * @return
     */
    private int passFilterRules(JSONObject adsJsonObject) {
        if (adsJsonObject == null)
            return NotifyAdsDef.RET_ERROR;

        String status = adsJsonObject.optString(NotifyAdsDef.JSON_TAG_STATUS);
        if (!status.equals(NotifyAdsDef.HTTP_RESPONSE_STATUS_SUCCESS)) {
            return NotifyAdsDef.RET_ERROR;
        }

        int nosense = adsJsonObject.optInt(NotifyAdsDef.JSON_TAG_NONSENSE);
        if (nosense != 0) {
            Log.e(NotifyAdsDef.TAG, "广告无效标志设置: " + nosense);
            LogUtils.logProcess("广告无效");
            return NotifyAdsDef.RET_NOSENSE;
        }

        //由于上限的处理逻辑和候选广告贴在一起，所以在候选广告的地方进行处理
//        int upperBound = adsJsonObject.optInt(NotifyAdsDef.JSON_TAG_UPPERBOUND);

        long lastShow = adsJsonObject.optLong(
                NotifyAdsDef.JSON_TAG_LASTSHOWTIME, 0L);
        NotifyAdsManagerNew.showLog("expireTime: " + lastShow + " currentTime: " + System.currentTimeMillis());
        if (lastShow != 0L) {
            if (lastShow < System.currentTimeMillis()) {
                LogUtils.logProcess("广告已经过期 " + "lastShow: " + lastShow
                        + " current: " + System.currentTimeMillis());
                return NotifyAdsDef.RET_EXPIRED;
            }
        }

        return NotifyAdsDef.RET_OK;
    }

    private int getShowTypeByJSON(JSONObject adsJsonObject) {
        return adsJsonObject.optInt(NotifyAdsDef.JSON_TAG_SHOWTYPE);
    }

    private MiuiAdsCell getAdsCellByType(int showType) {
        MiuiAdsCell cell = new MiuiAdsCell();
        switch (showType) {
        // 每次接受到广告的时候，我们需要进行判断，是否系统禁用了本app 的通知功能
        case NotifyAdsDef.ADS_TYPE_BUBBLE:
            cell = new BubbleAdsCell();
            break;

        case NotifyAdsDef.ADS_TYPE_NOTIFY:
            cell = new NotifyAdsCell();
            break;

        default:
            break;
        }

        cell.failedCount = mFailedCount;
        cell.adsJsonString = mAdsJsonString;
        return cell;
    }

    private boolean passLimitConstrain(int upperBound, int showType) {
        synchronized(mPrefer) {
            long current = System.currentTimeMillis();
            long start = mPrefer.getLong(NotifyAdsDef.PREFER_KEY_STARTTIME, 0);

            if (start == 0) {
                mPrefer.edit().putLong(NotifyAdsDef.PREFER_KEY_STARTTIME, current).commit();
                start = current;
                return true;
            }

            // 如果当前和原先的limit 已经超过了一天，那么就重新设置
            if (current - start > 24 * 3600 * 1000) {
                mPrefer.edit().putLong(NotifyAdsDef.PREFER_KEY_STARTTIME, 0).commit();
                mPrefer.edit().putInt(NotifyAdsDef.PREFER_KEY_NOTIFY_SUCCESS_COUNT, 0).commit();
                mPrefer.edit().putInt(NotifyAdsDef.PREFER_KEY_BUBBLE_SUCCESS_COUNT, 0).commit();
                return true;
            } else {
                //如果在一天之内，那么我们看是否超出了上限
                if (showType == NotifyAdsDef.ADS_TYPE_NOTIFY) {
                    int notifyCount = mPrefer.getInt(NotifyAdsDef.PREFER_KEY_NOTIFY_SUCCESS_COUNT, 0);
                    if (notifyCount < upperBound) return true;
                } else if(showType == NotifyAdsDef.ADS_TYPE_BUBBLE) {
                    int bubbleCount = mPrefer.getInt(NotifyAdsDef.PREFER_KEY_BUBBLE_SUCCESS_COUNT, 0);
                    if (bubbleCount < upperBound * NotifyAdsDef.BUBBLE_LIMIT_TIMES) return true;
                }
            }

            LogUtils.logUpLimit("超过了每天接受广告的上限");
            return false;
        }
    }

    /**
     * 匹配广告的分发策略，看是采用主要广告还是候选广告
     * @param adsJsonObject
     * @return
     */
    private int passAdsDistributePolicy(JSONObject adsJsonObject) {
        int showType = getShowTypeByJSON(adsJsonObject);

        //如果notify 被禁用， 而且首选的是Notify 的话，那么设置这个值
        boolean canOnlyBubble = false;
        boolean mainAdsReachUpperLimit = false;

        try {
            Class policyClass = Class.forName("miui.util.NotificationFilterHelper");
            Method method = policyClass.getMethod("canSendNotifications", new Class[]{Context.class, String.class});
            NotifyAdsManager.showLog(mAppPackageName);
            //判断该app 是否收到了通知栏发布限制
            canOnlyBubble = (Boolean)method.invoke(null, mContext, mAppPackageName);
            canOnlyBubble = !canOnlyBubble;
        } catch(Exception e) {
            Log.d("NotifyAdsDownloader", "reflect errors!");
            e.printStackTrace();
        }

        NotifyAdsManager.showLog("是否禁用了通知栏广告 " + canOnlyBubble);

        int upperBound = adsJsonObject.optInt(NotifyAdsDef.JSON_TAG_UPPERBOUND);

        // 如果是在白名单里面的用户，那么upperBound 将会设置为0 ，
        if (upperBound >0) {
            mainAdsReachUpperLimit = !passLimitConstrain(upperBound, showType);
        } else {
            mainAdsReachUpperLimit = false;
        }

        NotifyAdsManager.showLog("是否达到上限 " + mainAdsReachUpperLimit);

        try {
            if (mainAdsReachUpperLimit || (showType == NotifyAdsDef.ADS_TYPE_NOTIFY && canOnlyBubble)) {
                // 否则，我们需要得到 subAdInfo 的信息
                NotifyAdsManager.showLog("使用候选广告 ");
                long subAdId = adsJsonObject.optLong(NotifyAdsDef.JSON_TAG_SUBADID);
                if (subAdId <= 0) {
                    NotifyAdsManager.showLog("没有候选广告 ");
                    return NotifyAdsDef.RET_NOTMATCHED;
                }

                String subAdInfoString = adsJsonObject
                        .optString(NotifyAdsDef.JSON_TAG_SUBADINFO);

                JSONObject subAdJson = new JSONObject(subAdInfoString);
                int subShowType = getShowTypeByJSON(subAdJson);

                if (subShowType == NotifyAdsDef.ADS_TYPE_NOTIFY && canOnlyBubble) {
                    return NotifyAdsDef.RET_NOT_PERMITTED;
                }

                int ret = passFilterRules(subAdJson);
                NotifyAdsManager.showLog("候选广告解析参数并检查： " + ret);
                if (ret != NotifyAdsDef.RET_OK)
                    return ret;

                mAdsCell = getAdsCellByType(subShowType);
                mAdsCell.setValuesByJson(subAdJson);
            } else {
              NotifyAdsManager.showLog("使用主广告 ");
              mAdsCell = getAdsCellByType(showType);
              mAdsCell.setValuesByJson(adsJsonObject);
            }

            return NotifyAdsDef.RET_OK;
        } catch(JSONException e) {
            return NotifyAdsDef.RET_ERROR;
        }
    }

    /**
     *
     * @param parentFolder
     * @return 表示了下载的返回状态
     */
    private int downloadXiaomiAds(File parentFolder) {
        // TODO: Check wheter the ads already exists.
        int ret = NotifyAdsDef.RET_OK;
        NotifyAdsCell cell = (NotifyAdsCell) mAdsCell;
        String adsImage = cell.imgUrl;

        if (null == adsImage) return NotifyAdsDef.RET_ERROR;

        ret = DownloadUtils.downFile(mContext, parentFolder, adsImage, cell);
        LogUtils.logProcess("下载广告 imgUrl: " + adsImage + " 结果： " + ret);

        if (isCancelled() || ret != NotifyAdsDef.RET_OK) {
            if (isCancelled()) {
                LogUtils.logProcess("asynctask 被cancel");
            } else {
                NetState curState = NetUtils.getNetState(mContext);
                LogUtils.logProcess("网络类型改变，中断下载: " + curState + " " + ret);
            }
        }

        return ret;
    }
}
