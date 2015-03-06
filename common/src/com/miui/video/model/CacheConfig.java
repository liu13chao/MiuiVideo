/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   CacheConfig.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-12-18
 */
package com.miui.video.model;

import java.io.File;

import com.miui.video.DKApp;

/**
 * @author tianli
 *
 */
public class CacheConfig {

    public final static String CACHE_PATH = "/cache";

    public static String getCacheRootDir(){
        AppEnv appEnv = DKApp.getSingleton(AppEnv.class);
        String dir = appEnv.getInternalFilesDir() + CacheConfig.CACHE_PATH;
        File file = new File(dir);
        if(!file.exists()){
            file.mkdir();
        }
        return dir;
    }
}
