/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   CmccVideoView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014年9月22日
 */
package com.miui.video.live;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.cmcc.cmvsdk.main.MvSdkJar;
import com.miui.video.DKApp;
import com.miui.video.api.DKApi;
import com.miui.video.response.CmccKeyResponse;
import com.miui.video.util.DKLog;
import com.miui.videoplayer.model.SafeHandler;
import com.miui.videoplayer.videoview.DuoKanVideoView;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 * @author tianli
 *
 */
public class CmccVideoView extends DuoKanVideoView {

    public static String TAG = "CmccVideoView";
    
    private String mCmccId = "";
    private String mCmccPlayInfo = "";
    private ServiceRequest mKeyRequest = null;
    
    @SuppressLint("SimpleDateFormat")
    static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private CmccHandler mHandler = new CmccHandler(this);
    
    private boolean mPaused = false;
    
    public CmccVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CmccVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CmccVideoView(Context context) {
        super(context);
        init();
    }
    
    private void init(){
		try {
		    // init cmcc
            MvSdkJar.init(getContext().getApplicationContext(), 1, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void setDataSource(String uri) {
        setDataSource(uri, null);
    }

    @Override
    public void setDataSource(String uri, Map<String, String> headers) {
        Log.d(TAG, "setDataSource : " + uri );
        parseUri(uri);
        if(TextUtils.isEmpty(CmccHelper.getSecretKey())){
            getCmccKeyFromServer();
        }else {
            playCmcc();
        }
        if(mOnVideoLoadingListener != null){
            mOnVideoLoadingListener.onVideoLoading(this);
        }
   }
    
    private void playCmcc(){
        if(!TextUtils.isEmpty(mCmccPlayInfo)){
            getCmccPlayUrl();
        }else if(!TextUtils.isEmpty(mCmccId)){
            getLivingList();
        }
        start();
    }

    private void parseUri(String uri){
        try{
            JSONObject json = new JSONObject(uri);
            if(json.has("cmccid")){
                mCmccId = json.getString("cmccid");
            }
            if(json.has("cmccplayinfo")){
                mCmccPlayInfo = json.getString("cmccplayinfo");
            }
        }catch(Exception e){
        }
    }
    
    public void getCmccKeyFromServer(){
        if(mKeyRequest != null) {
            mKeyRequest.cancelRequest();
        }
        DKLog.d(TAG, "getCmccKeyFromServer");
        mKeyRequest = DKApi.getCmccKey(CmccHelper.CMCC_CHANNELID, CmccHelper.getAppId(getContext()),
                mCmccObserver);
    }
        
    public void playUrl(String playUrl){
        Log.d(TAG, "playUrl = " + playUrl);
        if(mPaused){
            return;
        }
        if(!TextUtils.isEmpty(playUrl)){
            super.setDataSource(playUrl);
        }
    }

    @Override
    public void onActivityPause() {
        super.onActivityPause();
        mPaused = true;
        Log.d(TAG, "onActivityPause");
        release();
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        Log.d(TAG, "onActivityResume");
        if(mPaused){
            mPaused = false;
            if(!TextUtils.isEmpty(mCmccPlayInfo) && 
                    !TextUtils.isEmpty(mCmccId)){
                if(!TextUtils.isEmpty(mCmccId)){
                    mCmccPlayInfo = null;
                }
                playCmcc();
                if(mOnVideoLoadingListener != null){
                    mOnVideoLoadingListener.onVideoLoading(this);
                }
            }
        }
    }
    
    public void getCmccPlayUrl(){
        if(mPaused){
            return;
        }
        Log.d(TAG, "CmccPlayInfo = " + mCmccPlayInfo);
        if(!TextUtils.isEmpty(mCmccPlayInfo)){
            String playInfo = mCmccPlayInfo;
            try {
                JSONObject json = new JSONObject(playInfo);
                String nodeid = json.getString("nodeid");
                String liveid = json.getString("liveid");
                int ret = MvSdkJar.doAuth(getContext(), nodeid, null, 
                        liveid, 4, "5", CmccHelper.CMCC_CHANNELID, CmccHelper.getAppId(getContext()),
                        DKApp.PACKAGE_NAME, 1, CmccHelper.getSecretKey(), mHandler);
                Log.d(TAG, "MvSdkJar.doAuth ret: " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void getLivingList(){
        if(mPaused){
            return;
        }
        Log.d(TAG, "Cmcc = " + mCmccId);
        if(!TextUtils.isEmpty(mCmccId)){
            String cmccid = mCmccId;
            int ret = MvSdkJar.getLivingList(getContext(),
                    cmccid, CmccHelper.CMCC_CHANNELID, CmccHelper.getAppId(getContext()), 
                    DKApp.PACKAGE_NAME, 0, CmccHelper.getSecretKey(),  mHandler);
            if(ret != 0){
                Log.d(TAG, "getLivingList() ret:" + ret);
            }
        }
    }
    
    public boolean isTargetProgram(long curTime, long startTime, long endTime){
        if(curTime > startTime && curTime < endTime){
            return true;
        }else{
            return false;
        }
    }
    
    public long stringToTimeStamp(String time){
        try {
            Date date = DATE_FORMATTER.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            long timestamp = cal.getTimeInMillis();
            return timestamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public void findContIdAndLiveId(String livingList){
        try {
            JSONObject json = new JSONObject(livingList);
            JSONArray jarr = json.getJSONArray("programs");
            long curTime = System.currentTimeMillis();
            for(int i = 0; i < jarr.length(); i++){
                JSONObject subJson = jarr.getJSONObject(i);
                String startTime = subJson.getString("startTime");
                String endTime = subJson.getString("endTime");
                long begin = stringToTimeStamp(startTime);
                long end = stringToTimeStamp(endTime);
                if(isTargetProgram(curTime, begin, end)){
                    DKLog.d(TAG, "isTargetProgram");
                    JSONObject cmccplayinfo = new JSONObject();
                    cmccplayinfo.put("nodeid", mCmccId);
                    cmccplayinfo.put("liveid", subJson.get("contId"));
                    mCmccPlayInfo = cmccplayinfo.toString();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class CmccHandler extends SafeHandler<CmccVideoView>{

        public CmccHandler(CmccVideoView ref) {
            super(ref);
        }
        
        @Override
        public void handleMessage(Message msg) {
            DKLog.d(TAG, "msg.what:" + msg.what + "," + "msg.obj:" + msg.obj);
            super.handleMessage(msg);
            CmccVideoView videoView = getReference();
            if(videoView == null){
                return;
            }
            switch(msg.what){
            case 22:
                if(msg.obj instanceof String){
                    String playUrl = (String) msg.obj;
                    DKLog.d(TAG, "playUrl:" + playUrl);
                    videoView.playUrl(playUrl);
                }
                break;
            case 81:
                if(msg.obj instanceof String){
                    String livingList = (String) msg.obj;
                    DKLog.d(TAG, " livingList:" + livingList);
                    videoView.findContIdAndLiveId(livingList);
                    videoView.getCmccPlayUrl();
                }
                break;
            default:
                break;
            }
        }
    }
    
    private Observer mCmccObserver = new Observer() {
        @Override
        public void onRequestCompleted(ServiceRequest request,
                ServiceResponse response) {
            if (response instanceof CmccKeyResponse) {
                CmccKeyResponse cmccKeyResponse = (CmccKeyResponse) response;
                String key = cmccKeyResponse.data;
                DKLog.d(TAG, "key:" + key);
                if( response.isSuccessful()) {
                    CmccHelper.setSecretKey(key);
                    playCmcc();
                }
            }
        }
        
        @Override
        public void onProgressUpdate(ServiceRequest request, int progress) {
        }
    };
}
