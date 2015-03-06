package com.xiaomi.miui.pushads.sdk;

import android.app.PendingIntent;
import android.os.Bundle;



/**
 * 这个类是 app 监听 sdk 是否有广告下载成功并返回
 * @author liuwei
 *
 */
public interface MiuiAdsListener {


    /**
     * 这里是sdk 通知应用通知栏消息达到，如果APP 返回true，那么说明被处理过了sdk将不会处理
     * 返回 false的话，那么sdk 将会代app 处理在app 自行处理的情况下，包括打log 等等全部都
     * 需要app 自行处理. 主线程调用
     * @param cell
     * @return
     * true: app 已经处理这个 cell
     * false 需要 sdk 处理这个cell
     */
    public boolean onNotifyReceived(NotifyAdsCell cell);

    /**
     * 冒泡信息达到，app 对于冒泡信息，app 需要自己实现处理和 trace 的功能，
     * 主线程调用
     * @param cell
     */
    public void onBubbleReceived(BubbleAdsCell cell);


    /**
     * NotifyAdsManager 将会通过这个接口告诉APP 是否打开channel 成功
     * 如果app 接受到这个消息后，将会判断是否成功打开了 push channel，如果没有，那么可以重新打开NotifyManager，调用
     * reopen() 方法重新进行打开，app 需要自己定义重试的工作
     * @param resultCode
     * @param regID
     */
    public void onChannelInitialized(long resultCode, String regID);

    /**
     * 在发出通知时， 显示在状态栏的图片
     * @return
     */
    public int getSmallIconID();

    /**
     * 获得当该通知栏的通知被点击后，APP 需要执行的click 的intent 操作，需要app 提供
     * @return
     */
    public PendingIntent getClickPendingIntent(NotifyAdsCell cell);

    /**
     *获得当该通知栏的通知按钮(如果存在的话)， 被点击后，需要执行的click 的intent 操作， 需要app 提供
     *@return
     */
    public PendingIntent getActionPendingIntent(NotifyAdsCell cell);
}
