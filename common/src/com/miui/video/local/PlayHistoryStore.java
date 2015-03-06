/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  PlayHistoryStore.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-21
 */
package com.miui.video.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.miui.video.DKApp;
import com.miui.video.model.AppEnv;
import com.miui.video.type.LocalMedia;
import com.miui.video.util.DKLog;
import com.miui.video.util.ObjectStore;
import com.miui.video.util.Util;

/**
 * @author tianli
 *
 */
public class PlayHistoryStore{

    public static final String TAG = "PlayHistoryStore";

    //	private static final int MAX_LOCALPLAYHISTORY_SIZE = 100;

    //sync var
    //	private List<PlayHistory> mPlayerHistoryList = new ArrayList<PlayHistory>();
    private List<PlayHistory> mVideoHistoryList = new ArrayList<PlayHistory>();
    private List<PlayHistory> mUIHistoryList = new ArrayList<PlayHistory>();

    private HashMap<String, PlayHistory> mPlayerHistory = new HashMap<String, PlayHistory>();

    private SerializableHistoryList mVideoSerializedList = new SerializableHistoryList();


    private static final String PLAY_HISTORY_SOURCE_PACKAGENAME = "com.miui.video";

    private static final String PLAY_HISTORY_FILE_NAME = "play_history.xml";

    private static final String PLAY_HISTORY_DIR = "/play_history/";
    private static final String ANONYMOUS = "anonymous";

    private static final String EXTENSION = ".cache";

    private static final String PLAY_HISTORY_NAMESPACE = "";

    private final String mHistoryDir;

    private Context mContext;

    public PlayHistoryStore(Context context) {
        mContext = context;
        AppEnv env = DKApp.getSingleton(AppEnv.class);
        mHistoryDir = env.getInternalFilesDir() + PLAY_HISTORY_DIR;
    }

    //public method
    protected void loadPlayHistory(String account) {
        loadPlayerHistory();
        loadVideoHistory(account);
        mergeHistoryList();
    }

    protected void reloadPlayerHistory(){
        loadPlayerHistory();
        mergeHistoryList();
    }

    protected List<PlayHistory> getHistoryList() {
        List<PlayHistory> list = new ArrayList<PlayHistory>();
        list.addAll(mUIHistoryList);
        return list;
    }

    protected List<PlayHistory> getPlayerHistoryList() {
        List<PlayHistory> list = new ArrayList<PlayHistory>();
        list.addAll(mPlayerHistory.values());
        return list;
    }

    //	protected List<PlayHistory> getPlayerHistoryList() {
    //	    List<PlayHistory> list = new ArrayList<PlayHistory>();
    //	    list.addAll(mPlayerHistoryList);
    //	    return list;
    //	}

    protected void delHistoryList(String account, List<PlayHistory> delPlayHisList) {
        for(PlayHistory history : delPlayHisList){
            if(history != null){
                mPlayerHistory.remove(history.hashCode() + "");
            }
        }
        //		mPlayerHistoryList.removeAll(delPlayHisList);
        mVideoHistoryList.removeAll(delPlayHisList);

        savePlayerHistoryList();
        writeVideoHistory(account);

        mergeHistoryList();
    }

    protected void saveVideoHistory(String account, PlayHistory playHistory) {
        if(playHistory == null) {
            return;
        }
        //		Log.d(TAG, "saveVideoHistory " + P);
        loadVideoHistory(account);
        int index = getPlayHistoryIndex(playHistory);
        if(index == -1) {
            mVideoHistoryList.add(0, playHistory);
        } else {
            mVideoHistoryList.set(index, playHistory);
        }
        writeVideoHistory(account);
        mergeHistoryList();
    }

    //packaged method
    private int getPlayHistoryIndex(PlayHistory playHistory) {
        if(playHistory != null) {
            for(int i = 0; i < mVideoHistoryList.size(); i++) {
                PlayHistory tmpHistory = mVideoHistoryList.get(i);
                if(tmpHistory != null && tmpHistory.equals(playHistory)) {
                    return i;
                }
            }
        }
        return -1;
    }

    //file input/output stream
    private void prepareDir(){
        File file = new File(mHistoryDir);
        if(!file.exists()){
            file.mkdir();
        }
    }

    private String getVideoHistoryFilePath(String account){
        prepareDir();
        String filename = mHistoryDir;
        if(!TextUtils.isEmpty(account)){
            filename += account;
        }else{
            filename += ANONYMOUS + EXTENSION;
        }
        return filename;
    }

    private InputStream getInputStream(Context context) {
        InputStream  is = null;
        try {
            Context sourceContext = context.createPackageContext(PLAY_HISTORY_SOURCE_PACKAGENAME,
                    Context.CONTEXT_IGNORE_SECURITY);
            if( sourceContext == null)
                return is;
            File file = sourceContext.getFilesDir();
            if(file == null) {
                return is;
            }
            String filesDirPath = file.getAbsolutePath();
            String filePath = filesDirPath + File.separator +PLAY_HISTORY_FILE_NAME;
            file = new File(filePath);
            if(!file.exists()) {
                return is;
            }

            FileInputStream fis = sourceContext.openFileInput(PLAY_HISTORY_FILE_NAME);  
            is = fis;    
        } catch (Exception e) {
            DKLog.e(TAG, e.getLocalizedMessage(), e);
        }
        return is;
    }

    private OutputStream getOutputStream(Context context) {
        OutputStream  os = null;
        try {
            Context sourceContext = context.createPackageContext(PLAY_HISTORY_SOURCE_PACKAGENAME,
                    Context.CONTEXT_IGNORE_SECURITY);
            if( sourceContext == null)
                return os;
            FileOutputStream fos = sourceContext.openFileOutput(PLAY_HISTORY_FILE_NAME, 
                    Context.MODE_PRIVATE);
            os = fos; 
        } catch (Exception e) {
            DKLog.e(TAG, "no play history xml", e);
        }
        return os;
    }

    //load history
    private void loadPlayerHistory(){
        long start = System.currentTimeMillis();
        //		mPlayerHistoryList.clear();
        mPlayerHistory.clear();
        parsePlayerHistoryTask(mContext);
        long end = System.currentTimeMillis();
        DKLog.i(TAG, "loadPlayHistory time = " + (end - start));
    }

    private void loadVideoHistory(String account){
        String path = getVideoHistoryFilePath(account);
        if(!TextUtils.isEmpty(path)){
            Object object = ObjectStore.readObject(path);
            if(object instanceof SerializableHistoryList){
                PlayHistory[] playHistorys = null;  
                playHistorys = ((SerializableHistoryList)object).historyList;
                mVideoHistoryList = filterVideoHistory(array2List(playHistorys));
            }
        }
    }

    private void mergeHistoryList(){
        Log.d(TAG, "mergeHistoryList  ");
        HashMap<String, PlayHistory> map = new HashMap<String, PlayHistory>();
        for(PlayHistory his : mPlayerHistory.values()){
            if(his != null){
                map.put(his.hashCode() + "", his);
            }
//            Log.d("1111111111", "mPlayerHistoryList " + his.playPosition);
//            Log.d("1111111111", "mPlayerHistoryList " + his.mediaId);
//            Log.d("1111111111", "mPlayerHistoryList " + his.mediaUrl);
//            Log.d("1111111111", "mPlayerHistoryList playdate  " + his.playDate);
        }
//        Log.d("222222", "mVideoHistoryList  " + JsonSerializer.getInstance().serialize(mVideoHistoryList));
        for(PlayHistory history : mVideoHistoryList){
            if(history != null){
                String key = String.valueOf(history.hashCode());
                PlayHistory playerHistory = null;
                if(map.containsKey(key)){
                    playerHistory = map.get(key);
                    if(playerHistory != null){
                        if(playerHistory.playDate > history.playDate){
                            history.updatePlayerHistory(playerHistory);
                        }
                    }
                }
                map.put(key, history);
            }
        }
//        Log.d("222222", "mVideoHistoryList  11111111111" + JsonSerializer.getInstance().serialize(map));
        //在线的播放历史，如果播放器记录了而视频没有记录，删除
        Set<Entry<String, PlayHistory>> entrySet =  map.entrySet();
        for(Iterator<Entry<String, PlayHistory>> iterator = entrySet.iterator(); iterator.hasNext();) {
            Entry<String, PlayHistory> entry = iterator.next();
            PlayHistory playHistory = entry.getValue();
            if(playHistory == null || playHistory.getPlayItem() == null) {
                iterator.remove();
            }
        }
//        Log.d("222222", "mVideoHistoryList  after merge: " + JsonSerializer.getInstance().serialize(map));
        ArrayList<PlayHistory> newList = new ArrayList<PlayHistory>();
        if(map.size() > 0){
            newList.addAll(map.values());
            Collections.sort(newList);
        }
        mUIHistoryList = newList;
    }

    //save history
    private void savePlayerHistoryList() {
        writePlayerHistory(mContext, mPlayerHistory.values());
    }

    private void writeVideoHistory(String account) {
        mVideoSerializedList.historyList = list2Array(mVideoHistoryList);
        if(mVideoSerializedList.historyList == null) {
            return;
        }
        String path = getVideoHistoryFilePath(account);
        if(!TextUtils.isEmpty(path)){
            ObjectStore.writeObject(path, mVideoSerializedList);
        }
    }

    //switch between array and list
    private List<PlayHistory> array2List(PlayHistory[] playHistorys) {
        ArrayList<PlayHistory> list = new ArrayList<PlayHistory>();
        if(playHistorys != null) {
            for(int i = 0; i < playHistorys.length; i++) {
                if(playHistorys[i] != null) {
                    list.add( playHistorys[i]);
                }
            }
        }
        return list;
    }

    private PlayHistory[] list2Array(List<PlayHistory> list) {
        PlayHistory[] playHistorys = null;
        if(list != null) {
            playHistorys = new PlayHistory[list.size()];
            for(int i = 0; i < list.size(); i++) {
                PlayHistory playHistory = list.get(i);
                if(playHistory != null) {
                    playHistorys[i] = playHistory;
                }
            }
        }
        return playHistorys;
    }

    //parse player history
    private boolean parsePlayerHistoryTask(Context context) {
        InputStream  is = getInputStream(context);
        if( is == null)
            return false;
        boolean parseSuccess = true;
        try  {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(is, "UTF-8");
            int eventType = xpp.getEventType();
            while( eventType != XmlPullParser.END_DOCUMENT) {
                switch(eventType){
                case XmlPullParser.START_DOCUMENT: {
                    //	DKLog.i(TAG, "Start document");
                }
                break;
                case XmlPullParser.START_TAG: {
                    //	DKLog.i(TAG, "Start tag : " + xpp.getName());
                    parseSuccess = parsePlayerHistoryXMLTagAttribute(xpp);
                }
                break;
                case XmlPullParser.END_TAG: {
                    //	DKLog.i(TAG, "End tag : " + xpp.getName());
                }
                break;
                case XmlPullParser.TEXT:{
                    //	DKLog.i(TAG, "Text : " +xpp.getText());
                }
                break;
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            parseSuccess = false;
            DKLog.e(TAG, e.getLocalizedMessage());
        } catch(IOException e) {
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

    private boolean parsePlayerHistoryXMLTagAttribute(XmlPullParser xpp) {
        String curTag = xpp.getName();
        boolean parseSuccess = true;
        if( curTag.equals(PlayHistoryFileXMLTag.PLAY_HISTORY_LIST))
            return true;
        else if( curTag.equals(PlayHistoryFileXMLTag.PLAY_HISTORY)) {
            parseSuccess = parsePlayerHistoryTagAttribute(xpp);
        }	
        return parseSuccess;
    }

    private boolean parsePlayerHistoryTagAttribute(XmlPullParser xpp) {
        int attrCount = xpp.getAttributeCount();
        OnlinePlayHistory onlineHistory = new OnlinePlayHistory();
        int mediaId = 0;
        LocalMedia localMedia = new LocalMedia();
        LocalPlayHistory localHistory = new LocalPlayHistory(localMedia);
        try {
            for(int i = 0; i < attrCount; i++) {
                String attrName = xpp.getAttributeName(i);
                String attrValue = xpp.getAttributeValue(i);
                if( attrName.equals(PlayHistoryTagAttribute.MEDIAID_ATTR)) {
                    if(!Util.isEmpty(attrValue)) {
                        try{
                            mediaId = Integer.parseInt(attrValue);
                            onlineHistory.mediaId =  localHistory.mediaId = mediaId;
                        }catch(Exception e){
                        }
                    }
                } else if( attrName.equals(PlayHistoryTagAttribute.PLAY_TYPE_ATTR)){
                    onlineHistory.playType = Integer.parseInt(attrValue);
                } else if( attrName.equals(PlayHistoryTagAttribute.MEDIACI_ATTR)){
                    onlineHistory.mediaCi = Integer.parseInt(attrValue);
                } else if( attrName.equals(PlayHistoryTagAttribute.MEDIASOURCE_ATTR)) {
                    onlineHistory.mediaSource = Integer.parseInt(attrValue);
                } else if( attrName.equals(PlayHistoryTagAttribute.VIDEONAME_ATTR)) {
                    localMedia.mediaTitle = attrValue;
                } else if( attrName.equals(PlayHistoryTagAttribute.QUALITY_ATTR)){
                    onlineHistory.quality = Integer.parseInt(attrValue);
                } else if( attrName.equals(PlayHistoryTagAttribute.POSITION_ATTR)){
                    onlineHistory.playPosition = Long.valueOf(attrValue);
                    localHistory.playPosition = onlineHistory.playPosition;
                } else if( attrName.equals(PlayHistoryTagAttribute.DURATION_ATTR)) {
                    localHistory.duration = Long.valueOf(attrValue);
                    localMedia.mediaDuration = Long.valueOf(attrValue);
                } else if( attrName.equals(PlayHistoryTagAttribute.URI_ATTR)){
                    String path = URLDecoder.decode(attrValue, "utf-8");
                    if(!Util.isEmpty(path) && path.startsWith("file://")) {
                        path = path.substring(7);
                    }
                    localHistory.mediaUrl = onlineHistory.mediaUrl = path;
                    localMedia.mediaPath = localHistory.mediaUrl;
                } else if( attrName.equals(PlayHistoryTagAttribute.TIMESTAMP_ATTR)){
                    localHistory.playDate = onlineHistory.playDate = Long.valueOf(attrValue);
                } else if( attrName.equals(PlayHistoryTagAttribute.HTML5PAGE_ATTR)){
                    onlineHistory.html5Page = attrValue;
                } else if( attrName.equals(PlayHistoryTagAttribute.PLAYPARAMETER_ATTR)){
                    //					playHistory.playParameter = attrValue;
                } else if( attrName.equals(PlayHistoryTagAttribute.MEDIASETTYPE_ATTR)){
                    onlineHistory.mediaSetType = Integer.parseInt(attrValue);
                    //					playHistory.mediaSetType = mediaSetType;
                } else if( attrName.equals(PlayHistoryTagAttribute.INBOX_ATTR)){
                    //					int inBox = Integer.parseInt(attrValue);
                    //					localPlayHistory.inBox = inBox;
                    //				} else if(attrName.equals(PlayHistoryTagAttribute.ISSUEDATE_ATTR)) {
                    //					localPlayHistory.issueDate = attrValue;
                } else if(attrName.equals(PlayHistoryTagAttribute.BUCKET_NAME_ATTR)) {
                    //					localPlayHistory.bucketName = attrValue;
                    //				} else if(attrName.equals(PlayHistoryTagAttribute.IMAGE_MD5_ATTR)) {
                    //					localPlayHistory.imageMd5 = attrValue;
                    //				} else if(attrName.equals(PlayHistoryTagAttribute.IMAGE_URL_ATTR)) {
                    //					localPlayHistory.imageUrl = attrValue;
                }
            }
        } catch (Exception e) {
            DKLog.e(TAG, "parse play history tag attribute exception");
        }
        PlayHistory  playHistory = null;
        if(mediaId > 0){
            playHistory = onlineHistory;
        }else{
            playHistory = localHistory;
        }
        String key = playHistory.hashCode() + "";
        if(mPlayerHistory.containsKey(key)){
            PlayHistory lastHistory = mPlayerHistory.get(key);
            if(lastHistory.playDate < playHistory.playDate){
                mPlayerHistory.put(key, playHistory);
            }
        }else{
            mPlayerHistory.put(key, playHistory);
        }
        return true;
    }
    
    public void listPlayHistory(Collection<PlayHistory> list){
        if(list != null){
            for(PlayHistory playHistory : list){
                if(playHistory != null){
                    Log.d(TAG, "listPlayHistory : mediaUrl : " + playHistory.mediaUrl);
                    Log.d(TAG, "listPlayHistory : mediaId : " + playHistory.mediaId);
                    Log.d(TAG, "listPlayHistory : playDate : " + playHistory.playDate);
                    Log.d(TAG, "listPlayHistory : playPosition : " + playHistory.playPosition);
                }
            }
        }
    }

    private List<PlayHistory> filterVideoHistory(List<PlayHistory> list){
        HashMap<String, PlayHistory> hisSet = new HashMap<String, PlayHistory>();
        for(PlayHistory playHistory : list){
            if(playHistory != null){
                String key = playHistory.hashCode() + "";
                if(hisSet.containsKey(key)){
                    PlayHistory lastHistory = hisSet.get(key);
                    if(lastHistory.playDate < playHistory.playDate){
                        hisSet.put(key, playHistory);
                    }else{
                        continue;
                    }
                }else{
                    hisSet.put(key, playHistory);
                }
            }
        }
        list.clear();
        list.addAll(hisSet.values());
        return list;
    }

    //write player history
    private void writePlayerHistory(Context context, Collection<PlayHistory>  playHistoryList) {
        if( playHistoryList == null) {
            return;
        }

        OutputStream  ous = getOutputStream(mContext);
        if(ous == null) {
            return;
        }
        XmlSerializer serializer = Xml.newSerializer(); 
        try {
            serializer.setOutput(ous, "utf8");
            serializer.startDocument("utf-8", true); 
            serializer.startTag(PLAY_HISTORY_NAMESPACE, PlayHistoryFileXMLTag.PLAY_HISTORY_LIST);

            for(PlayHistory playHistory : playHistoryList) {
                serializer.startTag(PLAY_HISTORY_NAMESPACE, PlayHistoryFileXMLTag.PLAY_HISTORY);
                if(playHistory != null) {
                    saveAttribute(serializer, PLAY_HISTORY_NAMESPACE, PlayHistoryTagAttribute.MEDIAID_ATTR, 
                            String.valueOf(playHistory.mediaId));
                    saveAttribute(serializer, PLAY_HISTORY_NAMESPACE, PlayHistoryTagAttribute.POSITION_ATTR, 
                            String.valueOf(playHistory.playPosition));
                    saveAttribute(serializer, PLAY_HISTORY_NAMESPACE, PlayHistoryTagAttribute.DURATION_ATTR, 
                            String.valueOf(playHistory.duration));
                    saveAttribute(serializer, PLAY_HISTORY_NAMESPACE, PlayHistoryTagAttribute.TIMESTAMP_ATTR, 
                            String.valueOf(playHistory.playDate));
                    saveAttribute(serializer, PLAY_HISTORY_NAMESPACE, PlayHistoryTagAttribute.URI_ATTR, 
                            playHistory.mediaUrl);
                    saveAttribute(serializer, PLAY_HISTORY_NAMESPACE, PlayHistoryTagAttribute.PLAY_TYPE_ATTR, 
                            String.valueOf(playHistory.playType));						
                }
                serializer.endTag(PLAY_HISTORY_NAMESPACE, PlayHistoryFileXMLTag.PLAY_HISTORY);
            }

            serializer.endTag(PLAY_HISTORY_NAMESPACE, PlayHistoryFileXMLTag.PLAY_HISTORY_LIST);
            serializer.endDocument();
            serializer.flush();	
            ous.flush();
        } catch (Exception e) {
            DKLog.e(TAG, e.getLocalizedMessage());
        } finally {
            try {	
                ous.close();
            } catch (IOException e) {
                DKLog.e(TAG, e.getLocalizedMessage());
            }
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

    //self def class
    private static class SerializableHistoryList implements Serializable{
        private static final long serialVersionUID = 2L;
        PlayHistory[] historyList;
    }

    private static class PlayHistoryFileXMLTag {
        public static final String  PLAY_HISTORY_LIST = "playHistoryList";
        public static final String  PLAY_HISTORY = "playHistory";
    }

    public static class PlayHistoryTagAttribute {
        //必须存在的attr个数
        //	public static final int  attrCount = 12;

        public static final String VIDEO_ATTR = "videoJason";
        public static final String PLAYER_ATTR = "playerJason";

        public static final String  MEDIAID_ATTR = "mediaId";
        public static final String  MEDIACI_ATTR = "mediaCi";
        public static final String  MEDIASOURCE_ATTR = "mediaSource";
        public static final String  VIDEONAME_ATTR = "videoName";
        public static final String  QUALITY_ATTR = "quality";
        public static final String  POSITION_ATTR = "position";
        public static final String  DURATION_ATTR = "duration";
        public static final String  URI_ATTR = "uri";
        public static final String  TIMESTAMP_ATTR = "timeStamp";
        public static final String  HTML5PAGE_ATTR = "html5Page";
        public static final String  PLAYPARAMETER_ATTR = "playParameter";
        public static final String  MEDIASETTYPE_ATTR = "mediaSetType";
        public static final String  INBOX_ATTR = "inBox";
        public static final String  ISSUEDATE_ATTR = "issueDate";
        public static final String  BUCKET_NAME_ATTR = "bucketName";
        public static final String  IMAGE_MD5_ATTR = "imageMd5";
        public static final String  IMAGE_URL_ATTR = "imageUrl";
        public static final String  PLAY_TYPE_ATTR = "playType";
    }


}
