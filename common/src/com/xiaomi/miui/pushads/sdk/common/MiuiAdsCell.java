package com.xiaomi.miui.pushads.sdk.common;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.xiaomi.miui.pushads.sdk.NotifyAdsDef;

/**
 * 这个类是一个广告的通用的类， 用于描述广告的通用属性， 每种特定的广告实现应该继承该类
 * @author liuwei
 *
 */
public class MiuiAdsCell {
    //该广告的id
    public long   id;

    //该广告的展示类型type
    public int    showType;

    //描述广告的 json string
    public String adsJsonString;

    //这个该广告是否有意义
    public int    nonsense;

    //表示该用户当天收到的广告上限
    public int    receiveUpperBound;

    //表示该广告的最晚展现时间，如果该时间之前没有下载的广告，那么就需要重新进行下载
    public long   lastShowTime;

    //这个是这个cell 下载失败的次数，如果连续下载失败，那么我们应该抛弃
    public int    failedCount;

    //区别是广播消息还是单播消息
    public  int   multi;

    //在客户端下载后得到的，这个是处理后的成员变量，这个是广告主要图片 path
    private String downloadedImgPath;

    public MiuiAdsCell() {

    }

    public MiuiAdsCell(MiuiAdsCell other) {
        this.id = other.id;
        this.showType = other.showType;
        this.adsJsonString = other.adsJsonString;
        this.nonsense = other.nonsense;
        this.receiveUpperBound = other.receiveUpperBound;
        this.lastShowTime = other.lastShowTime;
        this.failedCount = other.failedCount;
        this.downloadedImgPath = other.downloadedImgPath;
        this.multi = other.multi;
    }

    public String getDownloadedImagePath() {
        return downloadedImgPath;
    }

    public void setDownloadedImagePath(String downloadImagePath) {
        this.downloadedImgPath = downloadImagePath;
    }

    //从接受到的json 来设置广告的内容
    public void setValuesByJson(JSONObject adsCellJson) throws JSONException{
        this.id = adsCellJson.optLong(NotifyAdsDef.JSON_TAG_ID);
        this.showType = adsCellJson.optInt(NotifyAdsDef.JSON_TAG_SHOWTYPE);
        this.nonsense = adsCellJson.optInt(NotifyAdsDef.JSON_TAG_NONSENSE);
        this.receiveUpperBound = adsCellJson.optInt(NotifyAdsDef.JSON_TAG_UPPERBOUND);
        this.lastShowTime = adsCellJson.optLong(NotifyAdsDef.JSON_TAG_LASTSHOWTIME);
        this.multi = adsCellJson.optInt(NotifyAdsDef.JSON_TAG_MULTI);
    }

    @Override
    public String toString() {
        return "";
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong(NotifyAdsDef.JSON_TAG_ID, this.id);
        bundle.putInt(NotifyAdsDef.JSON_TAG_SHOWTYPE, this.showType);
        bundle.putInt(NotifyAdsDef.JSON_TAG_NONSENSE, this.nonsense);
        bundle.putInt(NotifyAdsDef.JSON_TAG_UPPERBOUND, this.receiveUpperBound);
        bundle.putLong(NotifyAdsDef.JSON_TAG_LASTSHOWTIME, this.lastShowTime);
        bundle.putInt(NotifyAdsDef.JSON_TAG_MULTI, this.multi);
        return bundle;
    }


}
