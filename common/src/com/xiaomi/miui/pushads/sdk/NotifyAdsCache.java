package com.xiaomi.miui.pushads.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


import android.util.Log;

/**
 * 这个类用于在我们接受到了广告的push ID后，但是下载广告确失败了。 此时，我们需要把广告进行cache 在本地
 * 等待网络条件好的时候，再次进行下载
 * @author liuwei
 *
 */
class NotifyAdsCache {
    private StringBuilder mBuilder;
    private File mCacheFile;

    private static final String TAG = "com.miui.ads.notify.model";

    public NotifyAdsCache(String savepath) {
        mBuilder = new StringBuilder();
        createOutFolderIfNeeded(savepath);
        mCacheFile = new File(savepath);
        if (!mCacheFile.exists()) {
            try {
                mCacheFile.createNewFile();
                if(!mCacheFile.exists()) {
                    return;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void appendInfo(String adsJsonString, long lastShowTime, int failedCount) {
        mBuilder.append(adsJsonString + "\t" + lastShowTime + "\t" + failedCount);
        mBuilder.append("\r\n");
    }

    public void flushFile() {
        try {
            FileWriter writer = new FileWriter(mCacheFile, true);
            writer.write(mBuilder.toString());
            writer.flush();
            writer.close();
            mBuilder.delete(0, mBuilder.length());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public ArrayList<NotifyAdsCacheCell> getAdsCacheCellFromCacheFile() {
        ArrayList<NotifyAdsCacheCell> list = new ArrayList<NotifyAdsCacheCell>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(mCacheFile), "utf-8"));

            for (String str = reader.readLine(); str != null; str = reader.readLine()) {
                LogUtils.logProcess("cache 内容: " + str);
                String[] splits = str.split("\t");
                NotifyAdsCacheCell cell = new NotifyAdsCacheCell();

                if (splits.length != 3) {
                    continue;
                } else {
                    try {
                        //可能会解析失败，所以加上 try catch
                        cell.adsJsonString = splits[0];
                        cell.lastShowTime = Long.valueOf(splits[1]);
                        cell.failedCount = Integer.valueOf(splits[2]);
                        list.add(cell);
                    } catch(Exception e) {
                        Log.e("NotifyAdsCache", "读取ads cache 失败");
                    }
                }
            }

            reader.close();
            mCacheFile.delete();
            mCacheFile.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return list;
    }


    private void createOutFolderIfNeeded(String outputPath) {
        int index = outputPath.lastIndexOf("/");
        String pFolderPath = null;
        if (index == -1) {
            pFolderPath = outputPath;
        } else {
            pFolderPath = outputPath.substring(0,index);
        }

        File pFolder = new File(pFolderPath);
        if (!pFolder.exists()) {
            pFolder.mkdirs();
        }
    }
}