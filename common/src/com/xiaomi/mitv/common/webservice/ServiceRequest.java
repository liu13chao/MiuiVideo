/**
 *  Copyright(C) 2012 DuoKan TV Group
 * 
 *  DKHttpRequest.java  
 * 
 *  @author tianli (tianli@duokan.com)
 *
 *  @date 2012-6-21
 */
package com.xiaomi.mitv.common.webservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.miui.video.DKApp;
import com.miui.video.util.AlertMessage;
import com.miui.video.util.Util;
import com.xiaomi.mitv.common.net.HttpClient;
import com.xiaomi.mitv.common.net.HttpRequest;
import com.xiaomi.mitv.common.net.HttpResponse;
import com.xiaomi.mitv.common.net.NetworkErrorException;
import com.xiaomi.mitv.common.net.ServerErrorException;
import com.xiaomi.mitv.common.net.TimeoutException;

/**
 * @author tianli
 *
 */
public abstract class ServiceRequest extends AsyncRequest {
    private static final String TAG = ServiceRequest.class.getName();
    
    private static final int MESSAGE_UPDATE_PROGRESS = 1000;
    
    private List<NameValuePair> mParams = new ArrayList<NameValuePair>();
    protected String mUrl;
    private Handler mHandler;
    Observer mObserver;
    
    private boolean showResultDesc = true;
    
    protected byte[] mBody;

    protected ServiceResponse mResponse;
    
    public ServiceRequest() {
        mHandler = new MessageHandler(this);
    }
    
    public int getResult() {
    	if(mResponse != null) {
    		return mResponse.getStatus();
    	}
    	return -1;
    }
    
	protected boolean isSecurity(){
    	return false;
    }

    @Override
    protected void onPreRequest() {
	}

    private void dumpUrl() {
        String fullUrl = mUrl;
        if(!fullUrl.contains("?")) {
            fullUrl += "?";
        }
        String query = URLEncodedUtils.format(mParams, HTTP.UTF_8);
        fullUrl += query;
//        Log.d(TAG, "request url = " + fullUrl);
    }
    
    public List<NameValuePair> getParams() {
		return mParams;
	}

	final protected void addParam(String key, String value){
    	if(key != null && key.length() > 0 && value != null && value.length() > 0){
    		mParams.add(new BasicNameValuePair(key, value));
    	}
    }
	
	public ServiceResponse execSync(){
		onRequestInBackground();
		return mResponse;
	}

    @Override
    protected void onRequestInBackground(){
    	dumpUrl();
        HttpRequest request = new HttpRequest();
        request.setUrl(mUrl);
        request.setParams(mParams);
        request.setBody(mBody);
        HttpClient httpClient = new HttpClient();
        JsonParser parser = createParser();
        mResponse = parser.createResponse();
        HttpResponse response = null;
        try {
            response = httpClient.doRequest(request, mBody == null && mParams.size() == 0);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            final int blockSize = 8192;
            byte[] buffer = new byte[blockSize];
            int count = 0;
            long received = 0;
            while((count = response.getContentStream().read(buffer, 0 , blockSize)) > 0) {
                byteStream.write(buffer,0, count);
                received += count;
                long total = response.getContentLength();
                if(total > 0) {
                    Message msg = mHandler.obtainMessage(MESSAGE_UPDATE_PROGRESS);
                    int progress = (int)(received * 100 / total);
                    msg.obj = progress > 100 ? 100 : progress;
                    mHandler.sendMessage(msg);
                }
            }
            ServiceResponse tmpResponse = parser.parse(byteStream.toByteArray(), "utf-8");
//            mResponse = parser.parse(byteStream.toByteArray(), "utf-8");
            response.getContentStream().close();
            if(tmpResponse != null){
            	mResponse = tmpResponse;
            }else {
                mResponse.setStatus(ServiceResponse.STATUS_UNKOWN_ERROR);
            }
        }catch(TimeoutException e) {
            mResponse.setStatus(ServiceResponse.STATUS_NETWORK_ERROR);
            Log.e(TAG, e.getMessage(), e);
        }catch(NetworkErrorException e) {
            mResponse.setStatus(ServiceResponse.STATUS_NETWORK_ERROR);
            Log.e(TAG, e.getMessage(), e);
        }catch(ServerErrorException e) {
            mResponse.setStatus(ServiceResponse.STATUS_SERVER_ERROR);
            Log.e(TAG, e.getMessage(), e);
        }catch(IOException e) {
            mResponse.setStatus(ServiceResponse.STATUS_UNKOWN_ERROR);
            Log.e(TAG, e.getMessage(), e);
        }catch(Exception e) {
            mResponse.setStatus(ServiceResponse.STATUS_UNKOWN_ERROR);
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    protected void onPostRequest() {
        if(mObserver != null) {
            if (mResponse == null) {
            	mResponse = new ServiceResponse(ServiceResponse.STATUS_UNKOWN_ERROR);
            }
            mResponse.completeData();
            mObserver.onRequestCompleted(this, mResponse);
            if (showResultDesc && !Util.isEmpty(mResponse.getDesc())) {
				AlertMessage.show(DKApp.getAppContext(), mResponse.getDesc());
			}
        }
    }

    protected JsonParser createParser(){
        return new JsonParser();
    }

    public void setObserver(Observer observer) {
        this.mObserver = observer;
    }
    
    public void setShowResultDesc(boolean showResultDesc) {
		this.showResultDesc = showResultDesc;
	}

    @Override
    public void onCancelReuqest() {
        super.onCancelReuqest();
        mObserver = null;
        this.showResultDesc = false;
    }
    
    public static interface Observer {
        public void onRequestCompleted(ServiceRequest request, ServiceResponse response);
        public void onProgressUpdate(ServiceRequest request, int progress);
    }
    
    static class MessageHandler extends Handler{
    	public ServiceRequest mRequest;
    	
    	public MessageHandler(ServiceRequest request){
    		super(Looper.getMainLooper());
    		mRequest = request;
    	}
    	
    	public void handleMessage(Message msg) {
    		switch(msg.what) {
    		case MESSAGE_UPDATE_PROGRESS:
    			Integer progress = (Integer)msg.obj;
    			if(mRequest.mObserver != null) {
    				mRequest.mObserver.onProgressUpdate(mRequest, progress);
    			}
    			break;
    		}
    	}
    }

}
