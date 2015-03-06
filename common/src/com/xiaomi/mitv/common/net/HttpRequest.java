/**
 *  Copyright(C) 2012 DuoKan TV Group
 * 
 *  HttpRequest.java  
 * 
 *  @author tianli (tianli@duokan.com)
 *
 *  @date 2012-6-21
 */
package com.xiaomi.mitv.common.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

/**
 * @author tianli
 *
 */
public class HttpRequest{
	
    private String mUrl;
    private List<NameValuePair> mParams = new ArrayList<NameValuePair>();
    private byte[] mBody;
    
    public String getUrl(){
        return mUrl;
    }
    public void setUrl(String url) {
        this.mUrl = url;
    }
    
	public List<NameValuePair> getParams() {
		return mParams;
	}
	public void setParams(List<NameValuePair> params) {
		if(params != null){
			this.mParams = params;
		}
	}
	public byte[] getBody() {
		return mBody;
	}
	public void setBody(byte[] body) {
		this.mBody = body;
	}
    
}
