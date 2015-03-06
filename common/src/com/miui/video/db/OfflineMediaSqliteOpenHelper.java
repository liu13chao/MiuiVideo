/**
 * 
 */
package com.miui.video.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.miui.video.DKApp;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.model.AppEnv;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.DKLog;
import com.miui.video.util.ObjectStore;

public class OfflineMediaSqliteOpenHelper extends SQLiteOpenHelper {
    private final static String TAG = "OfflineMediaSqliteOpenHelper";	

    public OfflineMediaSqliteOpenHelper(Context context) {
        super(context, DBUtil.OFFLINE_DB_NAME, null, DBUtil.OFFLINE_VERSION);
        // check the version.
        try{
            SQLiteDatabase db =  getSafeReadableDatabase();
            db.close();
        }catch(Exception e){
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DKLog.d(TAG, "on create");
        String sql = "create table if not exists " + DBUtil.OFFLINE_TABLE_NAME
                + "("
                + DBUtil.MEDIA_ID + " integer, "
                + DBUtil.CURRENT_EPISODE + " integer, "
                + DBUtil.MEDIA_LENGTH + " integer, "
                + DBUtil.MEDIA_SOURCE + " integer, "
                + DBUtil.MEDIA_NAME + " string, "
                + DBUtil.LOCAL_PATH + " string, "
                + DBUtil.REMOTE_URL + " string, "
                + DBUtil.MEDIA_STATUS + " int, "
                + DBUtil.MEDIA_FILE_SIZE + " int, "
                + DBUtil.MEDIA_COMPLETE_SIZE + " int, "
                + DBUtil.MEDIA_TYPE + " int, "
                + DBUtil.MEDIA_COMPLETE_LINES + " int, "
                + DBUtil.MEDIA_IS_MULTSET + " int, "
                + DBUtil.MEDIA_INFO + " string, "
                + DBUtil.MEDIA_EP_NAME + " string "
                + ")";
        execSQL(db, sql);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DKLog.d(TAG, "on downgrade: " + oldVersion + " to " + newVersion);
        dropTable(db);
        onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DKLog.d(TAG, "onUpgrade: ");
        dropTable(db);
        onCreate(db);
    }

    private boolean dropTable(SQLiteDatabase db) {
        if (db == null) {
            return true;
        }
        String sql = "drop table " + DBUtil.OFFLINE_TABLE_NAME;
        return execSQL(db, sql);
    }

    private SQLiteDatabase getSafeReadableDatabase() {
        try {
            return getReadableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private SQLiteDatabase getSafeWritableDatabase() {
        try {
            return getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isInDatabase(int mediaID, int episode) {
        return isInDatabase(getSafeReadableDatabase(), mediaID, episode);
    }

    public boolean isFinished(int mediaID, int episode) {
        return isFinished(getSafeReadableDatabase(), mediaID, episode);
    }

    public List<OfflineMedia> getAllRecords() {
        return getAllRecords(getSafeReadableDatabase());
    }

    public List<OfflineMedia> getFinishedRecords() {
        return getFinishedRecords(getSafeReadableDatabase());
    }

    public List<OfflineMedia> getUnfinishedRecords() {
        return getUnfinishedRecords(getSafeReadableDatabase());
    }

    public OfflineMedia getRecord(int mediaID, int episode) {
        return getRecord(getSafeReadableDatabase(), mediaID, episode);
    }

    public boolean deleteAllRecords() {
        return deleteAllRecords(getSafeWritableDatabase());
    }

    public boolean deleteFinishedRecords() {
        return deleteFinishedRecords(getSafeWritableDatabase());
    }

    public boolean deleteUnfinishedRecords() {
        return deleteUnfinishedRecords(getSafeWritableDatabase());
    }

    public boolean deleteRecord(int mediaID, int episode) {
        return deleteRecord(getSafeWritableDatabase(), mediaID, episode);
    }

    public boolean updateRecord(OfflineMedia offlineMedia) {
        return updateRecord(getSafeWritableDatabase(), offlineMedia);
    }

    public boolean addRecord(OfflineMedia offlineMedia) {
        return addRecord(getSafeWritableDatabase(), offlineMedia);
    }

    private boolean isInDatabase(SQLiteDatabase db, int mediaID, int episode) {
        String sql = "select * from " + DBUtil.OFFLINE_TABLE_NAME
                + " where "	+ DBUtil.MEDIA_ID + "=" + mediaID
                + " and " + DBUtil.CURRENT_EPISODE + "=" + episode;
        return exists(db, sql);
    }

    private boolean isFinished(SQLiteDatabase db, int mediaID, int episode) {
        String sql = "select * from " + DBUtil.OFFLINE_TABLE_NAME
                + " where "	+ DBUtil.MEDIA_ID + "=" + mediaID
                + " and " + DBUtil.CURRENT_EPISODE + "=" + episode
                + " and " + DBUtil.MEDIA_STATUS + "=" + MediaConstantsDef.OFFLINE_STATE_FINISH;
        return exists(db, sql);
    }

    private boolean exists(SQLiteDatabase db, String sql) {
        if (db == null || sql == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor == null) {
            return false;
        }
        int nCount = cursor.getCount();
        cursor.close();
        return nCount > 0;
    }

    private List<OfflineMedia> getFinishedRecords(SQLiteDatabase db) {
        String sql = "select * from " + DBUtil.OFFLINE_TABLE_NAME
                + " where "	+ DBUtil.MEDIA_STATUS + "=" + MediaConstantsDef.OFFLINE_STATE_FINISH;
        return getRecords(db, sql);
    }

    private List<OfflineMedia> getUnfinishedRecords(SQLiteDatabase db) {
        String sql = "select * from " + DBUtil.OFFLINE_TABLE_NAME
                + " where "	+ DBUtil.MEDIA_STATUS + "<>" + MediaConstantsDef.OFFLINE_STATE_FINISH;
        return getRecords(db, sql);
    }

    private List<OfflineMedia> getAllRecords(SQLiteDatabase db) {
        String sql = "select * from " + DBUtil.OFFLINE_TABLE_NAME;
        return getRecords(db, sql);
    }

    private OfflineMedia getRecord(SQLiteDatabase db, int mediaID, int episode) {
        String sql = "select * from " + DBUtil.OFFLINE_TABLE_NAME
                + " where "	+ DBUtil.MEDIA_ID + "=" + mediaID
                + " and " + DBUtil.CURRENT_EPISODE + "=" + episode;
        List<OfflineMedia> results = getRecords(db, sql);
        if (results == null || results.size() <= 0) {
            return null;
        }
        return results.get(0);
    }

    private List<OfflineMedia> getRecords(SQLiteDatabase db, String sql) {
        List<OfflineMedia> list = new ArrayList<OfflineMedia>();
        Cursor cursor = null;
        try{
            if (db == null || sql == null) {
                return list;
            }
            cursor = db.rawQuery(sql, null);
            if(cursor != null){
                while (cursor.moveToNext()) {
                    list.add(cursor2OfflineMedia(cursor));
                }
            }
        }catch(Throwable t){
        }finally{
            if(cursor != null){
                cursor.close();
            }
        }
        return list;
    }

    private static OfflineMedia cursor2OfflineMedia(Cursor cursor) {
        final OfflineMedia offlineMedia = new OfflineMedia();
        offlineMedia.mediaId = cursor.getInt(0);
        offlineMedia.episode = cursor.getInt(1);
        offlineMedia.playLength = cursor.getInt(2);
        offlineMedia.source = cursor.getInt(3);
        offlineMedia.mediaName = cursor.getString(4);
        offlineMedia.localPath = cursor.getString(5);
        offlineMedia.remoteUrl = cursor.getString(6);
        offlineMedia.status = cursor.getInt(7);
        offlineMedia.fileSize = cursor.getInt(8);
        offlineMedia.completeSize = cursor.getInt(9);
        offlineMedia.type = cursor.getInt(10);
        offlineMedia.nLineFinish = cursor.getInt(11);
        offlineMedia.ismultset = cursor.getInt(12);
        offlineMedia.mediaInfo = MediaInfo.parseFromJson(cursor.getString(13));
        offlineMedia.epName = cursor.getString(14);
        return offlineMedia;
    }

    private boolean deleteRecord(SQLiteDatabase db, int mediaID, int episode) {
        if (db == null) {
            return false;
        }
        String sql = "delete from " + DBUtil.OFFLINE_TABLE_NAME
                + " where "	+ DBUtil.MEDIA_ID + "=" + mediaID
                + " and " + DBUtil.CURRENT_EPISODE + "=" + episode;
        return execSQL(db, sql);
    }

    //	private boolean deleteRecord(SQLiteDatabase db, int mediaID) {
    //		if (db == null) {
    //			return false;
    //		}
    //		String sql = "delete from " + ProviderUtil.OFFLINE_TABLE_NAME
    //				+ " where "	+ ProviderUtil.MEDIA_ID + "=" + mediaID;
    //		return execSQL(db, sql);
    //	}

    private boolean deleteFinishedRecords(SQLiteDatabase db) {
        if (db == null) {
            return false;
        }
        String sql = "delete from " + DBUtil.OFFLINE_TABLE_NAME
                + " where "	+ DBUtil.MEDIA_STATUS + "=" + MediaConstantsDef.OFFLINE_STATE_FINISH;
        return execSQL(db, sql);
    }

    private boolean deleteUnfinishedRecords(SQLiteDatabase db) {
        if (db == null) {
            return false;
        }
        String sql = "delete from " + DBUtil.OFFLINE_TABLE_NAME
                + " where "	+ DBUtil.MEDIA_STATUS + "<>" + MediaConstantsDef.OFFLINE_STATE_FINISH;
        return execSQL(db, sql);
    }

    private boolean deleteAllRecords(SQLiteDatabase db) {
        if (db == null) {
            return false;
        }
        try {
            db.delete(DBUtil.OFFLINE_TABLE_NAME, null, null);
            return true;
        } catch (Exception e) {
            DKLog.d(TAG, e.getLocalizedMessage());
            return false;
        }
    }

    private boolean updateRecord(SQLiteDatabase db, OfflineMedia offlineMedia) {
        if(db == null || offlineMedia == null) {
            return false;
        }
        String sql = "update " + DBUtil.OFFLINE_TABLE_NAME
                + " set " + DBUtil.LOCAL_PATH + "=" + "'" + avoidNull(offlineMedia.localPath) + "'"
                + ", " + DBUtil.REMOTE_URL + "=" + "'" + avoidNull(offlineMedia.remoteUrl) + "'"
                + ", " + DBUtil.MEDIA_FILE_SIZE + "=" + offlineMedia.fileSize
                + ", " + DBUtil.MEDIA_COMPLETE_SIZE + "=" + offlineMedia.completeSize
                + ", " + DBUtil.MEDIA_STATUS + "=" + offlineMedia.status
                + ", " + DBUtil.MEDIA_TYPE + "=" + offlineMedia.type
                + ", " + DBUtil.MEDIA_COMPLETE_LINES + "=" + offlineMedia.nLineFinish
                + " where " + DBUtil.MEDIA_ID  + "=" + offlineMedia.mediaId
                + " and " + DBUtil.CURRENT_EPISODE + "=" + offlineMedia.episode;
        DKLog.d(TAG, "update offline media:  " + sql);
        return execSQL(db, sql);
    }

    private boolean addRecord(SQLiteDatabase db, OfflineMedia offlineMedia) {
        if(db == null || offlineMedia == null) {
            return false;
        }
        String sql = "insert into " + DBUtil.OFFLINE_TABLE_NAME
                + "("
                + DBUtil.MEDIA_ID + ", "
                + DBUtil.CURRENT_EPISODE + ", "
                + DBUtil.MEDIA_LENGTH + ", "
                + DBUtil.MEDIA_SOURCE + ", "
                + DBUtil.MEDIA_NAME + ", "
                + DBUtil.LOCAL_PATH + ", "
                + DBUtil.REMOTE_URL + ", "
                + DBUtil.MEDIA_STATUS + ", "
                + DBUtil.MEDIA_FILE_SIZE + ", "
                + DBUtil.MEDIA_COMPLETE_SIZE + ", "
                + DBUtil.MEDIA_TYPE + ", "
                + DBUtil.MEDIA_COMPLETE_LINES + ", "
                + DBUtil.MEDIA_IS_MULTSET + ", "
                + DBUtil.MEDIA_INFO + ", "
                + DBUtil.MEDIA_EP_NAME
                + ")"
                + " values("
                + "'" + offlineMedia.mediaId + "'" + ", "
                + "'" + offlineMedia.episode + "'" + ", "
                + "'" + offlineMedia.playLength + "'" + ", "
                + "'" + offlineMedia.source + "'" + ", "
                + "'" + avoidNull(offlineMedia.mediaName) + "'" + ", "
                + "'" + avoidNull(offlineMedia.localPath) + "'" + ", "
                + "'" + avoidNull(offlineMedia.remoteUrl) + "'" + ", "
                + "'" + offlineMedia.status + "'" + ", "
                + "'" + offlineMedia.fileSize + "'" + ", "
                + "'" + offlineMedia.completeSize + "'" + ", "
                + "'" + offlineMedia.type + "'" + ", "
                + "'" + offlineMedia.nLineFinish + "'" + ", "
                + "'" + offlineMedia.ismultset + "'"  + ", "
                + "'" + avoidNull(offlineMedia.mediaInfo != null ? offlineMedia.mediaInfo.toJson() : "") + "'"  + ", "
                + "'" + avoidNull(offlineMedia.epName) + "'"
                + ")";
        DKLog.d(TAG, "add:  " + sql);
        return execSQL(db, sql);
    }

    private static String avoidNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    private boolean execSQL(SQLiteDatabase db, String sql) {
        try {
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            DKLog.e(TAG, e.getLocalizedMessage());
            return false;
        }
    }

    public List<OfflineMedia> recoverAllRecords() {
        DKLog.d(TAG, "recoverAllRecords");
        List<OfflineMedia> list = new ArrayList<OfflineMedia>();
        try{
            String offlineDir = DKApp.getSingleton(AppEnv.class).getOfflineDir();
            File rootFile = new File(offlineDir);
            if(!rootFile.exists() || !rootFile.isDirectory()){
                return list;
            }
            File[] paths = rootFile.listFiles();
            for (File dir : paths) {
                if (dir == null ||!dir.isDirectory()) {
                    continue;
                }
                File[] files = dir.listFiles();
                for (File file: files) {
                    if (!DBUtil.OFFLINE_MEDIA_CONFIG.equals(file.getName())) {
                        continue;
                    }
                    Object object = ObjectStore.readObject(file.getAbsolutePath());
                    if (object instanceof OfflineMedia) {
                        OfflineMedia offlineMedia = (OfflineMedia)object;
                        list.add(offlineMedia);
                        addRecord(offlineMedia);
                    }
                }
            }
        }catch(Throwable t){
        }
        return list;
    }
}
