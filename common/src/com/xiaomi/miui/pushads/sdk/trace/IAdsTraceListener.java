package com.xiaomi.miui.pushads.sdk.trace;

/**
 * a listener to do call back
 * @author liuwei
 *
 */
interface IAdsTraceListener {
    public void onTraceTaskFinished(Integer status, AdsCacheCell cell);
    public void onNetStateChanged();
    public void onAccountChanged();
}
