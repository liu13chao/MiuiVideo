package com.xiaomi.miui.pushads.sdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;


/**
 * 下载的工具类，用于下载一个广告并存放在本地
 * @author liuwei
 *
 */
class DownloadUtils {

    private static final String ADS_DOWNLOAD = "ADS_DOWNLOAD";

    public static int downFile(Context context, File parentFolder, String adsImage, NotifyAdsCell cell){
        int ret = NotifyAdsDef.RET_ERROR;
        InputStream inputStream = null;
        File retFile = null;
        String fileName = DownloadUtils.urlToFileName(adsImage);
        String absolutePath = parentFolder.getAbsolutePath() + "/" + fileName;
        try {
            retFile = new File(absolutePath);
            if (retFile.exists()) {
                ret = NotifyAdsDef.RET_OK;
            } else {
                LogUtils.logProcess("从sever 下载文件 debug 模式");
                inputStream = getInputStreamFromURL(adsImage);
                ret = DownloadUtils.write2SDFromInput(context, absolutePath, inputStream);
            }
        } catch (Exception e) {
        } finally{
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (ret == NotifyAdsDef.RET_OK) {
            cell.setDownloadedImagePath(absolutePath);
        }

        return ret;
    }

    /**
     * 根据URL得到输入流
     * @param urlStr
     * @return
     */
    private static InputStream getInputStreamFromURL(String urlStr) {
        HttpURLConnection urlConn = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(urlStr);
            urlConn = (HttpURLConnection)url.openConnection();
            inputStream = urlConn.getInputStream();
        } catch (IOException e) {
            return null;
        }
        return inputStream;
    }

    /**
     * 将一个InputStream里面的数据写入到内部存储中
     * @param input
     * @return
     */
    private static int write2SDFromInput(Context context, String absolutePath,InputStream input){
        if (null == input) return NotifyAdsDef.RET_ERROR;

        int ret = NotifyAdsDef.RET_ERROR;
        File tmpFile = null;
        OutputStream output = null;
        String tmpPath = absolutePath + "_" + System.currentTimeMillis();
        File retFile = null;
        try {
            tmpFile = new File(tmpPath);
            output = new FileOutputStream(tmpFile);
            byte[] buffer = new byte[NotifyAdsDef.DOWNLOAD_BUFFER];
            int readCount = 0;

            //对于通知来说，没有强制停的机制，除非是网络异常
            boolean netOk = NetUtils.canDownloadAds(context);
            while((readCount = input.read(buffer)) != -1 && netOk){
                output.write(buffer,0, readCount);
                netOk = NetUtils.canDownloadAds(context);
            }

            output.flush();

            if (readCount == -1) {
                retFile = new File(absolutePath);
                tmpFile.renameTo(retFile);
                ret = NotifyAdsDef.RET_OK;
            } else if (!netOk) {
                ret = NotifyAdsDef.RET_ERROR;
            }
        }
        catch (Exception e) {
            ret = NotifyAdsDef.RET_ERROR;
        }
        finally{
            try {
                output.close();
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 把url转化为文件名，即从url中去掉路径部分.
     * @param url
     * @return 转换后的文件名.
     */
    private static String urlToFileName(String url) {
        int pos = url.lastIndexOf("/");
        if (pos < 0) pos = 0;
        else pos += 1;
        return url.substring(pos);
    }

    public static String getKeyFromParams(List<NameValuePair> nameValuePairs, String publishId) {
        Collections.sort(nameValuePairs, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair p1, NameValuePair p2) {
                return p1.getName().compareTo(p2.getName());
            }
        });

        StringBuilder keyBuilder = new StringBuilder();
        boolean isFirst = true;
        for (NameValuePair nvp : nameValuePairs) {
            if (!isFirst) {
                keyBuilder.append("&");
            }
            keyBuilder.append(nvp.getName()).append("=").append(nvp.getValue());
            isFirst = false;
        }

        keyBuilder.append("&").append(publishId);

        String key = keyBuilder.toString();
        byte[] keyBytes = getBytes(key);
        return getMd5Digest(new String(Base64.encode(keyBytes, Base64.NO_WRAP)));
    }

    private static byte[] getBytes(String s) {
        try {
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s.getBytes();
        }
    }

    public static String getMd5Digest(String pInput) {
        try {
            MessageDigest lDigest = MessageDigest.getInstance("MD5");
            lDigest.update(getBytes(pInput));
            BigInteger lHashInt = new BigInteger(1, lDigest.digest());
            return String.format("%1$032X", lHashInt);
        } catch (NoSuchAlgorithmException lException) {
            throw new RuntimeException(lException);
        }
    }
}
