package com.xiaomi.miui.pushads.sdk.trace;

import android.os.AsyncTask;

/**
 * AsyncTask to send log to server
 * @author liuwei
 *
 */
class AdsLogTraceTask extends AsyncTask<String, Integer, Integer> {
    IAdsTraceListener mTraceListener;
    String mAppId;
    String mAppToken;
    AdsCacheCell mCell;

    public AdsLogTraceTask(IAdsTraceListener traceListener, String appId, String appToken, AdsCacheCell cell) {
        mAppId = appId;
        mAppToken = appToken;
        mTraceListener = traceListener;
        mCell = cell;
    }

    @Override
    protected Integer doInBackground(String... params) {
        int retCode = AdsNetUtil.doAdsTrackLog(mAppId, mAppToken, mCell);
        return retCode;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (null != mTraceListener) {
            mTraceListener.onTraceTaskFinished(integer, mCell);
        }
    }

    //if user calls asyncTask.cancel() to stop the task,  doInBackground will return. but
    //onPostexecute will not be called. onCancelled will be called instead.
    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (null != mTraceListener) {
            mTraceListener.onTraceTaskFinished(LogDef.RET_ERROR, mCell);
        }
    }
}
