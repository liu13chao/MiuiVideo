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

import com.miui.video.DKApp;
import com.miui.videoplayer.download.SourceManager;
import com.miui.videoplayer.model.MediaConfig;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author tianli
 *
 */
public class IfengVideoView extends DexVideoView {
    
    public IfengVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public IfengVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IfengVideoView(Context context) {
        super(context);
    }

    @Override
    protected void requestDexPath() {
        setZipPath(DKApp.getSingleton(SourceManager.class).getSourceConfig(
                getSource()));
    }

    @Override
    protected int getSource() {
        return MediaConfig.MEDIASOURCE_IFENG_PHONE_TYPE_CODE;
    }
}
