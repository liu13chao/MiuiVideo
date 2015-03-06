package com.xiaomi.miui.pushads.sdk;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 这个是用于 notification 展现大图的
 * @author liuwei
 *
 */
class NotificationBigRemoteView extends NotificationBaseRemoteView{

    public NotificationBigRemoteView(Context context) {
        super(context.getPackageName(), context.getResources().
                getIdentifier(NotifyAdsDef.INTEGRATE_LAYOUT_BIGVIEW, "layout", context.getPackageName()));

        mContext = context;
    }

    public void setBigPicture(Bitmap bitmap) {
        int pictureLayoutId = mContext.getResources().
                getIdentifier(NotifyAdsDef.INTEGRATE_LAYOUT_BIGPICTURE, "id", mContext.getPackageName());

        setImageViewBitmap(pictureLayoutId, bitmap);
    }
}
