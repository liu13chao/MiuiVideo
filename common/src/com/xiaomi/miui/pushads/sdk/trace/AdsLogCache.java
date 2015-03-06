package com.xiaomi.miui.pushads.sdk.trace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


import android.os.Debug;
import android.util.Log;

/**
 * The log cache for ads
 * @author liuwei
 *
 */
class AdsLogCache {
    private StringBuilder mBuilder;
    private static final String TAG = "com.xiaomi.miui.pushads.sdk.trace";
    private File mCacheFile;

    public AdsLogCache(String savepath) {
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

    public void appendInfo(AdsCacheCell cell) {
        mBuilder.append(cell.mShowType + "\t");
        mBuilder.append(cell.mBase64 + "\t" + cell.mMd5);
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

    public ArrayList<AdsCacheCell> getAdsCacheCellFromCacheFile() {
        ArrayList<AdsCacheCell> list = new ArrayList<AdsCacheCell>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.mCacheFile), "utf-8"));
            for (String str = reader.readLine(); str != null; str = reader.readLine()) {
                String[] splits = str.split("\t");
                AdsCacheCell cell = new AdsCacheCell();

                if (splits == null || splits.length != 3) {
                    continue;
                } else {
                    try {
                        cell.mShowType = Integer.valueOf(splits[0]);
                        cell.mBase64 = splits[1];
                        cell.mMd5 = splits[2];
                        list.add(cell);
                    } catch(Exception e) {
                        Log.e("NotifyAdsCache", "读取log cache 失败");
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