/**
 * 
 */
package com.miui.video.type;


/**
 * @author tianli
 *
 */
public class OnlineMediaInfo extends BaseMediaInfo {

    public static final int MEDIA_TYPE_LONG = 0; // long video
    public static final int MEDIA_TYPE_SHORT = 1; //  short video

    
    private static final long serialVersionUID = 2L;

    public String shortdesc;  // 副标题
    public String posterurl;  //海报url
    public String md5; //海报md5
    public String webpposterurl;
    
    public int videoType; 
    public int mediaid;
    public String medianame;

    @Override
    public String getName() {
        return medianame;
    }

    @Override
    public String getDesc() {
        return "";
    }

    @Override
    public String getUrl() {
        return posterurl;
    }

    @Override
    public ImageUrlInfo getPosterInfo() {
        return new ImageUrlInfo(posterurl, md5, webpposterurl);
    }

    @Override
    public String getMediaStatus() {
        return "";
    }

    @Override
    public String getSubtitle() {
        if(shortdesc == null){
            return "";
        }
        return shortdesc;
    }
    
}
