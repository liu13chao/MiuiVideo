package com.xiaomi.miui.pushads.sdk.common;


/**
 * 这个类是 app 监听 sdk 是否有广告下载成功并返回
 * @author liuwei
 *
 */
public interface MiuiAdsReceivedListener {

    //这个方法是在主线程里面调用的
    public void onReceived(MiuiAdsCell cell);

    //NotifyAdsManager 将会通过这个接口告诉APP 是否打开channel 成功
    //如果app 接受到这个消息后，将会判断是否成功打开了 push channel，如果没有，那么可以重新打开NotifyManager
    public void onChannelInitialized(long resultCode, String regID);
}
