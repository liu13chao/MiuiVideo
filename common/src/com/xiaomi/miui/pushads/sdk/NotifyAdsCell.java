package com.xiaomi.miui.pushads.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.xiaomi.miui.pushads.sdk.common.MiuiAdsCell;


/**
 * 这个是描述通知栏广告的类
 * @author liuwei
 *
 */

public class NotifyAdsCell extends MiuiAdsCell{

    //点击广告后的 url 地址
    public String actionUrl;

    //这个是通知栏广告的图片url，用于增强通知栏的显示
    public String imgUrl;

    //通知达到时，显示在手机顶部的信息
    public String titText;

    //这个是通知栏第一行的主要注释文字
    public String priText;

    //这个是通知栏第一行的次要注释文字
    public String secText;

    //广告的类型，是web 或者是 app 等等
    public String type;

    //广告的 action buttong 的Text
    public String actionText;

    public NotifyAdsCell() {

    }

    public NotifyAdsCell(NotifyAdsCell other) {
        super(other);

        this.actionUrl = other.actionUrl;
        this.imgUrl = other.imgUrl;
        this.titText = other.titText;
        this.priText = other.priText;
        this.secText = other.secText;
        this.type = other.type;
        this.actionText = other.actionText;
    }

    public void setValuesByJson(JSONObject adsCellJson) throws JSONException {
        super.setValuesByJson(adsCellJson);
        this.actionUrl = adsCellJson.optString(NotifyAdsDef.JSON_TAG_ACTION_URL);
        this.imgUrl = adsCellJson.optString(NotifyAdsDef.JSON_TAG_IMAURL);
        this.titText = adsCellJson.optString(NotifyAdsDef.JSON_TAG_TITTEXT);
        this.priText = adsCellJson.optString(NotifyAdsDef.JSON_TAG_PRITEXT);
        this.secText = adsCellJson.optString(NotifyAdsDef.JSON_TAG_SECTEXT);
        this.type = adsCellJson.optString(NotifyAdsDef.JSON_TAG_ADSTYPE);
        this.actionText = adsCellJson.optString(NotifyAdsDef.JSON_TAG_ACTIONTEXT);
    }

    public String toString() {
        try {
            JSONObject object = new JSONObject();
            object.put("showType", this.showType);
            object.put("lastShowTime", this.lastShowTime);
            object.put("actionUrl", this.actionUrl);
            object.put("type", this.type);
            object.put("imgUrl", this.imgUrl);
            object.put("receiveUpperBound", this.receiveUpperBound);
            object.put("downloadedPath", this.getDownloadedImagePath());
            object.put("titText", this.titText);
            object.put("priText", this.priText);
            object.put("secText", this.secText);
            object.put(NotifyAdsDef.JSON_TAG_ACTIONTEXT, this.actionText);
            return object.toString();
        } catch(Exception e) {

        }

        return null;
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = super.toBundle();
        bundle.putString(NotifyAdsDef.JSON_TAG_ACTION_URL, this.actionUrl);
        bundle.putString(NotifyAdsDef.JSON_TAG_IMAURL, this.imgUrl);
        bundle.putString(NotifyAdsDef.JSON_TAG_TITTEXT, this.titText);
        bundle.putString(NotifyAdsDef.JSON_TAG_PRITEXT, this.priText);
        bundle.putString(NotifyAdsDef.JSON_TAG_SECTEXT, this.secText);
        bundle.putString(NotifyAdsDef.JSON_TAG_ADSTYPE, this.type);
        bundle.putString(NotifyAdsDef.JSON_TAG_ACTIONTEXT, this.actionText);
        return bundle;
    }
}
