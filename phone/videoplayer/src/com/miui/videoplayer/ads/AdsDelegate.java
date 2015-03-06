/**
 * 
 */
package com.miui.videoplayer.ads;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * @author tianli
 *
 */
public class AdsDelegate {
    
    public final static String TAG = "AdsDelegate";
    
    private Context mContext;
    private  IAdsService mBinder;
    
    private Object mBindLock = new Object();
    
    private static AdsDelegate mDelagate;
    
    private AdsDelegate(Context context){
        mContext = context.getApplicationContext();
    }
    
    public static synchronized AdsDelegate getDefault(Context context){
        if(mDelagate == null){
            mDelagate = new AdsDelegate(context.getApplicationContext());
        }
        return mDelagate;
    }
    
    public AdBean  getAdUrl(int mediaId, int ci,  int source){
        checkBindStatus(2000);
        try {
            String result = mBinder.getOnlineAd(mediaId, ci, source);
            if(result != null){
                return new AdBean(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;    
    }
    
    public AdBean getAdUrl(int tvId, int source, String tvProgram){
        checkBindStatus(2000);
        try {
            String result = mBinder.getTvAd(tvId, source, tvProgram);
            if(result != null){
                return new AdBean(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;     
    }
    
    public void logAdPlay(AdBean adBean, String extraJson){
        if(adBean != null){
            checkBindStatus(1000);
            try {
                mBinder.logAdPlay(adBean.toJson(), extraJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void logAdClick(AdBean adBean, String extraJson){
        if(adBean != null){
            checkBindStatus(1000);
            try {
                mBinder.logAdClick(adBean.toJson(), extraJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void logAdSkipped(AdBean adBean,  int playTime, String extraJson){
        if(adBean != null){
            checkBindStatus(1000);
            try {
                mBinder.logAdSkipped(adBean.toJson(), playTime, extraJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void logAdFinished(AdBean adBean, String extraJson){
        if(adBean != null){
            checkBindStatus(1000);
            try {
                mBinder.logAdFinished(adBean.toJson(), extraJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopService(){
        try{
            mContext.unbindService(mServiceConnection);
            mBinder = null;
        }catch(Exception e){
        }
    }
    
    private void checkBindStatus(int timeout){
        if(mBinder == null){
            // do rebind.
            bind();
            synchronized (mBindLock) {
                try {
                    mBindLock.wait(timeout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void bind(){
        try{
            Intent intent = new Intent(mContext, AdsService.class);
            mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }catch(Exception e){
        }
    }
    
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            mBinder = IAdsService.Stub.asInterface(service);
            synchronized (mBindLock) {
                try {
                    mBindLock.notifyAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
