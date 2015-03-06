package com.xiaomi.miui.pushads.sdk.common;

import java.util.ArrayList;


/**
 * the interface to send log. All function unit should implement this interface
 * @author liuwei
 *
 */

public interface IMiuiAdsLogSender {
    public void clickTrace(MiuiAdsTraceCell  cell);
    public void removeTrace(MiuiAdsTraceCell cell);
    public void receiveTrace(MiuiAdsTraceCell cell);

    //only call this when you don't use logger, if used in notification bar,
    //this will not called
    public void release();
}
