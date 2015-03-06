package com.miui.videoplayer.framework.milink;


public interface IDeviceDiscoveryListener {
    //public void onDeviceAvailable(List<String> deviceList);
    public void onOpened();
    public void onDeviceAdded(String newDevice);
    public void onDeviceRemoved(String removedDevice);
}
