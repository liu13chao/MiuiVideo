/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   GenericPlayFragment.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-24
 */

package com.miui.videoplayer.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import com.miui.video.R;
import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.common.Constants;
import com.miui.videoplayer.framework.history.PlayHistoryManager;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.menu.MenuItem;
import com.miui.videoplayer.model.BaseUri;
import com.miui.videoplayer.model.LocalUri;
import com.miui.videoplayer.model.PlayHistoryVistor;
import com.miui.videoplayer.model.UriArrayLoader;
import com.miui.videoplayer.model.UriLoader;

/**
 * @author tianli
 *
 */
public class GenericPlayFragment extends CoreFragment {

    public static final String TAG = "GenericPlayFragment";

    private String mTitle;
    private String mXvxPeerId;
    private String mXvxCertFile;

    public GenericPlayFragment(Context context, FrameLayout anchor) {
        super(context, anchor);
    }

    @Override
    public void launch(Intent intent) {
        init(intent);
    }

    private void init(Intent intent){
        Uri uri = intent.getData();
        int ci = 0;
        mTitle = intent.getStringExtra(Constants.INTENT_KEY_STRING_MEDIA_TITLE);
        if (uri != null && uri.getScheme() != null && TextUtils.isEmpty(mTitle)) {
            mTitle = uri.getLastPathSegment();
            if(!TextUtils.isEmpty(mTitle)){
                int pos = mTitle.lastIndexOf("/");
                if(pos >= 0 && pos < mTitle.length() - 1){
                    mTitle = mTitle.substring(pos + 1, mTitle.length());
                }
            }
        }
        //mXvxPeerId = "F8B156B8DBC57OSC";
        //mXvxCertFile = "file:///storage/sdcard0/Download/certificate.xlsn";
        mXvxPeerId = intent.getStringExtra(Constants.XVX_PEER_ID);
        mXvxCertFile = intent.getStringExtra(Constants.XVX_CERT_FILE);
        Log.i(TAG, "xvx peer id: " + mXvxPeerId + " , cert file: " + mXvxCertFile);
        //		mSubtitle = intent.getStringExtra(Constants.MEDIA_SUBTITLE);
        String[] uris = intent.getStringArrayExtra(Constants.INTENT_KEY_STRING_ARRAY_URI_LIST);
        ArrayList<String> uriList = new ArrayList<String>();
        if(uris == null){
            if(uri == null){
                throw new IllegalArgumentException("uri can not be null.");
            }
            uriList.add(uri.toString());
            ci = 0;
        }else{
            ci = intent.getIntExtra(Constants.INTENT_KEY_INT_PLAY_INDEX, 0);
            for(int i = 0; i < uris.length; i++){
                String value = uris[i];
                if(!TextUtils.isEmpty(value)){
                    uriList.add(value);
                }
            }
            if(ci >= 0 && ci < uriList.size()){
                uri = Uri.parse(uriList.get(ci));
            }else if(uriList.size() > 0){
                uri = Uri.parse(uriList.get(0));
                ci = 0;
            }else{
                throw new IllegalArgumentException("uri list size must larger than 0.");
            }
        }
        uris = new String[uriList.size()];
        for(int i = 0; i < uriList.size(); i++){
            uris[i] = uriList.get(i);
        }
        mUriLoader = new UriArrayLoader(uris, null);
        mUri = new LocalUri(uri, mTitle, ci);
        ((UriArrayLoader)mUriLoader).setRepeated(isRepeated());
    }

    @Override
    public UriLoader getUriLoader() {
        return mUriLoader;
    }

    @Override
    protected void onPlay(BaseUri uri) {
        if(uri instanceof LocalUri){
            if (mXvxPeerId != null && mXvxCertFile != null) {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("peer_id", mXvxPeerId);
                headers.put("cert_file", mXvxCertFile);
                mVideoView.setDataSource(uri.getUri().toString(), headers);
            } else {
                mVideoView.setDataSource(uri.getUri().toString());
            }
            mVideoView.start();
        }
    }

    @Override
    public void onSavePlayHistory(PlayHistoryManager playMgr) {
        BaseUri uri = getUri();
        if(uri instanceof LocalUri){
            String scheme = uri.getUri().getScheme();
            if(scheme != null && !(scheme.equals("rtsp") && mVideoView.getDuration() <= 0)){
                Log.d(TAG, " position " + 
                        mVideoView.getCurrentPosition() + ", duration = " + 
                        mVideoView.getDuration() + ",  uri " + uri.getUri());
                playMgr.savePlayPosition(uri.getUri(), mVideoView.getCurrentPosition(), 
                        mVideoView.getDuration());
                playMgr.save();
            }
        }
    }

    public boolean isRepeated(){
        return false;
    }

    @Override
    public PlayHistoryEntry onLoadPlayHistory(PlayHistoryManager playMgr) {
        return PlayHistoryVistor.create(mUri).visit(playMgr);
    }

    @Override
    public CharSequence getVideoTitle() {
        return mTitle;
    }

    @Override
    public CharSequence getVideoSubtitle() {
        if(mUri != null && AndroidUtils.isOnlineVideo(mUri.getUri())){
            return mContext.getResources().getString(R.string.top_status_online_media);
        }else{
            return mContext.getResources().getString(R.string.top_status_local_media);
        }
    }

    @Override
    public List<MenuItem> getMenu() {
        return super.getMenu();
    }
}
