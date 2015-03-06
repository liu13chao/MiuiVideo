
package com.xiaomi.common.util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;

public class ExceptionHandler {

    public static final int UNCAUGHT_EXCEPTION = 0;

    public static final int CAUGHT_EXCEPTION = 1;

    protected static final String LOG_TAG = "common/ExceptionHandler";

    private static boolean initialized = false;

    private static Context mContext;

    public static void registerExceptionHandler(Context context, String uuid, String channel) {
        registerExceptionHandler(context, uuid, Settings.DEFAULT_SERVER_URL, channel, false);
    }

    /**
     * 为当前的context设置exception handler.
     *
     * @param context 当前的context，
     * @param replace 是否替换以前的exception handler.
     */
    public static void registerExceptionHandler(Context context, String uuid, String channel,
            boolean replace) {
        registerExceptionHandler(context, uuid, Settings.DEFAULT_SERVER_URL, channel, replace);
    }

    public static void registerExceptionHandler(Context context, String uuid, String url,
            String channel, boolean replace) {
        initialized = true;
        UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (originalHandler instanceof DefaultExceptionHandler && !replace)
            return;

        UncaughtExceptionHandler handler = new DefaultExceptionHandler(replace ? null
                : originalHandler);

        Thread.setDefaultUncaughtExceptionHandler(handler);
        mContext = context;
        PackageManager pm = context.getPackageManager();
        try {
            Settings.uuid = uuid;
            Settings.serverUrl = url;
            Settings.appName = context.getPackageName();
            PackageInfo packageInfo = pm.getPackageInfo(Settings.appName, 0);
            Settings.appVersionName = packageInfo.versionName;
            Settings.versionCode = packageInfo.versionCode;
            Settings.phoneType = Build.MODEL;
            Settings.sdkVersion = Build.VERSION.SDK;
            Settings.channel = channel;
        } catch (NameNotFoundException e) {
            Log.e(LOG_TAG, "error", e);
            return;
        }
    }

    public static void handlerException(Thread thread, Throwable ex) {
        handlerException(thread, ex, UNCAUGHT_EXCEPTION, null);
    }

    public static void handlerException(Thread thread, Throwable ex, int type, String extraMessage) {
        if(!initialized)
            return;

        if (ex == null)
            throw new IllegalArgumentException("the throwable is null.");

        // if there is no useful callstack, just return.
        if (ex.getStackTrace() == null || ex.getStackTrace().length == 0)
            return;

        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        ex.printStackTrace(printWriter);

        String callstack = result.toString();

        ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("do", "error.report"));
        parameters.add(new BasicNameValuePair("uuid", Settings.uuid));
        parameters.add(new BasicNameValuePair("phone_type", Settings.phoneType));
        parameters.add(new BasicNameValuePair("sdk_version", Settings.sdkVersion));
        parameters.add(new BasicNameValuePair("app_name", Settings.appName));
        parameters.add(new BasicNameValuePair("app_version_name", Settings.appVersionName));
        parameters.add(new BasicNameValuePair("channel", Settings.channel));
        parameters.add(new BasicNameValuePair("version_code", String.valueOf(Settings.versionCode)));
        parameters.add(new BasicNameValuePair("callstack", callstack));
        parameters.add(new BasicNameValuePair("type", String.valueOf(type)));
        parameters.add(new BasicNameValuePair("extra", extraMessage));

        try {
            Network.doHttpPost(mContext, Settings.serverUrl, parameters);
        } catch (IOException e) {
            Log.e(LOG_TAG, "error", e);
        }
    }

    // 采用异步方法向服务器发送错误日志。
    public static void sendMessageAsync(final Thread thread, final Throwable ex, final int type,
            final String extraMessage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handlerException(thread, ex, type, extraMessage);
            }
        }).start();
    }
}

class DefaultExceptionHandler implements UncaughtExceptionHandler {

    private final UncaughtExceptionHandler mDefaultHandler;

    public DefaultExceptionHandler() {
        mDefaultHandler = null;
    }

    public DefaultExceptionHandler(UncaughtExceptionHandler handler) {
        mDefaultHandler = handler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ExceptionHandler.handlerException(thread, ex);
        if (mDefaultHandler != null)
            mDefaultHandler.uncaughtException(thread, ex);
    }
}

class Settings {
    public static String DEFAULT_SERVER_URL = "http://xshare.api.xiaomi.com/xShare";

    public static String serverUrl;

    public static String appName;

    public static String appVersionName;

    public static int versionCode;

    public static String sdkVersion;

    public static String phoneType;

    public static String uuid;

    public static String channel;
}
