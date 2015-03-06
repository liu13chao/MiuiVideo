package com.miui.video.db;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.content.Context;

import com.miui.video.model.AppSingleton;
import com.miui.video.offline.OfflineMedia;

/**
 * util class for accessing Offline DB.
 * 
 * All methods should be called in ui-thread.
 * @author zzc
 *
 */
public class OfflineDBManager extends AppSingleton {
	
	private static final Executor WORKER = Executors.newSingleThreadExecutor();
	
	private OfflineMediaSqliteOpenHelper mHelper;
	
	@Override
    public void init(Context context) {
        super.init(context);
        mHelper = new OfflineMediaSqliteOpenHelper(context);
    }

    public void isInDatabase(final int mediaID, final int episode, final DBOperationCallback<Boolean> callback) {
		new DBTask<Boolean>(callback) {
			@Override
			protected Boolean doDBOperation() {
				return mHelper.isInDatabase(mediaID, episode);
			}
			
		}.executeOnExecutor(WORKER);
	}
	
	public void isFinished(final int mediaID, final int episode, DBOperationCallback<Boolean> callback) {
		new DBTask<Boolean>(callback) {
			@Override
			protected Boolean doDBOperation() {
				return mHelper.isFinished(mediaID, episode);
			}
		}.executeOnExecutor(WORKER);
	}
	
	public void getAllRecords(final DBOperationCallback<List<OfflineMedia>> callback) {
	    getAllRecordsFromDB(new DBOperationCallback<List<OfflineMedia>>() {
	        @Override
	        public void onResult(List<OfflineMedia> result) {
	            if(result == null || result.size() == 0){
	                getAllRecordsFromStorage(callback);
	            }else{
	                if(callback != null){
	                    callback.onResult(result);
	                }
	            }
	        }
	    });
	}
	
	private void getAllRecordsFromDB(DBOperationCallback<List<OfflineMedia>> callback){
        new DBTask<List<OfflineMedia>>(callback) {
            @Override
            protected List<OfflineMedia> doDBOperation() {
                return mHelper.getAllRecords();
            }
        }.executeOnExecutor(WORKER);
	}
	
	   private void getAllRecordsFromStorage(DBOperationCallback<List<OfflineMedia>> callback){
	        new DBTask<List<OfflineMedia>>(callback) {
	            @Override
	            protected List<OfflineMedia> doDBOperation() {
	                return mHelper.recoverAllRecords();
	            }
	        }.executeOnExecutor(WORKER);
	    }
	
	public void getFinishedRecords(DBOperationCallback<List<OfflineMedia>> callback) {
		new DBTask<List<OfflineMedia>>(callback) {
			
			@Override
			protected List<OfflineMedia> doDBOperation() {
				return mHelper.getFinishedRecords();
			}
			
		}.executeOnExecutor(WORKER);
	}
	
	public void getUnfinishedRecords(DBOperationCallback<List<OfflineMedia>> callback) {
		new DBTask<List<OfflineMedia>>(callback) {
			
			@Override
			protected List<OfflineMedia> doDBOperation() {
				return mHelper.getUnfinishedRecords();
			}
			
		}.executeOnExecutor(WORKER);
	}
	
	public void getRecord(final int mediaID, final int episode, DBOperationCallback<OfflineMedia> callback) {
		new DBTask<OfflineMedia>(callback) {
			@Override
			protected OfflineMedia doDBOperation() {
				return mHelper.getRecord(mediaID, episode);
			}
			
		}.executeOnExecutor(WORKER);
	}
	
	public void addRecord(final OfflineMedia offlineMedia, DBOperationCallback<Boolean> callback) {
		new DBTask<Boolean>(callback) {
			
			@Override
			protected Boolean doDBOperation() {
				return mHelper.addRecord(offlineMedia);
			}
			
		}.executeOnExecutor(WORKER);
	}
	
	public void deleteAllRecords(DBOperationCallback<Boolean> callback) {
		new DBTask<Boolean>(callback) {
			
			@Override
			protected Boolean doDBOperation() {
				return mHelper.deleteAllRecords();
			}
			
		}.executeOnExecutor(WORKER);
	}
	
	public void deleteFinishedRecords(DBOperationCallback<Boolean> callback) {
		new DBTask<Boolean>(callback) {
			
			@Override
			protected Boolean doDBOperation() {
				return mHelper.deleteFinishedRecords();
			}
			
		}.executeOnExecutor(WORKER);
	}
	
	public void deleteUnfinishedRecords(DBOperationCallback<Boolean> callback) {
		new DBTask<Boolean>(callback) {
			
			@Override
			protected Boolean doDBOperation() {
				return mHelper.deleteUnfinishedRecords();
			}
			
		}.executeOnExecutor(WORKER);
	}
	
	public void deleteRecord(final OfflineMedia offlineMedia, DBOperationCallback<Boolean> callback) {
		new DBTask<Boolean>(callback) {
			
			@Override
			protected Boolean doDBOperation() {
				return mHelper.deleteRecord(offlineMedia.mediaId, offlineMedia.episode);
			}
			
		}.executeOnExecutor(WORKER);
	}
	
	public void updateRecord(final OfflineMedia offlineMedia, DBOperationCallback<Boolean> callback) {
		new DBTask<Boolean>(callback) {
			
			@Override
			protected Boolean doDBOperation() {
				return mHelper.updateRecord(offlineMedia);
			}
			
		}.executeOnExecutor(WORKER);
	}

}
