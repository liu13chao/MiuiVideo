/**
 * 
 */
package com.miui.videoplayer.ads;

import org.json.JSONObject;

/**
 * @author tianli
 *
 */
public class AdUtils {
    
    public static  JSONObject buildOnlineJson(int mediaId, int ci, int source){
        JSONObject json = new JSONObject();
        try{
            json.put("mediaId", mediaId + "");  
            json.put("source", source + "");
            json.put("ci", ci + "");
        }catch(Exception e){
        }
        return json;
    }
    
    public static JSONObject buildLiveJson(int tvId, int source, String tvProgram){
        JSONObject json = new JSONObject();
        try{
            json.put("tvId", tvId + "");  
            json.put("source", source + "");
            json.put("tvProgram", tvProgram);
        }catch(Exception e){
        }
        return json;
    }
}
