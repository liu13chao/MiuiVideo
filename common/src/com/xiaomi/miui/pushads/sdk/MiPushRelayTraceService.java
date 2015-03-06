package com.xiaomi.miui.pushads.sdk;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.xiaomi.miui.pushads.sdk.common.MiuiAdsTraceCell;
import com.xiaomi.miui.pushads.sdk.trace.AdsLogSender;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * 这个service 用于进行通知栏信息的relay, 我们先打出trace，然后再递交给真正的 class 进行处理
 * @author liuwei
 *
 */
public class MiPushRelayTraceService  extends Service{

    private static  AdsLogSender sLogSender;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        sLogSender = AdsLogSender.getInstance();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent == null || intent.getExtras() == null) {
            return 0;
        }

        Bundle bundle = intent.getExtras();
        int intentType = bundle.getInt(NotifyAdsDef.INTENT_FLAG_TYPE);
        long adId = bundle.getLong(NotifyAdsDef.JSON_TAG_ID);
        int showType = bundle.getInt(NotifyAdsDef.JSON_TAG_SHOWTYPE);

        MiuiAdsTraceCell tcell = new MiuiAdsTraceCell();
        tcell.adId = adId;
        tcell.showType = showType;
        tcell.content = "";

        if (sLogSender == null) {
            Log.e("MiPushRelayTraceService", "log sender is null!");
            return 0;
        }

        switch(intentType) {
        case NotifyAdsDef.INTENT_TYPE_CLICK:
        case NotifyAdsDef.INTENT_TYPE_ACTIONCLICK:
            NotifyAdsManagerNew.showLog("clickT:");
            sLogSender.clickTrace(tcell);
            PendingIntent click = intent.getParcelableExtra(NotifyAdsDef.INTENT_FLAG_PENDING);
            if (click != null) {
                try {
                    click.send();
                } catch (CanceledException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            break;
        case NotifyAdsDef.INTENT_TYPE_DELETE:
            NotifyAdsManagerNew.showLog("deleteT:");
            sLogSender.removeTrace(tcell);
            break;
        }

        int notifyId = bundle.getInt(NotifyAdsDef.INTENT_FLAG_NOTIFYID, 0);
        if (notifyId != 0) {
            NotifyAdsManagerNew.showLog("action，remove noti");
            NotificationManager manager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notifyId);

            //我们还需要把 status bar 进行关闭
            Method collapse = null;
            Field statusBarService = null;

            try{
                statusBarService = Context.class.getField("STATUS_BAR_SERVICE");
                String flag = (String) statusBarService.get(null);
                Object statusManager = this.getSystemService(flag);
                collapse = statusManager.getClass().getMethod("collapse");
                collapse.invoke(statusManager);
                NotifyAdsManager.showLog("关闭status bar 成功");
            }catch(Exception e){
                Log.e(NotifyAdsManager.TAG, "Reflect failed");
            }
        }
        return 0;
    }
}
