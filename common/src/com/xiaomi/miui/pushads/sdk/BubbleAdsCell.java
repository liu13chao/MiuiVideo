package com.xiaomi.miui.pushads.sdk;

/**
 * 用于 bubble 的描述
 * author: liuwei
 */

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.xiaomi.miui.pushads.sdk.common.MiuiAdsCell;

public class BubbleAdsCell extends MiuiAdsCell{
    public String content;

    public BubbleAdsCell() {

    }

    public BubbleAdsCell(BubbleAdsCell other) {
        super(other);
        this.content = other.content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    //从接受到的json 来设置广告的内容
    public void setValuesByJson(JSONObject adsCellJson) throws JSONException{
        super.setValuesByJson(adsCellJson);
        this.content = adsCellJson.optString(NotifyAdsDef.JSON_TAG_CONTENT);
    }

    @Override
    public String toString() {
        try {
            JSONObject object = new JSONObject();
            object.put("id", id);
            object.put("showType", showType);
            object.put("lastShowTime", lastShowTime);
            object.put("content", content);
            return object.toString();
        } catch(Exception e) {
        }

        return "";
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = super.toBundle();
        bundle.putString(NotifyAdsDef.JSON_TAG_CONTENT, this.content);
        return bundle;
    }
}
