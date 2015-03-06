package com.miui.videoplayer.playservice;

interface IPlayService {
    String getPlayUrl(int source, String sdkInfo, String extraInfo);
    void closePlay(int source, String extraInfo);
    void onEvent(int source, String extraInfo, String key, String value);
}
