/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   FavoriteStore.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-10-30
 */

package com.miui.video.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.miui.video.DKApp;
import com.miui.video.model.AppEnv;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;

/**
 * @author xuanmingliu
 * 
 */

public class FavoriteStore {

    private static final String TAG = FavoriteStore.class.getName();

    private static final String MYFAVORITE_DIR_NAME = "myfavorite";
    private static final String MYFAVORITE_FILE_NAME_SUFFIX = "_myfavorite.xml";
    private static final String MYFAVORITE_NAMESPACE = "";

    private static final String ANONYMOUS = "anonymous";

    private List<Favorite> mOnlineFavoriteList = new ArrayList<Favorite>();
    private List<Favorite> mUIFavoriteList = new ArrayList<Favorite>();

    public FavoriteStore() {
        prepareMyFavoriteDir();
    }

    //public method
    protected void loadFavorite(String account) {
        long start = System.currentTimeMillis();
        mOnlineFavoriteList.clear();
        parseFavoriteXML(account);
        mergeFavoriteList();
        long time = System.currentTimeMillis() - start;
        DKLog.d(TAG, "load favorite " + "costs " + time + " ms.");
    }

    //can run on ui task
    protected List<Favorite> getUIFavoriteList() {
        List<Favorite> list = new ArrayList<Favorite>();
        list.addAll(mUIFavoriteList);
        return list;
    }

    //can run on background task
    protected List<Favorite> getFavoriteList() {
        List<Favorite> list = new ArrayList<Favorite>();
        list.addAll(mOnlineFavoriteList);
        return list;
    }

    protected boolean saveFavorites(List<Favorite> favorites, String account){
        mOnlineFavoriteList.clear();
        for(int i = 0; i < favorites.size(); i++) {
            Favorite fav = favorites.get(i);
            if(fav instanceof OnlineFavorite) {
                mOnlineFavoriteList.add(fav);
            }
        }
        mergeFavoriteList();
        return saveOnlineFavorites(account);
    }

    //packaged method
    private void mergeFavoriteList() {
        mUIFavoriteList.clear();
        for(Favorite fav : mOnlineFavoriteList){
            if(!fav.isDeletedLocally()){
                mUIFavoriteList.add(fav);
            }
        }
        Collections.sort(mUIFavoriteList);
    }

    private InputStream getFavoriteInputStream(String account) {
        InputStream is = null;
        try {
            String path = getMyFavoriteXmlFilePath(account);
            File file = new File(path);
            if(file.exists()) {
                is = new FileInputStream(file);
            }
        } catch (Exception e) {
            DKLog.e(TAG, "no favorite inputstream.", e);
        }
        return is;
    }

    private OutputStream getFavoriteOutputStream(String account) {
        OutputStream os = null;
        try {
            String path = getMyFavoriteXmlFilePath(account);
            File file = new File(path);
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            DKLog.e(TAG, "" + e);
        }

        return os;
    }

    private void prepareMyFavoriteDir() {
        File myFavoriteDir = new File(getMyFavoriteDirPath());
        if (!myFavoriteDir.exists()){
            myFavoriteDir.mkdir();
        }
    }

    private String getMyFavoriteDirPath() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(DKApp.getSingleton(AppEnv.class)
                .getInternalFilesDir());
        strBuilder.append(File.separator);
        strBuilder.append(MYFAVORITE_DIR_NAME);
        return strBuilder.toString();
    }

    private String getMyFavoriteXmlFilePath(String accountName) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getMyFavoriteDirPath());
        strBuilder.append(File.separator);
        if (Util.isEmpty(accountName)) {
            strBuilder.append(ANONYMOUS);
        } else {
            strBuilder.append(accountName);
        }
        strBuilder.append(MYFAVORITE_FILE_NAME_SUFFIX);
        return strBuilder.toString();
    }

    private boolean parseFavoriteXML(String accountName) {
        boolean parseSuccess = true;
        InputStream is = null;
        try {
            is = getFavoriteInputStream(accountName);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(is, "UTF-8");
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT: {
                    // DKLog.i(TAG, "Start document");
                    break;
                }
                case XmlPullParser.START_TAG: {
                    // DKLog.i(TAG, "Start tag : " + xpp.getName());
                    parseSuccess = parseMyFavoriteXMLTagAttribute(xpp);
                    break;
                }
                case XmlPullParser.END_TAG: {
                    // DKLog.i(TAG, "End tag : " + xpp.getName());
                    break;
                }
                case XmlPullParser.TEXT: {
                    // DKLog.i(TAG, "Text : " +xpp.getText());
                    break;
                }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            parseSuccess = false;
            DKLog.e(TAG, e.getLocalizedMessage());
        } catch (Exception e) {
            parseSuccess = false;
            DKLog.e(TAG, e.getLocalizedMessage());
        } finally {
            try {
                if(is != null){
                    is.close();
                }
            } catch (Exception e) {
                DKLog.e(TAG, e.getLocalizedMessage());
            }
        }
        return parseSuccess;
    }

    private boolean parseMyFavoriteXMLTagAttribute(XmlPullParser xpp) {
        String curTag = xpp.getName();
        boolean parseSuccess = true;
        if (curTag.equals(MyFavoriteFileXMLTag.MYFAVORITE_LIST)) {
            return true;
        } else if (curTag.equals(MyFavoriteFileXMLTag.MYFAVORITE_ITEM)) {
            parseSuccess = parseMyFavoriteTagAttribute(xpp);
        }
        return parseSuccess;
    }

    private boolean parseMyFavoriteTagAttribute(XmlPullParser xpp) {
        int attrCount = xpp.getAttributeCount();
        MediaInfo mediaInfo = new MediaInfo();
        int status = Favorite.STATUS_SYNC;
        long createTime = 0;
        for (int i = 0; i < attrCount; i++) {
            String attrName = xpp.getAttributeName(i);
            String attrValue = xpp.getAttributeValue(i);
            if (attrName.equals(MyFavoriteItemTagAttribute.CREATE_TIME_ATTR)) {
                createTime = Long.parseLong(attrValue);
            } else if (attrName.equals(MyFavoriteItemTagAttribute.MEDIAID_ATTR)) {
                //				localMyFavoriteItemInfo.localVideo = false;
                mediaInfo.mediaid = Integer.parseInt(attrValue);
            } else if (attrName.equals(MyFavoriteItemTagAttribute.FLAG_ATTR)) {
                // mediaInfo.flag = Integer.parseInt(attrValue);
            } else if (attrName.equals(MyFavoriteItemTagAttribute.RESOLUTION_ATTR)) {
                mediaInfo.resolution = Integer.parseInt(attrValue);
            } else if (attrName.equals(MyFavoriteItemTagAttribute.CATEGORY_ATTR)) {
                mediaInfo.category = attrValue;
            } else if (attrName.equals(MyFavoriteItemTagAttribute.MEDIANAME_ATTR)) {
                mediaInfo.medianame = attrValue;
            } else if (attrName.equals(MyFavoriteItemTagAttribute.DIRECTOR_ATTR)) {
                mediaInfo.director = attrValue;
            } else if (attrName.equals(MyFavoriteItemTagAttribute.ACTORS_ATTR)) {
                mediaInfo.actors = attrValue;
            } else if (attrName.equals(MyFavoriteItemTagAttribute.TAGS_ATTR)) {
                mediaInfo.allcategorys = attrValue;
            } else if (attrName.equals(MyFavoriteItemTagAttribute.IMAGEURLMD5_ATTR)) {
                mediaInfo.md5 = attrValue;
            } else if (attrName.equals(MyFavoriteItemTagAttribute.IMAGEURL_ATTR)) {
                mediaInfo.posterurl = attrValue;
            } else if (attrName.equals(MyFavoriteItemTagAttribute.SCORE_ATTR)) {
                mediaInfo.score = Float.parseFloat(attrValue);
            } else if (attrName.equals(MyFavoriteItemTagAttribute.SETNOW_ATTR)) {
                mediaInfo.setnow = Integer.parseInt(attrValue);
            } else if (attrName.equals(MyFavoriteItemTagAttribute.PLAYLENGTH_ATTR)) {
                mediaInfo.playlength = Integer.parseInt(attrValue);
            } else if (attrName.equals(MyFavoriteItemTagAttribute.AREA_ATTR)) {
                mediaInfo.area = attrValue;
            } else if (attrName.equals(MyFavoriteItemTagAttribute.ISSUEDATE_ATTR)) {
                mediaInfo.issuedate = attrValue;
            } else if (attrName.equals(MyFavoriteItemTagAttribute.LASTISSUEDATE_ATTR)) {
                mediaInfo.lastissuedate = attrValue;
            } else if (attrName.equals(MyFavoriteItemTagAttribute.SETCOUNT_ATTR)) {
                mediaInfo.setcount = Integer.parseInt(attrValue);
            } else if (attrName.equals(MyFavoriteItemTagAttribute.PLAYCOUNT_ATTR)) {
                mediaInfo.playcount = Integer.parseInt(attrValue);
            } else if (attrName.equals(MyFavoriteItemTagAttribute.SCORECOUNT_ATTR)) {
                mediaInfo.scorecount = Integer.parseInt(attrValue);
            } else if (attrName.equals(MyFavoriteItemTagAttribute.SETTYPE_ATTR)) {
                mediaInfo.ismultset = Integer.parseInt(attrValue);
            } else if (attrName.equals(MyFavoriteItemTagAttribute.LOCALCOUNT_ATTR)) {
                //				localMyFavoriteItemInfo.localCount = Integer
                //						.parseInt(attrValue);
            } else if (attrName.equals(MyFavoriteItemTagAttribute.LOCALVIDEOPATH_ATTR)) {
                //				localMyFavoriteItemInfo.localVideoPath = attrValue;
            } else if (attrName.equals(MyFavoriteItemTagAttribute.LOCALFLAG_ATTR)) {
                //				localMyFavoriteItemInfo.localFlag = Integer.parseInt(attrValue);
            } else if (attrName.equals(MyFavoriteItemTagAttribute.LOCALALBUM_ATTR)) {
                //				localMyFavoriteItemInfo.localAlbum = attrValue;
            } else if (attrName.equals(MyFavoriteItemTagAttribute.LOCALID_ATTR)) {
                //				localMyFavoriteItemInfo.localId = Integer.parseInt(attrValue);
            } else if (attrName.equals(MyFavoriteItemTagAttribute.CREATE_TIME_ATTR)) {
                //				localMyFavoriteItemInfo.addDate = attrValue;
            } else if (attrName.equals(MyFavoriteItemTagAttribute.ADDDATE_ATTR)) {
            } else if (attrName.equals(MyFavoriteItemTagAttribute.SYNCEDTONETWORK_ATTR)) {
                boolean synced = Boolean.parseBoolean(attrValue);
                if(!synced && status != Favorite.STATUS_DELETED){
                    status = Favorite.STATUS_ADDED;
                }
            } else if (attrName.equals(MyFavoriteItemTagAttribute.LOCALDELETED_ATTR)) {
                boolean localDeleted = Boolean.parseBoolean(attrValue);
                if(localDeleted){
                    status = Favorite.STATUS_DELETED;
                }
            } else if (attrName.equals(MyFavoriteItemTagAttribute.STATUS_ATTR)) {
                status = Integer.parseInt(attrValue);
            }
        }
        Favorite fav = null;
        if(mediaInfo.mediaid > 0){
            fav = new OnlineFavorite(mediaInfo);
            if(!mOnlineFavoriteList.contains(fav)){
                mOnlineFavoriteList.add(fav);
            }
            fav.mCreateTime = createTime;
            fav.mStatus = status;
        }
        return true;
    }

    private boolean saveOnlineFavorites(String account) {
        return saveFavoritesByName(account, mOnlineFavoriteList);
    }

    private boolean saveFavoritesByName(String name, List<Favorite> favList){
        OutputStream os = getFavoriteOutputStream(name);
        if (os == null){
            return false;
        }
        long start = System.currentTimeMillis();
        boolean saveSuccess = true;
        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(os, "utf8");
            serializer.startDocument("utf-8", true);
            serializer.startTag(MYFAVORITE_NAMESPACE, MyFavoriteFileXMLTag.MYFAVORITE_LIST);
            for(Favorite fav : favList){
                serializer.startTag(MYFAVORITE_NAMESPACE, MyFavoriteFileXMLTag.MYFAVORITE_ITEM);
                saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.CREATE_TIME_ATTR,
                        String.valueOf(fav.getCreateTime()));
                saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.STATUS_ATTR,
                        String.valueOf(fav.getStatus()));
                if(fav instanceof OnlineFavorite){
                    saveFavoriteAttrs(serializer, (OnlineFavorite)fav);
                }
                serializer.endTag(MYFAVORITE_NAMESPACE, MyFavoriteFileXMLTag.MYFAVORITE_ITEM);	
            }
            serializer.endTag(MYFAVORITE_NAMESPACE, MyFavoriteFileXMLTag.MYFAVORITE_LIST);
            serializer.endDocument();
            serializer.flush();
            os.flush();
        } catch (Exception e) {
            DKLog.e(TAG, "sav fav list failed.", e);
            saveSuccess = false;
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                DKLog.e(TAG, "close output stream exception. ", e);
            }
        }
        long time = System.currentTimeMillis() - start;
        DKLog.d(TAG, "saveMyFavoriteInfo time = " + time);
        return saveSuccess;
    }

    private void saveFavoriteAttrs(XmlSerializer serializer, OnlineFavorite fav) {
        MediaInfo mediaInfo = fav.getMediaInfo();
        if (mediaInfo != null && mediaInfo.mediaid > 0) {
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.MEDIAID_ATTR, 
                    String.valueOf(mediaInfo.mediaid));
            // serializer.attribute(MYFAVORITE_NAMESPACE,
            // MyFavoriteItemTagAttribute.FLAG_ATTR,
            // String.valueOf(mediaInfo.flag));
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.RESOLUTION_ATTR, 
                    String.valueOf(mediaInfo.resolution));
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.CATEGORY_ATTR,
                    mediaInfo.category);
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.MEDIANAME_ATTR,
                    mediaInfo.medianame);
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.DIRECTOR_ATTR,
                    mediaInfo.director);
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.ACTORS_ATTR,
                    mediaInfo.actors);
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.TAGS_ATTR,
                    mediaInfo.allcategorys);
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.IMAGEURLMD5_ATTR,
                    mediaInfo.md5);
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.IMAGEURL_ATTR,
                    mediaInfo.posterurl);
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.SCORE_ATTR,
                    String.valueOf(mediaInfo.score));
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.SETNOW_ATTR,
                    String.valueOf(mediaInfo.setnow));
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.PLAYLENGTH_ATTR,
                    String.valueOf(mediaInfo.playlength));
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.AREA_ATTR,
                    mediaInfo.area);
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.ISSUEDATE_ATTR,
                    mediaInfo.issuedate);
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.LASTISSUEDATE_ATTR,
                    mediaInfo.lastissuedate);
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.SETCOUNT_ATTR,
                    String.valueOf(mediaInfo.setcount));
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.PLAYCOUNT_ATTR,
                    String.valueOf(mediaInfo.playcount));
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.SCORECOUNT_ATTR,
                    String.valueOf(mediaInfo.scorecount));
            saveAttribute(serializer, MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.SETTYPE_ATTR,
                    String.valueOf(mediaInfo.ismultset));
            //			serializer.attribute(MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.MYFAVORITEFLAG_ATTR,
            //					String.valueOf(localMyFavoriteItemInfo.flag));
            //			serializer.attribute(MYFAVORITE_NAMESPACE, MyFavoriteItemTagAttribute.CURLEVEL_ATTR,
            //					String.valueOf(localMyFavoriteItemInfo.level));
        }
    }

    private void saveAttribute(XmlSerializer serializer, String namespace, String name, String value) {
        if(serializer == null || Util.isEmpty(name) || Util.isEmpty(value)) {
            return;
        }
        try {
            serializer.attribute(namespace, name, value);
        } catch (Exception e) {
            DKLog.e(TAG, e.getLocalizedMessage());
        }
    }

    private static class MyFavoriteFileXMLTag {
        public static final String MYFAVORITE_LIST = "myFavoriteList";
        public static final String MYFAVORITE_ITEM = "myFavoriteItem";
    }

    private static class MyFavoriteItemTagAttribute {

        //		public static final String STYLE_ATTR = "favStyle";
        public static final String STATUS_ATTR = "status";
        public static final String CREATE_TIME_ATTR = "createTime";

        //		public static final String MYFAVORITEFLAG_ATTR = "favoriteflag";
        //		public static final String CURLEVEL_ATTR = "level";


        // for mediaInfo
        public static final String MEDIAID_ATTR = "mediaId";
        public static final String FLAG_ATTR = "flag";
        public static final String RESOLUTION_ATTR = "resolution";
        public static final String CATEGORY_ATTR = "category";
        public static final String MEDIANAME_ATTR = "mediaName";
        public static final String DIRECTOR_ATTR = "director";
        public static final String ACTORS_ATTR = "actors";
        public static final String TAGS_ATTR = "tags";
        public static final String IMAGEURLMD5_ATTR = "md5";
        public static final String IMAGEURL_ATTR = "imageurl";
        public static final String SCORE_ATTR = "score";
        public static final String SETNOW_ATTR = "setNow";
        public static final String PLAYLENGTH_ATTR = "playLength";
        public static final String AREA_ATTR = "area";
        public static final String ISSUEDATE_ATTR = "issueDate";
        public static final String LASTISSUEDATE_ATTR = "lastIssueDate";
        public static final String SETCOUNT_ATTR = "setCount";
        public static final String PLAYCOUNT_ATTR = "playCount";
        public static final String SCORECOUNT_ATTR = "scoreCount";
        public static final String SETTYPE_ATTR = "settype";

        // local

        public static final String LOCALCOUNT_ATTR = "localCount";
        public static final String LOCALVIDEOPATH_ATTR = "localVideoPath";
        public static final String LOCALFLAG_ATTR = "localFlag";
        public static final String LOCALALBUM_ATTR = "localAlbum";
        public static final String LOCALID_ATTR = "localId";


        // used for syncing myfavorite between network and localization

        public static final String ADDDATE_ATTR = "addDate";
        public static final String SYNCEDTONETWORK_ATTR = "syncedToNetwork";
        public static final String LOCALDELETED_ATTR = "localDeleted";
    }

}
