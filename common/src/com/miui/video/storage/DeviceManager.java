/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   DeviceManager.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2013-1-17 
 */
package com.miui.video.storage;

import java.util.ArrayList;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import com.miui.video.model.AppSingleton;
import com.miui.video.util.DKLog;

/**
 * @author tianli
 *
 */
public class DeviceManager extends AppSingleton{
	
	public final static String TAG = DeviceManager.class.getName();
	
	private ArrayList<DeviceObserver> mObservers = new ArrayList<DeviceObserver>();
	private ArrayList<BaseDevice> mDevices = new ArrayList<BaseDevice>();

	private AndroidUpnpService mUpnpService;
	private BrowseRegistryListener mRegistryListener = new BrowseRegistryListener();
	
	private Handler mHandler = new Handler();
	
	public void scan(){
		searchDLNADevices();
	}
	
	public void addObserver(DeviceObserver observer){
		if(observer != null && !mObservers.contains(observer)){
			mObservers.add(observer);
		}
	}
	
	public void removeObserver(DeviceObserver observer){
		if(observer != null && mObservers.contains(observer)){
			mObservers.remove(observer);
		}
	}
	
	public BaseDevice findDeviceByName(String name) {
		DKLog.i(TAG, "findDeviceByName: " + name);
		for(BaseDevice device : mDevices){
			DKLog.i(TAG, "find device: " + device.getRootPath() + " ,name: " + device.getName());
			if(device.getName() == null){
				continue;
			}
			if(device.getName().equals(name)){
				return device;
			}
		}
		return null;
	}
	
	public BaseDevice findDeviceByRootPath(String rootPath) {
		for(BaseDevice device : mDevices){
			DKLog.i(TAG, "find device: " + device.getRootPath() + " ,name: " + device.getName());
			if(device.getRootPath() == null){
				continue;
			}
			
			if(device.getRootPath().equals(rootPath)){
				return device;
			}
		}
		return null;
	}
	
	public ArrayList<BaseDevice> getDevices(){
		return mDevices;
	}
	
	public int getDeviceCount(){
		return mDevices.size();
	}
	
	//packaged method
	private void addDLNADevice(RemoteDevice device) {
		mHandler.post(new AddDLNADeviceRunnable(device));
	}
	
	private void removeDLNADevice(RemoteDevice device) {
		mHandler.post(new RemoveDLNADeviceRunnable(device));
	}

	private void searchDLNADevices(){
		if(mUpnpService == null || mUpnpService.getControlPoint() == null){
			bindDLNAService();
		}else{
			mUpnpService.getControlPoint().search();
		}
	}
	
	private void bindDLNAService(){
		this.mContext.bindService(new Intent(this.mContext, AndroidUpnpServiceImpl.class),
	    		   serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void notifyDeviceAdded(BaseDevice device){
		for(DeviceObserver observer : mObservers){
			if(observer != null){
				observer.onDeviceAdded(device);
			}
		}
	}
	
	private void notifyDeviceRemoved(BaseDevice device){
		for(DeviceObserver observer : mObservers){
			if(observer != null){
				observer.onDeviceRemoved(device);
			}
		}
	}
	
	//UI task
	private class AddDLNADeviceRunnable implements Runnable {

		private RemoteDevice device;
		
		public AddDLNADeviceRunnable(RemoteDevice device) {
			this.device = device;
		}
		
		@Override
		public void run() {
			DLNADevice dlnaDevice = new DLNADevice(mContext, (RemoteDevice) device, 
					mUpnpService.getControlPoint());
			if(!mDevices.contains(dlnaDevice)){
				mDevices.add(dlnaDevice);
				notifyDeviceAdded(dlnaDevice);
			}
		}
	}
	
	private class RemoveDLNADeviceRunnable implements Runnable {

		private RemoteDevice device;
		
		public RemoveDLNADeviceRunnable(RemoteDevice device) {
			this.device = device;
		}

		@Override
		public void run() {
			DLNADevice dlnaDevice = new DLNADevice(mContext, (RemoteDevice) device, 
					mUpnpService.getControlPoint());
			if(mDevices.contains(dlnaDevice)){
				int pos = mDevices.indexOf(dlnaDevice);
				 if(pos >= 0){
					 notifyDeviceRemoved(mDevices.remove(pos));
				 }
			}
		}
	}
	
	private class BrowseRegistryListener extends DefaultRegistryListener {
		@Override
        public void beforeShutdown(Registry registry) {
        	DKLog.d(TAG, "Before shutdown, the registry has devices: " 
        			+ registry.getDevices().size());
        } 

		@Override
        public void afterShutdown() {
			DKLog.d(TAG,"Shutdown of registry complete!");
        }
		
		@SuppressWarnings("rawtypes")
		@Override
		public void deviceAdded(Registry registry, final Device device) {
			DKLog.d(TAG, "deviceAdded");
			if (device.getType().getType().equals("MediaServer") && (device instanceof RemoteDevice)) {
				if (mUpnpService != null && mUpnpService.getControlPoint() != null) {
					addDLNADevice((RemoteDevice)device);
				}
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void deviceRemoved(Registry registry, final Device device) {
			DKLog.d(TAG, "deviceRemoved");
			if (device.getType().getType().equals("MediaServer")) {
				if (mUpnpService != null && mUpnpService.getControlPoint() != null) {
					removeDLNADevice((RemoteDevice)device);
				}
			}
		}
	} 
	
	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			mUpnpService = (AndroidUpnpService) service;
			for (Device<?, ?, ?> device : mUpnpService.getRegistry().getDevices()) {
				mRegistryListener.deviceAdded(mUpnpService.getRegistry(), device);
			}

			// Getting ready for future device advertisements
			mUpnpService.getRegistry().addListener(mRegistryListener);
			
			// Search asynchronously for all devices
			mUpnpService.getControlPoint().search();
		}

		public void onServiceDisconnected(ComponentName className) {
			mUpnpService = null;
		}
	};
	
	//self def class
	public interface DeviceObserver{
		public void onDeviceAdded(BaseDevice device);
		public void onDeviceRemoved(BaseDevice device);
	}
}
