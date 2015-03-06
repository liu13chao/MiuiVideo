package com.xiaomi.miui.pushads.sdk.trace;


import android.content.Context;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.xiaomi.miui.pushads.sdk.NetUtils;
import com.xiaomi.miui.pushads.sdk.NotifyAdsDef;
import com.xiaomi.miui.pushads.sdk.NotifyAdsManager.NetState;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * a tool class to support network related
 * @author liuwei
 *
 */
class AdsNetUtil  {
    private static final String HTTP_KEY_LOGVALUE                 = "logValue";
    private static final String HTTP_KEY_APPID                    = "appId";
    private static final String HTTP_KEY_SHOWTYPE                 = "showType";
    private static final String HTTP_KEY_SIGNATURE                = "s";


    public static boolean canUploadLogs(Context context) {
        NetState curState = NetUtils.getNetState(context);
        boolean ret = true;

        //no matter the network type, log can upload always
        if (NetState.NONE == curState) {
            ret = false;
        }

        return ret;
    }


    /*使用新版的 appId + appToken 来进行 trace log 的验证*/
    public static int doAdsTrackLog(String appId, String appToken, AdsCacheCell cell) {
        List<NameValuePair> paramList = new LinkedList<NameValuePair>();
        paramList.add(new BasicNameValuePair(HTTP_KEY_LOGVALUE, cell.mBase64));
        paramList.add(new BasicNameValuePair(HTTP_KEY_APPID, appId));
        paramList.add(new BasicNameValuePair(HTTP_KEY_SHOWTYPE, cell.mShowType + ""));

        String s = AdsSaltUtil.getKeyFromParams(paramList, appToken);
        paramList.add(new BasicNameValuePair(HTTP_KEY_SIGNATURE, s));

        try {
            HttpPost post = null;
            if (NotifyAdsDef.DEBUG_MODE) {
                post = new HttpPost(LogDef.URL_NOTIFY_TRACKLOG_DEBUG);
            } else {
                post = new HttpPost(LogDef.URL_NOTIFY_TRACKLOG_OFFICIAL);
            }
            post.setEntity(new UrlEncodedFormEntity(paramList, HTTP.UTF_8));
            HttpResponse response = new DefaultHttpClient().execute(post);
            if ( HttpStatus.SC_OK == response.getStatusLine().getStatusCode() ) {
                return LogDef.RET_OK;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return LogDef.RET_ERROR;
    }

    public static void logTrack(String info) {
        if (NotifyAdsDef.DEBUG_MODE) {
            Log.d("TRACK", info);
        }
    }
}
