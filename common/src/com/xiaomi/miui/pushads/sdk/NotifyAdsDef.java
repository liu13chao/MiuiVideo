package com.xiaomi.miui.pushads.sdk;


/**
 * 包含了通知栏广告需要的所有常量值
 * @author liuwei
 *
 */
public class NotifyAdsDef {

    //服务器测试的时候，设置为 true, 发布到正式的版本，一定要设置为false
    public static final boolean DEBUG_MODE = false;

    //这里还是需要打出log，不过把接受到的消息的详细信息去掉，
    public static final boolean LOG_OPEN   = true;
    // download buffer, same as httpclient socket buffer
    public static int DOWNLOAD_BUFFER = 1024 * 8;

    //一个广告最多失败下载的上限
    public static final int VALUE_MAX_FAILEDCOUNT = 10;

    public static final String TAG = "MIUIADSPUSH";

    //the return value for functions
    public static final int RET_OK                  = 0;
    public static final int RET_ERROR               = -1;
    public static final int RET_NOSENSE             = -2;
    public static final int RET_LIMITREACH          = -3;
    public static final int RET_EXPIRED             = -4;
    public static final int RET_NOTMATCHED          = -5;
    public static final int RET_NOT_PERMITTED       = -6;

    //return json key in the notification ads
    public static final String JSON_TAG_ERRORINFO        = "errorInfo"; //failure or sucessfully
    public static final String JSON_TAG_STATUS           = "status"; //failure or sucessfully
    public static final String JSON_TAG_USERID           = "userId";
    public static final String JSON_TAG_IMEI             = "imei";
    public static final String JSON_TAG_IP               = "ip";
    public static final String JSON_TAG_ID               = "id";
    public static final String JSON_TAG_ACTIONTYPE       = "actionType"; //click, close, received
    public static final String JSON_TAG_ACTIONTIME       = "time";
    public static final String JSON_TAG_NETSTATE         = "netstate";
    public static final String JSON_TAG_NONSENSE         = "nonsense";
    public static final String JSON_TAG_UPPERBOUND       = "receiveUpperBound";
    public static final String JSON_TAG_LASTSHOWTIME     = "lastShowTime";
    public static final String JSON_TAG_FAILEDCOUNT      = "failedCount";
    public static final String JSON_TAG_SHOWTYPE         = "showType";
    public static final String JSON_TAG_SUBADINFO        = "subAdInfo";
    public static final String JSON_TAG_SUBADID          = "subAdId";
    //区分是广播还是单播, 0--单播  1--广播
    public static final String JSON_TAG_MULTI            = "multi";

    //for notify ads
    public static final String JSON_TAG_ACTION_URL       = "actionUrl";
    public static final String JSON_TAG_ADSTYPE          = "type";
    public static final String JSON_TAG_IMAURL           = "imgUrl";
    public static final String JSON_TAG_TITTEXT          = "titText";
    public static final String JSON_TAG_PRITEXT          = "priText";
    public static final String JSON_TAG_SECTEXT          = "secText";
    public static final String JSON_TAG_ACTIONTEXT       = "actionText";

    //for relay intent
    public static final String INTENT_FLAG_TYPE          = "intenttype";
    public static final String INTENT_FLAG_PENDING       = "pendingintent";
    //如果用户点击的是remote view 里面的action，那么我们也需要把原始的notification 给隐藏掉
    public static final String INTENT_FLAG_NOTIFYID      = "notifyid";

    public static final int    INTENT_TYPE_DELETE        = 1;
    public static final int    INTENT_TYPE_CLICK         = 2;
    //这里我们需要添加一种新的类型，这样才能够区别不同的pending intent，以免被update
    public static final int    INTENT_TYPE_ACTIONCLICK   = 3;


    //for buuble
    public static final String JSON_TAG_CONTENT          = "content";

    public static final int   ADS_TYPE_BUBBLE = 1;
    public static final int   ADS_TYPE_NOTIFY = 2;
    public static final int   ADS_TYPE_SCREEN = 4;

    //一种外部用的message
    public static final int   ADS_TYPE_OUTER  = 1000;

    //return value from server
    public static final String HTTP_RESPONSE_STATUS_SUCCESS      = "success";
    public static final String HTTP_RESPONSE_STATUS_FAILURE      = "failure";

    //advertisement type
    public static final String TYPE_ADS_WEB                      = "web";
    public static final String TYPE_ADS_APP                      = "app";
    public static final String TYPE_ADS_OPEN                     = "open";

    //notification action type
    public static final String TYPE_ACTION_CLOSE                 = "close";
    public static final String TYPE_ACTION_RECEIVED              = "received";
    public static final String TYPE_ACTION_CLICK                 = "click";
    public static final String TYPE_ACTION_REMOVE                = "remove";
    public static final String TYPE_ACTION_SHOW                  = "show";

    //key for preference to save watermark
    public static final String PREFERENCE_FILE_NAME              = "adsPreference";

    //log tag
    public static final String ADS_LOGTAG                        = "miuiads";
    static final String ADS_FOLDER = "comxiaomimiuipushadssdk";

    //用于在服务器失效的情况下，客户端本地的流量控制
    public static final String PREFER_KEY_STARTTIME                      = "starttime";
    public static final String PREFER_KEY_NOTIFY_SUCCESS_COUNT           = "notifycount";
    public static final String PREFER_KEY_BUBBLE_SUCCESS_COUNT           = "bubblecount";
    //这个是个倍数的关系， 也就是冒泡比通知栏多发的倍数
    public static final int    BUBBLE_LIMIT_TIMES                        = 4;

    public static final String INTEGRATE_LAYOUT_BASEVIEW                 = "notification_base_layout";
    public static final String INTEGRATE_LAYOUT_SUBTITLE                 = "sub_title";
    public static final String INTEGRATE_LAYOUT_TITLE                    = "title";
    public static final String INTEGRATE_LAYOUT_ACTIONBUTTON             = "action_button";
    public static final String INTEGRATE_LAYOUT_BIGVIEW                  = "notification_big_picture_layout";
    public static final String INTEGRATE_LAYOUT_BIGPICTURE               = "big_picture";

}
