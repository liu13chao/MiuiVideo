package com.xiaomi.miui.pushads.sdk;

/**
 * 这个类用于我们在广告下载失败的时候，放入到我们本地的file cache 里面进行缓存
 * @author liuwei
 *
 */
class NotifyAdsCacheCell {
    public String   adsJsonString;
    public long     lastShowTime;
    public int      failedCount; //表示下载失败了多少次
}
