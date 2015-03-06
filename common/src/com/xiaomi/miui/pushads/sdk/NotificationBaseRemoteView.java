package com.xiaomi.miui.pushads.sdk;

import android.app.PendingIntent;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

/*所有的 layout id，都是app 嵌入了 layout 模板后， 在R 生成的id */
class NotificationBaseRemoteView extends RemoteViews{

    protected Context mContext;
    public NotificationBaseRemoteView(Context context) {
        super(context.getPackageName(), context.getResources().getIdentifier(NotifyAdsDef.INTEGRATE_LAYOUT_BASEVIEW, "layout", context.getPackageName()));
        mContext = context;
    }

    public NotificationBaseRemoteView(String packageName, int layoutId) {
        super(packageName, layoutId);
    }

    public void setIcon(int srcId) {
        setImageViewResource(android.R.id.icon, srcId);
    }

    public void setTitles(String title, String subTitle) {

        if (title != null) title = title.trim();
        if (subTitle != null) subTitle = subTitle.trim();

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(subTitle)) return;

        //如果title 是空，那么使用 subTitle 作为替代
        if (TextUtils.isEmpty(title)) {
            title = subTitle;
            subTitle = "";
        }

        //如果subTitle 是空，那么使用单行格式，否则使用双行格式
        int titleLayoutId    = 0;
        int subTitleLayoutId = 0;

        if (!TextUtils.isEmpty(subTitle)) {
            subTitleLayoutId = mContext.getResources().getIdentifier(NotifyAdsDef.INTEGRATE_LAYOUT_SUBTITLE, "id", mContext.getPackageName());
            setTextViewText(subTitleLayoutId, subTitle);
            setViewVisibility(subTitleLayoutId, View.VISIBLE);
        }

        titleLayoutId = mContext.getResources().getIdentifier(NotifyAdsDef.INTEGRATE_LAYOUT_TITLE, "id", mContext.getPackageName());
        setTextViewText(titleLayoutId, title);
    }

    public void setActionButton(String text, PendingIntent pendingIntent) {

        if (text != null) text = text.trim();

        int buttonLayoutId = mContext.getResources().getIdentifier(NotifyAdsDef.INTEGRATE_LAYOUT_ACTIONBUTTON, "id", mContext.getPackageName());
        if (!TextUtils.isEmpty(text)) {
            setTextViewText(buttonLayoutId, text);
            if (null != pendingIntent)
                setOnClickPendingIntent(buttonLayoutId, pendingIntent);

            setViewVisibility(buttonLayoutId, View.VISIBLE);
        }
        else {
            setViewVisibility(buttonLayoutId, View.GONE);
        }
    }
}
