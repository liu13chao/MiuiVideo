/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   SoundEffect.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-9-2
 */

package com.miui.videoplayer.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.util.Log;

import com.miui.videoplayer.common.DuoKanCodecConstants;

/**
 * @author tianli
 *
 */
public class SoundEffect {
	
	public static void turnOnMusicMode(){
		if (DuoKanCodecConstants.USE_DIRAC_SOUND) {
			try {
				Class<?> DiracSound = Class.forName("android.media.audiofx.DiracSound");
				Object diracSound = DiracSound.getDeclaredConstructor(new Class[] {int.class, int.class})
						.newInstance(new Object[] {Integer.valueOf(0), Integer.valueOf(0)});

				Method method = DiracSound.getDeclaredMethod("setMovie", new Class[] {int.class});
				method.invoke(diracSound, new Object[] {Integer.valueOf(0)});

				method = DiracSound.getDeclaredMethod("setMode", new Class[] {int.class});
				Field field = DiracSound.getDeclaredField("DIRACSOUND_MODE_MUSIC");
				int diracMode = field.getInt(diracSound);
				method.invoke(diracSound, new Object[] {Integer.valueOf(diracMode)});

				Log.i("EffectDiracSound", "diable sound effect,and set music mode ");
				method = DiracSound.getSuperclass().getDeclaredMethod("release", new Class[]{});
				method.invoke(diracSound, new Object[]{});		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void turnOnMovieMode(boolean isAudioEnhance){
		if (DuoKanCodecConstants.USE_DIRAC_SOUND) {
			try {
				Class<?> DiracSound = Class.forName("android.media.audiofx.DiracSound");
				Object diracSound = DiracSound.getDeclaredConstructor(new Class[] {int.class, int.class})
						.newInstance(new Object[] {Integer.valueOf(0), Integer.valueOf(0)});

				Method method = DiracSound.getDeclaredMethod("setMode", new Class[] {int.class});
				Field field = DiracSound.getDeclaredField("DIRACSOUND_MODE_MOVIE");
				int diracMode = field.getInt(diracSound);
				method.invoke(diracSound, new Object[] {Integer.valueOf(diracMode)});
				Log.i("EffectDiracSound", "set movie mode in videoplayer");
				if(isAudioEnhance){
					method = DiracSound.getDeclaredMethod("setMovie", new Class[] {int.class});
					method.invoke(diracSound, new Object[] {Integer.valueOf(1)});
					Log.i("EffectDiracSound", "enable sound effect in videoplayer");
				} else {
					method = DiracSound.getDeclaredMethod("setMovie", new Class[] {int.class});
					method.invoke(diracSound, new Object[] {Integer.valueOf(0)});
					Log.i("EffectDiracSound", "disable sound effect in videoplayer");
				}
				method = DiracSound.getSuperclass().getDeclaredMethod("release", new Class[]{});
				method.invoke(diracSound, new Object[]{});	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
