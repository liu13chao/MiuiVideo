/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   IfengVideoView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-23
 */
package com.miui.videoplayer.videoview;

import android.content.Context;
import android.util.AttributeSet;

import com.miui.video.DKApp;
import com.miui.videoplayer.download.SourceManager;
import com.miui.videoplayer.model.MediaConfig;

/**
 * @author tianli
 *
 */
public class QiyiVideoView extends DexVideoView {

    public QiyiVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public QiyiVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QiyiVideoView(Context context) {
        super(context);
    }

    @Override
    protected void requestDexPath() {
        setZipPath(DKApp.getSingleton(SourceManager.class).getSourceConfig(
                getSource()));
    }
    
    @Override
    protected int getSource() {
        return MediaConfig.MEDIASOURCE_IQIYI_PHONE_TYPE_CODE;
    }

    @Override
    public boolean hasLoadingAfterAd() {
        return false;
    }
}
