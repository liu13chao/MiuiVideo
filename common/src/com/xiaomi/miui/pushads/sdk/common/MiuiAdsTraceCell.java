package com.xiaomi.miui.pushads.sdk.common;

/**
 * as parameter when notification bar call trace functions
 * 新版广告的 trace cell， 这里 adId 代表的是广告的唯一 id, showType 代表的是广告的展示类型， content 代表的是其他的有用的信息。
 * 比如通知栏里面的 index
 * @author liuwei
 *
 */
public class MiuiAdsTraceCell {

    //代表了该广告的对应的 adId
    public long  adId;

    //代表该广告对应的展示类型
    public int showType;

    //{"index": 1} 这样的形式的json 串，代表了第三方自定义的一些信息
    public String content;

    public MiuiAdsTraceCell(MiuiAdsCell cell) {
        this.adId = cell.id;
        this.showType = cell.showType;
    }

    public MiuiAdsTraceCell() {}
}
