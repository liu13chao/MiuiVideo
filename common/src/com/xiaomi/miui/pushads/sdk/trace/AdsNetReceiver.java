package com.xiaomi.miui.pushads.sdk.trace;


import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * a receiver listen to the net status change
 * @author liuwei
 *
 */
class AdsNetReceiver extends BroadcastReceiver{

    private IAdsTraceListener mTraceListener;

    public AdsNetReceiver(IAdsTraceListener listener) {
        mTraceListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (mTraceListener != null) {
                mTraceListener.onNetStateChanged();
            }
        } else if(action.equals(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION)) {
            if (mTraceListener != null) {
                mTraceListener.onAccountChanged();
            }
        }
    }

}
