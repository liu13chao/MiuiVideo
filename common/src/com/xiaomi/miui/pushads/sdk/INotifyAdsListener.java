package com.xiaomi.miui.pushads.sdk;

import com.xiaomi.miui.pushads.sdk.common.MiuiAdsCell;


/**
 * called when the downloader get the ads sucessfully
 * @author liuwei
 *
 */
interface INotifyAdsListener {
    public void onAdsReceived(int status, MiuiAdsCell cell, NotifyAdsDownloader downloader);
    public void onNetChanged();
    public void onAccountChanged();
}
