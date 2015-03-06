/**
 *   Copyright(c) 2012 DuoKan TV Group
 *
 *   JsonSerializer.java
 *
 *   @author tianli (tianli@duokan.com)
 *
 *   @date 2012-6-24
 */
package com.xiaomi.mitv.common.json;

import java.util.Collection;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import android.util.Log;

/**
 * @author tianli
 *
 */
public class JsonSerializer {
	
    private static final String TAG = JsonSerializer.class.getName();
    private static JsonSerializer sInstance = new JsonSerializer(); 
    
    private ObjectMapper mImpl;

    public static JsonSerializer getInstance() {
        return sInstance;
    }
    
    JsonSerializer() {
        try {
            mImpl = new ObjectMapper();
        } catch (Exception e) {
        }
        DeserializationConfig cfg = mImpl.getDeserializationConfig();
        cfg.set(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    public String serialize(Object object) {
        try {
            return mImpl.writeValueAsString(object);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(),e);
            return null;
        }
    }
        
    public <T> T deserialize(String json,  Class<T> clazz) {
        try {
            return mImpl.readValue(json, clazz);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage(),e);
            return null;
        }
    }
    
    public <T extends Collection<?>,V> Object deserialize(String json, Class<T> collection, Class<V> data) {
        try {
            return mImpl.readValue(json, TypeFactory.collectionType(collection, data));
        }catch(Exception e) {
            Log.e(TAG, e.getMessage(),e);
            return null;
        }
    }
}
