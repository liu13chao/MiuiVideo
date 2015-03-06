/**
 * 
 */
package com.miui.videoplayer.ads;

import org.json.JSONObject;

import com.xiaomi.miui.ad.api.VideoAdCell;

/**
 * @author tianli
 *
 */
public class AdBean {
    String mAdUrl;
    String mClickUrl;
    String mAdId;
    String mAdSession;
    String mAdTime;
    
    public AdBean(){
    }
    
    public AdBean(VideoAdCell cell){
        mAdUrl = cell.videoPath;
        mClickUrl = cell.clickUrl;
        mAdId = cell.adId;
        mAdSession = cell.session;
        mAdTime = cell.duringTime;
    }
    
    public AdBean(String  json){
        try{
            JSONObject jsonObj = new JSONObject(json);
            mAdUrl = jsonObj.getString("adUrl");
            mClickUrl = jsonObj.getString("clickUrl");
            mAdId = jsonObj.getString("adId");
            mAdSession = jsonObj.getString("adSession");
            mAdTime = jsonObj.getString("adTime");
        }catch(Exception e){
        }
    }
    
    public String toJson(){
        JSONObject json = new JSONObject();
        try{
            json.put("adUrl", mAdUrl);
            json.put("clickUrl", mClickUrl);
            json.put("adId", mAdId);
            json.put("adTime", mAdTime);
            json.put("adSession", mAdSession);
        }catch(Exception e){
        }
        return json.toString();
    }

    public String getAdUrl() {
        return mAdUrl;
    }

    public void setAdUrl(String adUrl) {
        this.mAdUrl = adUrl;
    }

    public String getClickUrl() {
        return mClickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.mClickUrl = clickUrl;
    }

    public String getAdId() {
        return mAdId;
    }

    public void setAdId(String adId) {
        this.mAdId = adId;
    }

    public String getAdSession() {
        return mAdSession;
    }

    public void setAdSession(String adSession) {
        this.mAdSession = adSession;
    }

    public String getAdTime() {
        return mAdTime;
    }

    public void setAdTime(String adTime) {
        this.mAdTime = adTime;
    }

    @Override
    public String toString() {
        return "adId = " + mAdId + ", adDuration = " + mAdTime + ", adUrl = " +
                mAdUrl + ", session = " + mAdSession;
    }
    
    
    
}
