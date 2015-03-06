/**
 *   Copyright(c) 2012 DuoKan TV Group
 *
 *   JsonParser.java
 *
 *   @author tianli (tianli@xiaomi.com)
 *
 *   @date 2013-10-13
 */
package com.xiaomi.mitv.common.webservice;


import com.xiaomi.mitv.common.json.JsonSerializer;

/**
 * @author tianli
 *
 */
public class JsonParser implements AbsParser<ServiceResponse> {
	
    public static final String TAG = JsonParser.class.getName();
    @Override
    public ServiceResponse parse(byte[] buf, String encode) {
    	ServiceResponse response = createResponse();
        try {
            String json = new String(buf, 0, buf.length, "utf-8");
//            Log.d(TAG, "got response: " + json);
            ServiceResponse ret = null;
            ret = JsonSerializer.getInstance().deserialize(json, response.getClass());
            if(ret != null){
            	return ret;
            }
        } catch (Exception e) {

        }
        response.setStatus(ServiceResponse.STATUS_UNKOWN_ERROR);
        return response;

    }
    
    public ServiceResponse createResponse() {
        return new ServiceResponse();
    }
    
}
