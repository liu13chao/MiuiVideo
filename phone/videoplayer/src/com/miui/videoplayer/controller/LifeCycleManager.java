/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   LifeCycleManager.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-27
 */
package com.miui.videoplayer.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tianli
 *
 */
public class LifeCycleManager implements LifeCycle{
    
    List<LifeCycle> mQueue = new ArrayList<LifeCycle>();
    
    public LifeCycleManager(){
    }
    
    public void add(LifeCycle cycle){
        if(cycle != null && !mQueue.contains(cycle)){
            mQueue.add(cycle);
        }
    }
    
    public void remove(LifeCycle cycle){
        if(cycle != null && mQueue.contains(cycle)){
            mQueue.remove(cycle);
        }
    }
    
    @Override
    public void onCreate() {
        for(LifeCycle cycle : mQueue){
            cycle.onCreate();
        }
    }

    @Override
    public void onStart() {
        for(LifeCycle cycle : mQueue){
            cycle.onStart();
        }
    }

    @Override
    public void onResume() {
        for(LifeCycle cycle : mQueue){
            cycle.onResume();
        }
    }

    @Override
    public void onPause() {
        for(LifeCycle cycle : mQueue){
            cycle.onPause();
        }
    }

    @Override
    public void onStop() {
        for(LifeCycle cycle : mQueue){
            cycle.onStop();
        }
    }

    @Override
    public void onDestroy() {
        for(LifeCycle cycle : mQueue){
            cycle.onDestroy();
        }
    }
    
}
