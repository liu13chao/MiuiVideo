/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  DexLoader.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-23
 */
package com.miui.video.model;

import java.io.File;
import java.util.HashMap;

import android.text.TextUtils;
import dalvik.system.DexClassLoader;

/**
 * @author tianli
 *
 */
public class DexLoader extends AppSingleton{
    
    private static final String DEX_OUT = "dex";
    
    private HashMap<String, DexClassLoader> mClassLoaderCache = 
            new HashMap<String, DexClassLoader>();
    
    public DexClassLoader getClassLoader(String dexPath, ClassLoader classLoader){
        if(TextUtils.isEmpty(dexPath)){
            return null;
        }
        String key = getKey(dexPath);
        if(mClassLoaderCache.containsKey(key)){
            return mClassLoaderCache.get(key);
        }
        File dexOutputDir = mContext.getDir(DEX_OUT, 0);
        final String dexOutputPath = dexOutputDir.getAbsolutePath();
//        ClassLoader classLoader = null;
//        if(clazz != null){
//            classLoader = clazz.getClassLoader();
//        }else{
//            classLoader = ClassLoader.getSystemClassLoader();
//        }
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, dexOutputPath, 
                null, classLoader);
        mClassLoaderCache.put(key, dexClassLoader);
        return dexClassLoader;
    }
    
//    public DexClassLoader getClassLoader(String dexPath){
//        return getClassLoader(dexPath, null);
//    }

    private String getKey(String dexPath){
//        if(clazz != null){
//            return dexPath + "," + clazz.getName();
//        }
        return dexPath;
    }
    
//    public <T> T loadClassInDex (String dexPath, Class<T> clazz, String className, Class<?>... paramTypes){
//        try{
//            DexClassLoader classLoader = getClassLoader(dexPath, clazz);
//            if(classLoader != null){
//                Class<?>  c = classLoader.loadClass(className);
//                Constructor<?> ctor = c.getConstructor(paramTypes);
//            }
//        }catch(Exception e){
//        }
//    }

}
