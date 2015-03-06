/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   SeriesEpListPopup.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-30
 */
package com.miui.videoplayer.menu.popup;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.miui.video.R;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.videoplayer.adapter.OnlineEpisodeAdapter;
import com.miui.videoplayer.fragment.VideoProxy;
import com.miui.videoplayer.model.Episode;
import com.miui.videoplayer.model.OnlineLoader;
import com.miui.videoplayer.model.UriLoader;

/**
 * @author tianli
 *
 */
public class SeriesEpListPopup extends BaseMenuPopup {

    private UriLoader mUriLoader;
    private OnlineEpisodeAdapter mOnlineEpisodeAdapter;
    private VideoProxy mVideoProxy;
    
    public SeriesEpListPopup(Context context, UriLoader uriLoader, VideoProxy proxy) {
        super(context);
        mVideoProxy = proxy;
        mUriLoader = uriLoader;
        mOnlineEpisodeAdapter = new OnlineEpisodeAdapter(context);
        buildData();
        setTitle(context.getResources().getString(R.string.vp_select_ci));
    }
    
    private void buildData(){
        if(mUriLoader != null){
            if(mUriLoader.getPlayingUri() != null) {
                mOnlineEpisodeAdapter.setSelectedEpisode(mUriLoader.getPlayingUri().getCi());
            }
            mOnlineEpisodeAdapter.setGroup(mUriLoader.getEpisodeList());
            mListView.setAdapter(mOnlineEpisodeAdapter);
            mListView.setOnItemClickListener(mOnItemClickListener);
        }
    }

    @Override
    protected int getPopupWidth() {
        if(mUriLoader instanceof OnlineLoader) {
            int mediaSetStyle = ((OnlineLoader) mUriLoader).getMediaStyle();
            if(mediaSetStyle == MediaConstantsDef.MEDIA_TYPE_VARIETY) {
                return getContext().getResources().getDimensionPixelSize(
                        R.dimen.vp_menu_popup_variety_width);
            }
        }  
        return getContext().getResources().getDimensionPixelSize(
                R.dimen.vp_menu_popup_ep_width);
    }

    //UI callback
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            dismiss();
            Episode episode = mOnlineEpisodeAdapter.getItem(position);
            if(episode != null) {
                if(mUriLoader.getPlayingUri() != null
                        && mUriLoader.getPlayingUri().getCi() == episode.getCi()) {
                    return;
                }
                if(mVideoProxy != null){
                    mVideoProxy.playCi(episode.getCi());
                }
            }
        }
    };
    
    
}
