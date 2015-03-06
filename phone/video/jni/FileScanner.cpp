/*
 * NativeApi.cpp
 *
 *  Created on: 2013-1-18
 *      Author: niuyi
 */

#include <jni.h>
#include <iostream>
#include <stdio.h>
#include <sys/stat.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <android/log.h>
#include <errno.h>
#include <algorithm>

using namespace std;

#define TAG "FileScanner"

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)

static const char * kClassFileScanner = "com/miui/video/storage/DiskDevice$FileScanner";

static const char * kClassNativeFile = "com/miui/video/storage/NativeFile";

static const char * kClassMediaItem =
		"com/miui/video/storage/MediaItem";

static const char * kClassFileMediaItem =
		"com/miui/video/storage/FileMediaItem";

static const char * kClassDiskDevice =
		"com/miui/video/storage/DiskDevice";


static const char * kClassList = "java/util/ArrayList";
static const char * kClassObject = "java/lang/Object";

//#include "vector"
//#include "string"

extern "C"
{

JNIEXPORT jobject JNICALL
Java_com_miui_video_storage_DiskDevice_listFiles(
		JNIEnv* env, jclass clazz, jstring path)
{
	LOGV("%s enter", __FUNCTION__);
	const char* rootPath = (char*) env->GetStringUTFChars(path, NULL);
	if (rootPath == NULL)
	{
		LOGV("Out of memory");
		return NULL;
	}
	string relativePath("/");
	jclass nativeFileClass = env->FindClass(kClassNativeFile);
	jclass listClass = env->FindClass(kClassList);

	jfieldID nativeFilePath = env->GetFieldID(nativeFileClass, "path", "Ljava/lang/String;");
	jfieldID nativeFileName = env->GetFieldID(nativeFileClass, "name", "Ljava/lang/String;");
	jfieldID nativeFileIsDir = env->GetFieldID(nativeFileClass, "isDir", "Z");

	jmethodID listInit = env->GetMethodID(listClass,"<init>","()V");
    jmethodID listAdd = env->GetMethodID(listClass,"add","(Ljava/lang/Object;)Z");
	jobject list = env->NewObject(listClass,listInit);

	string fullDirPath = string(rootPath) + relativePath;
	DIR* dir = opendir(fullDirPath.c_str());
	if (!dir)
	{
		LOGV("Error opening directory '%s', skipping: %s.", fullDirPath.c_str(), strerror(errno));
		return JNI_FALSE;
	}
	struct dirent* entry;
	struct stat statbuf;
	while ((entry = readdir(dir)))
	{
		const char* name = entry->d_name;
		LOGV("process file name : %s", name);
		if (name[0] == '.')
		{
			//skip all file or dir start with "." , include . and ..
//			LOGV("skip hidden file %s", name);
			continue;
		}
		string fullPath = fullDirPath + name;
		unsigned int type = entry->d_type;
		if (type == DT_UNKNOWN)
		{
			// If the type is unknown, stat() the file instead.
			// This is sometimes necessary when accessing NFS mounted filesystems, but
			// could be needed in other cases well.
//			LOGV("DT_UNKNOWN: %s", fullPath.c_str());
			if (stat(fullPath.c_str(), &statbuf) >= 0)
			{
				if (S_ISREG(statbuf.st_mode))
				{
					type = DT_REG;
				}
				else if (S_ISDIR(statbuf.st_mode))
				{
					type = DT_DIR;
				}
			}
			else
			{
				LOGV("stat() failed for %s: %s", fullPath.c_str(), strerror(errno));
				continue;
			}
		}
		jstring strPath = env->NewStringUTF(fullPath.c_str());
		jstring strName = env->NewStringUTF(name);
		jobject nativeFile = env->AllocObject(nativeFileClass);
		env->SetObjectField(nativeFile, nativeFilePath, strPath);
		env->SetObjectField(nativeFile, nativeFileName, strName);
		env->SetBooleanField(nativeFile, nativeFileIsDir, type == DT_DIR ? JNI_TRUE: JNI_FALSE);
		env->CallBooleanMethod(list, listAdd, nativeFile);
		env->DeleteLocalRef(nativeFile);
		env->DeleteLocalRef(strPath);
		env->DeleteLocalRef(strName);
	}
	closedir(dir);
	env->ReleaseStringUTFChars(path, rootPath);
	LOGV("%s exit", __FUNCTION__);
	return list;
}

void Java_com_miui_video_storage_DiskDevice_listFilesEx(
		JNIEnv* env, jobject diskDevice, jstring path, jobject fileList, jobject dirList)
{
	LOGV("%s enter", __FUNCTION__);
	const char* rootPath = (char*) env->GetStringUTFChars(path, NULL);
	if (rootPath == NULL)
	{
		LOGV("Out of memory");
//		return NULL;
		return;
	}
	string relativePath("/");
	jclass nativefileClass = env->FindClass(kClassNativeFile);
	jclass listClass = env->FindClass(kClassList);

	jfieldID nativeFilePath = env->GetFieldID(nativefileClass, "path", "Ljava/lang/String;");
	jfieldID nativeFileName = env->GetFieldID(nativefileClass, "name", "Ljava/lang/String;");
	jfieldID nativeFileIsDir = env->GetFieldID(nativefileClass, "isDir", "Z");

//	jmethodID listInit = env->GetMethodID(listClass,"<init>","()V");
    jmethodID listAdd = env->GetMethodID(listClass,"add","(Ljava/lang/Object;)Z");
//	jobject list = env->NewObject(listClass,listInit);

	jclass fileMediaItemClass = env->FindClass(kClassFileMediaItem);
	jmethodID fileMediaItemInit = env->GetMethodID(fileMediaItemClass,"<init>",
			"(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)V");

	jclass diskDeviceClass = env->FindClass(kClassDiskDevice);
	jmethodID prepareItem = env->GetMethodID(diskDeviceClass,"prepareMediaItemFile",
			"(Ljava/lang/String;Ljava/lang/String;)Lcom/xiaomi/mitv/mediaexplorer/model/MediaItem;");

	string fullDirPath = string(rootPath) + relativePath;
	DIR* dir = opendir(fullDirPath.c_str());
	if (!dir)
	{
		LOGV("Error opening directory '%s', skipping: %s.", fullDirPath.c_str(), strerror(errno));
//		return JNI_FALSE;
		return;
	}
	struct dirent* entry;
	struct stat statbuf;
	while ((entry = readdir(dir)))
	{
		const char* name = entry->d_name;
		LOGV("process file name : %s", name);
		if (name[0] == '.')
		{
			//skip all file or dir start with "." , include . and ..
//			LOGV("skip hidden file %s", name);
			continue;
		}
		string fullPath = fullDirPath + name;
		unsigned int type = entry->d_type;
		if (type == DT_UNKNOWN)
		{
			// If the type is unknown, stat() the file instead.
			// This is sometimes necessary when accessing NFS mounted filesystems, but
			// could be needed in other cases well.
//			LOGV("DT_UNKNOWN: %s", fullPath.c_str());
			if (stat(fullPath.c_str(), &statbuf) >= 0)
			{
				if (S_ISREG(statbuf.st_mode))
				{
					type = DT_REG;
				}
				else if (S_ISDIR(statbuf.st_mode))
				{
					type = DT_DIR;
				}
			}
			else
			{
				LOGV("stat() failed for %s: %s", fullPath.c_str(), strerror(errno));
				continue;
			}
		}
		jstring strPath = env->NewStringUTF(fullPath.c_str());
//		jobject nativeFile = env->AllocObject(nativefileClass);
//		env->SetObjectField(nativeFile, nativeFilePath, strPath);
//		env->SetObjectField(nativeFile, nativeFileName, strName);
//		env->SetBooleanField(nativeFile, nativeFileIsDir, type == DT_DIR ? JNI_TRUE: JNI_FALSE);
		if(type == DT_DIR)
		{
			env->CallBooleanMethod(dirList, listAdd, strPath);
		}
		else
		{
			jstring strName = env->NewStringUTF(name);
//			jobject mediaItem = env->NewObject(fileMediaItemClass,fileMediaItemInit, 0, strPath, strName);
//			jobject mediaItem = env->AllocObject(fileMediaItemClass);
			jobject mediaItem = env->CallObjectMethod(diskDevice, prepareItem, strPath, strName);
			if(mediaItem != 0)
			{
				env->CallBooleanMethod(fileList, listAdd, mediaItem);
				env->DeleteLocalRef(mediaItem);
			}
			env->DeleteLocalRef(strName);
		}
//		env->CallBooleanMethod(list, listAdd, nativeFile);
//		env->DeleteLocalRef(nativeFile);
		env->DeleteLocalRef(strPath);
	}
	closedir(dir);
	env->ReleaseStringUTFChars(path, rootPath);
	LOGV("%s exit", __FUNCTION__);
//	return list;
}

JNIEXPORT jboolean JNICALL
Java_com_miui_video_storage_DiskDevice_nativeScanDir(
		JNIEnv* env, jclass clazz, jobject fileScanner, jstring path, jboolean fullScan)
{
	LOGV("%s enter", __FUNCTION__);
	const char* rootPath = (char*) env->GetStringUTFChars(path, NULL);
	if (rootPath == NULL)
	{
		LOGV("Out of memory");
		return JNI_FALSE;
	}
//	LOGV("root path %s", rootPath);
//	MyMediaScannerClient client(env, thiz);
//	string pathStr(rootPath);
	string relativePath("/");
	jclass fileScannerClass = env->FindClass(kClassFileScanner);

	jmethodID handleFile = env->GetMethodID(
			fileScannerClass, "handleFile", "(Ljava/lang/String;Z)Z");
	string fullDirPath = string(rootPath) + relativePath;
	DIR* dir = opendir(fullDirPath.c_str());
	if (!dir)
	{
		LOGV("Error opening directory '%s', skipping: %s.", fullDirPath.c_str(), strerror(errno));
		return JNI_FALSE;
	}
	jboolean result = JNI_FALSE;
	struct dirent* entry;
	struct stat statbuf;
	while ((entry = readdir(dir)))
	{
		const char* name = entry->d_name;
//		LOGV("process file name : %s", name);
		if (name[0] == '.')
		{
			//skip all file or dir start with "." , include . and ..
			LOGV("skip hidden file %s", name);
			continue;
		}
		string fullPath = fullDirPath + name;
		jstring strPath = env->NewStringUTF(fullPath.c_str());
		unsigned int type = entry->d_type;
		if (type == DT_UNKNOWN)
		{
			// If the type is unknown, stat() the file instead.
			// This is sometimes necessary when accessing NFS mounted filesystems, but
			// could be needed in other cases well.
//			LOGV("DT_UNKNOWN: %s", fullPath.c_str());
			if (stat(fullPath.c_str(), &statbuf) >= 0)
			{
				if (S_ISREG(statbuf.st_mode))
				{
					type = DT_REG;
				}
				else if (S_ISDIR(statbuf.st_mode))
				{
					type = DT_DIR;
				}
			}
			else
			{
				LOGV("stat() failed for %s: %s", fullPath.c_str(), strerror(errno));
				continue;
			}
		}
		jboolean handled = env->CallBooleanMethod(fileScanner, handleFile, strPath,
				type == DT_DIR ? JNI_TRUE: JNI_FALSE);
		env->DeleteLocalRef(strPath);
		if(handled)
		{
			result = JNI_TRUE;
			break;
		}

//		if (type == DT_DIR)
//		{
//
//			if (shouldIngoreDir(name))
//				continue;
//			// report the directory to the client
//			if (stat(fullPath.c_str(), &statbuf) >= 0)
//			{
//				//do not scan dir now
//				//client.scanFile(newPath.c_str(), MEDIA_TYPE_DIR, statbuf.st_mtime, 0, true /*isDirectory*/, false);
//				string subRelativeDirPath = newReltaviePath + "/";
//				//subRelativeDirPath: /mydir/subdir/
//				doProcessDir(rootPath, subRelativeDirPath, client, currentDepth);
//			}
//
//		}
//		else if (type == DT_REG)
//		{
//			if (stat(newFullPath.c_str(), &statbuf) >= 0)
//			{
//				string nameNoExt;
//				int type = getMediaType(name, nameNoExt);
//				if (type == MEDIA_TYPE_VIDEO || type == MEDIA_TYPE_AUDIO
//						|| type == MEDIA_TYPE_IMAGE)
//				{
//					if(!shouldIngoreFile(name, type))
//					{
//						client.scanFile(newReltaviePath.c_str(), nameNoExt.c_str(),
//								type, statbuf.st_mtime, statbuf.st_size,
//								false /*isDirectory*/, false);
//					}
//					else{
//						LOGV("ingore %s", name);
//					}
//				}
//			}
//
//		}
	}
	closedir(dir);
//	doProcessDir(pathStr, relativePath, client, MAX_DEPTH);
	env->ReleaseStringUTFChars(path, rootPath);
	LOGV("%s exit", __FUNCTION__);
	return result;
}


}
