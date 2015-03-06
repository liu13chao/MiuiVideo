/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   AddonProxyActivity.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-5-25
 */

package com.miui.video.addon;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

import junit.framework.Assert;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import dalvik.system.DexClassLoader;

/**
 * @author tianli
 *
 */
public class AddonProxyActivity extends Activity {
	
	private static HashMap<String, DexClassLoader> sClassLoaderCache = 
			new HashMap<String, DexClassLoader>();
	
	private HashMap<String, Method> mAddonMethods = new HashMap<String, Method>();
	
	public static final String ADDON_CLASS = "addonClass";
	public static final String ADDON_PATH = "addonPath";
	private static final String DEX_OUT = "dex";
	
	private Class<?> mAddonClass = null;
	private Object mAddonObject = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadAddon(getIntent());
		invoke("onCreate", savedInstanceState);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		invoke("onNewIntent", intent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		invoke("onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		invoke("onResume");
	}

	@Override
	protected void onStart() {
		super.onStart();
		invoke("onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();
		invoke("onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		invoke("onDestroy");
	}

	@Override
	protected void onPause() {
		super.onPause();
		invoke("onPause");
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Object result = invoke("dispatchKeyEvent", event);
		if(result instanceof Boolean && (Boolean)result){
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Object result = invoke("dispatchTouchEvent", ev);
		if(result instanceof Boolean && (Boolean)result){
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		invoke("onActivityResult", requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		invoke("onBackPressed");
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		invoke("onLowMemory");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		invoke("onRestoreInstanceState", savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		invoke("onSaveInstanceState", outState);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		invoke("onConfigurationChanged", newConfig);
	}

	/* Action Mode start.*/
	@Override
	public void onActionModeFinished(ActionMode mode) {
		super.onActionModeFinished(mode);
		invoke("onActionModeFinished", mode);
	}

	@Override
	public void onActionModeStarted(ActionMode mode) {
		super.onActionModeStarted(mode);
		invoke("onActionModeStarted", mode);
	}
	/* Action Mode end.*/

	/* Menu methods start */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Object result = invoke("onCreateOptionsMenu", menu);
		if(result instanceof Boolean && (Boolean)result){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Object result = invoke("onOptionsItemSelected", item);
		if(result instanceof Boolean && (Boolean)result){
			return true;
		}
		return false;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		invoke("onOptionsMenuClosed", menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Object result = invoke("onPrepareOptionsMenu", menu);
		if(result instanceof Boolean && (Boolean)result){
			return true;
		}
		return false;
	}
	
	/* Menu methods end.*/

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		Object result = invoke("onGenericMotionEvent", event);
		if(result instanceof Boolean && (Boolean)result){
			return true;
		}
		return super.onGenericMotionEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Object result = invoke("onKeyDown", keyCode, event);
		if(result instanceof Boolean && (Boolean)result){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		Object result = invoke("onKeyLongPress", keyCode, event);
		if(result instanceof Boolean && (Boolean)result){
			return true;
		}
		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		Object result = invoke("onKeyMultiple", keyCode, repeatCount, event);
		if(result instanceof Boolean && (Boolean)result){
			return true;
		}
		return super.onKeyMultiple(keyCode, repeatCount, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Object result = invoke("onKeyUp", keyCode, event);
		if(result instanceof Boolean && (Boolean)result){
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	public Method getMethod(String name){
		return mAddonMethods.get(name);
	}

	private void loadMethod(Class<?> clazz, String name, Class<?>... paramTypes){
		Method method = null;
		try {
			method = clazz.getMethod(name, paramTypes);
			method.setAccessible(true);
			mAddonMethods.put(name, method);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertTrue("method " + name + " not found.", method != null);
	}
	public Object invoke(String name, Object... args){
		try {
			return getMethod(name).invoke(mAddonObject, args);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue("invoke exception.", false);
		}
		return null;
	}

	DexClassLoader getClassLoader(String addonPath){
		if(sClassLoaderCache.containsKey(addonPath)){
			return sClassLoaderCache.get(addonPath);
		}
		File dexOutputDir = getDir(DEX_OUT, 0);
		final String dexOutputPath = dexOutputDir.getAbsolutePath();
		ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
		DexClassLoader dexClassLoader = new DexClassLoader(addonPath, dexOutputPath, null, localClassLoader);
		sClassLoaderCache.put(addonPath, dexClassLoader);
		return dexClassLoader;
	}
	
	public void loadAddon(Intent intent){
		String addonName = intent.getStringExtra(ADDON_CLASS);
		String addonPath = intent.getStringExtra(ADDON_PATH);
		try{
			mAddonClass = getClassLoader(addonPath).loadClass(addonName);
			loadAddonMethods(mAddonClass);
			Constructor<?> ctor = mAddonClass.getConstructor();
			mAddonObject = ctor.newInstance();
			Method setProxy = mAddonClass.getMethod("setProxy", Activity.class, String.class);
			setProxy.setAccessible(true);
			setProxy.invoke(mAddonObject, this, addonPath);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadAddonMethods(Class<?> addonClass){
		loadMethod(addonClass, "onCreate", Bundle.class);
		loadMethod(addonClass, "onNewIntent", Intent.class);
		loadMethod(addonClass, "onStart", new Class[]{});
		loadMethod(addonClass, "onResume", new Class[]{});
		loadMethod(addonClass, "onRestart", new Class[]{});
		loadMethod(addonClass, "onPause", new Class[]{});
		loadMethod(addonClass, "onStop", new Class[]{});
		loadMethod(addonClass, "onDestroy", new Class[]{});
		loadMethod(addonClass, "dispatchKeyEvent", KeyEvent.class);
		loadMethod(addonClass, "dispatchTouchEvent", MotionEvent.class);
		loadMethod(addonClass, "onBackPressed", new Class[]{});
		loadMethod(addonClass, "onLowMemory", new Class[]{});
		loadMethod(addonClass, "onRestoreInstanceState", Bundle.class);
		loadMethod(addonClass, "onSaveInstanceState", Bundle.class);
		loadMethod(addonClass, "onActivityResult", int.class, int.class, Intent.class);
		loadMethod(addonClass, "onConfigurationChanged", Configuration.class);
		loadMethod(addonClass, "onActionModeFinished", ActionMode.class);
		loadMethod(addonClass, "onActionModeStarted", ActionMode.class);
		loadMethod(addonClass, "onCreateOptionsMenu", Menu.class);
		loadMethod(addonClass, "onOptionsItemSelected", MenuItem.class);
		loadMethod(addonClass, "onOptionsMenuClosed", Menu.class);
		loadMethod(addonClass, "onPrepareOptionsMenu", Menu.class);
		loadMethod(addonClass, "onGenericMotionEvent", MotionEvent.class);
		loadMethod(addonClass, "onKeyDown", int.class, KeyEvent.class);
		loadMethod(addonClass, "onKeyLongPress", int.class, KeyEvent.class);
		loadMethod(addonClass, "onKeyMultiple", int.class, int.class, KeyEvent.class);
		loadMethod(addonClass, "onKeyUp", int.class, KeyEvent.class);
	}

}
