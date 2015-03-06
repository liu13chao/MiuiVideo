package com.miui.videoplayer.framework.milink;

import java.util.List;

public interface IDeviceDiscoveryListener {
    //public void onDeviceAvailable(List<String> deviceList);
    public void onOpened();
    public void onDeviceAdded(String newDevice);
    public void onDeviceRemoved(String removedDevice);
}
