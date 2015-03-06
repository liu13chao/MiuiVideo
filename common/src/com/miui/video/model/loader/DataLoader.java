/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  DataLoader.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-11-25
 */
package com.miui.video.model.loader;

import java.util.ArrayList;

import android.os.AsyncTask;

/**
 * @author tianli
 *
 */
public abstract class DataLoader {

    protected ArrayList<LoadListener> mListeners = new ArrayList<LoadListener>();

    public interface LoadListener{
        public void onLoadFinish(DataLoader loader);
        public void onLoadFail(DataLoader loader);
    }
    
    public abstract void load();

    public void onPreStorageLoad(){
    }
    
    public  void doStorageLoad(){
    }
    
    public  void onPostStorageLoad(){
    }


    public void addListener(LoadListener listener) {
        synchronized (mListeners) {
            mListeners.add(listener);
        }
    }

    public void removeListener(LoadListener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    public void notifyDataReady(){
        synchronized (mListeners) {
            for(LoadListener listener : mListeners) {
                if(listener != null) {
                    listener.onLoadFinish(this);
                }
            }
        }
    }

    public void notifyDataFail(){
        synchronized (mListeners) {
            for(LoadListener listener : mListeners) {
                if(listener != null) {
                    listener.onLoadFail(this);
                }
            }
        }
    }
    
    public class AsyncLoadTask extends AsyncTask<Void, Void, Void>{
        
        public void start(){
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onPreStorageLoad();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            doStorageLoad();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            onPostStorageLoad();
        }
    }

    
}

