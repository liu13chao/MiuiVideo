package com.miui.video.offline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.MediaSetInfo;

public class OfflineMedia extends BaseMediaInfo implements Serializable, Comparable<OfflineMedia>,
Cloneable {

    public static final String TAG = "OfflineMedia";

    private static final long serialVersionUID = 2L;

    public MediaInfo mediaInfo;

    public int mediaId;
    public int episode;
    public int playLength;
    public int source;
    public String epName = "";
    public String mediaName = "";
    public String localPath = "";
    public String remoteUrl = "";
    public int status;
    public int fileSize;
    public int completeSize;
    public int type; // 0: mp4, 1: m3u8
    public int nLineFinish;
    public int ismultset; // 单集 、多集

    public OfflineMedia() {
    }

    public static OfflineMedia from(MediaDetailInfo2 detail, int ci,
            int preferSource, String epName) {
        if (detail == null || detail.mediainfo == null) {
            return null;
        }
        OfflineMedia media = new OfflineMedia();
        media.mediaId = detail.mediainfo.mediaid;
        media.episode = ci;
        if (detail.mediaciinfo != null) {
            media.playLength = detail.mediaciinfo.getPlayLength(ci);
        } else {
            media.playLength = 0;
        }
        media.mediaInfo = detail.mediainfo;
        media.source = preferSource;
        media.epName = epName;
        media.mediaName = detail.mediainfo.medianame;
        media.status = MediaConstantsDef.OFFLINE_NONE;
        media.fileSize = 0;
        media.completeSize = 0;
        media.type = MediaConstantsDef.SOURCE_TYPE_MEDIA;
        media.nLineFinish = -1;
        media.ismultset = detail.mediainfo.ismultset;
        return media;
    }

    public static List<OfflineMedia> from(MediaDetailInfo2 detail,
            int preferSource) {
        List<OfflineMedia> medias = new ArrayList<OfflineMedia>();
        if (detail == null || detail.mediainfo == null
                || detail.mediaciinfo == null
                || detail.mediaciinfo.videos == null) {
            return medias;
        }
        for (MediaSetInfo video : detail.mediaciinfo.videos) {
            if (video != null) {
                OfflineMedia media = from(detail, video.ci, preferSource, video.videoname);
                if (media != null) {
                    medias.add(media);
                }
            }
        }
        return medias;
    }

    public String getKey() {
        return mediaId + "_" + episode;
    }

    public int getType() {
        if (remoteUrl != null && remoteUrl.contains(".m3u")) {
            return MediaConstantsDef.SOURCE_TYPE_M3U8;
        } else {
            return MediaConstantsDef.SOURCE_TYPE_MEDIA;
        }
    }

    public String getName() {
        return DKApp.getAppContext().getResources()
                .getString(R.string.di_count_ji, episode);
    }

    public String getStatus() {
        switch (status) {
        case MediaConstantsDef.OFFLINE_STATE_IDLE:
            return DKApp.getAppContext().getResources()
                    .getString(R.string.waiting_to_download);
        case MediaConstantsDef.OFFLINE_STATE_LOADING:
            return DKApp.getAppContext().getResources()
                    .getString(R.string.download_percent_hint, getPercent());
        case MediaConstantsDef.OFFLINE_STATE_PAUSE:
            return DKApp.getAppContext().getResources()
                    .getString(R.string.pause);
        case MediaConstantsDef.OFFLINE_STATE_FINISH:
            return formatMediaDuration();
        case MediaConstantsDef.OFFLINE_STATE_INIT:
            return DKApp.getAppContext().getResources()
                    .getString(R.string.init);
        case MediaConstantsDef.OFFLINE_STATE_CONNECT_ERROR:
            return DKApp.getAppContext().getResources()
                    .getString(R.string.connect_error);
        case MediaConstantsDef.OFFLINE_STATE_FILE_ERROR:
            return DKApp.getAppContext().getResources()
                    .getString(R.string.file_error);
        case MediaConstantsDef.OFFLINE_STATE_SOURCE_ERROR:
            return DKApp.getAppContext().getResources()
                    .getString(R.string.source_error);
        default:
            return DKApp.getAppContext().getResources()
                    .getString(R.string.download_error);
        }
    }
    
    public float getPercent() {
        float percent = 0;
        if (fileSize > 0) {
            final int type = getType();
            if (type == MediaConstantsDef.SOURCE_TYPE_M3U8) {
                if (nLineFinish > 0) {
                    percent = nLineFinish / (float) fileSize * 100;
                }
            } else {
                if (completeSize > 0) {
                    percent = completeSize / (float) fileSize * 100;
                }
            }
        }
        return percent;
    }

    private static final int ONE_MINUTE = 60;
    private static final int ONE_HOUR = ONE_MINUTE * 60;

    private String formatMediaDuration() {
        final int second = playLength % ONE_MINUTE;
        final int hour = playLength / ONE_HOUR;
        final int minute = (playLength - hour * ONE_HOUR) / ONE_MINUTE;
        StringBuilder strBuilder = new StringBuilder();
        if (hour < 10) {
            strBuilder.append("0");
        }
        strBuilder.append(hour);
        strBuilder.append(":");
        if (minute < 10) {
            strBuilder.append("0");
        }
        strBuilder.append(minute);
        strBuilder.append(":");
        if (second < 10) {
            strBuilder.append("0");
        }
        strBuilder.append(second);
        return strBuilder.toString();
    }

    public boolean isNone() {
        return status == MediaConstantsDef.OFFLINE_NONE;
    }

    public boolean isWaiting() {
        return status == MediaConstantsDef.OFFLINE_STATE_IDLE;
    }

    public boolean isLoading() {
        return status == MediaConstantsDef.OFFLINE_STATE_INIT
                || status == MediaConstantsDef.OFFLINE_STATE_LOADING;
    }

    public boolean isPaused() {
        return status == MediaConstantsDef.OFFLINE_STATE_PAUSE;
    }

    public boolean isFinished() {
        return status == MediaConstantsDef.OFFLINE_STATE_FINISH;
    }

    public boolean isError() {
        return status == MediaConstantsDef.OFFLINE_STATE_CONNECT_ERROR
                || status == MediaConstantsDef.OFFLINE_STATE_FILE_ERROR
                || status == MediaConstantsDef.OFFLINE_STATE_SOURCE_ERROR;
    }

    public boolean isUnrecovrableError() {
        return status == MediaConstantsDef.OFFLINE_STATE_FILE_ERROR
                || status == MediaConstantsDef.OFFLINE_STATE_SOURCE_ERROR;
    }

    public boolean isMultiSetType() {
        return ismultset > 0;
    }

    @Override
    public int compareTo(OfflineMedia another) {
        if (another == null) {
            return 1;
        } else {
            if (mediaId < another.mediaId) {
                return -1;
            } else if (mediaId > another.mediaId) {
                return 1;
            } else {
                if (episode < another.episode) {
                    return -1;
                } else if (episode > another.episode) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public ImageUrlInfo getPosterInfo() {
        if(mediaInfo != null){
            return mediaInfo.getPosterInfo();
        }
        return null;
    }

    @Override
    public String getMediaStatus() {
        return "";
    }

    @Override
    public String getSubtitle() {
        return "";
    }

    @Override
    public String getDesc() {
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if(o == null){
            return super.equals(o);
        }else{
            return hashCode() == o.hashCode();
        }
    }

    @Override
    public int hashCode() {
        return (mediaId + "_"  + episode).hashCode();
    }
}