package com.miui.videoplayer.framework.milink;

public class VideoControlData {
    private String TAG = "VCD";
    private byte resolution = 0;
    private int duration = -1;
    private int position = 0;
    private byte volume = 10;
    private boolean playing = false;
    private boolean pausing = false;
    private String url;

    public VideoControlData() {
    }

    public byte getResolution() {
        return resolution;
    }
    public void setResolution(byte resolution) {
        this.resolution = resolution;
        return;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
        return;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
        return;
    }
    public void setURL(String url) {
        this.url = url;
    }
    public String getURL() {
        return url;
    }
    public byte getVolume() {
        return volume;
    }
    public void setVolume(byte volume) {
        this.volume = volume;
        return;
    }
    public boolean isPlaying() {
        return playing && !pausing;
    }
    public boolean playing() {
        return playing;
    }
    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
    public boolean pausing() {
        return pausing;
    }
    public void setPausing(boolean pausing) {
        this.pausing = pausing;
    }
}
