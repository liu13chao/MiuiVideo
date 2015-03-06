package com.xiaomi.miui.pushads.sdk;


import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * 网络状态广播接，如果网络变为可以下载广告，那么就进行回调
 * @author liuwei
 *
 */
class NotifyAdsNetReceiver extends BroadcastReceiver{

    private INotifyAdsListener mAdsListener;

    public NotifyAdsNetReceiver(INotifyAdsListener listener) {
        mAdsListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //网络如果发生了改变并且可以下载广告，那么我们通知app
            if (mAdsListener != null) {
                mAdsListener.onNetChanged();
            }
        } else if(action.equals(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION)) {
            if (mAdsListener != null) {
                mAdsListener.onAccountChanged();
            }
        }
    }

}