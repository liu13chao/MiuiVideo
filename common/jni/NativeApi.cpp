#include <jni.h>
#include <android/log.h>
#include <string>
#include "JniUtil.h"
#include "Interface.h"
#include "pthread.h"

#ifdef _DEBUG

#define TAG "jni_api"

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)

#else

#define LOGV(...)

#endif

#define   FAILED_ERROR       0

static const char *KSigStr = "Ljava/lang/String;";
static const char *KSigChar = "C";
static const char *KSigInt = "I";
static const char *KSigLong = "J";
static const char *KSigFloat = "F";
static const char *KSigIntArray = "[I";
static const char *KSigStrArray = "[Ljava/lang/String;";

static const char *KSigTelevisionShow = "Lcom/duokan/tv/type/TelevisionShow;";
static const char *KSigCategoryInfo = "Lcom/duokan/tv/type/CategoryInfo;";
static const char *KSigMediaInfo = "Lcom/duokan/tv/type/MediaInfo;";
static const char *KSigPersonInfo = "Lcom/duokan/tv/type/PersonInfo;";
static const char *KSigAlbumInfo = "Lcom/duokan/tv/type/AlbumInfo;";
static const char *KSigTelevisionInfo = "Lcom/duokan/tv/type/TelevisionInfo;";
static const char *KSigIQIYIPlayUrlMediaSourceInfo = "Lcom/duokan/tv/type/IQIYIPlayUrlMediaSourceInfo;";
static const char *KSigSOHUPlayUrlMediaSourceInfo = "Lcom/duokan/tv/type/SOHUPlayUrlMediaSourceInfo;";

static const char *KSigChannelInfoArray = "[Lcom/duokan/tv/type/Channel;";
static const char *KSigRecommendationArray = "[Lcom/duokan/tv/type/Recommendation;";
static const char *KSigChannelRecommendationArray = "[Lcom/duokan/tv/type/ChannelRecommendation;";
static const char *KSigCategoryInfoArray = "[Lcom/duokan/tv/type/CategoryInfo;";
static const char *KSigMediaInfoArray = "[Lcom/duokan/tv/type/MediaInfo;";
static const char *KSigTelevisionShowDataArray = "[Lcom/duokan/tv/type/TelevisionShowData;";
static const char *KSigTelevisionShowDataListArray = "[Lcom/duokan/tv/type/TelevisionShowDataList;";
static const char *KSigTelevisionShowArray = "[Lcom/duokan/tv/type/TelevisionShow;";
static const char *KSigTelevisonInfoArray = "[Lcom/duokan/tv/type/TelevisionInfo;";
static const char *KSigPersonInfoArray = "[Lcom/duokan/tv/type/PersonInfo;";
static const char *KSigSpecialSubjectArray = "[Lcom/duokan/tv/type/SpecialSubject;";
static const char *KSigSpecialSubjectMediaArray = "[Lcom/duokan/tv/type/SpecialSubjectMedia;";
static const char *KSigMediaReviewArray = "[Lcom/duokan/tv/type/MediaReview;";
static const char *KSigVarietyArray = "[Lcom/duokan/tv/type/Variety;";
static const char *KSigRankInfoArray = "[Lcom/duokan/tv/type/RankInfo;";
static const char *KSigMediaUrlInfoArray = "[Lcom/duokan/tv/type/MediaUrlInfo;";
static const char *KSigTokenInfo = "Lcom/duokan/tv/type/TokenInfo;";
static const char *KSigPlayHistoryArray = "[Lcom/duokan/tv/type/PlayHistory;";
static const char *KSigBannerArray = "[Lcom/duokan/tv/type/Banner;";
static const char *KSigImageUrlInfo = "Lcom/duokan/tv/type/ImageUrlInfo;";
static const char *KSigMyFavoriteItemInfoArray = "[Lcom/duokan/tv/type/MyFavoriteItemInfo;";
static const char *KSigMediaSetInfoList = "Lcom/duokan/tv/type/MediaSetInfoList;";
static const char *KSigMediaSetInfoArray = "[Lcom/duokan/tv/type/MediaSetInfo;";
static const char *KSigMediaDetailInfo = "Lcom/duokan/tv/type/MediaDetailInfo;";
//static const char *KSigBookmarkArray = "[Lcom/duokan/tv/type/Bookmark;";
//static const char *KSigWeatherForcastArray = "[Lcom/duokan/tv/type/WeatherForcast;";
static const char *KClassString = "java/lang/String";
static const char *KClassChannelRecommendation = "com/duokan/tv/type/ChannelRecommendation";
static const char *KClassChannelRecommendationList = "com/duokan/tv/type/ChannelRecommendationList";
static const char *KClassRecommendation = "com/duokan/tv/type/Recommendation";
static const char *KClassRecommendationList = "com/duokan/tv/type/RecommendationList";
static const char *KClassTelevisionShowList = "com/duokan/tv/type/TelevisionShowList";
static const char *KClassTelevisionShowData = "com/duokan/tv/type/TelevisionShowData";
static const char *KClassTelevisionShowDataList = "com/duokan/tv/type/TelevisionShowDataList";
static const char *KClassTelevisionShow = "com/duokan/tv/type/TelevisionShow";
static const char *KClassCategoryInfo = "com/duokan/tv/type/CategoryInfo";
static const char *KClassMediaInfo = "com/duokan/tv/type/MediaInfo";
static const char *KClassTelevisionInfo = "com/duokan/tv/type/TelevisionInfo";
static const char *KClassTelevisionInfoList = "com/duokan/tv/type/TelevisionInfoList";
static const char *KClassMediaDetailInfo = "com/duokan/tv/type/MediaDetailInfo";
static const char *KClassMediaDetailInfo2 = "com/duokan/tv/type/MediaDetailInfo2";
static const char *KClassMediaSetInfo = "com/duokan/tv/type/MediaSetInfo";
static const char *KClassMediaSetInfoList = "com/duokan/tv/type/MediaSetInfoList";
static const char *KClassPersonInfo = "com/duokan/tv/type/PersonInfo";
static const char *KClassMediaInfoQuery = "com/duokan/tv/type/MediaInfoQuery";
static const char *KClassSearchMediaInfoList = "com/duokan/tv/type/SearchMediaInfoList";
static const char *KClassMediaInfoList = "com/duokan/tv/type/MediaInfoList";
static const char *KClassInitConnection = "com/duokan/tv/type/InitConnection";
static const char *KClassSpecialSubject = "com/duokan/tv/type/SpecialSubject";
static const char *KClassSpecialSubjectList = "com/duokan/tv/type/SpecialSubjectList";
static const char *KClassSpecialSubjectMedia = "com/duokan/tv/type/SpecialSubjectMedia";
static const char *KClassSpecialSubjectMediaList = "com/duokan/tv/type/SpecialSubjectMediaList";
static const char *KClassMediaReview = "com/duokan/tv/type/MediaReview";
static const char *KClassMediaReviewList = "com/duokan/tv/type/MediaReviewList";
static const char *KClassVarietyQuery = "com/duokan/tv/type/VarietyQuery";
static const char *KClassVarietyList = "com/duokan/tv/type/VarietyList";
static const char *KClassVariety = "com/duokan/tv/type/Variety";
static const char *KClassRankInfoList = "com/duokan/tv/type/RankInfoList";
static const char *KClassRankInfo = "com/duokan/tv/type/RankInfo";
static const char *KClassMediaUrlInfoList = "com/duokan/tv/type/MediaUrlInfoList";
static const char *KClassMediaUrlInfo = "com/duokan/tv/type/MediaUrlInfo";
static const char *KClassSearchInfo = "com/duokan/tv/type/SearchInfo";
static const char *KClassTokenInfo = "com/duokan/tv/type/TokenInfo";
static const char *KClassPlayHistoryList = "com/duokan/tv/type/PlayHistoryList";
static const char *KClassPlayHistory = "com/duokan/tv/type/PlayHistory";
static const char *KClassUserDataParam = "com/duokan/tv/type/UserDataParam";
static const char *KClassBanner = "com/duokan/tv/type/Banner";
static const char *KClassBannerList = "com/duokan/tv/type/BannerList";
static const char *KClassAlbumInfo = "com/duokan/tv/type/AlbumInfo";
static const char *KClassMyFavoriteItemInfo = "com/duokan/tv/type/MyFavoriteItemInfo";
static const char *KClassMyFavoriteItemInfoList = "com/duokan/tv/type/MyFavoriteItemInfoList";
//static const char *KClassBookmarkList = "com/duokan/tv/type/BookmarkList";
static const char *KClassChannel = "com/duokan/tv/type/Channel";
static const char *KClassChannelList = "com/duokan/tv/type/ChannelList";
static const char *KClassPlayUrlMediaSourceInfo = "com/duokan/tv/type/PlayUrlMediaSourceInfo";
static const char *KClassImageUrlInfo = "com/duokan/tv/type/ImageUrlInfo";
//static const char *KClassWeatherForcast = "com/duokan/tv/type/WeatherForcast";
//static const char *KClassWeatherForcastList = "com/duokan/tv/type/WeatherForcastList";

//static const char *KClassVarietyDetail = "com/duokan/tv/type/VarietyDetail";
//static const char *KClassVarietyInfo = "com/duokan/tv/type/VarietyInfo";
//static const char *KClassTraditional = "com/duokan/tv/type/Traditional";
//static const char *KClassMediaByCelebrityGetter = "com/duokan/tv/type/MediaByCelebrityGetter";

extern "C" {

JNIEXPORT void JNICALL Java_com_duokan_tv_api_DKJniClient_nativeInitInterface(JNIEnv * aEnv, jobject aObj, jobject aConnection){
	LOGV("%s enter ", __FUNCTION__);
	jclass initConnectionClass = JniUtil::findClass(aEnv, KClassInitConnection);
	jclass tokenInfoClass = JniUtil::findClass(aEnv, KClassTokenInfo);
	jfieldID szClientVer = aEnv->GetFieldID(initConnectionClass, "clientVer", KSigStr);
	jfieldID szDeviceID = aEnv->GetFieldID(initConnectionClass, "deviceID", KSigStr);
	jfieldID szM3U8Path = aEnv->GetFieldID(initConnectionClass, "m3U8Path", KSigStr);
	jfieldID szPythonPath = aEnv->GetFieldID(initConnectionClass, "pythonPath", KSigStr);
	jfieldID szCookieFile = aEnv->GetFieldID(initConnectionClass, "cookieFile", KSigStr);
	jfieldID szServerURL = aEnv->GetFieldID(initConnectionClass, "serverURL", KSigStr);
	jfieldID szLogServerURL = aEnv->GetFieldID(initConnectionClass, "logServerURL", KSigStr);
	jfieldID nPlatform = aEnv->GetFieldID(initConnectionClass, "platform", KSigInt);
	jfieldID uVideoCapabilities = aEnv->GetFieldID(initConnectionClass, "videoCapability", KSigInt);
	jfieldID uAudioCapabilities = aEnv->GetFieldID(initConnectionClass, "audioCapability", KSigInt);
	jfieldID uImageCapabilities = aEnv->GetFieldID(initConnectionClass, "imageCapability", KSigInt);
	jfieldID maxMediaDescLength = aEnv->GetFieldID(initConnectionClass, "maxMediaDescLength", KSigInt);
	jfieldID modelInfo = aEnv->GetFieldID(initConnectionClass, "modeInfo", KSigStr);

	jfieldID userID = aEnv->GetFieldID(initConnectionClass, "userID", KSigStr);
	jfieldID xiaomiAuthToken = aEnv->GetFieldID(initConnectionClass, "authToken", KSigStr);
	jfieldID ssec = aEnv->GetFieldID(initConnectionClass, "ssec", KSigStr);
	jfieldID proxy = aEnv->GetFieldID(initConnectionClass, "proxy", KSigStr);

	jfieldID tokenInfo = aEnv->GetFieldID(initConnectionClass, "tokenInfo", KSigTokenInfo);
	jfieldID authKey = aEnv->GetFieldID(tokenInfoClass, "key", KSigStr);
	jfieldID authToken = aEnv->GetFieldID(tokenInfoClass, "token", KSigStr);
	jfieldID userAgent = aEnv->GetFieldID(initConnectionClass, "userAgent", KSigStr);

	INITCONNECTION ConnectionInfo;
	memset(&ConnectionInfo, 0, sizeof(ConnectionInfo));
	ConnectionInfo.nPlatfrom = aEnv->GetIntField(aConnection, nPlatform);
	jstring JStr = (jstring)aEnv->GetObjectField(aConnection, szClientVer);
	const char *Cstr = aEnv->GetStringUTFChars(JStr, 0);
	strcpy(ConnectionInfo.szClientVer, Cstr);
	aEnv->ReleaseStringUTFChars(JStr, Cstr);

	JStr = (jstring)aEnv->GetObjectField(aConnection, szDeviceID);
	Cstr = aEnv->GetStringUTFChars(JStr, 0);
	strcpy(ConnectionInfo.szDeviceID, Cstr);

	aEnv->ReleaseStringUTFChars(JStr, Cstr);
	JStr = (jstring)aEnv->GetObjectField(aConnection, szM3U8Path);
	if(JStr != NULL){
		Cstr = aEnv->GetStringUTFChars(JStr, 0);
		strcpy(ConnectionInfo.szM3U8Path, Cstr);
		aEnv->ReleaseStringUTFChars(JStr, Cstr);
	}
	
	JStr = (jstring)aEnv->GetObjectField(aConnection, szCookieFile);
	if(JStr != NULL){
		Cstr = aEnv->GetStringUTFChars(JStr, 0);
		strcpy(ConnectionInfo.szCookieFile, Cstr);
		aEnv->ReleaseStringUTFChars(JStr, Cstr);
	}
	
	LOGV("cookie file : %s ", ConnectionInfo.szCookieFile);
	
	JStr = (jstring)aEnv->GetObjectField(aConnection, szServerURL);
	Cstr = aEnv->GetStringUTFChars(JStr, 0);
	strcpy(ConnectionInfo.szTVServiceURL, Cstr);
	aEnv->ReleaseStringUTFChars(JStr, Cstr);

	JStr = (jstring)aEnv->GetObjectField(aConnection, szLogServerURL);
	Cstr = aEnv->GetStringUTFChars(JStr, 0);
	strcpy(ConnectionInfo.szUploadLogURL, Cstr);
	aEnv->ReleaseStringUTFChars(JStr, Cstr);

	ConnectionInfo.nMaxMediaDescribeLength = (int)aEnv->GetIntField(aConnection, maxMediaDescLength);
	JStr = (jstring)aEnv->GetObjectField(aConnection, userID);
	if( JStr != NULL)
	{
		Cstr = aEnv->GetStringUTFChars(JStr, 0);
		strcpy(ConnectionInfo.szUserID, Cstr);
		aEnv->ReleaseStringUTFChars(JStr, Cstr);
		//	LOGV("account user id : %s", ConnectionInfo.szUserID);
	}
	JStr = (jstring)aEnv->GetObjectField(aConnection, modelInfo);
	if( JStr != NULL)
	{
		Cstr = aEnv->GetStringUTFChars(JStr, 0);
		strcpy(ConnectionInfo.szModelInfo, Cstr);
		aEnv->ReleaseStringUTFChars(JStr, Cstr);
	}
	// auth token
	jobject jtokenInfo = aEnv->GetObjectField(aConnection, tokenInfo);
	if(jtokenInfo != NULL) {
		JStr = (jstring)aEnv->GetObjectField(jtokenInfo, authKey);
		if(JStr != NULL){
			Cstr = aEnv->GetStringUTFChars(JStr, 0);
			strcpy(ConnectionInfo.tokenInfo.szKey, Cstr);
			LOGV("key = %s ", ConnectionInfo.tokenInfo.szKey);
			aEnv->ReleaseStringUTFChars(JStr, Cstr);
		}
		JStr = (jstring)aEnv->GetObjectField(jtokenInfo, authToken);
		if(JStr != NULL){
			Cstr = aEnv->GetStringUTFChars(JStr, 0);
			strcpy(ConnectionInfo.tokenInfo.szToken, Cstr);
			LOGV("token = %s ", ConnectionInfo.tokenInfo.szToken);
			aEnv->ReleaseStringUTFChars(JStr, Cstr);
		}
	}
	JStr = (jstring)aEnv->GetObjectField(aConnection, xiaomiAuthToken);
	if( JStr != NULL)
	{
		Cstr = aEnv->GetStringUTFChars(JStr, 0);
		strcpy(ConnectionInfo.tokenInfo.szToken, Cstr);
		aEnv->ReleaseStringUTFChars(JStr, Cstr);
	}
	JStr = (jstring)aEnv->GetObjectField(aConnection, ssec);
	if( JStr != NULL)
	{
		Cstr = aEnv->GetStringUTFChars(JStr, 0);
		strcpy(ConnectionInfo.tokenInfo.szKey, Cstr);
		aEnv->ReleaseStringUTFChars(JStr, Cstr);
	}
	JStr = (jstring)aEnv->GetObjectField(aConnection, proxy);
	if( JStr != NULL)
	{
		Cstr = aEnv->GetStringUTFChars(JStr, 0);
		strcpy(ConnectionInfo.szProxyAddress, Cstr);
		aEnv->ReleaseStringUTFChars(JStr, Cstr);
	}
	JStr = (jstring)aEnv->GetObjectField(aConnection, userAgent);
	if( JStr != NULL)
	{
		Cstr = aEnv->GetStringUTFChars(JStr, 0);
		strcpy(ConnectionInfo.szUserAgent, Cstr);
		aEnv->ReleaseStringUTFChars(JStr, Cstr);
	}
	LOGV("user agent : %s ", ConnectionInfo.szUserAgent);
	DK_InitInterface(&ConnectionInfo);
	LOGV("%s exit ", __FUNCTION__);
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_nativeUserLogin(JNIEnv *, jobject) {
	LOGV("%s enter ", __FUNCTION__);
	//	int res = DK_UserLogin(NULL);
	int res = DK_UserLogin(0);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

//#include "Mutex.h"
//using namespace util;
//Mutex mutex;
//
//JNIEXPORT void JNICALL Java_com_duokan_tv_api_DKJniClient_testMutex(JNIEnv *, jobject) {
//}


JNIEXPORT void JNICALL Java_com_duokan_tv_api_DKJniClient_nativeUserLogout (JNIEnv *, jobject) {
	DK_UserLogOut();
}

jobject buildJChannel(JNIEnv *aEnv, struct tagCHANNELINFO* aChannelInfo){
	//	LOGV("%s enter ", __FUNCTION__);
	jclass channelInfoClass = JniUtil::findClass(aEnv, KClassChannel);
	jfieldID channelNameField = JniUtil::getFieldID(aEnv, KClassChannel, "channelName", KSigStr);
	jfieldID channelIdField = JniUtil::getFieldID(aEnv, KClassChannel, "channelID", KSigInt);
	jfieldID channelTypeField = JniUtil::getFieldID(aEnv, KClassChannel, "channelType", KSigInt);
	jfieldID subChannelsField = JniUtil::getFieldID(aEnv, KClassChannel, "subChannels", KSigChannelInfoArray);
	jobject channelInfoObj = aEnv->AllocObject(channelInfoClass);
	jstring jStr = aEnv->NewStringUTF(aChannelInfo->channelname);
	aEnv->SetObjectField(channelInfoObj, channelNameField, jStr);
	aEnv->DeleteLocalRef(jStr);
	aEnv->SetIntField(channelInfoObj, channelIdField, aChannelInfo->nChannelId);
	aEnv->SetIntField(channelInfoObj, channelTypeField, aChannelInfo->channeltype);
	if(aChannelInfo->SubChannelInfoList.nDataCount != 0){
		int subItemCnt =aChannelInfo->SubChannelInfoList.nDataCount;
		jobjectArray subChannelArray = aEnv->NewObjectArray(subItemCnt, JniUtil::findClass(aEnv, KClassChannel), 0);
		for(int i = 0; i < subItemCnt; ++ i){
			jobject subChannelInfoObj = buildJChannel(aEnv, &aChannelInfo->SubChannelInfoList.lpChannelInfo[i]);
			aEnv->SetObjectArrayElement(subChannelArray, i, subChannelInfoObj);
			aEnv->DeleteLocalRef(subChannelInfoObj);
		}
		aEnv->SetObjectField(channelInfoObj, subChannelsField, subChannelArray);
		aEnv->DeleteLocalRef(subChannelArray);
	}
	else{
		aEnv->SetObjectField(channelInfoObj, subChannelsField, NULL);
	}
	//	LOGV("%s exit", __FUNCTION__);
	return channelInfoObj;
}


JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getChannelList(JNIEnv *aEnv, jobject aObj, jint aChannelID, jobject aChannelList){
	LOGV("%s enter", __FUNCTION__);
	CHANNELINFOLIST *channelInfoList = (CHANNELINFOLIST*)malloc(sizeof(*channelInfoList));
	memset(channelInfoList, 0, sizeof(*channelInfoList));
	int res = DK_GetChannelInfoList(aChannelID, channelInfoList);
	if (res >= EMPTY_RESULT){
		jsize  itemCnt = 0;
		jclass channelClass = JniUtil::findClass(aEnv, KClassChannel);
		itemCnt = channelInfoList->nDataCount;
		jobjectArray channelInfoArray = aEnv->NewObjectArray(itemCnt, channelClass, 0);
		for(int i = 0; i < itemCnt; ++ i){
			channelInfoList->lpChannelInfo[i];
			jobject channelInfo = buildJChannel(aEnv, &channelInfoList->lpChannelInfo[i]);
			aEnv->SetObjectArrayElement(channelInfoArray, i,channelInfo);
		}

		jfieldID jchannelInfoListID = JniUtil::getFieldID(aEnv, KClassChannelList, "channels", KSigChannelInfoArray);
		aEnv->SetObjectField(aChannelList, jchannelInfoListID, channelInfoArray);
	}
	DK_DestroyChannelInfoList(channelInfoList);
	free(channelInfoList);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

jobject buildJPersonInfo(JNIEnv *aEnv, LPPERSONINFOR aCPersonInfo){
	//	LOGV("%s enter", __FUNCTION__);
	if(aCPersonInfo == NULL) {
		LOGV("%s : personInfo is null.", __FUNCTION__);
		return NULL;
	}
	jclass personInfoClass = aEnv->FindClass(KClassPersonInfo);
	jclass imageUrlInfoClass = aEnv->FindClass(KClassImageUrlInfo);
	jfieldID country = JniUtil::getFieldID(aEnv, KClassPersonInfo, "country", KSigStr);
	jfieldID nameCn = JniUtil::getFieldID(aEnv, KClassPersonInfo, "nameCn", KSigStr);
	jfieldID nameEn = JniUtil::getFieldID(aEnv, KClassPersonInfo, "nameEn", KSigStr);
	jfieldID alias = JniUtil::getFieldID(aEnv, KClassPersonInfo, "alias", KSigStr);
	jfieldID bigImageUrl = JniUtil::getFieldID(aEnv, KClassPersonInfo, "bigImageUrl", KSigImageUrlInfo);
	jfieldID cv = JniUtil::getFieldID(aEnv, KClassPersonInfo, "cv", KSigStr);
	jfieldID md5 = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "md5", KSigStr);
	jfieldID imageUrl = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "imageUrl", KSigStr);

	jobject personInfoObj = aEnv->AllocObject(personInfoClass);
	jstring jStr;
	jStr = aEnv->NewStringUTF(aCPersonInfo->szCountry);
	aEnv->SetObjectField(personInfoObj, country, jStr);
	aEnv->DeleteLocalRef(jStr);
	jStr = aEnv->NewStringUTF(aCPersonInfo->szcName);
	aEnv->SetObjectField(personInfoObj, nameCn, jStr);
	aEnv->DeleteLocalRef(jStr);
	jStr = aEnv->NewStringUTF(aCPersonInfo->szeName);
	aEnv->SetObjectField(personInfoObj, nameEn, jStr);
	aEnv->DeleteLocalRef(jStr);
	jStr = aEnv->NewStringUTF(aCPersonInfo->szAlias);
	aEnv->SetObjectField(personInfoObj, alias, jStr);
	aEnv->DeleteLocalRef(jStr);
	jobject imageUrlInfoObj = aEnv->AllocObject(imageUrlInfoClass);
	jStr = aEnv->NewStringUTF(aCPersonInfo->poster.szURL);
	aEnv->SetObjectField(imageUrlInfoObj, imageUrl, jStr);
	aEnv->DeleteLocalRef(jStr);
	jStr = aEnv->NewStringUTF(aCPersonInfo->poster.szMd5);
	aEnv->SetObjectField(imageUrlInfoObj, md5, jStr);
	aEnv->DeleteLocalRef(jStr);
	aEnv->SetObjectField(personInfoObj, bigImageUrl, imageUrlInfoObj);
	jStr = aEnv->NewStringUTF(aCPersonInfo->szCv);
	aEnv->SetObjectField(personInfoObj, cv, jStr);
	aEnv->DeleteLocalRef(jStr);
	//	LOGV("%s exit", __FUNCTION__);
	return personInfoObj;
}


jobject buildJCategoryInfo(JNIEnv *aEnv, LPCATEGORYINFO aCCagegoryInfo)
{
	//	LOGV("%s enter", __FUNCTION__);
	if(aCCagegoryInfo == NULL)
	{
		LOGV("%s : categoryInfo is null.", __FUNCTION__);
		return NULL;
	}
	jclass categoryInfoClass = aEnv->FindClass(KClassCategoryInfo);
	jfieldID categoryName = JniUtil::getFieldID(aEnv, KClassCategoryInfo, "categoryName", KSigStr);
	jfieldID mediaCount = JniUtil::getFieldID(aEnv, KClassCategoryInfo, "mediaCount", KSigInt);
	jfieldID searchMask = JniUtil::getFieldID(aEnv, KClassCategoryInfo, "searchMask", KSigInt);

	jobject categoryInfoObj = aEnv->AllocObject(categoryInfoClass);
	jstring str = aEnv->NewStringUTF(aCCagegoryInfo->szCategory);
	aEnv->SetObjectField(categoryInfoObj, categoryName, str);
	aEnv->DeleteLocalRef(str);
	aEnv->SetIntField(categoryInfoObj, mediaCount, aCCagegoryInfo->nMediaCount);
	aEnv->SetIntField(categoryInfoObj, searchMask, aCCagegoryInfo->nSearchMask);
	//	LOGV("%s exit", __FUNCTION__);
	return categoryInfoObj;
}

jobject buildJMediaInfo(JNIEnv *aEnv, LPMEDIAINFO aCMediaInfo) {
	//	LOGV("%s enter", __FUNCTION__);
	if(aCMediaInfo == NULL) {
		LOGV("%s : mediaInfo is null.", __FUNCTION__);
		return NULL;
	}
	
	jclass mediaInfoClass = aEnv->FindClass(KClassMediaInfo);
	jclass imageUrlInfoClass = aEnv->FindClass(KClassImageUrlInfo);
	
	jfieldID mediaID = JniUtil::getFieldID(aEnv, KClassMediaInfo, "mediaID", KSigInt);
	jfieldID flag = JniUtil::getFieldID(aEnv, KClassMediaInfo, "flag", KSigInt);
	jfieldID resolution = JniUtil::getFieldID(aEnv, KClassMediaInfo, "resolution", KSigInt);
	jfieldID category = JniUtil::getFieldID(aEnv, KClassMediaInfo, "category", KSigStr);
	jfieldID mediaName = JniUtil::getFieldID(aEnv, KClassMediaInfo, "mediaName", KSigStr);
	jfieldID director = JniUtil::getFieldID(aEnv, KClassMediaInfo, "director", KSigStr);
	jfieldID actors = JniUtil::getFieldID(aEnv, KClassMediaInfo, "actors", KSigStr);
	jfieldID smallImageURL = JniUtil::getFieldID(aEnv, KClassMediaInfo, "smallImageURL", KSigImageUrlInfo);
	jfieldID area = JniUtil::getFieldID(aEnv, KClassMediaInfo, "area", KSigStr);
	jfieldID playLength = JniUtil::getFieldID(aEnv, KClassMediaInfo, "playLength", KSigInt);
	jfieldID setNow = JniUtil::getFieldID(aEnv, KClassMediaInfo, "setNow", KSigInt);
	jfieldID score = JniUtil::getFieldID(aEnv, KClassMediaInfo, "score", KSigFloat);
	jfieldID tags = JniUtil::getFieldID(aEnv, KClassMediaInfo, "tags", KSigStr);
	jfieldID issueDate = JniUtil::getFieldID(aEnv, KClassMediaInfo, "issueDate", KSigStr);
	jfieldID lastIssueDate = JniUtil::getFieldID(aEnv, KClassMediaInfo, "lastIssueDate", KSigStr);
	jfieldID setCount = JniUtil::getFieldID(aEnv, KClassMediaInfo, "setCount", KSigInt);
	jfieldID isMultipleSet = JniUtil::getFieldID(aEnv, KClassMediaInfo, "mediaSetType", KSigInt);
	jfieldID md5 = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "md5", KSigStr);
	jfieldID imageUrl = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "imageUrl", KSigStr);

	jobject mediaInfoObj = aEnv->AllocObject(mediaInfoClass);
	
	aEnv->SetIntField(mediaInfoObj, mediaID, aCMediaInfo->nMediaID);
//	LOGV("%s  %d", aCMediaInfo->szMediaName, aCMediaInfo->nMediaID);
	aEnv->SetIntField(mediaInfoObj, flag, aCMediaInfo->nFlag);
	aEnv->SetIntField(mediaInfoObj, resolution, aCMediaInfo->nResolution);
	aEnv->SetIntField(mediaInfoObj, playLength, aCMediaInfo->playlength);
	aEnv->SetIntField(mediaInfoObj, setNow, aCMediaInfo->nSetNow);
	aEnv->SetIntField(mediaInfoObj, setCount, aCMediaInfo->nSetCount);
	aEnv->SetFloatField(mediaInfoObj, score, aCMediaInfo->score);
	aEnv->SetIntField(mediaInfoObj, isMultipleSet, aCMediaInfo->nIsMultipleSet);
	jstring str = aEnv->NewStringUTF(aCMediaInfo->szArea);
	aEnv->SetObjectField(mediaInfoObj, area, str);
	aEnv->DeleteLocalRef(str);
	str = aEnv->NewStringUTF(aCMediaInfo->szCategory);
	aEnv->SetObjectField(mediaInfoObj, category, str);
	aEnv->DeleteLocalRef(str);
	str = aEnv->NewStringUTF(aCMediaInfo->szMediaName);
	aEnv->SetObjectField(mediaInfoObj, mediaName, str);
	aEnv->DeleteLocalRef(str);
	str = aEnv->NewStringUTF(aCMediaInfo->szDirector);
	aEnv->SetObjectField(mediaInfoObj, director, str);
	aEnv->DeleteLocalRef(str);
	str = aEnv->NewStringUTF(aCMediaInfo->szActors);
	aEnv->SetObjectField(mediaInfoObj, actors, str);
	aEnv->DeleteLocalRef(str);
	jobject imageUrlInfoObj = aEnv->AllocObject(imageUrlInfoClass);
	str = aEnv->NewStringUTF(aCMediaInfo->poster.szURL);
	aEnv->SetObjectField(imageUrlInfoObj, imageUrl, str);
	aEnv->DeleteLocalRef(str);
//	LOGV("poster url : %s", aCMediaInfo->poster.szURL);
	str = aEnv->NewStringUTF(aCMediaInfo->poster.szMd5);
	aEnv->SetObjectField(imageUrlInfoObj, md5, str);
	aEnv->DeleteLocalRef(str);
//	LOGV("poster url md5 : %s", aCMediaInfo->poster.szMd5);
	aEnv->SetObjectField(mediaInfoObj, smallImageURL, imageUrlInfoObj);
	aEnv->DeleteLocalRef(imageUrlInfoObj);
	str = aEnv->NewStringUTF(aCMediaInfo->szTags);
	aEnv->SetObjectField(mediaInfoObj, tags, str);
	aEnv->DeleteLocalRef(str);
//	LOGV("test %s", aCMediaInfo->szLatestissuedate);
	str = aEnv->NewStringUTF(aCMediaInfo->szLatestissuedate);
	aEnv->SetObjectField(mediaInfoObj, lastIssueDate, str);
	aEnv->DeleteLocalRef(str);
	str = aEnv->NewStringUTF(aCMediaInfo->szIssuedate);
	aEnv->SetObjectField(mediaInfoObj, issueDate, str);
	aEnv->DeleteLocalRef(str);
  //LOGV("%s exit", __FUNCTION__);
	return mediaInfoObj;
}

jobject buildJTelevisionShow(JNIEnv *aEnv, LPTVPROGRAMME aCTelevisionShow) {
//	LOGV("%s enter", __FUNCTION__);
	if(aCTelevisionShow == NULL) {
		LOGV("%s : television show is null.", __FUNCTION__);
		return NULL;
	}
	
	jclass televisionShowClass = JniUtil::findClass(aEnv, KClassTelevisionShow);
	jfieldID startTime = JniUtil::getFieldID(aEnv, KClassTelevisionShow, "startTime", KSigInt);
	jfieldID stopTime = JniUtil::getFieldID(aEnv, KClassTelevisionShow, "stopTime", KSigInt);
	jfieldID showName = JniUtil::getFieldID(aEnv, KClassTelevisionShow, "showName", KSigStr);
	
	jobject televisionShowObj = aEnv->AllocObject(televisionShowClass);
	jstring str = aEnv->NewStringUTF(aCTelevisionShow->szVideName);
	aEnv->SetObjectField(televisionShowObj, showName, str);
	aEnv->DeleteLocalRef(str);
	aEnv->SetIntField(televisionShowObj, startTime, aCTelevisionShow->nStartTime);
	aEnv->SetIntField(televisionShowObj, stopTime, aCTelevisionShow->nStopTime);
//	LOGV("%s exit", __FUNCTION__);
	return televisionShowObj;
}

jobject buildJTelevisionShowData(JNIEnv *aEnv, LPDATETVPROGRAMME aCTelevisionShowData) {
//	LOGV("%s enter", __FUNCTION__);
	if(aCTelevisionShowData == NULL) {
		LOGV("%s : television show data is null.", __FUNCTION__);
		return NULL;
	}
	
	jclass televisionShowDataClass = JniUtil::findClass(aEnv, KClassTelevisionShowData);
	jclass televisionShowClass = JniUtil::findClass(aEnv, KClassTelevisionShow);
	
	jfieldID data = JniUtil::getFieldID(aEnv, KClassTelevisionShowData, "data", KSigStr);
	jfieldID dataShowCount = JniUtil::getFieldID(aEnv, KClassTelevisionShowData, "dataShowCount", KSigInt);
	jfieldID televisionShows = JniUtil::getFieldID(aEnv, KClassTelevisionShowData, "televisionShows", KSigTelevisionShowArray);
	
	jobject televisionShowDataObj = aEnv->AllocObject(televisionShowDataClass);
	aEnv->SetIntField(televisionShowDataObj, dataShowCount, aCTelevisionShowData->nDataCount);
	jstring str = aEnv->NewStringUTF(aCTelevisionShowData->szDate);
	aEnv->SetObjectField(televisionShowDataObj, data, str);
	aEnv->DeleteLocalRef(str);
	
	jsize dataShowSize = aCTelevisionShowData->nDataCount;
	jobjectArray televisionShowArray = aEnv->NewObjectArray(dataShowSize, televisionShowClass, 0);
//	LOGV("%s  dataShowSize : %d", __FUNCTION__, dataShowSize);
	for(int index = 0; index < dataShowSize; index++){
		jobject televisionShowObj = buildJTelevisionShow(aEnv, &aCTelevisionShowData->lpProgramme[index]);
		aEnv->SetObjectArrayElement(televisionShowArray, index, televisionShowObj);
		aEnv->DeleteLocalRef(televisionShowObj);
	}
	aEnv->SetObjectField(televisionShowDataObj, televisionShows, televisionShowArray);
	aEnv->DeleteLocalRef(televisionShowArray);
//	LOGV("%s exit", __FUNCTION__);
	return televisionShowDataObj;
}

jobject buildJTelevisionShowDataList(JNIEnv *aEnv, LPTVPROGRAMMEDATE aCTelevisionShowDataList) {
//	LOGV("%s enter", __FUNCTION__);
	if(aCTelevisionShowDataList == NULL) {
		LOGV("%s : television show data is null.", __FUNCTION__);
		return NULL;
	}
	
	jclass televisionShowDataListClass = JniUtil::findClass(aEnv, KClassTelevisionShowDataList);
	jclass televisionShowDataClass = JniUtil::findClass(aEnv, KClassTelevisionShowData);
	
	jfieldID channelId = JniUtil::getFieldID(aEnv, KClassTelevisionShowDataList, "channelId", KSigInt);
	jfieldID dataCount = JniUtil::getFieldID(aEnv, KClassTelevisionShowDataList, "dataCount", KSigInt);
	jfieldID televisionShowDatas = JniUtil::getFieldID(aEnv, KClassTelevisionShowDataList, "televisionShowDatas", KSigTelevisionShowDataArray);
	
	jobject televisionShowDataListObj = aEnv->AllocObject(televisionShowDataListClass);
	
	aEnv->SetIntField(televisionShowDataListObj, channelId, aCTelevisionShowDataList->nChannelId);
	aEnv->SetIntField(televisionShowDataListObj, dataCount, aCTelevisionShowDataList->nDataCount);
	
	jsize showDatasSize = aCTelevisionShowDataList->nDataCount;
	jobjectArray televisionShowDataArray = aEnv->NewObjectArray(showDatasSize, televisionShowDataClass, 0);
	for(int index = 0; index < showDatasSize; index++){
		jobject televisionShowDataObj = buildJTelevisionShowData(aEnv, &aCTelevisionShowDataList->lpDateProgramme[index]);
		aEnv->SetObjectArrayElement(televisionShowDataArray, index, televisionShowDataObj);
		aEnv->DeleteLocalRef(televisionShowDataObj);
	}
	aEnv->SetObjectField(televisionShowDataListObj, televisionShowDatas, televisionShowDataArray);
	aEnv->DeleteLocalRef(televisionShowDataArray);
//	LOGV("%s eixt", __FUNCTION__);
	return televisionShowDataListObj;
}

jobject buildJTelevisionInfo(JNIEnv *aEnv, LPTELEVISION  aCTVInfo) {
//	LOGV("%s enter", __FUNCTION__);
	if(aCTVInfo == NULL) {
		LOGV("%s : tvInfo is null.", __FUNCTION__);
		return NULL;
	}
	
	jclass tvInfoClass = JniUtil::findClass(aEnv, KClassTelevisionInfo);
	jclass imageUrlInfoClass = JniUtil::findClass(aEnv, KClassImageUrlInfo);
	jfieldID tvID = JniUtil::getFieldID(aEnv, KClassTelevisionInfo, "televisionId", KSigInt);
	jfieldID tvBgColor = JniUtil::getFieldID(aEnv, KClassTelevisionInfo, "televisionBgColor", KSigInt);
	jfieldID tvName = JniUtil::getFieldID(aEnv, KClassTelevisionInfo, "televisionName", KSigStr);
	jfieldID tvImageUrlInfo = JniUtil::getFieldID(aEnv, KClassTelevisionInfo, "smallImageURL", KSigImageUrlInfo);
	jfieldID md5 = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "md5", KSigStr);
	jfieldID imageUrl = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "imageUrl", KSigStr);
	jfieldID playId = JniUtil::getFieldID(aEnv, KClassTelevisionInfo, "televisionPlayId", KSigStr);

	jmethodID televisionInfoID = aEnv->GetMethodID(tvInfoClass, "<init>", "()V");
	jobject tvInfoObj = aEnv->NewObject(tvInfoClass, televisionInfoID);
	aEnv->SetIntField(tvInfoObj, tvID, aCTVInfo->nTVID);
//	LOGV("%s  %d", aCTVInfo->szTVName, aCTVInfo->nTVID);
	aEnv->SetIntField(tvInfoObj, tvBgColor, aCTVInfo->nBackGroundColor);
	jstring str = aEnv->NewStringUTF(aCTVInfo->szPlayId);
	aEnv->SetObjectField(tvInfoObj, playId, str);
	aEnv->DeleteLocalRef(str);
	str = aEnv->NewStringUTF(aCTVInfo->szTVName);
	aEnv->SetObjectField(tvInfoObj, tvName, str);
	aEnv->DeleteLocalRef(str);
	jobject imageUrlInfoObj = aEnv->AllocObject(imageUrlInfoClass);
	str = aEnv->NewStringUTF(aCTVInfo->poster.szURL);
	aEnv->SetObjectField(imageUrlInfoObj, imageUrl, str);
	aEnv->DeleteLocalRef(str);
	LOGV("poster url : %s", aCTVInfo->poster.szURL);
	str = aEnv->NewStringUTF(aCTVInfo->poster.szMd5);
	aEnv->SetObjectField(imageUrlInfoObj, md5, str);
	aEnv->DeleteLocalRef(str);
	LOGV("poster url md5 : %s", aCTVInfo->poster.szMd5);
	aEnv->SetObjectField(tvInfoObj, tvImageUrlInfo, imageUrlInfoObj);
	aEnv->DeleteLocalRef(imageUrlInfoObj);	
//  LOGV("%s exit", __FUNCTION__);
	return tvInfoObj;
}

jobject buildJTelevisionInfoTv(JNIEnv *aEnv, LPTVINFO  aCTVInfo) {
//	LOGV("%s enter", __FUNCTION__);
	if(aCTVInfo == NULL) {
		LOGV("%s : tvInfo is null.", __FUNCTION__);
		return NULL;
	}
	
	jclass tvInfoClass = JniUtil::findClass(aEnv, KClassTelevisionInfo);
	jclass imageUrlInfoClass = JniUtil::findClass(aEnv, KClassImageUrlInfo);
	jfieldID tvID = JniUtil::getFieldID(aEnv, KClassTelevisionInfo, "televisionId", KSigInt);
	jfieldID tvBgColor = JniUtil::getFieldID(aEnv, KClassTelevisionInfo, "televisionBgColor", KSigInt);
	jfieldID tvName = JniUtil::getFieldID(aEnv, KClassTelevisionInfo, "televisionName", KSigStr);
	jfieldID tvHeadChar = JniUtil::getFieldID(aEnv, KClassTelevisionInfo, "televisionHeadChar", KSigChar);
	jfieldID tvImageUrlInfo = JniUtil::getFieldID(aEnv, KClassTelevisionInfo, "smallImageURL", KSigImageUrlInfo);
	jfieldID md5 = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "md5", KSigStr);
	jfieldID imageUrl = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "imageUrl", KSigStr);
	jfieldID playId = JniUtil::getFieldID(aEnv, KClassTelevisionInfo, "televisionPlayId", KSigStr);
	jfieldID televisionShow = JniUtil::getFieldID(aEnv, KClassTelevisionInfo, "televisionShow", KSigTelevisionShow);
	
	jmethodID televisionInfoID = aEnv->GetMethodID(tvInfoClass, "<init>", "()V");
	jobject tvInfoObj = aEnv->NewObject(tvInfoClass, televisionInfoID);

	jobject televisionShowObj = buildJTelevisionShow(aEnv, &aCTVInfo->programe);
	aEnv->SetObjectField(tvInfoObj, televisionShow, televisionShowObj);
	aEnv->DeleteLocalRef(televisionShowObj);
	aEnv->SetIntField(tvInfoObj, tvID, aCTVInfo->nChannelId);
	aEnv->SetIntField(tvInfoObj, tvBgColor, aCTVInfo->nBackGroundColor);
	aEnv->SetCharField(tvInfoObj, tvHeadChar, aCTVInfo->szHeadLetter[0]);

	jstring str = aEnv->NewStringUTF(aCTVInfo->szPlayId);
	aEnv->SetObjectField(tvInfoObj, playId, str);
	aEnv->DeleteLocalRef(str);
	str = aEnv->NewStringUTF(aCTVInfo->szChannelName);
	aEnv->SetObjectField(tvInfoObj, tvName, str);
	aEnv->DeleteLocalRef(str);
	jobject imageUrlInfoObj = aEnv->AllocObject(imageUrlInfoClass);
	str = aEnv->NewStringUTF(aCTVInfo->poster.szURL);
	aEnv->SetObjectField(imageUrlInfoObj, imageUrl, str);
	aEnv->DeleteLocalRef(str);
	str = aEnv->NewStringUTF(aCTVInfo->poster.szMd5);
	aEnv->SetObjectField(imageUrlInfoObj, md5, str);
	aEnv->DeleteLocalRef(str);
	aEnv->SetObjectField(tvInfoObj, tvImageUrlInfo, imageUrlInfoObj);
	aEnv->DeleteLocalRef(imageUrlInfoObj);	
//  LOGV("%s exit", __FUNCTION__);
	return tvInfoObj;
}

jobject buildJAlbumInfo(JNIEnv *aEnv, LPALBUMINFO aCAlbumInfo) {
//	LOGV("%s enter", __FUNCTION__);
	if(aCAlbumInfo == NULL) {
		LOGV("%s : album info is null.", __FUNCTION__);
		return NULL;
	}
	jclass albumInfoClass = aEnv->FindClass(KClassAlbumInfo);
	jclass imageUrlInfoClass = aEnv->FindClass(KClassImageUrlInfo);
	jfieldID albumID = JniUtil::getFieldID(aEnv, KClassAlbumInfo, "albumID", KSigInt);
	jfieldID name = JniUtil::getFieldID(aEnv, KClassAlbumInfo, "name", KSigStr);
	jfieldID desc = JniUtil::getFieldID(aEnv, KClassAlbumInfo, "desc", KSigStr);
	jfieldID posterUrl = JniUtil::getFieldID(aEnv, KClassAlbumInfo, "posterUrl", KSigImageUrlInfo);
	jfieldID md5 = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "md5", KSigStr);
	jfieldID imageUrl = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "imageUrl", KSigStr);
	
	jobject albumInfoObj = aEnv->AllocObject(albumInfoClass);
	aEnv->SetIntField(albumInfoObj, albumID, aCAlbumInfo->nAlbumId);
	jstring str = aEnv->NewStringUTF(aCAlbumInfo->szName);
	aEnv->SetObjectField(albumInfoObj, name, str);
	aEnv->DeleteLocalRef(str);
	str = aEnv->NewStringUTF(aCAlbumInfo->szDesc);
	aEnv->SetObjectField(albumInfoObj, desc, str);
	aEnv->DeleteLocalRef(str);
    jobject imageUrlInfoObj = aEnv->AllocObject(imageUrlInfoClass);
	str = aEnv->NewStringUTF(aCAlbumInfo->poster.szURL);
	aEnv->SetObjectField(imageUrlInfoObj, imageUrl, str);
	aEnv->DeleteLocalRef(str);
	str = aEnv->NewStringUTF(aCAlbumInfo->poster.szMd5);
	aEnv->SetObjectField(imageUrlInfoObj, md5, str);
	aEnv->DeleteLocalRef(str);
	aEnv->SetObjectField(albumInfoObj, posterUrl, imageUrlInfoObj);
//	LOGV("%s exit", __FUNCTION__);
	return albumInfoObj;
}

int buildJRecommendationList(JNIEnv *aEnv, LPRECOMMENDMEDIALIST mediaList, jobject aRecommendationList){
//	LOGV("%s enter", __FUNCTION__);
	jclass recommendationClass = JniUtil::findClass(aEnv, KClassRecommendation);
	jsize  itemCnt =  mediaList->nDataCount;
	jobjectArray mediaArray = aEnv->NewObjectArray(itemCnt, recommendationClass, 0);
//	LOGV("%s aMediaList itemcnt %d", __FUNCTION__, itemCnt);
	jfieldID desc = JniUtil::getFieldID(aEnv, KClassRecommendation, "desc", KSigStr);
	jfieldID score = JniUtil::getFieldID(aEnv, KClassRecommendation, "score", KSigFloat);
	jfieldID scoreCount = JniUtil::getFieldID(aEnv, KClassRecommendation, "scoreCount", KSigInt);
	jfieldID episodeAvailable = JniUtil::getFieldID(aEnv, KClassRecommendation, "episodeAvailable", KSigInt);
	jfieldID mediaType = JniUtil::getFieldID(aEnv, KClassRecommendation, "mediaType", KSigInt);
	jfieldID mediaInfo = JniUtil::getFieldID(aEnv, KClassRecommendation, "mediaInfo", KSigMediaInfo);
	jfieldID personInfo = JniUtil::getFieldID(aEnv, KClassRecommendation, "personInfo", KSigPersonInfo);
	for(int i = 0; i < itemCnt; ++ i){
		jobject recommendation = aEnv->AllocObject(recommendationClass);
		aEnv->SetObjectField(recommendation, desc, aEnv->NewStringUTF(mediaList->lpRecommendMedia[i].desc));
		aEnv->SetFloatField(recommendation, score, mediaList->lpRecommendMedia[i].score);
		aEnv->SetIntField(recommendation, scoreCount, mediaList->lpRecommendMedia[i].nScoreCount);
		aEnv->SetIntField(recommendation, episodeAvailable, mediaList->lpRecommendMedia[i].nSetNow);
		aEnv->SetIntField(recommendation, mediaType, mediaList->lpRecommendMedia[i].nMediaType);
  //	LOGV("%s addItem %d of %d", __FUNCTION__, i, itemCnt);
		if(mediaList->lpRecommendMedia[i].nMediaType != 0){
			jobject personInfoObj = buildJPersonInfo(aEnv, mediaList->lpRecommendMedia[i].lpPersonInfor);
			if(personInfoObj != NULL){
				aEnv->SetObjectField(recommendation, personInfo, personInfoObj);
				aEnv->DeleteLocalRef(personInfoObj);
			}
		}else {
			jobject mediaInfoObj = buildJMediaInfo(aEnv, &mediaList->lpRecommendMedia[i].mediaInfo);
			if(mediaInfoObj != NULL){
				aEnv->SetObjectField(recommendation, mediaInfo, mediaInfoObj);
				aEnv->DeleteLocalRef(mediaInfoObj);
			}
		}
		aEnv->SetObjectArrayElement(mediaArray, i, recommendation);
		aEnv->DeleteLocalRef(recommendation);
	}
	jfieldID recommendations = JniUtil::getFieldID(aEnv, KClassRecommendationList, "recommendations", KSigRecommendationArray);
	aEnv->SetObjectField(aRecommendationList, recommendations, mediaArray);
	//	LOGV("%s exit ", __FUNCTION__);
	return 0;
}

int buildJTelevisionShowList(JNIEnv *aEnv, LPTVPROGRAMMELIST televisionShowList, jobject aTelevisionShowList){
//	LOGV("%s enter", __FUNCTION__);
	jclass televisionShowListClass = JniUtil::findClass(aEnv, KClassTelevisionShowList);
	jclass televisionShowDataListClass = JniUtil::findClass(aEnv, KClassTelevisionShowDataList);
	jsize  itemCnt =  televisionShowList->nDataCount;
	jobjectArray televisionShowDataListArray = aEnv->NewObjectArray(itemCnt, televisionShowDataListClass, 0);
	
	for(int i = 0; i < itemCnt; ++ i){
		jobject televisionShowDataList = buildJTelevisionShowDataList(aEnv, &televisionShowList->lpTVProgramme[i]);
		aEnv->SetObjectArrayElement(televisionShowDataListArray, i, televisionShowDataList);
		aEnv->DeleteLocalRef(televisionShowDataList);
	}
	
	jfieldID televisionShowDataLists = JniUtil::getFieldID(aEnv, KClassTelevisionShowList, "televisionShowDataLists", KSigTelevisionShowDataListArray);
	jfieldID dataCount = JniUtil::getFieldID(aEnv, KClassTelevisionShowList, "dataCount", KSigInt);
	aEnv->SetObjectField(aTelevisionShowList, televisionShowDataLists, televisionShowDataListArray);
	aEnv->DeleteLocalRef(televisionShowDataListArray);
	aEnv->SetIntField(aTelevisionShowList, dataCount, televisionShowList->nDataCount);
//	LOGV("%s exit", __FUNCTION__);
	return 0;
}

int buildJTelevisionInfoListTv(JNIEnv *aEnv, LPTVINFOLIST televisionInfoListTv, jobject aTelevisionInfoListTv){
	jclass televisionInfoListClass = JniUtil::findClass(aEnv, KClassTelevisionInfoList);
	jclass televisionInfoClass = JniUtil::findClass(aEnv, KClassTelevisionInfo);
	jsize  itemCnt =  televisionInfoListTv->nDataCount;
	jobjectArray televisionInfoArray = aEnv->NewObjectArray(itemCnt, televisionInfoClass, 0);
	
	for(int i = 0; i < itemCnt; ++ i){
		jobject televisionInfo = buildJTelevisionInfoTv(aEnv, &televisionInfoListTv->lpTVInfo[i]);
		aEnv->SetObjectArrayElement(televisionInfoArray, i, televisionInfo);
		aEnv->DeleteLocalRef(televisionInfo);
	}
	
	jfieldID televisionInfos = JniUtil::getFieldID(aEnv, KClassTelevisionInfoList, "televisionInfos", KSigTelevisonInfoArray);
	jfieldID totalCount = JniUtil::getFieldID(aEnv, KClassTelevisionInfoList, "totalCount", KSigInt);
	aEnv->SetObjectField(aTelevisionInfoListTv, televisionInfos, televisionInfoArray);
	aEnv->SetIntField(aTelevisionInfoListTv, totalCount, televisionInfoListTv->nDataCount);
	return 0;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getRecommendationList(JNIEnv *aEnv, jobject, jint aMeidaID, jobject aRecommendationList){
	LOGV("%s enter", __FUNCTION__);
	RECOMMENDMEDIALIST *mediaList = (RECOMMENDMEDIALIST*)malloc(sizeof(*mediaList));
	memset(mediaList, 0, sizeof(*mediaList));
	int res = DK_GetRecommendMedia(aMeidaID, 0, mediaList);
	if (res >= EMPTY_RESULT){
		buildJRecommendationList(aEnv, mediaList, aRecommendationList);
	}
	DK_DestroyRecommendMedia(mediaList);
	free(mediaList);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}


jobject buildJBanner(JNIEnv *aEnv, LPRECOMMENDMEDIADATA banner){
	//	LOGV("%s enter", __FUNCTION__);
	jclass bannerClass = JniUtil::findClass(aEnv, KClassBanner);
	////	LOGV("%s aMediaList itemcnt %d", __FUNCTION__, itemCnt);
	//	jfieldID desc = JniUtil::getFieldID(aEnv, KClassRecommendation, "desc", KSigStr);
	//	jfieldID score = JniUtil::getFieldID(aEnv, KClassRecommendation, "score", KSigFloat);
	//	jfieldID scoreCount = JniUtil::getFieldID(aEnv, KClassRecommendation, "scoreCount", KSigInt);
	//	jfieldID episodeAvailable = JniUtil::getFieldID(aEnv, KClassRecommendation, "episodeAvailable", KSigInt);
	jobject bannerObj = aEnv->AllocObject(bannerClass);
	jfieldID mediaType = JniUtil::getFieldID(aEnv, KClassBanner, "mediaType", KSigInt);
	jfieldID mediaInfo = JniUtil::getFieldID(aEnv, KClassBanner, "mediaInfo", KSigMediaInfo);
	jfieldID personInfo = JniUtil::getFieldID(aEnv, KClassBanner, "personInfo", KSigPersonInfo);
	jfieldID albumInfo = JniUtil::getFieldID(aEnv, KClassBanner, "albumInfo", KSigAlbumInfo);
	aEnv->SetIntField(bannerObj, mediaType, banner->nMediaType);
	jobject obj;
	if(banner->nMediaType == ID_TYPE_MEDIA){
		obj = buildJMediaInfo(aEnv, &banner->lpRecommendMediaInfo->mediaInfo);
		if(obj != NULL){
			aEnv->SetObjectField(bannerObj, mediaInfo, obj);
			return bannerObj;
		}
	}else if(banner->nMediaType == ID_TYPE_PERSON){
		obj =  buildJPersonInfo(aEnv, &banner->lpRecommendPersonInfo->personInfor);
		if(obj != NULL){
			aEnv->SetObjectField(bannerObj, personInfo, obj);
			return bannerObj;
		}
	}else if(banner->nMediaType == ID_TYPE_SPECIALSUBJECT){
		obj = buildJAlbumInfo(aEnv, &banner->lpRecommendAlbumInfo->albumInfo);
		if(obj != NULL){
			aEnv->SetObjectField(bannerObj, albumInfo, obj);
			return bannerObj;
		}
	}
	return NULL;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getBannerList(JNIEnv *aEnv, jobject, jint aChannelID, jobject aBannerList){
	LOGV("%s enter", __FUNCTION__);
	RECOMMENDMEDIA2 mediaList;
	memset(&mediaList, 0, sizeof(RECOMMENDMEDIA2));
	int res = DK_GetBannerMedia(aChannelID, &mediaList);
	if (res >= EMPTY_RESULT){
		jclass bannerClass = JniUtil::findClass(aEnv, KClassBanner);
		jclass televisionInfoClass = JniUtil::findClass(aEnv, KClassTelevisionInfo);
		jfieldID banners = JniUtil::getFieldID(aEnv, KClassBannerList, "banners", KSigBannerArray);
		jfieldID tvInfos = JniUtil::getFieldID(aEnv, KClassBannerList, "tvInfos", KSigTelevisonInfoArray);
		jfieldID keywords = JniUtil::getFieldID(aEnv, KClassBannerList, "searchKeyWords", KSigStrArray);

		jsize itemCnt =  mediaList.nDataCount;
		jobjectArray bannerArray = aEnv->NewObjectArray(itemCnt, bannerClass, 0);
		for(int i = 0; i < itemCnt; i++){
			jobject banner = buildJBanner(aEnv, &mediaList.lpRecommendMedia[i]);
			if(banner != NULL) {
				aEnv->SetObjectArrayElement(bannerArray, i, banner);
				aEnv->DeleteLocalRef(banner);
			}
		}
		aEnv->SetObjectField(aBannerList, banners, bannerArray);
		aEnv->DeleteLocalRef(bannerArray);

		LPTVDATA lpTvData = mediaList.lpTvData;
		if(lpTvData) {
			jsize itemTvCnt = lpTvData->nDataCount;
			if(itemTvCnt) {
				jobjectArray tvArray = aEnv->NewObjectArray(itemTvCnt, televisionInfoClass, 0);
				for(int i = 0; i < itemTvCnt; i++){
					jobject tv = buildJTelevisionInfo(aEnv, &lpTvData->lpTvDataInfos[i]);
					if(tv != NULL) {
						aEnv->SetObjectArrayElement(tvArray, i, tv);
						aEnv->DeleteLocalRef(tv);
					}
				}
				aEnv->SetObjectField(aBannerList, tvInfos, tvArray);
				aEnv->DeleteLocalRef(tvArray);
			}
		}

		if(mediaList.lpHotSearchKeyWord != NULL)
		{
			int count = mediaList.lpHotSearchKeyWord->nDataCount;
			if( count > 0 && mediaList.lpHotSearchKeyWord->lpszSearchKeyWord != NULL)
			{
				jclass keyword = JniUtil::findClass(aEnv, KClassString);
				jobjectArray keywordsArray = aEnv->NewObjectArray(count, keyword, 0);
				for(int i = 0; i < count; i++)
				{
					jstring str = aEnv->NewStringUTF(mediaList.lpHotSearchKeyWord->lpszSearchKeyWord[i]);
					aEnv->SetObjectArrayElement(keywordsArray, i, str);
					aEnv->DeleteLocalRef(str);
				}
				aEnv->SetObjectField(aBannerList, keywords, keywordsArray);
				aEnv->DeleteLocalRef(keywordsArray);
			}
		}
	}
	DK_DestroyBannerMedia(&mediaList);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

int buildCGetMediaInfoList(JNIEnv *aEnv, LPGETMEDIAINFOLIST aCGetMediaInfoList, jobject aJGetMediaInfoList){
//	LOGV("%s enter", __FUNCTION__);
	jfieldID ids = JniUtil::getFieldID(aEnv, KClassMediaInfoQuery, "ids", KSigIntArray);
	jfieldID pageNo = JniUtil::getFieldID(aEnv, KClassMediaInfoQuery, "pageNo", KSigInt);
	jfieldID pageSize = JniUtil::getFieldID(aEnv, KClassMediaInfoQuery, "pageSize", KSigInt);
	jfieldID orderBy = JniUtil::getFieldID(aEnv, KClassMediaInfoQuery, "orderBy", KSigInt);
	jfieldID listType = JniUtil::getFieldID(aEnv, KClassMediaInfoQuery, "listType", KSigInt);
	jfieldID posterType = JniUtil::getFieldID(aEnv, KClassMediaInfoQuery, "posterType", KSigInt);
	jfieldID statisticInfo = JniUtil::getFieldID(aEnv, KClassMediaInfoQuery, "statisticInfo", KSigStr);

	memset(aCGetMediaInfoList, 0, sizeof(GETMEDIAINFOLIST));
	jintArray jIds = (jintArray)aEnv->GetObjectField(aJGetMediaInfoList, ids);
	int *pIds = aEnv->GetIntArrayElements(jIds, NULL);
	int idsCnt = aEnv->GetArrayLength(jIds);
	for(int i = 0; i < idsCnt; ++ i){
		aCGetMediaInfoList->arnIDs[i] = pIds[i];
	}
	aEnv->ReleaseIntArrayElements(jIds, pIds, 0);
	aCGetMediaInfoList->nIDsCount = idsCnt;
//	LOGV("idsCount %d", aCGetMediaInfoList->nIDsCount);
//	LOGV("arnIDs[0] %d", aCGetMediaInfoList->arnIDs[0]);
	aCGetMediaInfoList->nPageNo = aEnv->GetIntField(aJGetMediaInfoList, pageNo);
//	LOGV("pageNo %d", aCGetMediaInfoList->nPageNo);
	aCGetMediaInfoList->pagesize = aEnv->GetIntField(aJGetMediaInfoList, pageSize);
//	LOGV("pageSize %d", aCGetMediaInfoList->pagesize);
	aCGetMediaInfoList->orderby = aEnv->GetIntField(aJGetMediaInfoList, orderBy);
//	LOGV("orderby %d", aCGetMediaInfoList->orderby);
	aCGetMediaInfoList->listtype = aEnv->GetIntField(aJGetMediaInfoList, listType);
//	LOGV("listtype %d", aCGetMediaInfoList->listtype);
	aCGetMediaInfoList->postertype = aEnv->GetIntField(aJGetMediaInfoList, posterType);
//	LOGV("listtype %d", aCGetMediaInfoList->postertype);
	jstring JStr = (jstring)aEnv->GetObjectField(aJGetMediaInfoList, statisticInfo);
	if( JStr != NULL)
	{
		const char *Cstr = aEnv->GetStringUTFChars(JStr, 0);
		strcpy(aCGetMediaInfoList->szUserBehavData, Cstr);
		aEnv->ReleaseStringUTFChars(JStr, Cstr);
	}
//	LOGV(" buildCGetMediaInfoList %s %d", aCGetMediaInfoList->szUserBehavData, strlen(aCGetMediaInfoList->szUserBehavData));
//	LOGV("%s exit", __FUNCTION__);
	return 0;
}


int buildJMediaInfoList(JNIEnv *aEnv, LPMEDIAINFOLIST aCMediaInfoList, jobject aJMediaInfoList){
	//	LOGV("%s enter", __FUNCTION__);
	jobjectArray mediaInfoArray = NULL;
	jclass mediaInfoListClass = JniUtil::findClass(aEnv, KClassMediaInfoList);

	jclass mediaInfoClass = JniUtil::findClass(aEnv, KClassMediaInfo);
	const jsize itemCnt = aCMediaInfoList->nMediaInfoArrayDataCount;
	LOGV("%s itemCnt %d", __FUNCTION__, itemCnt);
	mediaInfoArray = aEnv->NewObjectArray(itemCnt, mediaInfoClass, 0);

	for(int i = 0; i < itemCnt; ++ i){
		//	LOGV("%s process Item %d", __FUNCTION__, i);
		jobject mediaInfoObj = buildJMediaInfo(aEnv, &aCMediaInfoList->lpMediaInfo[i]);
		aEnv->SetObjectArrayElement(mediaInfoArray, i, mediaInfoObj);
		aEnv->DeleteLocalRef(mediaInfoObj);
	}
	jfieldID mediaInfos = JniUtil::getFieldID(aEnv, KClassMediaInfoList, "mediaInfos", KSigMediaInfoArray);
	aEnv->SetObjectField(aJMediaInfoList, mediaInfos, mediaInfoArray);
	jfieldID totalCount = JniUtil::getFieldID(aEnv, KClassMediaInfoList, "totalCount", KSigInt);
	aEnv->SetIntField(aJMediaInfoList, totalCount, aCMediaInfoList->nTotalCount);
	//	LOGV("%s exit", __FUNCTION__);
	return 0;
}

int buildJSearchMediaInfoList(JNIEnv *aEnv, LPMEDIAINFOLIST aCMediaInfoList, jobject aJSearchMediaInfoList){
	//	LOGV("%s enter", __FUNCTION__);
	jobjectArray mediaInfoArray = NULL;
	jobjectArray categroyInfoArray = NULL;
	jobjectArray recommendInfoArray = NULL;
	jclass searchMediaInfoListClass = JniUtil::findClass(aEnv, KClassSearchMediaInfoList);

	jclass mediaInfoClass = JniUtil::findClass(aEnv, KClassMediaInfo);
	const jsize itemCnt = aCMediaInfoList->nMediaInfoArrayDataCount;
	LOGV("%s itemCnt %d", __FUNCTION__, itemCnt);
	mediaInfoArray = aEnv->NewObjectArray(itemCnt, mediaInfoClass, 0);
	for(int i = 0; i < itemCnt; ++ i){
//		LOGV("%s process Item %d", __FUNCTION__, i);
		jobject mediaInfoObj = buildJMediaInfo(aEnv, &aCMediaInfoList->lpMediaInfo[i]);
		aEnv->SetObjectArrayElement(mediaInfoArray, i, mediaInfoObj);
		aEnv->DeleteLocalRef(mediaInfoObj);
	}
	jfieldID mediaInfos = JniUtil::getFieldID(aEnv, KClassSearchMediaInfoList, "mediaInfos", KSigMediaInfoArray);
	aEnv->SetObjectField(aJSearchMediaInfoList, mediaInfos, mediaInfoArray);

	const jsize recommendItemCnt = aCMediaInfoList->nRecommendArrayDataCount;
	LOGV("%s recommendItemCnt %d", __FUNCTION__, recommendItemCnt);
	recommendInfoArray = aEnv->NewObjectArray(recommendItemCnt, mediaInfoClass, 0);
	for(int i = 0; i < recommendItemCnt; ++ i){
//		LOGV("%s process Item %d", __FUNCTION__, i);
		jobject mediaInfoObj = buildJMediaInfo(aEnv, &aCMediaInfoList->lpRecommend[i]);
		aEnv->SetObjectArrayElement(recommendInfoArray, i, mediaInfoObj);
		aEnv->DeleteLocalRef(mediaInfoObj);
	}
	jfieldID recommendInfos = JniUtil::getFieldID(aEnv, KClassSearchMediaInfoList, "recommendInfos", KSigMediaInfoArray);
	aEnv->SetObjectField(aJSearchMediaInfoList, recommendInfos, recommendInfoArray);

	jclass categoryInfoClass = JniUtil::findClass(aEnv, KClassCategoryInfo);
	const jsize categoryItemCnt = aCMediaInfoList->nCategoryArrayDataCount;
	LOGV("%s categoryItemCnt %d", __FUNCTION__, categoryItemCnt);
	categroyInfoArray = aEnv->NewObjectArray(categoryItemCnt, categoryInfoClass, 0);
	for(int j = 0; j < categoryItemCnt; j++) {
//		LOGV("%s process Category Item %d", __FUNCTION__, j);
		jobject categoryInfoObj = buildJCategoryInfo(aEnv, &aCMediaInfoList->lpCateGoryInfo[j]);
		aEnv->SetObjectArrayElement(categroyInfoArray, j, categoryInfoObj);
		aEnv->DeleteLocalRef(categoryInfoObj);
	}
	jfieldID  categoryInfos = JniUtil::getFieldID(aEnv, KClassSearchMediaInfoList, "categoryInfos", KSigCategoryInfoArray);
	aEnv->SetObjectField(aJSearchMediaInfoList, categoryInfos, categroyInfoArray);

	jfieldID totalCount = JniUtil::getFieldID(aEnv, KClassSearchMediaInfoList, "totalCount", KSigInt);
	aEnv->SetIntField(aJSearchMediaInfoList, totalCount, aCMediaInfoList->nTotalCount);
	//	LOGV("%s exit", __FUNCTION__);
	return 0;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getFilterMediaInfo(JNIEnv *aEnv, jobject, jobject aMediaInfoQuery, jobject aMediaInfoList){
	LOGV("%s enter", __FUNCTION__);
	GETMEDIAINFOLIST mediaList;
	buildCGetMediaInfoList(aEnv, &mediaList, aMediaInfoQuery);
	LOGV("%d , ", mediaList.orderby);
	MEDIAINFOLIST infoList;
	memset(&infoList, 0, sizeof(infoList));

	int requestId = 0;
	int res = DK_GetFilterMediaInfo(&mediaList, &infoList, &requestId);

	if (res >= EMPTY_RESULT){
		buildJMediaInfoList(aEnv, &infoList, aMediaInfoList);
	}
	DK_DestroyMediaInfoList(&infoList);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;

}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_searchMedia(JNIEnv *aEnv, jobject, jobject aSearchInfo, jobject aMediaList){
	LOGV("%s enter", __FUNCTION__);
	jclass searchQueryClass = JniUtil::findClass(aEnv, KClassSearchInfo);
	jfieldID mediaName = JniUtil::getFieldID(aEnv, KClassSearchInfo, "mediaName", KSigStr);
	jfieldID mediaNameSearchType = JniUtil::getFieldID(aEnv, KClassSearchInfo, "mediaNameSearchType", KSigInt);
	jfieldID pageNo = JniUtil::getFieldID(aEnv, KClassSearchInfo, "pageNo", KSigInt);
	jfieldID pageSize = JniUtil::getFieldID(aEnv, KClassSearchInfo, "pageSize", KSigInt);
	jfieldID searchMask = JniUtil::getFieldID(aEnv, KClassSearchInfo, "searchMask", KSigInt);
	jfieldID statisticInfo = JniUtil::getFieldID(aEnv, KClassSearchInfo, "statisticInfo", KSigStr);

	MEDIAINFOLIST mediaList;
	memset(&mediaList, 0, sizeof(mediaList));

	SEARCHINFO searchInfo;
	memset(&searchInfo, 0, sizeof(searchInfo));
	searchInfo.nPageNo = aEnv->GetIntField(aSearchInfo, pageNo);
	searchInfo.nPageSize = aEnv->GetIntField(aSearchInfo, pageSize);
	searchInfo.nMediaNameSearchType = aEnv->GetIntField(aSearchInfo, mediaNameSearchType);
	searchInfo.nSearchMask = aEnv->GetIntField(aSearchInfo, searchMask);
	jstring JStr = (jstring)aEnv->GetObjectField(aSearchInfo, mediaName);
	const char* Cstr = aEnv->GetStringUTFChars(JStr, 0);
	strcpy(searchInfo.szMediaName, Cstr);
	aEnv->ReleaseStringUTFChars(JStr, Cstr);
	JStr = (jstring)aEnv->GetObjectField(aSearchInfo, statisticInfo);
	if( JStr != NULL)
	{
		Cstr = aEnv->GetStringUTFChars(JStr, 0);
		strcpy(searchInfo.szUserBehavData, Cstr);
		aEnv->ReleaseStringUTFChars(JStr, Cstr);
	}
	LOGV(" searchMedia statisticInfo %s  %d", searchInfo.szUserBehavData, strlen(searchInfo.szUserBehavData));
	
	int identifier = 0;
	int res = DK_SearchMedia(&searchInfo, &mediaList, &identifier);

	if (res >= EMPTY_RESULT){
		buildJMediaInfoList(aEnv, &mediaList, aMediaList);
	}
	DK_DestroyMediaInfoList(&mediaList);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_searchMediaInfo(JNIEnv *aEnv, jobject, jobject aSearchInfo, jobject aSearchMediaList){
	LOGV("%s enter", __FUNCTION__);
	jclass searchQueryClass = JniUtil::findClass(aEnv, KClassSearchInfo);
	jfieldID mediaName = JniUtil::getFieldID(aEnv, KClassSearchInfo, "mediaName", KSigStr);
	jfieldID mediaNameSearchType = JniUtil::getFieldID(aEnv, KClassSearchInfo, "mediaNameSearchType", KSigInt);
	jfieldID pageNo = JniUtil::getFieldID(aEnv, KClassSearchInfo, "pageNo", KSigInt);
	jfieldID pageSize = JniUtil::getFieldID(aEnv, KClassSearchInfo, "pageSize", KSigInt);
	jfieldID searchMask = JniUtil::getFieldID(aEnv, KClassSearchInfo, "searchMask", KSigInt);
	jfieldID statisticInfo = JniUtil::getFieldID(aEnv, KClassSearchInfo, "statisticInfo", KSigStr);

	MEDIAINFOLIST mediaList;
	memset(&mediaList, 0, sizeof(mediaList));

	SEARCHINFO searchInfo;
	memset(&searchInfo, 0, sizeof(searchInfo));
	searchInfo.nPageNo = aEnv->GetIntField(aSearchInfo, pageNo);
	searchInfo.nPageSize = aEnv->GetIntField(aSearchInfo, pageSize);
	searchInfo.nMediaNameSearchType = aEnv->GetIntField(aSearchInfo, mediaNameSearchType);
	searchInfo.nSearchMask = aEnv->GetIntField(aSearchInfo, searchMask);
	jstring JStr = (jstring)aEnv->GetObjectField(aSearchInfo, mediaName);
	const char* Cstr = aEnv->GetStringUTFChars(JStr, 0);
	strcpy(searchInfo.szMediaName, Cstr);
	aEnv->ReleaseStringUTFChars(JStr, Cstr);
	JStr = (jstring)aEnv->GetObjectField(aSearchInfo, statisticInfo);
	if( JStr != NULL)
	{
		Cstr = aEnv->GetStringUTFChars(JStr, 0);
		strcpy(searchInfo.szUserBehavData, Cstr);
		aEnv->ReleaseStringUTFChars(JStr, Cstr);
	}
	LOGV("searchMedia statisticInfo %s  %d", searchInfo.szUserBehavData, strlen(searchInfo.szUserBehavData));
	
	int identifier = 0;
	int res = DK_SearchMedia(&searchInfo, &mediaList, &identifier);

	if (res >= EMPTY_RESULT){
		buildJSearchMediaInfoList(aEnv, &mediaList, aSearchMediaList);
	}
	DK_DestroyMediaInfoList(&mediaList);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getMediaDetailInfo(JNIEnv *aEnv, jobject, jint aMediaID, jstring aStatisticInfo,
		jboolean aGetAll, jint fee, jobject aMediaDetailInfo2){
	LOGV("%s enter ", __FUNCTION__);
	string szStatisticInfo;
	JniUtil::getCStrFromJStr(aEnv, szStatisticInfo, aStatisticInfo);
//	LOGV("szStatisticInfo : %s  %d", szStatisticInfo.c_str(), szStatisticInfo.size());
	
	INPUTDETAILINFO2  inputDetailInfo2;
	memset(&inputDetailInfo2, 0, sizeof(inputDetailInfo2));
	inputDetailInfo2.nMediaId = aMediaID;
	inputDetailInfo2.nPageNo = 1;
	if( aGetAll)
		inputDetailInfo2.nPageSize = 1000;
	else
		inputDetailInfo2.nPageSize = 1;
	inputDetailInfo2.nOrder = -1;
	inputDetailInfo2.lpszUserBehavData = (char *)szStatisticInfo.c_str();
	MEDIADETAILINFO2 medaiDetailInfo2;
	memset(&medaiDetailInfo2, 0, sizeof(medaiDetailInfo2));
	int res = DK_GetMediaDetailInfo2(&inputDetailInfo2, &medaiDetailInfo2, fee);
	
//	MEDIADETAILINFO mediaInfo;
//	memset(&mediaInfo, 0, sizeof(mediaInfo));
//	int res = DK_GetMediaDetailInfo2(aMediaID, NULL, &mediaInfo);
//	int res = DK_GetMediaDetailInfo(aMediaID, szStatisticInfo.c_str(), &mediaInfo);
//	int res = DK_GetMediaDetailInfo(aMediaID, &mediaInfo);

	if (res >= EMPTY_RESULT){
		jclass mediaDetailInfo2Class = JniUtil::findClass(aEnv, KClassMediaDetailInfo2);
		jclass mediaSetInfoListClass = JniUtil::findClass(aEnv, KClassMediaSetInfoList);
		jclass mediaSetInfoClass = JniUtil::findClass(aEnv, KClassMediaSetInfo);
		jclass mediaDetailInfoClass = JniUtil::findClass(aEnv, KClassMediaDetailInfo);
		jclass imageUrlInfoClass = JniUtil::findClass(aEnv, KClassImageUrlInfo);
		
		jfieldID mediaDetailInfo = JniUtil::getFieldID(aEnv, KClassMediaDetailInfo2, "mediaDetailInfo", KSigMediaDetailInfo);
		jfieldID mediaSetInfoList = JniUtil::getFieldID(aEnv, KClassMediaDetailInfo2, "mediaSetInfoList", KSigMediaSetInfoList);
		
		//media detail info
		jobject mediaDetailInfoObj = aEnv->GetObjectField(aMediaDetailInfo2, mediaDetailInfo);
		jfieldID playCount = JniUtil::getFieldID(aEnv, KClassMediaDetailInfo, "playCount", KSigInt);
		jfieldID setCount = JniUtil::getFieldID(aEnv, KClassMediaDetailInfo, "setCount", KSigInt);
		jfieldID currentCount = JniUtil::getFieldID(aEnv, KClassMediaDetailInfo, "currentCount", KSigInt);
		jfieldID playLength = JniUtil::getFieldID(aEnv, KClassMediaDetailInfo, "playLength", KSigInt);
		jfieldID issueDate = JniUtil::getFieldID(aEnv, KClassMediaDetailInfo, "issueDate", KSigStr);
		jfieldID desc = JniUtil::getFieldID(aEnv, KClassMediaDetailInfo, "desc", KSigStr);
		jfieldID language = JniUtil::getFieldID(aEnv, KClassMediaDetailInfo, "language", KSigStr);
		jfieldID bigImageURL = JniUtil::getFieldID(aEnv, KClassMediaDetailInfo, "bigImageURL", KSigImageUrlInfo);
		jfieldID smallImageURL = JniUtil::getFieldID(aEnv, KClassMediaDetailInfo, "smallImageURL", KSigImageUrlInfo);
		jfieldID score = JniUtil::getFieldID(aEnv, KClassMediaDetailInfo, "score", KSigFloat);
		jfieldID scoreCount = JniUtil::getFieldID(aEnv, KClassMediaDetailInfo, "scoreCount", KSigInt);
		jfieldID md5 = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "md5", KSigStr);
		jfieldID imageUrl = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "imageUrl", KSigStr);

		aEnv->SetIntField(mediaDetailInfoObj, playCount, medaiDetailInfo2.mediaFullInfo.nPlayCount);
		aEnv->SetIntField(mediaDetailInfoObj, setCount, medaiDetailInfo2.mediaFullInfo.nSetCount);
		aEnv->SetIntField(mediaDetailInfoObj, currentCount, medaiDetailInfo2.mediaFullInfo.nSetNow);
		aEnv->SetIntField(mediaDetailInfoObj, playLength, medaiDetailInfo2.mediaFullInfo.playlength);
		jstring jStr = aEnv->NewStringUTF(medaiDetailInfo2.mediaFullInfo.szIssuedate);
		aEnv->SetObjectField(mediaDetailInfoObj, issueDate, jStr);
		aEnv->DeleteLocalRef(jStr);
		jStr = aEnv->NewStringUTF(medaiDetailInfo2.mediaFullInfo.desc);
		aEnv->SetObjectField(mediaDetailInfoObj, desc, jStr);
		aEnv->DeleteLocalRef(jStr);
		jStr = aEnv->NewStringUTF(medaiDetailInfo2.mediaFullInfo.szLanguage);
		aEnv->SetObjectField(mediaDetailInfoObj, language, jStr);
		aEnv->DeleteLocalRef(jStr);
		jobject bigImageUrlObj = aEnv->GetObjectField(mediaDetailInfoObj, bigImageURL);
		jStr = aEnv->NewStringUTF(medaiDetailInfo2.mediaFullInfo.bigPoster.szURL);
		aEnv->SetObjectField(bigImageUrlObj, imageUrl, jStr);
		aEnv->DeleteLocalRef(jStr);
		jStr = aEnv->NewStringUTF(medaiDetailInfo2.mediaFullInfo.bigPoster.szMd5);
		aEnv->SetObjectField(bigImageUrlObj, md5, jStr);
		aEnv->DeleteLocalRef(jStr);
		jobject smallImageUrlObj = aEnv->GetObjectField(mediaDetailInfoObj, smallImageURL);
		jStr = aEnv->NewStringUTF(medaiDetailInfo2.mediaFullInfo.smallPoster.szURL);
		aEnv->SetObjectField(smallImageUrlObj, imageUrl, jStr);
		aEnv->DeleteLocalRef(jStr);
		jStr = aEnv->NewStringUTF(medaiDetailInfo2.mediaFullInfo.smallPoster.szMd5);
		aEnv->SetObjectField(smallImageUrlObj, md5, jStr);
		aEnv->DeleteLocalRef(jStr);
		aEnv->SetFloatField(mediaDetailInfoObj, score, medaiDetailInfo2.mediaFullInfo.score);
		aEnv->SetIntField(mediaDetailInfoObj, scoreCount, medaiDetailInfo2.mediaFullInfo.nScoreCount);
		
		//media set info 
		jobject mediaSetInfoListObj = aEnv->GetObjectField(aMediaDetailInfo2, mediaSetInfoList);
		jfieldID dataCount = JniUtil::getFieldID(aEnv, KClassMediaSetInfoList, "nDataCount", KSigInt);
		jfieldID mediaSetInfoArray = JniUtil::getFieldID(aEnv, KClassMediaSetInfoList, "mediaSetInfos", KSigMediaSetInfoArray);
		if(medaiDetailInfo2.VarietyList.nStyle == TRADITIONAL_STYLE)
		{
			int count = medaiDetailInfo2.VarietyList.nDataCount;
			LOGV("variety list count %d", count);
			aEnv->SetIntField(mediaSetInfoListObj, dataCount, count);
			jobjectArray mediaSetInfoObjArray = aEnv->NewObjectArray(count, mediaSetInfoClass, 0);
			jfieldID nCi = JniUtil::getFieldID(aEnv, KClassMediaSetInfo, "nCi", KSigInt);
			jfieldID szDate = JniUtil::getFieldID(aEnv, KClassMediaSetInfo, "szDate", KSigStr);
			jfieldID szVideoName = JniUtil::getFieldID(aEnv, KClassMediaSetInfo, "szVideoName", KSigStr);
			jfieldID szFocus = JniUtil::getFieldID(aEnv, KClassMediaSetInfo, "szFocus", KSigStr);
			for(int i = 0; i < count; i++) 
			{
				jobject mediaSetInfoObj = aEnv->AllocObject(mediaSetInfoClass);
				aEnv->SetIntField(mediaSetInfoObj, nCi, medaiDetailInfo2.VarietyList.lpTraditional[i].nCi);
//				jstring jstr = aEnv->NewStringUTF(medaiDetailInfo2.VarietyList.lpTraditional[i].szDate);
//				aEnv->SetObjectField(mediaSetInfoObj, szDate, jstr);
//				aEnv->DeleteLocalRef(jStr);
//				jstr = aEnv->NewStringUTF(medaiDetailInfo2.VarietyList.lpTraditional[i].szVideoName);
//				aEnv->SetObjectField(mediaSetInfoObj, szVideoName, jstr);
//				aEnv->DeleteLocalRef(jStr);
//				jstr = aEnv->NewStringUTF(medaiDetailInfo2.VarietyList.lpTraditional[i].szFocus);
//				aEnv->SetObjectField(mediaSetInfoObj, szFocus, jstr);
//				aEnv->DeleteLocalRef(jStr);
				aEnv->SetObjectArrayElement(mediaSetInfoObjArray, i, mediaSetInfoObj);
				aEnv->DeleteLocalRef(mediaSetInfoObj);
			}
			aEnv->SetObjectField(mediaSetInfoListObj, mediaSetInfoArray, mediaSetInfoObjArray);
			aEnv->DeleteLocalRef(mediaSetInfoObjArray);
		}
	}
	DK_DestroyMediaDetailInfo2(&medaiDetailInfo2);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

int buildJChannelRecommendationList(JNIEnv *aEnv, LPCHANNELRECOMMENDMEDIALIST channelMediaList, jobject aChannelRecommendationList){
	//	LOGV("%s enter", __FUNCTION__);
	jclass channelRecommendationClass = JniUtil::findClass(aEnv, KClassChannelRecommendation);
	jsize  itemCnt =  channelMediaList->nTotalCount;
	jobjectArray mediaArray = aEnv->NewObjectArray(itemCnt, channelRecommendationClass, 0);
	jclass mediaInfoClass = JniUtil::findClass(aEnv, KClassMediaInfo);
	jclass televisionInfoClass = JniUtil::findClass(aEnv, KClassTelevisionInfo);
	jobjectArray mediaInfoArray = NULL;
	jobjectArray televisionInfoArray = NULL;
	jfieldID isManual = JniUtil::getFieldID(aEnv, KClassChannelRecommendation, "isManual", KSigInt);
	jfieldID channelID = JniUtil::getFieldID(aEnv, KClassChannelRecommendation, "channelID", KSigInt);
	jfieldID totalCount = JniUtil::getFieldID(aEnv, KClassChannelRecommendation, "totalCount", KSigInt);
	jfieldID mediaInfoList = JniUtil::getFieldID(aEnv, KClassChannelRecommendation, "mediaInfoList", KSigMediaInfoArray);
	jfieldID televisionInfoList = JniUtil::getFieldID(aEnv, KClassChannelRecommendation, "televisionInfoList", KSigTelevisonInfoArray);
	//LOGV("itemCnt = %d ", itemCnt);
	for(int i = 0; i < itemCnt; ++ i){
		jobject channelRecommendation = aEnv->AllocObject(channelRecommendationClass);
		aEnv->SetIntField(channelRecommendation, isManual, channelMediaList->lpMediaInfo[i].nIsManual);
		aEnv->SetIntField(channelRecommendation, channelID, channelMediaList->lpMediaInfo[i].nChannelId);
		aEnv->SetIntField(channelRecommendation, totalCount, channelMediaList->lpMediaInfo[i].nTotalCount);
		int count = channelMediaList->lpMediaInfo[i].nMediaInfoArrayDataCount;
		if( channelMediaList->lpMediaInfo[i].nMediaType == ID_TYPE_MEDIA) {
			mediaInfoArray = aEnv->NewObjectArray(count, mediaInfoClass, 0);
			for(int index = 0; index < count; ++index){
				jobject mediaInfoObj = buildJMediaInfo(aEnv, &channelMediaList->lpMediaInfo[i].lpMediaInfo[index]);
				aEnv->SetObjectArrayElement(mediaInfoArray, index, mediaInfoObj);
				aEnv->DeleteLocalRef(mediaInfoObj);
			}
			aEnv->SetObjectField(channelRecommendation, mediaInfoList, mediaInfoArray);
			aEnv->SetObjectArrayElement(mediaArray, i, channelRecommendation);
			aEnv->DeleteLocalRef(mediaInfoArray);
		} else if( channelMediaList->lpMediaInfo[i].nMediaType == ID_TYPE_TELEVISION) {
			televisionInfoArray = aEnv->NewObjectArray(count, televisionInfoClass, 0);
			for(int index = 0; index < count; ++index){
				jobject tvInfoObj = buildJTelevisionInfo(aEnv, &channelMediaList->lpMediaInfo[i].lpTVInfo[index]);
				aEnv->SetObjectArrayElement(televisionInfoArray, index, tvInfoObj);
				aEnv->DeleteLocalRef(tvInfoObj);
			}
			aEnv->SetObjectField(channelRecommendation, televisionInfoList, televisionInfoArray);
			aEnv->SetObjectArrayElement(mediaArray, i, channelRecommendation);
			aEnv->DeleteLocalRef(televisionInfoArray);
		}
		aEnv->DeleteLocalRef(channelRecommendation);
	}
	
	jfieldID channelRecommendations = JniUtil::getFieldID(aEnv, KClassChannelRecommendationList, "channelRecommendations", KSigChannelRecommendationArray);
	aEnv->SetObjectField(aChannelRecommendationList, channelRecommendations, mediaArray);
	//	LOGV("%s exit", __FUNCTION__);
	return 0;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getChannelRecommendation(JNIEnv *aEnv, jobject, jobject aMediaInfoQuery, jobject aChannelRecommendationList){
	LOGV("%s enter", __FUNCTION__);

	GETMEDIAINFOLIST getMediaList;
	buildCGetMediaInfoList(aEnv, &getMediaList, aMediaInfoQuery);

	CHANNELRECOMMENDMEDIALIST channelMediaList;
	memset(&channelMediaList, 0, sizeof(CHANNELRECOMMENDMEDIALIST));
	int res = DK_GetChannelRecommendMedia(&getMediaList, &channelMediaList);
	if (res >= EMPTY_RESULT){
		buildJChannelRecommendationList(aEnv, &channelMediaList, aChannelRecommendationList);
	}
	LOGV("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
	DK_DestroyChannelRecommendMedia(&channelMediaList);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getTelevisionShowInfo(JNIEnv *aEnv, jobject, jobject aMediaInfoQuery, jobject aTelevisionShowList, jstring aStatisticInfo){
	LOGV("%s enter", __FUNCTION__);

	GETMEDIAINFOLIST getMediaList;
	buildCGetMediaInfoList(aEnv, &getMediaList, aMediaInfoQuery);

	string szStatisticInfo;
	JniUtil::getCStrFromJStr(aEnv, szStatisticInfo, aStatisticInfo);

	TVPROGRAMMELIST televisionShowList;
	memset(&televisionShowList, 0, sizeof(TVPROGRAMMELIST));
	int res = DK_GetTVProgramme(&getMediaList, &televisionShowList, szStatisticInfo.c_str());
	LOGV("%s return %d", __FUNCTION__, res);
	if (res >= EMPTY_RESULT){
		buildJTelevisionShowList(aEnv, &televisionShowList, aTelevisionShowList);
	}

	DK_DestroyTVProgramme(&televisionShowList);
	LOGV("%s exit", __FUNCTION__);
	return res;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getTelevisionRecommendation(JNIEnv *aEnv, jobject, 
	jobject aMediaInfoQuery, jobject aTelevisionInfoList){
	
	GETMEDIAINFOLIST getMediaList;
	buildCGetMediaInfoList(aEnv, &getMediaList, aMediaInfoQuery);
	
	TVINFOLIST televisionInfoList;
	memset(&televisionInfoList, 0, sizeof(TVINFOLIST));
	int res = DK_GetTVChannelRecommend(&getMediaList, &televisionInfoList);
	if (res >= EMPTY_RESULT){
		buildJTelevisionInfoListTv(aEnv, &televisionInfoList, aTelevisionInfoList);
	}
	DK_DestroyTVChannelRecommend(&televisionInfoList);
	return res;
}

jobject buildJSpecialSubject(JNIEnv *aEnv, LPSPECIALSUBJECT aCSubject) {
	//	LOGV("%s enter", __FUNCTION__);
	if(aCSubject == NULL) {
		LOGV("%s : no subject.", __FUNCTION__);
		return NULL;
	}
	jclass subjectClass = aEnv->FindClass(KClassSpecialSubject);
	jclass imageUrlInfoClass = aEnv->FindClass(KClassImageUrlInfo);
	jfieldID name = JniUtil::getFieldID(aEnv, KClassSpecialSubject, "name", KSigStr);
	jfieldID desc = JniUtil::getFieldID(aEnv, KClassSpecialSubject, "desc", KSigStr);
	jfieldID posterUrl = JniUtil::getFieldID(aEnv, KClassSpecialSubject, "posterUrl", KSigImageUrlInfo);
	jfieldID channelID = JniUtil::getFieldID(aEnv, KClassSpecialSubject, "channelID", KSigInt);
	jfieldID mediaCount = JniUtil::getFieldID(aEnv, KClassSpecialSubject, "mediaCount", KSigInt);
	jfieldID md5 = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "md5", KSigStr);
	jfieldID imageUrl = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "imageUrl", KSigStr);

	jobject subjectObj = aEnv->AllocObject(subjectClass);
	jstring jStr = aEnv->NewStringUTF(aCSubject->szName);
	aEnv->SetObjectField(subjectObj, name, jStr);
	aEnv->DeleteLocalRef(jStr);
	jStr = aEnv->NewStringUTF(aCSubject->szDesc);
	aEnv->SetObjectField(subjectObj, desc, jStr);
	aEnv->DeleteLocalRef(jStr);
	jobject posterObj = aEnv->AllocObject(imageUrlInfoClass);
	jStr = aEnv->NewStringUTF(aCSubject->poster.szURL);
	aEnv->SetObjectField(posterObj, imageUrl, jStr);
	aEnv->DeleteLocalRef(jStr);
	jStr = aEnv->NewStringUTF(aCSubject->poster.szMd5);
	aEnv->SetObjectField(posterObj, md5, jStr);
	aEnv->DeleteLocalRef(jStr);
	aEnv->SetObjectField(subjectObj, posterUrl, posterObj);
	aEnv->DeleteLocalRef(posterObj);
	aEnv->SetIntField(subjectObj, channelID, aCSubject->nChannelId);
	aEnv->SetIntField(subjectObj, mediaCount, aCSubject->nMediaCount);
	//	LOGV("%s exit", __FUNCTION__);
	return subjectObj;
}

void buildJSpecialSubjectList(JNIEnv *aEnv, LPSPECIALSUBJECTLIST aCSpecialSubjectList, jobject aJSpecialSubjectList){
	//	LOGV("%s enter", __FUNCTION__);
	jobjectArray subjectArray = NULL;
	jclass subjectListClass = JniUtil::findClass(aEnv, KClassSpecialSubjectList);
	jclass subjectClass = JniUtil::findClass(aEnv, KClassSpecialSubject);
	const jsize itemCnt = aCSpecialSubjectList->nDataCount;
	//	LOGV("%s itemCnt %d", __FUNCTION__, itemCnt);
	subjectArray = aEnv->NewObjectArray(itemCnt, subjectClass, 0);

	for(int i = 0; i < itemCnt; ++ i){
		jobject subjectObj = buildJSpecialSubject(aEnv, &aCSpecialSubjectList->lpSpecialSubject[i]);
		aEnv->SetObjectArrayElement(subjectArray, i, subjectObj);
		aEnv->DeleteLocalRef(subjectObj);
	}
	jfieldID subjects = JniUtil::getFieldID(aEnv, KClassSpecialSubjectList, "subjects", KSigSpecialSubjectArray);
	aEnv->SetObjectField(aJSpecialSubjectList, subjects, subjectArray);
	//	LOGV("%s exit", __FUNCTION__);
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getSpecialSubjectList(JNIEnv *aEnv, jobject, jobject aSpecialSubjectList){
	LOGV("%s enter", __FUNCTION__);

	SPECIALSUBJECTLIST subjectList;
	memset(&subjectList, 0, sizeof(SPECIALSUBJECTLIST));

	int res = DK_GetSpecialSubjectList(&subjectList);
	if (res >= EMPTY_RESULT){
		buildJSpecialSubjectList(aEnv, &subjectList, aSpecialSubjectList);
	}
	DK_DestroySpecialSubjectList(&subjectList);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

int buildJSpecialSubjectMediaList(JNIEnv *aEnv, LPSPECIALSUBJECTMEDIALIST aCSpecialSubjectMediaList, jobject aJSpecialSubjectMediaList){
	//	LOGV("%s enter", __FUNCTION__);
	jobjectArray mediaArray = NULL;
	jclass subjectMediaListClass = JniUtil::findClass(aEnv, KClassSpecialSubjectMediaList);
	jclass subjectMediaClass = JniUtil::findClass(aEnv, KClassSpecialSubjectMedia);
	jfieldID mediaType = JniUtil::getFieldID(aEnv, KClassSpecialSubjectMedia, "mediaType", KSigInt);
	jfieldID mediaInfo = JniUtil::getFieldID(aEnv, KClassSpecialSubjectMedia, "mediaInfo", KSigMediaInfo);
	jfieldID personInfo = JniUtil::getFieldID(aEnv, KClassSpecialSubjectMedia, "personInfo", KSigPersonInfo);
	const jsize itemCnt = aCSpecialSubjectMediaList->nDataCount;
	LOGV("%s itemCnt %d", __FUNCTION__, itemCnt);
	mediaArray = aEnv->NewObjectArray(itemCnt, subjectMediaClass, 0);
	for(int i = 0; i < itemCnt; ++ i){
		jobject subjectMediaObj = aEnv->AllocObject(subjectMediaClass);
		aEnv->SetIntField(subjectMediaObj, mediaType, aCSpecialSubjectMediaList->lpMediaList[i].nMediaTpye);
		if(aCSpecialSubjectMediaList->lpMediaList[i].nMediaTpye != 0){
			jobject personInfoObj = buildJPersonInfo(aEnv, aCSpecialSubjectMediaList->lpMediaList[i].lpPersonInfo);
			if(personInfoObj != NULL){
				aEnv->SetObjectField(subjectMediaObj, personInfo, personInfoObj);
				aEnv->DeleteLocalRef(personInfoObj);
			}
		} else {
			jobject mediaInfoObj = buildJMediaInfo(aEnv, aCSpecialSubjectMediaList->lpMediaList[i].lpMediaInfo);
			if(mediaInfoObj != NULL){
				aEnv->SetObjectField(subjectMediaObj, mediaInfo, mediaInfoObj);
				aEnv->DeleteLocalRef(mediaInfoObj);
			}
		}
		aEnv->SetObjectArrayElement(mediaArray, i, subjectMediaObj);
		aEnv->DeleteLocalRef(subjectMediaObj);
	}
	jfieldID medias = JniUtil::getFieldID(aEnv, KClassSpecialSubjectMediaList, "medias", KSigSpecialSubjectMediaArray);
	aEnv->SetObjectField(aJSpecialSubjectMediaList, medias, mediaArray);
	//	LOGV("%s exit", __FUNCTION__);
	return 0;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getSpecialSubjectMedia(JNIEnv *aEnv, jobject, jint aSpecialID, jstring aStatisticInfo, jobject aSpecialSubjectMediaList){
	LOGV("%s enter", __FUNCTION__);
	SPECIALSUBJECTMEDIALIST mediaList;
	memset(&mediaList, 0, sizeof(SPECIALSUBJECTMEDIALIST));
    string szStatisticInfo;
	JniUtil::getCStrFromJStr(aEnv, szStatisticInfo, aStatisticInfo);
	LOGV("szStatisticInfo : %s  %d", szStatisticInfo.c_str(), szStatisticInfo.size());
	int res = DK_GetSpecialSubjectMedia2(aSpecialID, szStatisticInfo.c_str(), &mediaList);
//	int res = DK_GetSpecialSubjectMedia(aSpecialID, &mediaList);
	if (res >= EMPTY_RESULT){
		buildJSpecialSubjectMediaList(aEnv, &mediaList, aSpecialSubjectMediaList);
	}
	DK_DestroySpecialSubjectMedia(&mediaList);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

jobject buildJMediaReview(JNIEnv *aEnv, LPFILMREVIEW aCReview){
	LOGV("%s enter", __FUNCTION__);
	if(aCReview == NULL) {
		LOGV("%s : LPFILMREVIEW is NULL.", __FUNCTION__);
		return NULL;
	}
	jclass reviewClass = aEnv->FindClass(KClassMediaReview);
	jfieldID userID = JniUtil::getFieldID(aEnv, KClassMediaReview, "userID", KSigStr);
	jfieldID comment = JniUtil::getFieldID(aEnv, KClassMediaReview, "comment", KSigStr);
	jfieldID createTime = JniUtil::getFieldID(aEnv, KClassMediaReview, "createTime", KSigStr);
	jfieldID score = JniUtil::getFieldID(aEnv, KClassMediaReview, "score", KSigInt);
	jfieldID choice = JniUtil::getFieldID(aEnv, KClassMediaReview, "choice", KSigInt);
	jobject reviewObj = aEnv->AllocObject(reviewClass);
	aEnv->SetObjectField(reviewObj, userID, aEnv->NewStringUTF(aCReview->szUserId));
	aEnv->SetObjectField(reviewObj, comment, aEnv->NewStringUTF(aCReview->szFilmReview));
	aEnv->SetObjectField(reviewObj, createTime, aEnv->NewStringUTF(aCReview->szCreateTime));
	aEnv->SetIntField(reviewObj, score, aCReview->nScore);
	aEnv->SetIntField(reviewObj, choice, aCReview->nChoice);
	LOGV("%s exit", __FUNCTION__);
	return reviewObj;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getReviewList(JNIEnv *aEnv, jobject, jint aMediaID, jint aPageNo, jint aPageSize, jobject aMediaReviewList){
	LOGV("%s enter", __FUNCTION__);

	FILMREVIEWLIST reviewList;// = (LPFILMREVIEWLIST)malloc(sizeof(FILMREVIEWLIST));
	memset(&reviewList, 0, sizeof(FILMREVIEWLIST));
	int res = DK_GetFilmReview(aMediaID, aPageSize, aPageNo, &reviewList);
	if (res >= EMPTY_RESULT){
		jclass mediaReviewListClass = JniUtil::findClass(aEnv, KClassMediaReviewList);
		jclass mediaReviewClass = JniUtil::findClass(aEnv, KClassMediaReview);
		jfieldID totalCount = JniUtil::getFieldID(aEnv, KClassMediaReviewList, "totalCount", KSigInt);
		aEnv->SetIntField(aMediaReviewList, totalCount, reviewList.nTotalCount);
		int itemCount = reviewList.nDataCount;

		jobjectArray reviewArray = aEnv->NewObjectArray(itemCount, mediaReviewClass, 0);
		jobject reviewObj;
		for(int i = 0 ; i < itemCount; i++) {
			reviewObj = buildJMediaReview(aEnv, &reviewList.lpFilmReview[i]);
			aEnv->SetObjectArrayElement(reviewArray, i, reviewObj);
			aEnv->DeleteLocalRef(reviewObj);
		}
		jfieldID reviews = JniUtil::getFieldID(aEnv, KClassMediaReviewList, "reviews", KSigMediaReviewArray);
		aEnv->SetObjectField(aMediaReviewList, reviews, reviewArray);
	}
	DK_DestroyFilmReview(&reviewList);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_comment(JNIEnv *aEnv, jobject, jint aMediaID, jint aScore, jstring aComment){
	LOGV("%s enter", __FUNCTION__);
	string szComment;
	JniUtil::getCStrFromJStr(aEnv, szComment, aComment);
	LOGV("%s ", szComment.c_str());
	int res = DK_SetFilmReview(aMediaID, aScore, szComment.c_str());
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

int buildCGetMediaVarietyList(JNIEnv *aEnv, LPGETMEDIAVARIETY aCGetMediaVarietyList, jobject aJVarietyQuery){
	//	LOGV("%s enter", __FUNCTION__);
	jfieldID mediaID = JniUtil::getFieldID(aEnv, KClassVarietyQuery, "mediaID", KSigInt);
	jfieldID pageNo = JniUtil::getFieldID(aEnv, KClassVarietyQuery, "pageNo", KSigInt);
	jfieldID pageSize = JniUtil::getFieldID(aEnv, KClassVarietyQuery, "pageSize", KSigInt);
	jfieldID orderBy = JniUtil::getFieldID(aEnv, KClassVarietyQuery, "orderBy", KSigInt);
	jfieldID year = JniUtil::getFieldID(aEnv, KClassVarietyQuery, "year", KSigInt);

	memset(aCGetMediaVarietyList, 0, sizeof(GETMEDIAVARIETY));

	aCGetMediaVarietyList->nMediaId = aEnv->GetIntField(aJVarietyQuery, mediaID);;
	aCGetMediaVarietyList->nPageNo = aEnv->GetIntField(aJVarietyQuery, pageNo);
	aCGetMediaVarietyList->nPageSize = aEnv->GetIntField(aJVarietyQuery, pageSize);
	aCGetMediaVarietyList->nOrder = aEnv->GetIntField(aJVarietyQuery, orderBy);
	aCGetMediaVarietyList->nYear = aEnv->GetIntField(aJVarietyQuery, year);
	//	LOGV("%s exit", __FUNCTION__);
	return 0;
}


JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getVarietyMediaInfo(JNIEnv *aEnv, jobject, jobject aVarietyQuery, jobject aVarietyList){
	LOGV("%s enter", __FUNCTION__);

	GETMEDIAVARIETY getMediaVariety;
	buildCGetMediaVarietyList(aEnv, &getMediaVariety, aVarietyQuery);
	VARIETYLIST varietyList;
	memset(&varietyList, 0, sizeof(varietyList));
	int res = DK_GetVarietyMediaInfo2(&getMediaVariety, &varietyList);
	if (res >= EMPTY_RESULT){
		jobjectArray args = NULL;
		jstring jStr = NULL;
		if(TRADITIONAL_STYLE == varietyList.nStyle){
			jclass varietyClass = JniUtil::findClass(aEnv, KClassVariety);
			jfieldID varietyDetails = JniUtil::getFieldID(aEnv, KClassVarietyList, "varietyDetails", KSigVarietyArray);
			jfieldID ci = JniUtil::getFieldID(aEnv, KClassVariety, "ci", KSigInt);
			jfieldID date = JniUtil::getFieldID(aEnv, KClassVariety, "date", KSigStr);
			jfieldID name = JniUtil::getFieldID(aEnv, KClassVariety, "name", KSigStr);
			int itemCount = varietyList.nDataCount;
			jobjectArray varietyArray =  aEnv->NewObjectArray(itemCount, varietyClass, 0);
			for(int i = 0; i < itemCount; ++ i){
				jobject varietyObj = aEnv->AllocObject(varietyClass);
				aEnv->SetIntField(varietyObj, ci, varietyList.lpTraditional[i].nCi);
				aEnv->SetObjectField(varietyObj, date, aEnv->NewStringUTF(varietyList.lpTraditional[i].szDate));
				aEnv->SetObjectField(varietyObj, name, aEnv->NewStringUTF(varietyList.lpTraditional[i].szVideoName));
				aEnv->SetObjectArrayElement(varietyArray, i, varietyObj);
				aEnv->DeleteLocalRef(varietyObj);
			}
			aEnv->SetObjectField(aVarietyList, varietyDetails, varietyArray);
		}else {
			// deprecated
		}
	}
	DK_DestroyVarietyMediaInfo(&varietyList);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

jobject buildJRankInfo(JNIEnv *aEnv, LPRANKINGLISTINFO aCRankInfo) {
	//	LOGV("%s enter", __FUNCTION__);
	if(aCRankInfo == NULL) {
		LOGV("%s : aCRankInfo is null.", __FUNCTION__);
		return NULL;
	}

	jclass rankInfoClass = JniUtil::findClass(aEnv, KClassRankInfo);
	jclass mediaInfoClass = JniUtil::findClass(aEnv,KClassMediaInfo);
	jclass televisionInfoClass = JniUtil::findClass(aEnv, KClassTelevisionInfo);

	jfieldID totalCount = JniUtil::getFieldID(aEnv, KClassRankInfo, "totalCount", KSigInt);
	jfieldID channelID = JniUtil::getFieldID(aEnv, KClassRankInfo, "channelID", KSigInt);
	jfieldID channelName = JniUtil::getFieldID(aEnv, KClassRankInfo, "channelName", KSigStr);
	jfieldID mediaInfos = JniUtil::getFieldID(aEnv, KClassRankInfo, "mediaInfos", KSigMediaInfoArray);

	jobject rankInfoObj = aEnv->AllocObject(rankInfoClass);
	aEnv->SetIntField(rankInfoObj, channelID, aCRankInfo->nChannelId);
	aEnv->SetIntField(rankInfoObj, totalCount, aCRankInfo->nTotalCount);
	aEnv->SetObjectField(rankInfoObj, channelName, aEnv->NewStringUTF(aCRankInfo->szChannelName));
	int mediaCount = aCRankInfo->nMediaInfoArrayDataCount;
	jobjectArray mediaArray = aEnv->NewObjectArray(mediaCount, mediaInfoClass, 0);
	for(int i = 0; i < mediaCount; i++) {
		jobject mediaObj = buildJMediaInfo(aEnv, &aCRankInfo->lpMediaInfo[i]);
		aEnv->SetObjectArrayElement(mediaArray, i, mediaObj);
		aEnv->DeleteLocalRef(mediaObj);
	}
	aEnv->SetObjectField(rankInfoObj, mediaInfos, mediaArray);
	return rankInfoObj;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getRankInfoList(JNIEnv *aEnv, jobject, jobject aMediaInfoQuery, jobject aRankInfoList){
	LOGV("%s enter", __FUNCTION__);
	GETMEDIAINFOLIST mediaList;
	buildCGetMediaInfoList(aEnv, &mediaList, aMediaInfoQuery);
	RANKINGLIST rankList;
	memset(&rankList, 0, sizeof(RANKINGLIST));
	int res = DK_GetRankingListMediaInfor(&mediaList, &rankList);
	if (res >= EMPTY_RESULT){
		jfieldID ranks = JniUtil::getFieldID(aEnv, KClassRankInfoList, "ranks", KSigRankInfoArray);
		jclass rankInfoClass = JniUtil::findClass(aEnv, KClassRankInfo);
		int rankCount = rankList.nTotalCount;
		jobjectArray rankArray =  aEnv->NewObjectArray(rankCount, rankInfoClass, 0);
		for(int i = 0; i < rankCount; ++ i){
			jobject rankObj = buildJRankInfo(aEnv, &rankList.lpRankinglistInfo[i]);
			aEnv->SetObjectArrayElement(rankArray, i, rankObj);
			aEnv->DeleteLocalRef(rankObj);
		}
		aEnv->SetObjectField(aRankInfoList, ranks, rankArray);
	}
	DK_DestroyRankingList(&rankList);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;

}

JNIEXPORT void JNICALL Java_com_duokan_tv_api_DKJniClient_prepareMediaUrlInfo(JNIEnv *, jobject, jint aMediaID, jint aCi, jint aSource){
	LOGV("%s enter", __FUNCTION__);
	DK_PrepareMediaUrlInfo(aMediaID, aCi, aSource);
	LOGV("%s return", __FUNCTION__);

}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getMediaUrlInfo(JNIEnv * aEnv, jobject, jint aMediaID, jint aCi, jint aSource, jobject aMediaUrlList){
	LOGV("%s enter", __FUNCTION__);
	MEDIAURLINFOLIST urlInfoList;
	memset(&urlInfoList, 0, sizeof(urlInfoList));
	LOGV("get url info: media %d , ci = %d", aMediaID, aCi);
	int res = DK_GetMediaUrlInfo(aMediaID, aCi, aSource, &urlInfoList);
	if (res >= EMPTY_RESULT){
		jfieldID videoName = JniUtil::getFieldID(aEnv, KClassMediaUrlInfoList, "videoName", KSigStr);
		jfieldID urlNormal = JniUtil::getFieldID(aEnv, KClassMediaUrlInfoList, "urlNormal", KSigMediaUrlInfoArray);
		jfieldID urlHigh = JniUtil::getFieldID(aEnv, KClassMediaUrlInfoList, "urlHigh", KSigMediaUrlInfoArray);
		jfieldID urlSuper = JniUtil::getFieldID(aEnv, KClassMediaUrlInfoList, "urlSuper", KSigMediaUrlInfoArray);

		aEnv->SetObjectField(aMediaUrlList, videoName,aEnv->NewStringUTF(urlInfoList.szVideoName));

		jclass mediaUrlInfoClass = JniUtil::findClass(aEnv, KClassMediaUrlInfo);
		jfieldID mediaSource = JniUtil::getFieldID(aEnv, KClassMediaUrlInfo, "mediaSource", KSigInt);
		jfieldID startOffset = JniUtil::getFieldID(aEnv, KClassMediaUrlInfo, "startOffset", KSigInt);
		jfieldID endOffset = JniUtil::getFieldID(aEnv, KClassMediaUrlInfo, "endOffset", KSigInt);
		jfieldID isHtml = JniUtil::getFieldID(aEnv, KClassMediaUrlInfo, "isHtml", KSigInt);
		jfieldID mediaUrl = JniUtil::getFieldID(aEnv, KClassMediaUrlInfo, "mediaUrl", KSigStr);
		//url Normal
//		LOGV("%s process Url Normal", __FUNCTION__);
		jobjectArray normalArray = aEnv->NewObjectArray(URL_MAX_COUNT, mediaUrlInfoClass, 0);
		for(int i = 0; i < URL_MAX_COUNT; ++ i){
			jobject mediaUrlInfoObj = aEnv->AllocObject(mediaUrlInfoClass);
			LOGV("get url info: normal mediaSource %d ", urlInfoList.urlNormal[i].nMediaSource);
			aEnv->SetIntField(mediaUrlInfoObj, mediaSource, urlInfoList.urlNormal[i].nMediaSource);
			aEnv->SetIntField(mediaUrlInfoObj, startOffset, urlInfoList.urlNormal[i].nStartOffset);
			aEnv->SetIntField(mediaUrlInfoObj, endOffset, urlInfoList.urlNormal[i].nEndOffset);
			aEnv->SetIntField(mediaUrlInfoObj, isHtml, urlInfoList.urlNormal[i].nIsHtml);
//			LOGV("get url info: normal url %s ", urlInfoList.urlNormal[i].szMediaUrl);
			aEnv->SetObjectField(mediaUrlInfoObj, mediaUrl,aEnv->NewStringUTF(urlInfoList.urlNormal[i].szMediaUrl));
			aEnv->SetObjectArrayElement(normalArray, i, mediaUrlInfoObj);
		}
		aEnv->SetObjectField(aMediaUrlList, urlNormal, normalArray);


		//url High
//		LOGV("%s process Url High", __FUNCTION__);
		jobjectArray highArray = aEnv->NewObjectArray(URL_MAX_COUNT, mediaUrlInfoClass, 0);
		for(int i = 0; i < URL_MAX_COUNT; ++ i){
			jobject mediaUrlInfoObj = aEnv->AllocObject(mediaUrlInfoClass);
			LOGV("get url info: high mediaSource %d ", urlInfoList.urlHigh[i].nMediaSource);
			aEnv->SetIntField(mediaUrlInfoObj, mediaSource, urlInfoList.urlHigh[i].nMediaSource);
			aEnv->SetIntField(mediaUrlInfoObj, startOffset, urlInfoList.urlHigh[i].nStartOffset);
			aEnv->SetIntField(mediaUrlInfoObj, endOffset, urlInfoList.urlHigh[i].nEndOffset);
			aEnv->SetIntField(mediaUrlInfoObj, isHtml, urlInfoList.urlHigh[i].nIsHtml);
//			LOGV("get url info: high url %s ", urlInfoList.urlHigh[i].szMediaUrl);
			aEnv->SetObjectField(mediaUrlInfoObj, mediaUrl,aEnv->NewStringUTF(urlInfoList.urlHigh[i].szMediaUrl));
			aEnv->SetObjectArrayElement(highArray, i, mediaUrlInfoObj);
		}
		aEnv->SetObjectField(aMediaUrlList, urlHigh, highArray);


		//url Super
//		LOGV("%s process Url Super", __FUNCTION__);
		jobjectArray superArray = aEnv->NewObjectArray(URL_MAX_COUNT, mediaUrlInfoClass, 0);
		for(int i = 0; i < URL_MAX_COUNT; ++ i){
			jobject mediaUrlInfoObj = aEnv->AllocObject(mediaUrlInfoClass);
			LOGV("get url info: super mediaSource %d ", urlInfoList.urlSuper[i].nMediaSource);
			aEnv->SetIntField(mediaUrlInfoObj, mediaSource, urlInfoList.urlSuper[i].nMediaSource);
			aEnv->SetIntField(mediaUrlInfoObj, startOffset, urlInfoList.urlSuper[i].nStartOffset);
			aEnv->SetIntField(mediaUrlInfoObj, endOffset, urlInfoList.urlSuper[i].nEndOffset);
			aEnv->SetIntField(mediaUrlInfoObj, isHtml, urlInfoList.urlSuper[i].nIsHtml);
//			LOGV("get url info: super url %s ", urlInfoList.urlSuper[i].szMediaUrl);
			aEnv->SetObjectField(mediaUrlInfoObj, mediaUrl,aEnv->NewStringUTF(urlInfoList.urlSuper[i].szMediaUrl));
			aEnv->SetObjectArrayElement(superArray, i, mediaUrlInfoObj);
		}
		aEnv->SetObjectField(aMediaUrlList, urlSuper, superArray);
	}
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

JNIEXPORT void JNICALL Java_com_duokan_tv_api_DKJniClient_setWaitLoginParam(JNIEnv *, jobject, jint aWaitLogin, jint aTimout){
	DK_SetWaitLoginParam(aWaitLogin, aTimout);
}

JNIEXPORT int JNICALL Java_com_duokan_tv_api_DKJniClient_nativeGetAuthToken(JNIEnv *aEnv, jobject, jobject aTokenInfo){
	TOKENINFO aCTokenInfo;
	memset(&aCTokenInfo, 0, sizeof(aCTokenInfo));
	int res = DK_GenerateDeviceSecurity(&aCTokenInfo);
	if (res >= EMPTY_RESULT){
		jfieldID key = JniUtil::getFieldID(aEnv, KClassTokenInfo, "key", KSigStr);
		jfieldID token = JniUtil::getFieldID(aEnv, KClassTokenInfo, "token", KSigStr);
		LOGV("%s , %s" , aCTokenInfo.szKey,  aCTokenInfo.szToken);
		aEnv->SetObjectField(aTokenInfo, key, aEnv->NewStringUTF(aCTokenInfo.szKey));
		aEnv->SetObjectField(aTokenInfo, token, aEnv->NewStringUTF(aCTokenInfo.szToken));
	}
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

void getCStrFromJField(JNIEnv *aEnv, char* szArray, jobject aObject, jfieldID aFieldID){
	jstring JStr = (jstring)aEnv->GetObjectField(aObject, aFieldID);
	const char* Cstr = aEnv->GetStringUTFChars(JStr, 0);
	strcpy(szArray, Cstr);
	aEnv->ReleaseStringUTFChars(JStr, Cstr);
}

int buildCMediaInfo(JNIEnv *aEnv, LPMEDIAINFO aCMediaInfo, jobject aJMediaInfo){
	LOGV("%s enter", __FUNCTION__);
	jfieldID nMediaID = JniUtil::getFieldID(aEnv, KClassMediaInfo, "mediaID", KSigInt);
	jfieldID nFlag = JniUtil::getFieldID(aEnv, KClassMediaInfo, "flag", KSigInt);
	jfieldID nResolution = JniUtil::getFieldID(aEnv, KClassMediaInfo, "resolution", KSigInt);
	jfieldID szCategory = JniUtil::getFieldID(aEnv, KClassMediaInfo, "category", KSigStr);
	jfieldID szMediaName = JniUtil::getFieldID(aEnv, KClassMediaInfo, "mediaName", KSigStr);
	jfieldID szDirector = JniUtil::getFieldID(aEnv, KClassMediaInfo, "director", KSigStr);
	jfieldID szActors = JniUtil::getFieldID(aEnv, KClassMediaInfo, "actors", KSigStr);
	jfieldID szTags = JniUtil::getFieldID(aEnv, KClassMediaInfo, "tags", KSigStr);
	jfieldID score = JniUtil::getFieldID(aEnv, KClassMediaInfo, "score", KSigFloat);
	jfieldID setNow = JniUtil::getFieldID(aEnv, KClassMediaInfo, "setNow", KSigInt);
	jfieldID playLength = JniUtil::getFieldID(aEnv, KClassMediaInfo, "playLength", KSigInt);
	jfieldID area = JniUtil::getFieldID(aEnv, KClassMediaInfo, "area", KSigStr);
	jfieldID issueDate = JniUtil::getFieldID(aEnv, KClassMediaInfo, "issueDate", KSigStr);
	jfieldID lastIssueDate = JniUtil::getFieldID(aEnv, KClassMediaInfo, "lastIssueDate", KSigStr);
	jfieldID setCount = JniUtil::getFieldID(aEnv, KClassMediaInfo, "setCount", KSigInt);
	jfieldID playCount = JniUtil::getFieldID(aEnv, KClassMediaInfo, "playCount", KSigInt);
	jfieldID scoreCount = JniUtil::getFieldID(aEnv, KClassMediaInfo, "scoreCount", KSigInt);
	jfieldID mediaSetType = JniUtil::getFieldID(aEnv, KClassMediaInfo, "mediaSetType", KSigInt);
	
	jfieldID szSmallImageURL = JniUtil::getFieldID(aEnv, KClassMediaInfo, "smallImageURL", KSigImageUrlInfo);
	jfieldID szMd5 = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "md5", KSigStr);
	jfieldID szImageUrl = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "imageUrl", KSigStr);

	aCMediaInfo->nMediaID = aEnv->GetIntField(aJMediaInfo, nMediaID);
	aCMediaInfo->nFlag = aEnv->GetIntField(aJMediaInfo, nFlag);
	aCMediaInfo->nResolution = aEnv->GetIntField(aJMediaInfo, nResolution);
	getCStrFromJField(aEnv, aCMediaInfo->szCategory, aJMediaInfo, szCategory);
	getCStrFromJField(aEnv, aCMediaInfo->szMediaName, aJMediaInfo, szMediaName);
	getCStrFromJField(aEnv, aCMediaInfo->szDirector, aJMediaInfo, szDirector);
	getCStrFromJField(aEnv, aCMediaInfo->szActors, aJMediaInfo, szActors);
	jobject imageUrlObj = aEnv->GetObjectField(aJMediaInfo, szSmallImageURL);
	if( imageUrlObj != NULL)
	{
		getCStrFromJField(aEnv, aCMediaInfo->poster.szURL, imageUrlObj, szImageUrl);
		getCStrFromJField(aEnv, aCMediaInfo->poster.szMd5, imageUrlObj, szMd5);
	}
	getCStrFromJField(aEnv, aCMediaInfo->szTags, aJMediaInfo, szTags);
	aCMediaInfo->score = aEnv->GetFloatField(aJMediaInfo, score);
	aCMediaInfo->nSetNow = aEnv->GetIntField(aJMediaInfo, setNow);
	aCMediaInfo->playlength = aEnv->GetIntField(aJMediaInfo, playLength);
	getCStrFromJField(aEnv, aCMediaInfo->szArea, aJMediaInfo, area);
	getCStrFromJField(aEnv, aCMediaInfo->szIssuedate, aJMediaInfo, issueDate);
	getCStrFromJField(aEnv, aCMediaInfo->szLatestissuedate, aJMediaInfo, lastIssueDate);
	aCMediaInfo->nSetCount = aEnv->GetIntField(aJMediaInfo, setCount);
	aCMediaInfo->nPlayCount = aEnv->GetIntField(aJMediaInfo, playCount);
	aCMediaInfo->nScoreCount = aEnv->GetIntField(aJMediaInfo, scoreCount);
	aCMediaInfo->nIsMultipleSet = aEnv->GetIntField(aJMediaInfo, mediaSetType);
	LOGV("%s exit", __FUNCTION__);
	return 0;
}

int buildCPlayHistory(JNIEnv *aEnv, LPPLAYHISTORY aCPlayHistory, jobject aJPlayHistory){
	LOGV("%s enter", __FUNCTION__);
	jclass playHistoryClass = JniUtil::findClass(aEnv, KClassPlayHistory);
	jclass mediaInfoClass = JniUtil::findClass(aEnv, KClassMediaInfo);
	jclass personInfoClass = JniUtil::findClass(aEnv, KClassPersonInfo);
	jclass imageUrlInfoClass = JniUtil::findClass(aEnv, KClassImageUrlInfo);

	jfieldID mediaInfo = JniUtil::getFieldID(aEnv, KClassPlayHistory, "mediaInfo", KSigMediaInfo);
	jfieldID personInfo = JniUtil::getFieldID(aEnv, KClassPlayHistory, "personInfo", KSigPersonInfo);
	jfieldID mediaCi = JniUtil::getFieldID(aEnv, KClassPlayHistory, "mediaCi", KSigInt);
	jfieldID mediaSource = JniUtil::getFieldID(aEnv, KClassPlayHistory, "mediaSource", KSigInt);
	jfieldID videoName = JniUtil::getFieldID(aEnv, KClassPlayHistory, "videoName", KSigStr);
	jfieldID percent = JniUtil::getFieldID(aEnv, KClassPlayHistory, "percent", KSigFloat);
	jfieldID playDate = JniUtil::getFieldID(aEnv, KClassPlayHistory, "playDate", KSigStr);
	jfieldID scoreCount = JniUtil::getFieldID(aEnv, KClassPlayHistory, "scoreCount", KSigInt);
	jfieldID score = JniUtil::getFieldID(aEnv, KClassPlayHistory, "score", KSigFloat);
	jfieldID setNow = JniUtil::getFieldID(aEnv, KClassPlayHistory, "setNow", KSigInt);
	jfieldID duration = JniUtil::getFieldID(aEnv, KClassPlayHistory, "duration", KSigInt);
	jfieldID mediaType = JniUtil::getFieldID(aEnv, KClassPlayHistory, "mediaType", KSigInt);
	jfieldID totalSet = JniUtil::getFieldID(aEnv, KClassPlayHistory, "totalSet", KSigInt);
	jfieldID playSeconds = JniUtil::getFieldID(aEnv, KClassPlayHistory, "playSeconds", KSigLong);

	//load meidainfo
	jobject mediaInfoObj = aEnv->GetObjectField(aJPlayHistory, mediaInfo);
	buildCMediaInfo(aEnv, &aCPlayHistory->mediaInfo, mediaInfoObj);

	//load others
	aCPlayHistory->nMediaCi = aEnv->GetIntField(aJPlayHistory, mediaCi);
	aCPlayHistory->nMediaSource = aEnv->GetIntField(aJPlayHistory, mediaSource);
	getCStrFromJField(aEnv, aCPlayHistory->szVideoName, aJPlayHistory, videoName);
	aCPlayHistory->fPercent = aEnv->GetFloatField(aJPlayHistory, percent);
	getCStrFromJField(aEnv, aCPlayHistory->szPlaydate, aJPlayHistory, playDate);
	aCPlayHistory->nScoreCount = aEnv->GetIntField(aJPlayHistory, scoreCount);
	aCPlayHistory->score = aEnv->GetFloatField(aJPlayHistory, score);
	aCPlayHistory->nSetNow = aEnv->GetIntField(aJPlayHistory, setNow);
	aCPlayHistory->nDuration = aEnv->GetIntField(aJPlayHistory, duration);
	aCPlayHistory->nMediaType = aEnv->GetIntField(aJPlayHistory, mediaType);
	aCPlayHistory->nTotalSet = aEnv->GetIntField(aJPlayHistory, totalSet);
	aCPlayHistory->lPlayseconds = aEnv->GetLongField(aJPlayHistory, playSeconds);

	//load personinfo
	if(aCPlayHistory->nMediaType != 0){ //aleady asign above
		jfieldID country = JniUtil::getFieldID(aEnv, KClassPersonInfo, "country", KSigStr);
		jfieldID nameCn = JniUtil::getFieldID(aEnv, KClassPersonInfo, "nameCn", KSigStr);
		jfieldID nameEn = JniUtil::getFieldID(aEnv, KClassPersonInfo, "nameEn", KSigStr);
		jfieldID alias = JniUtil::getFieldID(aEnv, KClassPersonInfo, "alias", KSigStr);
		jfieldID bigImageUrl = JniUtil::getFieldID(aEnv, KClassPersonInfo, "bigImageUrl", KSigImageUrlInfo);
		jfieldID cv = JniUtil::getFieldID(aEnv, KClassPersonInfo, "cv", KSigStr);
		jfieldID md5 = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "md5", KSigStr);
		jfieldID imageUrl = JniUtil::getFieldID(aEnv, KClassImageUrlInfo, "imageUrl", KSigStr);
		jobject personInfoObj = aEnv->GetObjectField(aJPlayHistory, personInfo);
		jobject imageUrlInfoObj = aEnv->GetObjectField(personInfoObj, personInfo);

		aCPlayHistory->lpPersonInfor = (LPPERSONINFOR)malloc(sizeof(*aCPlayHistory->lpPersonInfor));
		getCStrFromJField(aEnv, aCPlayHistory->lpPersonInfor->szCountry, personInfoObj, country);
		getCStrFromJField(aEnv, aCPlayHistory->lpPersonInfor->szcName, personInfoObj, nameCn);
		getCStrFromJField(aEnv, aCPlayHistory->lpPersonInfor->szeName, personInfoObj, nameEn);
		getCStrFromJField(aEnv, aCPlayHistory->lpPersonInfor->szAlias, personInfoObj, alias);
		getCStrFromJField(aEnv, aCPlayHistory->lpPersonInfor->poster.szMd5, imageUrlInfoObj, md5);
		getCStrFromJField(aEnv, aCPlayHistory->lpPersonInfor->poster.szURL, imageUrlInfoObj, imageUrl);
		getCStrFromJField(aEnv, aCPlayHistory->lpPersonInfor->szCv, personInfoObj, cv);
	}
	else{
		aCPlayHistory->lpPersonInfor = NULL;
	}
	LOGV("%s exit", __FUNCTION__);
	return 0;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_setPlayHistory(JNIEnv *aEnv, jobject, jobject aPlayHistory, jintArray aLostHistory){
	LOGV("%s enter", __FUNCTION__);
	PLAYHISTORY playHistory;
	memset(&playHistory, 0, sizeof(PLAYHISTORY));
	buildCPlayHistory(aEnv,&playHistory, aPlayHistory);

	int lostHistory[2] = {-1, -1};
	int res = DK_SetPlayHistory(&playHistory, &lostHistory[0], &lostHistory[1]);
	if(lostHistory[0] != -1 && lostHistory[0] != -1){//history overwrite
		aEnv->SetIntArrayRegion(aLostHistory, 0, 2,  lostHistory);
	}
	free(playHistory.lpPersonInfor);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

int buildCUserDataParam(JNIEnv *aEnv, LPUSERDATAPARAM aCUserDataParam, jobject aJUserDataParam){
	jclass userDataParamClass = aEnv->FindClass(KClassUserDataParam);
	jfieldID channelID = JniUtil::getFieldID(aEnv, KClassUserDataParam, "channelID", KSigInt);
	jfieldID pageNo = JniUtil::getFieldID(aEnv, KClassUserDataParam, "pageNo", KSigInt);
	jfieldID pageSize = JniUtil::getFieldID(aEnv, KClassUserDataParam, "pageSize", KSigInt);
	jfieldID latestDays = JniUtil::getFieldID(aEnv, KClassUserDataParam, "latestDays", KSigInt);

	aCUserDataParam->nChannelID =  aEnv->GetIntField(aJUserDataParam, channelID);
	aCUserDataParam->nPageNo =  aEnv->GetIntField(aJUserDataParam, pageNo);
	aCUserDataParam->nPageSize =  aEnv->GetIntField(aJUserDataParam, pageSize);
	aCUserDataParam->nLatestDays =  aEnv->GetIntField(aJUserDataParam, latestDays);
	return 0;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getPlayHistory(JNIEnv *aEnv, jobject, jobject aInputData, jobject aPlayHistoryList){
	LOGV("%s enter", __FUNCTION__);
	USERDATAPARAM userParam;
	buildCUserDataParam(aEnv,&userParam, aInputData);
	PLAYHISTORYLIST historyList;
	memset(&historyList, 0, sizeof(historyList));
	int res = DK_GetPlayHistory(&userParam, &historyList);
	if (res > FAILED_ERROR){
		jobjectArray args = NULL;
		jclass playHistoryListClass = JniUtil::findClass(aEnv, KClassPlayHistoryList);
		jfieldID lpPlayHistory = JniUtil::getFieldID(aEnv, KClassPlayHistoryList, "playHistoryInfos", KSigPlayHistoryArray);

		jclass playHistoryClass = JniUtil::findClass(aEnv, KClassPlayHistory);
		jsize itemCnt = historyList.nDataCount;
		jobjectArray historyArray =  aEnv->NewObjectArray(itemCnt, playHistoryClass, 0);

		jfieldID mediaInfo = JniUtil::getFieldID(aEnv, KClassPlayHistory, "mediaInfo", KSigMediaInfo);
		jfieldID personInfo = JniUtil::getFieldID(aEnv, KClassPlayHistory, "personInfo", KSigPersonInfo);
		jfieldID mediaCi = JniUtil::getFieldID(aEnv, KClassPlayHistory, "mediaCi", KSigInt);
		jfieldID mediaSource = JniUtil::getFieldID(aEnv, KClassPlayHistory, "mediaSource", KSigInt);
		jfieldID videoName = JniUtil::getFieldID(aEnv, KClassPlayHistory, "videoName", KSigStr);
		jfieldID percent = JniUtil::getFieldID(aEnv, KClassPlayHistory, "percent", KSigFloat);
		jfieldID playDate = JniUtil::getFieldID(aEnv, KClassPlayHistory, "playDate", KSigStr);
		jfieldID scoreCount = JniUtil::getFieldID(aEnv, KClassPlayHistory, "scoreCount", KSigInt);
		jfieldID score = JniUtil::getFieldID(aEnv, KClassPlayHistory, "score", KSigFloat);
		jfieldID setNow = JniUtil::getFieldID(aEnv, KClassPlayHistory, "setNow", KSigInt);
		jfieldID duration = JniUtil::getFieldID(aEnv, KClassPlayHistory, "duration", KSigInt);
		jfieldID mediaType = JniUtil::getFieldID(aEnv, KClassPlayHistory, "mediaType", KSigInt);
		jfieldID totalSet = JniUtil::getFieldID(aEnv, KClassPlayHistory, "totalSet", KSigInt);
		jfieldID playSeconds = JniUtil::getFieldID(aEnv, KClassPlayHistory, "playSeconds", KSigLong);

		jclass mediaInfoClass = JniUtil::findClass(aEnv, KClassMediaInfo);
		jclass personInfoClass = JniUtil::findClass(aEnv, KClassPersonInfo);

		for(int i = 0; i < itemCnt; ++ i){
			jobject playHistoryObj = aEnv->AllocObject(playHistoryClass);

			//load meidainfo
			jobject mediaInfoObj = buildJMediaInfo(aEnv, &historyList.lpPlayHistory[i].mediaInfo);
			aEnv->SetObjectField(playHistoryObj, mediaInfo, mediaInfoObj);
			//load others
			aEnv->SetIntField(playHistoryObj, mediaCi, historyList.lpPlayHistory[i].nMediaCi);
			aEnv->SetIntField(playHistoryObj, mediaSource, historyList.lpPlayHistory[i].nMediaSource);
			aEnv->SetObjectField(playHistoryObj, videoName, aEnv->NewStringUTF(historyList.lpPlayHistory[i].szVideoName));
			aEnv->SetFloatField(playHistoryObj, percent, historyList.lpPlayHistory[i].fPercent);
			aEnv->SetObjectField(playHistoryObj, playDate, aEnv->NewStringUTF(historyList.lpPlayHistory[i].szPlaydate));
			aEnv->SetIntField(playHistoryObj, scoreCount, historyList.lpPlayHistory[i].nScoreCount);
			aEnv->SetFloatField(playHistoryObj, score, historyList.lpPlayHistory[i].score);
			aEnv->SetIntField(playHistoryObj, setNow, historyList.lpPlayHistory[i].nSetNow);
			aEnv->SetIntField(playHistoryObj, duration, historyList.lpPlayHistory[i].nDuration);
			aEnv->SetIntField(playHistoryObj, mediaType, historyList.lpPlayHistory[i].nMediaType);
			aEnv->SetIntField(playHistoryObj, totalSet, historyList.lpPlayHistory[i].nTotalSet);
			aEnv->SetLongField(playHistoryObj, playSeconds, historyList.lpPlayHistory[i].lPlayseconds);
			//load personinfo
			if(historyList.lpPlayHistory[i].nMediaType != 0){ //aleady asign above
				jobject personInfoObj = buildJPersonInfo(aEnv, historyList.lpPlayHistory[i].lpPersonInfor);
				if(personInfoObj != NULL){
					aEnv->SetObjectField(playHistoryObj, personInfo, personInfoObj);
					aEnv->DeleteLocalRef(personInfoObj);
				}
			}
			else{
				aEnv->SetObjectField(playHistoryObj, personInfo, NULL);
			}
			aEnv->SetObjectArrayElement(historyArray, i, playHistoryObj);
			aEnv->DeleteLocalRef(mediaInfoObj);
			aEnv->DeleteLocalRef(playHistoryObj);
		}
		aEnv->SetObjectField(aPlayHistoryList, lpPlayHistory, historyArray);
	}
	DK_DestroyPlayHistory(&historyList);
	LOGV("%s return err %d", __FUNCTION__, res);
	return res;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DKJniClient_deletePlayHistory(JNIEnv *aEnv, jobject, jobject aPlayHistory){
	LOGV("%s enter", __FUNCTION__);
	PLAYHISTORY playHistory;
	memset(&playHistory, 0, sizeof(PLAYHISTORY));
	buildCPlayHistory(aEnv, &playHistory, aPlayHistory);
	int res = DK_DeletePlayHistory(&playHistory);
	free(playHistory.lpPersonInfor);
	LOGV("%s return err %d", __FUNCTION__, res);
	return res;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DKJniClient_getMaxHistory(JNIEnv *, jobject){
	return DK_GetMaxHistory();
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DKJniClient_getMediaBatch(JNIEnv *aEnv, jobject, jobject aMediaInfoQuery, jobject aMediaInfoList){
	LOGV("%s enter", __FUNCTION__);
	GETMEDIAINFOLIST mediaList;
	buildCGetMediaInfoList(aEnv, &mediaList, aMediaInfoQuery);
	MEDIAINFOLIST infoList;
	memset(&infoList, 0, sizeof(infoList));

	int requestId = 0;
	int res = DK_GetMediaBatch(&mediaList, &infoList, &requestId);
	if (res >= EMPTY_RESULT){
		buildJMediaInfoList(aEnv, &infoList, aMediaInfoList);
	}
	DK_DestroyMediaInfoList(&infoList);
	LOGV("%s return err %d", __FUNCTION__, res);
	return res;
}

int buildJPlayUrlMediaSourceInfo(JNIEnv *aEnv, LPMEDIASOURCEINFO lpMediaSourceInfo, jobject aPlayUrlMediaSourceInfo)
{
	return 0;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getPlayUrlMediaSourceInfo(JNIEnv *aEnv, jobject, jstring aPlayUrl, jint aSource, 
                                                jobject aPlayUrlMediaSourceInfoObj, jstring aStatisticInfo)
{
	LOGV("%s enter", __FUNCTION__);
	MEDIASOURCEINFO  mediaSourceInfo;
	memset(&mediaSourceInfo, 0, sizeof(mediaSourceInfo));
	string szPalyUrl;
	JniUtil::getCStrFromJStr(aEnv, szPalyUrl, aPlayUrl);
	LOGV(" play url %s ", szPalyUrl.c_str());
	string szStatisticInfo;
	JniUtil::getCStrFromJStr(aEnv, szStatisticInfo, aStatisticInfo);
	LOGV("statisticInfo %s  %d", szStatisticInfo.c_str(), szStatisticInfo.size());
	int res = DK_GetMediaInfoByPlayUrl(szPalyUrl.c_str(), aSource, &mediaSourceInfo, szStatisticInfo.c_str());
	LOGV("szPalyUrl %s", szPalyUrl.c_str());
//	LOGV("source %d", aSource);
	if(res >= EMPTY_RESULT)
	{
		jclass playUrlMediaSourceInfoClass = JniUtil::findClass(aEnv, KClassPlayUrlMediaSourceInfo);
		
		jfieldID urlMediaSource = JniUtil::getFieldID(aEnv, KClassPlayUrlMediaSourceInfo, "urlMediaSource", KSigInt);
		jfieldID mediaBox = JniUtil::getFieldID(aEnv, KClassPlayUrlMediaSourceInfo, "inBox", KSigInt);
		jfieldID mediaId = JniUtil::getFieldID(aEnv, KClassPlayUrlMediaSourceInfo, "mediaId", KSigInt);
		jfieldID mediaCi = JniUtil::getFieldID(aEnv, KClassPlayUrlMediaSourceInfo, "mediaCi", KSigInt);
		jfieldID mediaQuality = JniUtil::getFieldID(aEnv, KClassPlayUrlMediaSourceInfo, "quality", KSigInt);
		jfieldID mediaName = JniUtil::getFieldID(aEnv, KClassPlayUrlMediaSourceInfo, "mediaName", KSigStr);
		jfieldID mediaSetType = JniUtil::getFieldID(aEnv, KClassPlayUrlMediaSourceInfo, "mediaSetType", KSigInt);
		jfieldID sourceParam = JniUtil::getFieldID(aEnv, KClassPlayUrlMediaSourceInfo, "sourceParam", KSigStr);
		jfieldID issueDate = JniUtil::getFieldID(aEnv, KClassPlayUrlMediaSourceInfo, "issueDate", KSigStr);
		
		aEnv->SetIntField(aPlayUrlMediaSourceInfoObj, urlMediaSource, mediaSourceInfo.nSource);
		aEnv->SetIntField(aPlayUrlMediaSourceInfoObj, mediaBox, mediaSourceInfo.nBox);
		aEnv->SetIntField(aPlayUrlMediaSourceInfoObj, mediaId, mediaSourceInfo.nMeaiaId);
		aEnv->SetIntField(aPlayUrlMediaSourceInfoObj, mediaCi, mediaSourceInfo.nCi);
		aEnv->SetIntField(aPlayUrlMediaSourceInfoObj, mediaQuality, mediaSourceInfo.nResolution);
		aEnv->SetObjectField(aPlayUrlMediaSourceInfoObj, mediaName, aEnv->NewStringUTF(mediaSourceInfo.szMediaName));
		aEnv->SetIntField(aPlayUrlMediaSourceInfoObj, mediaSetType, mediaSourceInfo.nIsMultipleSet);
		aEnv->SetObjectField(aPlayUrlMediaSourceInfoObj, sourceParam, aEnv->NewStringUTF(mediaSourceInfo.szSourceParam));
		aEnv->SetObjectField(aPlayUrlMediaSourceInfoObj, issueDate, aEnv->NewStringUTF(mediaSourceInfo.szIssuseDate));
	//	LOGV("getPlayUrlMediaSourceInfo sourceid : %d", mediaSourceInfo.nSource);
	//	LOGV("getPlayUrlMediaSourceInfo lpQiYiInfo : %d", mediaSourceInfo.lpQiYiInfo);
	//	LOGV("getPlayUrlMediaSourceInfo lpSoHuInfo : %d", mediaSourceInfo.lpSoHuInfo);
	}
    DK_DestroyMediaInfoByPlayUrl(&mediaSourceInfo);
	LOGV("%s exit %d", __FUNCTION__, res);
	return res;
}

int buildCBookmark(JNIEnv *aEnv, LPBOOKMARK aCBookmark, jobject aMyFavoriteItemInfo){
   jfieldID mediaInfo = JniUtil::getFieldID(aEnv, KClassMyFavoriteItemInfo, "mediaInfo", KSigMediaInfo);
   jfieldID szDate = JniUtil::getFieldID(aEnv, KClassMyFavoriteItemInfo, "date", KSigStr);
   jfieldID ltime = JniUtil::getFieldID(aEnv, KClassMyFavoriteItemInfo, "time", KSigLong);
   jfieldID lcreateTime = JniUtil::getFieldID(aEnv, KClassMyFavoriteItemInfo, "createTime", KSigLong);
   
   jobject jMediaInfo = aEnv->GetObjectField(aMyFavoriteItemInfo, mediaInfo);
   buildCMediaInfo(aEnv, &aCBookmark->mediainfo, jMediaInfo);
   getCStrFromJField(aEnv, aCBookmark->szDate, aMyFavoriteItemInfo, szDate);
   aCBookmark->ltime = aEnv->GetLongField(aMyFavoriteItemInfo, ltime);
   aCBookmark->lCreateTime = aEnv->GetLongField(aMyFavoriteItemInfo, lcreateTime);
   return 0;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_setMyFavoriteItem(JNIEnv *aEnv, jobject, jobject aMyFavoriteItemInfo){
   LOGV("%s enter", __FUNCTION__);
   BOOKMARK bookmark;
   memset(&bookmark, 0, sizeof(BOOKMARK));
   buildCBookmark(aEnv, &bookmark, aMyFavoriteItemInfo);
   string userParam;
   jfieldID jUserParamID = JniUtil::getFieldID(aEnv, KClassMyFavoriteItemInfo, "userParam", KSigStr);
   jstring aUserParam = (jstring)aEnv->GetObjectField(aMyFavoriteItemInfo, jUserParamID);
   JniUtil::getCStrFromJStr(aEnv, userParam, aUserParam);
   bookmark.lpszPrivateData = (char *)userParam.c_str();
   int res = DK_SetBookMark(&bookmark);
   LOGV("%s return %d", __FUNCTION__, res);
   return res;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getMyFavoriteItemInfoList(JNIEnv *aEnv, jobject, jobject aInputData, jobject aMyFavoriteItemInfoList){
   LOGV("%s enter", __FUNCTION__);
   USERDATAPARAM userParam;
   buildCUserDataParam(aEnv,&userParam, aInputData);
   BOOKMARKLIST bookmarkList;
   memset(&bookmarkList, 0, sizeof(bookmarkList));
   int res = DK_GetBookMarksDetails(&userParam, &bookmarkList);
   if (res > FAILED_ERROR){
       jfieldID myFavoriteItemInfos = JniUtil::getFieldID(aEnv, KClassMyFavoriteItemInfoList, "myFavoriteItemInfos", KSigMyFavoriteItemInfoArray);
	   jfieldID myFavoriteItemInfoCount = JniUtil::getFieldID(aEnv, KClassMyFavoriteItemInfoList, "count", KSigInt);
       jclass myFavoriteItemInfoClass = JniUtil::findClass(aEnv, KClassMyFavoriteItemInfo);
       jsize itemCnt = bookmarkList.nDataCount;
	   LOGV("itemCnt = %d", itemCnt);
       jobjectArray myFavoriteItemInfoArray =  aEnv->NewObjectArray(itemCnt, myFavoriteItemInfoClass, 0);
       jfieldID mediainfo = JniUtil::getFieldID(aEnv, KClassMyFavoriteItemInfo, "mediaInfo", KSigMediaInfo);
       jfieldID date = JniUtil::getFieldID(aEnv, KClassMyFavoriteItemInfo, "date", KSigStr);
       jfieldID ltime = JniUtil::getFieldID(aEnv, KClassMyFavoriteItemInfo, "time", KSigLong);
	   jfieldID lcreateTime = JniUtil::getFieldID(aEnv, KClassMyFavoriteItemInfo, "createTime", KSigLong);
	   jfieldID userParam = JniUtil::getFieldID(aEnv, KClassMyFavoriteItemInfo, "userParam", KSigStr);
	   
       jclass mediaInfoClass = JniUtil::findClass(aEnv, KClassMediaInfo);

       for(int i = 0; i < itemCnt; ++ i){
           jobject myFavoriteItemInfoObj = aEnv->AllocObject(myFavoriteItemInfoClass);
           jobject mediaInfoObj = buildJMediaInfo(aEnv, &bookmarkList.lpBookMarks[i].mediainfo);
           aEnv->SetObjectField(myFavoriteItemInfoObj, mediainfo, mediaInfoObj);
		   jstring jszDate = aEnv->NewStringUTF(bookmarkList.lpBookMarks[i].szDate);
           aEnv->SetObjectField(myFavoriteItemInfoObj, date, jszDate);
		   aEnv->DeleteLocalRef(jszDate);
           aEnv->SetLongField(myFavoriteItemInfoObj, ltime, bookmarkList.lpBookMarks[i].ltime);
		   aEnv->SetLongField(myFavoriteItemInfoObj, lcreateTime, bookmarkList.lpBookMarks[i].lCreateTime);
		   jstring jszUserParam = aEnv->NewStringUTF(bookmarkList.lpBookMarks[i].lpszPrivateData);
		   aEnv->SetObjectField(myFavoriteItemInfoObj, userParam, jszUserParam);
		   aEnv->DeleteLocalRef(jszUserParam);
           aEnv->SetObjectArrayElement(myFavoriteItemInfoArray, i, myFavoriteItemInfoObj);
		   aEnv->DeleteLocalRef(mediaInfoObj);
		   aEnv->DeleteLocalRef(myFavoriteItemInfoObj);
       }
       aEnv->SetObjectField(aMyFavoriteItemInfoList, myFavoriteItemInfos, myFavoriteItemInfoArray);
	   aEnv->SetIntField(aMyFavoriteItemInfoList, myFavoriteItemInfoCount, itemCnt);
	   aEnv->DeleteLocalRef(myFavoriteItemInfoArray);
   }
   DK_DestroyBookMarksDetails(&bookmarkList);
   LOGV("%s return %d", __FUNCTION__, res);
   return res;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_deleteMyFavoriteItem(JNIEnv *aEnv, jobject, jint aMediaID){
   LOGV("%s enter", __FUNCTION__);
   int res = DK_DeleteBookMark(aMediaID);
   LOGV("%s return %d", __FUNCTION__, res);
   return res;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_deleteMyFavoriteItemEx(JNIEnv *aEnv, jobject, jintArray aMediaIDArray){
   LOGV("%s enter", __FUNCTION__);
   int res = 0;
   int count = aEnv->GetArrayLength(aMediaIDArray);
   LOGV("count = %d", count);
   int* deleteMediaIds = aEnv->GetIntArrayElements(aMediaIDArray, 0);
   if( count > 0)
   {
	  res = DK_DeleteBookMark2(deleteMediaIds, count);  
	  LOGV("%s return %d", __FUNCTION__, res);
   }
   aEnv->ReleaseIntArrayElements(aMediaIDArray, deleteMediaIds, 0);
   return res;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_setPlayInfo(JNIEnv *aEnv, jobject, jstring aStatisticInfo) {
	LOGV("%s enter", __FUNCTION__);
	string szStatisticInfo;
	JniUtil::getCStrFromJStr(aEnv, szStatisticInfo, aStatisticInfo);
	const char* pSetPlayInfo = szStatisticInfo.c_str();
	int res = DK_SetPlayInfo(pSetPlayInfo);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_uploadIMEIBootInfo(JNIEnv *aEnv, jobject, jstring aIMEI, jstring aUniqueIdentifier,
		jstring aDeviceName, jstring aNetworkType) {
	LOGV("%s enter", __FUNCTION__);
	string szImei;
	JniUtil::getCStrFromJStr(aEnv, szImei, aIMEI);
	const char* pIMEI = szImei.c_str();
	string szUniqueIdentifier;
	JniUtil::getCStrFromJStr(aEnv, szUniqueIdentifier, aUniqueIdentifier);
	const char* pUniqueIdentifier = szUniqueIdentifier.c_str();
	string szDeviceName;
	JniUtil::getCStrFromJStr(aEnv, szDeviceName, aDeviceName);
	const char* pDeviceName = szDeviceName.c_str();
	string szNetworkType;
	JniUtil::getCStrFromJStr(aEnv, szNetworkType, aNetworkType);
	const char* pNetworkType = szNetworkType.c_str();
	int res = DK_UploadIMEIBootInfo(pIMEI, pUniqueIdentifier, pDeviceName, pNetworkType);
	LOGV("%s return %d", __FUNCTION__, res);
	return res;
}

//
//JNIEXPORT jint JNICALL Java_com_duokan_tv_api_DKJniClient_getUserBasedRecommendMedia(JNIEnv *aEnv, jobject, jint aMeidaID, jobject aRecommendMediaList){
//    LOGV("%s enter", __FUNCTION__);
//    RECOMMENDMEDIALIST *mediaList = (RECOMMENDMEDIALIST*)malloc(sizeof(*mediaList));
//    memset(mediaList, 0, sizeof(*mediaList));
//    int res = DK_GetUserBasedRecommendMedia(aMeidaID, mediaList);
//    if (res > FAILED_ERROR){
//        buildJRecommendationList(aEnv, mediaList, aRecommendMediaList);
//    }
//    DK_DestroyRecommendMedia(mediaList);
//    free(mediaList);
//    LOGV("%s return mediaList", __FUNCTION__);
//    return res;
//}

//int buildCUserDataParam(JNIEnv *aEnv, LPUSERDATAPARAM aCUserDataParam, jobject aJUserDataParam){
//    jclass userDataParamClass = aEnv->FindClass(KClassUserDataParam);
//    jfieldID nChannelID = JniUtil::getFieldID(aEnv, KClassUserDataParam, "nChannelID", KSigInt);
//    jfieldID nPageNo = JniUtil::getFieldID(aEnv, KClassUserDataParam, "nPageNo", KSigInt);
//    jfieldID nPageSize = JniUtil::getFieldID(aEnv, KClassUserDataParam, "nPageSize", KSigInt);
//    jfieldID nLatestDays = JniUtil::getFieldID(aEnv, KClassUserDataParam, "nLatestDays", KSigInt);
//
//    aCUserDataParam->nChannelID =  aEnv->GetIntField(aJUserDataParam, nChannelID);
//    aCUserDataParam->nPageNo =  aEnv->GetIntField(aJUserDataParam, nPageNo);
//    aCUserDataParam->nPageSize =  aEnv->GetIntField(aJUserDataParam, nPageSize);
//    aCUserDataParam->nLatestDays =  aEnv->GetIntField(aJUserDataParam, nLatestDays);
//    return 0;
//}
//
//int buildCMediaInfo(JNIEnv *aEnv, LPMEDIAINFO aCMediaInfo, jobject aJMediaInfo){
//    LOGV("%s enter", __FUNCTION__);
//    jfieldID nMediaID = JniUtil::getFieldID(aEnv, KClassMediaInfo, "nMediaID", KSigInt);
//    jfieldID nFlag = JniUtil::getFieldID(aEnv, KClassMediaInfo, "nFlag", KSigInt);
//    jfieldID nResolution = JniUtil::getFieldID(aEnv, KClassMediaInfo, "nResolution", KSigInt);
//    jfieldID szCategory = JniUtil::getFieldID(aEnv, KClassMediaInfo, "szCategory", KSigStr);
//    jfieldID szMediaName = JniUtil::getFieldID(aEnv, KClassMediaInfo, "szMediaName", KSigStr);
//    jfieldID szDirector = JniUtil::getFieldID(aEnv, KClassMediaInfo, "szDirector", KSigStr);
//    jfieldID szActors = JniUtil::getFieldID(aEnv, KClassMediaInfo, "szActors", KSigStr);
//    jfieldID szSmallImageURL = JniUtil::getFieldID(aEnv, KClassMediaInfo, "szSmallImageURL", KSigStr);
//
//
//    aCMediaInfo->nMediaID = aEnv->GetIntField(aJMediaInfo, nMediaID);
//    aCMediaInfo->nFlag = aEnv->GetIntField(aJMediaInfo, nFlag);
//    aCMediaInfo->nResolution = aEnv->GetIntField(aJMediaInfo, nResolution);
//    getCStrFromJField(aEnv, aCMediaInfo->szCategory, aJMediaInfo, szCategory);
//    getCStrFromJField(aEnv, aCMediaInfo->szMediaName, aJMediaInfo, szMediaName);
//    getCStrFromJField(aEnv, aCMediaInfo->szDirector, aJMediaInfo, szDirector);
//    getCStrFromJField(aEnv, aCMediaInfo->szActors, aJMediaInfo, szActors);
//    getCStrFromJField(aEnv, aCMediaInfo->szSmallImageURL, aJMediaInfo, szSmallImageURL);
//    LOGV("%s exit", __FUNCTION__);
//    return 0;
//}
//
//
// int buildCBookmark(JNIEnv *aEnv, LPBOOKMARK aCBookmark, jobject aJBookmark){
   // jfieldID mediaInfo = JniUtil::getFieldID(aEnv, KClassBookmark, "mediainfo", KSigMediaInfo);
   // jfieldID szDate = JniUtil::getFieldID(aEnv, KClassBookmark, "szDate", KSigStr);
   // jfieldID ltime = JniUtil::getFieldID(aEnv, KClassBookmark, "ltime", KSigLong);
   // jobject jMediaInfo = aEnv->GetObjectField(aJBookmark, mediaInfo);
   // buildCMediaInfo(aEnv, &aCBookmark->mediainfo, jMediaInfo);
   // getCStrFromJField(aEnv, aCBookmark->szDate, aJBookmark, szDate);
   // aCBookmark->ltime = aEnv->GetLongField(aJBookmark, ltime);
   // return 0;
// }
//
//int buildCMediaByCelebrityGetter(JNIEnv *aEnv, LPGETMEDIABYCELEBRITY aCMediaByCelebrityGetter, jobject aJMediaByCelebrityGetter){
//    jfieldID nCelebrityId = JniUtil::getFieldID(aEnv, KClassMediaByCelebrityGetter, "nCelebrityId", KSigInt);
//    jfieldID nFilterType = JniUtil::getFieldID(aEnv, KClassMediaByCelebrityGetter, "nFilterType", KSigInt);
//    jfieldID nPageNo = JniUtil::getFieldID(aEnv, KClassMediaByCelebrityGetter, "nPageNo", KSigInt);
//    jfieldID nPageSize = JniUtil::getFieldID(aEnv, KClassMediaByCelebrityGetter, "nPageSize", KSigInt);
//    jfieldID nOrder = JniUtil::getFieldID(aEnv, KClassMediaByCelebrityGetter, "nOrder", KSigInt);
//    jfieldID nPosterType = JniUtil::getFieldID(aEnv, KClassMediaByCelebrityGetter, "nPosterType", KSigInt);
//    jfieldID nListType = JniUtil::getFieldID(aEnv, KClassMediaByCelebrityGetter, "nListType", KSigInt);
//
//    aCMediaByCelebrityGetter->nCelebrityId = aEnv->GetIntField(aJMediaByCelebrityGetter, nCelebrityId);
//    aCMediaByCelebrityGetter->nFilterType = aEnv->GetIntField(aJMediaByCelebrityGetter, nFilterType);
//    aCMediaByCelebrityGetter->nPageNo = aEnv->GetIntField(aJMediaByCelebrityGetter, nPageNo);
//    aCMediaByCelebrityGetter->nPageSize = aEnv->GetIntField(aJMediaByCelebrityGetter, nPageSize);
//    aCMediaByCelebrityGetter->nOrder = aEnv->GetIntField(aJMediaByCelebrityGetter, nOrder);
//    aCMediaByCelebrityGetter->nPosterType = aEnv->GetIntField(aJMediaByCelebrityGetter, nPosterType);
//    aCMediaByCelebrityGetter->nListType = aEnv->GetIntField(aJMediaByCelebrityGetter, nListType);
//    return 0;
//}
//
//
//
//int buildCPlayHistory(JNIEnv *aEnv, LPPLAYHISTORY aCPlayHistory, jobject aJPlayHistory){
//    LOGV("%s enter", __FUNCTION__);
//    jclass playHistoryClass = getClassFromCache(aEnv, KClassPlayHistory);
//    jclass mediaInfoClass = getClassFromCache(aEnv, KClassMediaInfo);
//    jclass personInfoClass = getClassFromCache(aEnv, KClassPersonInfo);
//
//    jfieldID mediaInfo = JniUtil::getFieldID(aEnv, KClassPlayHistory, "mediaInfo", KSigMediaInfo);
//    jfieldID lpPersonInfor = JniUtil::getFieldID(aEnv, KClassPlayHistory, "lpPersonInfor", KSigPersonInfo);
//    jfieldID nMediaCi = JniUtil::getFieldID(aEnv, KClassPlayHistory, "nMediaCi", KSigInt);
//    jfieldID nMediaSource = JniUtil::getFieldID(aEnv, KClassPlayHistory, "nMediaSource", KSigInt);
//    jfieldID szVideoName = JniUtil::getFieldID(aEnv, KClassPlayHistory, "szVideoName", KSigStr);
//    jfieldID fPercent = JniUtil::getFieldID(aEnv, KClassPlayHistory, "fPercent", KSigFloat);
//    jfieldID szPlaydate = JniUtil::getFieldID(aEnv, KClassPlayHistory, "szPlaydate", KSigStr);
//    jfieldID nScoreCount = JniUtil::getFieldID(aEnv, KClassPlayHistory, "nScoreCount", KSigInt);
//    jfieldID score = JniUtil::getFieldID(aEnv, KClassPlayHistory, "score", KSigFloat);
//    jfieldID nSetNow = JniUtil::getFieldID(aEnv, KClassPlayHistory, "nSetNow", KSigInt);
//    jfieldID nDuration = JniUtil::getFieldID(aEnv, KClassPlayHistory, "nDuration", KSigInt);
//    jfieldID nMediaType = JniUtil::getFieldID(aEnv, KClassPlayHistory, "nMediaType", KSigInt);
//    jfieldID nTotalSet = JniUtil::getFieldID(aEnv, KClassPlayHistory, "nTotalSet", KSigInt);
//    jfieldID lPlayseconds = JniUtil::getFieldID(aEnv, KClassPlayHistory, "lPlayseconds", KSigLong);
//
//
//    //load meidainfo
//    jobject mediaInfoObj = aEnv->GetObjectField(aJPlayHistory, mediaInfo);
//    buildCMediaInfo(aEnv, &aCPlayHistory->mediaInfo, mediaInfoObj);
//
//
//    //load others
//    aCPlayHistory->nMediaCi = aEnv->GetIntField(aJPlayHistory, nMediaCi);
//    aCPlayHistory->nMediaSource = aEnv->GetIntField(aJPlayHistory, nMediaSource);
//    getCStrFromJField(aEnv, aCPlayHistory->szVideoName, aJPlayHistory, szVideoName);
//    aCPlayHistory->fPercent = aEnv->GetFloatField(aJPlayHistory, fPercent);
//    getCStrFromJField(aEnv, aCPlayHistory->szPlaydate, aJPlayHistory, szPlaydate);
//    aCPlayHistory->nScoreCount = aEnv->GetIntField(aJPlayHistory, nScoreCount);
//    aCPlayHistory->score = aEnv->GetFloatField(aJPlayHistory, score);
//    aCPlayHistory->nSetNow = aEnv->GetIntField(aJPlayHistory, nSetNow);
//    aCPlayHistory->nDuration = aEnv->GetIntField(aJPlayHistory, nDuration);
//    aCPlayHistory->nMediaType = aEnv->GetIntField(aJPlayHistory, nMediaType);
//    aCPlayHistory->nTotalSet = aEnv->GetIntField(aJPlayHistory, nTotalSet);
//    aCPlayHistory->lPlayseconds = aEnv->GetLongField(aJPlayHistory, lPlayseconds);
//
//    //load personinfo
//    if(aCPlayHistory->nMediaType != 0){ //aleady asign above
//        jfieldID szCountry = JniUtil::getFieldID(aEnv, KClassPersonInfo, "szCountry", KSigStr);
//        jfieldID szcName = JniUtil::getFieldID(aEnv, KClassPersonInfo, "szcName", KSigStr);
//        jfieldID szeName = JniUtil::getFieldID(aEnv, KClassPersonInfo, "szeName", KSigStr);
//        jfieldID szAlias = JniUtil::getFieldID(aEnv, KClassPersonInfo, "szAlias", KSigStr);
//        jfieldID szBigImageUrl = JniUtil::getFieldID(aEnv, KClassPersonInfo, "szBigImageUrl", KSigStr);
//        jfieldID szCv = JniUtil::getFieldID(aEnv, KClassPersonInfo, "szCv", KSigStr);
//        jobject personInfoObj = aEnv->GetObjectField(aJPlayHistory, lpPersonInfor);
//
//        aCPlayHistory->lpPersonInfor = (LPPERSONINFOR)malloc(sizeof(*aCPlayHistory->lpPersonInfor));
//        getCStrFromJField(aEnv, aCPlayHistory->lpPersonInfor->szCountry, personInfoObj, szCountry);
//        getCStrFromJField(aEnv, aCPlayHistory->lpPersonInfor->szcName, personInfoObj, szcName);
//        getCStrFromJField(aEnv, aCPlayHistory->lpPersonInfor->szeName, personInfoObj, szeName);
//        getCStrFromJField(aEnv, aCPlayHistory->lpPersonInfor->szAlias, personInfoObj, szAlias);
//        getCStrFromJField(aEnv, aCPlayHistory->lpPersonInfor->szBigImageUrl, personInfoObj, szBigImageUrl);
//        getCStrFromJField(aEnv, aCPlayHistory->lpPersonInfor->szCv, personInfoObj, szCv);
//    }
//    else{
//        aCPlayHistory->lpPersonInfor = NULL;
//    }
//    LOGV("%s exit", __FUNCTION__);
//    return 0;
//}
//
//

//JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DkJNIClient_getMediaInfo(JNIEnv *aEnv, jobject, jobject aMediaInfoListGetter, jobject aMediaInfoList){
//    LOGV("%s enter", __FUNCTION__);
//    GETMEDIAINFOLIST mediaList;
//
//    buildCMediaInfoListGetter(aEnv, &mediaList, aMediaInfoListGetter);
//
//    MEDIAINFOLIST infoList;
//    memset(&infoList, 0, sizeof(infoList));
//
//    int requestId = 0;
//    int res = DK_GetMediaInfoList(&mediaList, &infoList, &requestId);
//
//
//    if (res > FAILED_ERROR){
//        buildJMediaInfoList(aEnv, &infoList, aMediaInfoList);
//    }
//    DK_DestroyMediaInfoList(&infoList);
//    LOGV("%s return err %d", __FUNCTION__, res);
//    return res;
//}
//


//JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DkJNIClient_getVarietyMediaInfo(JNIEnv *aEnv, jobject, jint aMediaID, jint aYear, jobject aVarietyList){
//
//    VARIETYLIST varietyList;
//    memset(&varietyList, 0, sizeof(varietyList));
//    int res = DK_GetVarietyMediaInfo(aMediaID, aYear, &varietyList);
//    if (res > FAILED_ERROR){
//        jobjectArray args = NULL;
//
//        jfieldID nStyle = getFieldIDFromCache(aEnv, KClassVarietyList, "nStyle", KSigInt);
//        jfieldID lpVariety = getFieldIDFromCache(aEnv, KClassVarietyList, "lpVariety", KSigVarietyArray);
//        jfieldID lpTraditional = getFieldIDFromCache(aEnv, KClassVarietyList, "lpTraditional", KSigTraditionalArray);
//        jstring jStr = NULL;
//        aEnv->SetIntField(aVarietyList, nStyle, varietyList.nStyle);
//        if(YEAR_STYLE == varietyList.nStyle){
//            aEnv->SetObjectField(aVarietyList, lpTraditional, NULL);
//
//            jclass varietyClass = getClassFromCache(aEnv, KClassVariety);
//            jfieldID szMonth = getFieldIDFromCache(aEnv, KClassVariety, "szMonth", KSigStr);
//            jfieldID lpInfo = getFieldIDFromCache(aEnv, KClassVariety, "lpInfo", KSigVarietyInfoArray);
//
//            jclass varietyInfoClass = getClassFromCache(aEnv, KClassVarietyInfo);
//            jfieldID szDate = getFieldIDFromCache(aEnv, KClassVarietyInfo, "szDate", KSigStr);
//            jfieldID lpDetail = getFieldIDFromCache(aEnv, KClassVarietyInfo, "lpDetail", KSigVarietyDetailArray);
//
//            jclass varietyDetailClass = getClassFromCache(aEnv, KClassVarietyDetail);
//            jfieldID nCi = getFieldIDFromCache(aEnv, KClassVarietyDetail, "nCi", KSigInt);
//            jfieldID szVideoName = getFieldIDFromCache(aEnv, KClassVarietyDetail, "szVideoName", KSigStr);
//
//            int varietyCnt = varietyList.nDataCount;
//            jobjectArray varietyArray =  aEnv->NewObjectArray(varietyCnt, varietyClass, 0);
//            for(int i = 0; i < varietyCnt; ++i){//setup for variety
//                jobject varietyObj = aEnv->AllocObject(varietyClass);
//                aEnv->SetObjectField(varietyObj, szMonth, aEnv->NewStringUTF(varietyList.lpVariety[i].szMonth));
//
//                int varietyInfoCnt = varietyList.lpVariety[i].nDataCount;
//                jobjectArray varietyInfoArray =  aEnv->NewObjectArray(varietyInfoCnt, varietyInfoClass, 0);
//                for(int j = 0; j < varietyInfoCnt; ++j){//setup for varietyInfo
//                    jobject varietyInfoObj = aEnv->AllocObject(varietyInfoClass);
//                    jStr = aEnv->NewStringUTF(varietyList.lpVariety[i].lpInfo[j].szDate);
//                    aEnv->SetObjectField(varietyInfoObj, szDate, jStr);
//                    aEnv->DeleteLocalRef(jStr);
//
//                    int varietyDetailCnt = varietyList.lpVariety[i].lpInfo[j].nDataCount;
//                    jobjectArray varietyDetailArray =  aEnv->NewObjectArray(varietyDetailCnt, varietyDetailClass, 0);
//                    for(int k = 0; k < varietyDetailCnt; ++ k){//setup for varietyDetail
//                        jobject varietyDetailObj = aEnv->AllocObject(varietyDetailClass);
//                        jStr = aEnv->NewStringUTF(varietyList.lpVariety[i].lpInfo[j].lpDetail[k].szVideoName);
//                        aEnv->SetObjectField(varietyDetailObj, szVideoName, jStr);
//                        aEnv->DeleteLocalRef(jStr);
//                        aEnv->SetIntField(varietyDetailObj, nCi, varietyList.lpVariety[i].lpInfo[j].lpDetail[k].nCi);
//                        aEnv->SetObjectArrayElement(varietyDetailArray, k, varietyDetailObj);
//                        aEnv->DeleteLocalRef(varietyDetailObj);
//                    }
//                    aEnv->SetObjectField(varietyInfoObj, lpDetail, varietyDetailArray);
//                    aEnv->SetObjectArrayElement(varietyInfoArray, j, varietyInfoObj);
//                    aEnv->DeleteLocalRef(varietyInfoObj);
//                    aEnv->DeleteLocalRef(varietyDetailArray);
//                }
//                aEnv->SetObjectField(varietyObj, lpInfo, varietyInfoArray);
//                aEnv->SetObjectArrayElement(varietyArray, i, varietyObj);
//            }
//            aEnv->SetObjectField(aVarietyList, lpVariety, varietyArray);
//        }
//        else{//setup for triditonal
//            aEnv->SetObjectField(aVarietyList, lpVariety, NULL);
//
//            jclass traditionalClass = getClassFromCache(aEnv, KClassTraditional);
//            jfieldID nCi = getFieldIDFromCache(aEnv, KClassVarietyInfo, "nCi", KSigInt);
//            jfieldID szDate = getFieldIDFromCache(aEnv, KClassVarietyInfo, "szDate", KSigStr);
//            jfieldID szVideoName = getFieldIDFromCache(aEnv, KClassVarietyInfo, "szVideoName", KSigStr);
//            jfieldID szFocus = getFieldIDFromCache(aEnv, KClassVarietyInfo, "szFocus", KSigStr);
//
//            int traditionalCnt = varietyList.nDataCount;
//            jobjectArray traditionalArray =  aEnv->NewObjectArray(traditionalCnt, traditionalClass, 0);
//            for(int i = 0; i < traditionalCnt; ++ i){//setup for varietyDetail
//                jobject traditionalObj = aEnv->AllocObject(traditionalClass);
//                aEnv->SetIntField(traditionalObj, nCi, varietyList.lpTraditional[i].nCi);
//                aEnv->SetObjectField(traditionalObj, szDate, aEnv->NewStringUTF(varietyList.lpTraditional[i].szDate));
//                aEnv->SetObjectField(traditionalObj, szVideoName, aEnv->NewStringUTF(varietyList.lpTraditional[i].szVideoName));
//                aEnv->SetObjectField(traditionalObj, szFocus, aEnv->NewStringUTF(varietyList.lpTraditional[i].szFocus));
//                aEnv->SetObjectArrayElement(traditionalArray, i, traditionalObj);
//            }
//            aEnv->SetObjectField(aVarietyList, lpTraditional, traditionalArray);
//        }
//    }
//
//
//    DK_DestroyVarietyMediaInfo(&varietyList);
//    LOGV("%s return err %d", __FUNCTION__, res);
//    return res;
//}
//
//JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DkJNIClient_setPlayHistory(JNIEnv *aEnv, jobject, jobject aPlayHistory, jintArray aLostHistory){
//    LOGV("%s enter", __FUNCTION__);
//    PLAYHISTORY playHistory;
//    buildCPlayHistory(aEnv,&playHistory, aPlayHistory);
//
//    int lostHistory[2] = {-1, -1};
//    int res = DK_SetPlayHistory(&playHistory, &lostHistory[0], &lostHistory[1]);
//    if(lostHistory[0] != -1 && lostHistory[0] != -1){//history overwrite
//        aEnv->SetIntArrayRegion(aLostHistory, 0, 2,  lostHistory);
//    }
//    free(playHistory.lpPersonInfor);
//    LOGV("%s return err %d", __FUNCTION__, res);
//    return res;
//}
//
//
//JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DkJNIClient_getPlayHistory(JNIEnv *aEnv, jobject, jobject aInputData, jobject aPlayHistoryList){
//    LOGV("%s enter", __FUNCTION__);
//    USERDATAPARAM userParam;
//    buildCUserDataParam(aEnv,&userParam, aInputData);
//    PLAYHISTORYLIST historyList;
//    memset(&historyList, 0, sizeof(historyList));
//    int res = DK_GetPlayHistory(&userParam, &historyList);
//    if (res > FAILED_ERROR){
//        jobjectArray args = NULL;
//        jclass playHistoryListClass = getClassFromCache(aEnv, KClassPlayHistoryList);
//        jfieldID lpPlayHistory = JniUtil::getFieldID(aEnv, KClassPlayHistoryList, "lpPlayHistory", KSigPlayHistoryArray);
//
//        jclass playHistoryClass = getClassFromCache(aEnv, KClassPlayHistory);
//        jsize itemCnt = historyList.nDataCount;
//        jobjectArray historyArray =  aEnv->NewObjectArray(itemCnt, playHistoryClass, 0);
//
//        jfieldID mediaInfo = JniUtil::getFieldID(aEnv, KClassPlayHistory, "mediaInfo", KSigMediaInfo);
//        jfieldID lpPersonInfor = JniUtil::getFieldID(aEnv, KClassPlayHistory, "lpPersonInfor", KSigPersonInfo);
//        jfieldID nMediaCi = JniUtil::getFieldID(aEnv, KClassPlayHistory, "nMediaCi", KSigInt);
//        jfieldID nMediaSource = JniUtil::getFieldID(aEnv, KClassPlayHistory, "nMediaSource", KSigInt);
//        jfieldID szVideoName = JniUtil::getFieldID(aEnv, KClassPlayHistory, "szVideoName", KSigStr);
//        jfieldID fPercent = JniUtil::getFieldID(aEnv, KClassPlayHistory, "fPercent", KSigFloat);
//        jfieldID szPlaydate = JniUtil::getFieldID(aEnv, KClassPlayHistory, "szPlaydate", KSigStr);
//        jfieldID nScoreCount = JniUtil::getFieldID(aEnv, KClassPlayHistory, "nScoreCount", KSigInt);
//        jfieldID score = JniUtil::getFieldID(aEnv, KClassPlayHistory, "score", KSigFloat);
//        jfieldID nSetNow = JniUtil::getFieldID(aEnv, KClassPlayHistory, "nSetNow", KSigInt);
//        jfieldID nDuration = JniUtil::getFieldID(aEnv, KClassPlayHistory, "nDuration", KSigInt);
//        jfieldID nMediaType = JniUtil::getFieldID(aEnv, KClassPlayHistory, "nMediaType", KSigInt);
//        jfieldID nTotalSet = JniUtil::getFieldID(aEnv, KClassPlayHistory, "nTotalSet", KSigInt);
//        jfieldID lPlayseconds = JniUtil::getFieldID(aEnv, KClassPlayHistory, "lPlayseconds", KSigLong);
//
//        jclass mediaInfoClass = getClassFromCache(aEnv, KClassMediaInfo);
//        jclass personInfoClass = getClassFromCache(aEnv, KClassPersonInfo);
//
//        for(int i = 0; i < itemCnt; ++ i){
//            jobject playHistoryObj = aEnv->AllocObject(playHistoryClass);
//            jobject mediaInfoObj = aEnv->AllocObject(mediaInfoClass);
//
//            //load meidainfo
//            buildJMediaInfo(aEnv, &historyList.lpPlayHistory[i].mediaInfo, mediaInfoObj);
//            aEnv->SetObjectField(playHistoryObj, mediaInfo, mediaInfoObj);
//
//            //load others
//            aEnv->SetIntField(playHistoryObj, nMediaCi, historyList.lpPlayHistory[i].nMediaCi);
//            aEnv->SetIntField(playHistoryObj, nMediaSource, historyList.lpPlayHistory[i].nMediaSource);
//            aEnv->SetObjectField(playHistoryObj, szVideoName, aEnv->NewStringUTF(historyList.lpPlayHistory[i].szVideoName));
//            aEnv->SetFloatField(playHistoryObj, fPercent, historyList.lpPlayHistory[i].fPercent);
//            aEnv->SetObjectField(playHistoryObj, szPlaydate, aEnv->NewStringUTF(historyList.lpPlayHistory[i].szPlaydate));
//            aEnv->SetIntField(playHistoryObj, nScoreCount, historyList.lpPlayHistory[i].nScoreCount);
//            aEnv->SetFloatField(playHistoryObj, score, historyList.lpPlayHistory[i].score);
//            aEnv->SetIntField(playHistoryObj, nSetNow, historyList.lpPlayHistory[i].nSetNow);
//            aEnv->SetIntField(playHistoryObj, nScoreCount, historyList.lpPlayHistory[i].nScoreCount);
//            aEnv->SetIntField(playHistoryObj, nDuration, historyList.lpPlayHistory[i].nDuration);
//            aEnv->SetIntField(playHistoryObj, nMediaType, historyList.lpPlayHistory[i].nMediaType);
//            aEnv->SetIntField(playHistoryObj, nTotalSet, historyList.lpPlayHistory[i].nTotalSet);
//            aEnv->SetLongField(playHistoryObj, lPlayseconds, historyList.lpPlayHistory[i].lPlayseconds);
//
//            //load personinfo
//            if(historyList.lpPlayHistory[i].nMediaType != 0){ //aleady asign above
//                jobject personInfoObj = aEnv->AllocObject(personInfoClass);
//                buildJPersonInfo(aEnv, historyList.lpPlayHistory[i].lpPersonInfor, personInfoObj);
//                aEnv->SetObjectField(playHistoryObj, lpPersonInfor, personInfoObj);
//            }
//            else{
//                aEnv->SetObjectField(playHistoryObj, lpPersonInfor, NULL);
//            }
//
//            aEnv->SetObjectArrayElement(historyArray, i, playHistoryObj);
//        }
//        aEnv->SetObjectField(aPlayHistoryList, lpPlayHistory, historyArray);
//    }
//    DK_DestroyPlayHistory(&historyList);
//    LOGV("%s return err %d", __FUNCTION__, res);
//    return res;
//}
//
//
//JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DkJNIClient_deletePlayHistory(JNIEnv *aEnv, jobject, jobject aPlayHistory){
//    LOGV("%s enter", __FUNCTION__);
//    PLAYHISTORY playHistory;
//    buildCPlayHistory(aEnv,&playHistory, aPlayHistory);
//    int res = DK_DeletePlayHistory(&playHistory);
//    free(playHistory.lpPersonInfor);
//    LOGV("%s return err %d", __FUNCTION__, res);
//    return res;
//}
//
//
//JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DkJNIClient_setBookmark(JNIEnv *aEnv, jobject, jobject aBookmark){
//    LOGV("%s enter", __FUNCTION__);
//    BOOKMARK bookmark;
//    buildCBookmark(aEnv, &bookmark, aBookmark);
//    int res = DK_SetBookMark(&bookmark);
//    LOGV("%s return err %d", __FUNCTION__, res);
//    return res;
//}
//
//
//JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DkJNIClient_getBookmarksDetails(JNIEnv *aEnv, jobject, jobject aInputData, jobject aBookmarkList){
//    LOGV("%s enter", __FUNCTION__);
//    USERDATAPARAM userParam;
//    buildCUserDataParam(aEnv,&userParam, aInputData);
//    BOOKMARKLIST bookmarkList;
//    memset(&bookmarkList, 0, sizeof(bookmarkList));
//    int res = DK_GetBookMarksDetails(&userParam, &bookmarkList);
//    if (res > FAILED_ERROR){
//
//        jfieldID lpBookMarks = JniUtil::getFieldID(aEnv, KClassBookmarkList, "lpBookMarks", KSigBookmarkArray);
//
//        jclass bookmarkClass = getClassFromCache(aEnv, KClassBookmark);
//        jsize itemCnt = bookmarkList.nDataCount;
//        jobjectArray bookmarkArray =  aEnv->NewObjectArray(itemCnt, bookmarkClass, 0);
//
//        jfieldID mediainfo = JniUtil::getFieldID(aEnv, KClassBookmark, "mediainfo", KSigMediaInfo);
//        jfieldID szDate = JniUtil::getFieldID(aEnv, KClassBookmark, "szDate", KSigStr);
//        jfieldID ltime = JniUtil::getFieldID(aEnv, KClassBookmark, "ltime", KSigLong);
//
//
//        jclass mediaInfoClass = getClassFromCache(aEnv, KClassMediaInfo);
//
//        for(int i = 0; i < itemCnt; ++ i){
//            jobject bookmarkObj = aEnv->AllocObject(bookmarkClass);
//            jobject mediaInfoObj = aEnv->AllocObject(mediaInfoClass);
//
//            //load meidainfo
//            buildJMediaInfo(aEnv, &bookmarkList.lpBookMarks[i].mediainfo, mediaInfoObj);
//            aEnv->SetObjectField(bookmarkObj, mediainfo, mediaInfoObj);
//            aEnv->SetObjectField(bookmarkObj, szDate, aEnv->NewStringUTF(bookmarkList.lpBookMarks[i].szDate));
//            aEnv->SetLongField(bookmarkObj, ltime, bookmarkList.lpBookMarks[i].ltime);
//
//            aEnv->SetObjectArrayElement(bookmarkArray, i, bookmarkObj);
//        }
//        aEnv->SetObjectField(aBookmarkList, lpBookMarks, bookmarkArray);
//    }
//    DK_DestroyBookMarksDetails(&bookmarkList);
//    LOGV("%s return err %d", __FUNCTION__, res);
//    return res;
//}
//
//JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DkJNIClient_getBookmarksSimple(JNIEnv *aEnv, jobject, jobject aInputData, jintArray aSimpleList){
//    LOGV("%s enter", __FUNCTION__);
//    USERDATAPARAM userParam;
//    buildCUserDataParam(aEnv,&userParam, aInputData);
//    int *bookmarkList = NULL;
//    int res = DK_GetBookMarksSimple(&userParam, &bookmarkList);
//    if (res > FAILED_ERROR){
//        int length =  aEnv->GetArrayLength(aSimpleList);
//        aEnv->SetIntArrayRegion(aSimpleList, 0, min(res, length), bookmarkList);
//    }
//    LOGV("%s return err %d", __FUNCTION__, res);
//    return res;
//}
//
//JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DkJNIClient_deleteBookmark(JNIEnv *, jobject, jint aMediaID){
//    LOGV("%s enter", __FUNCTION__);
//    int res = DK_DeleteBookMark(aMediaID);
//    LOGV("%s return err %d", __FUNCTION__, res);
//    return res;
//}
//
//
//

//
//
//JNIEXPORT jstring JNICALL Java_com_duokan_tv_client_DkJNIClient_getSourceNameByID(JNIEnv *aEnv, jobject, jint aSourceID){
//    const int KNameLength = 32;
//    char sourceName[KNameLength];
//    memset(sourceName, 0, KNameLength);
//    DK_GetSourceNameByID(aSourceID, sourceName, KNameLength);
//    return aEnv->NewStringUTF(sourceName);
//}
//
//
//JNIEXPORT jstring JNICALL Java_com_duokan_tv_client_DkJNIClient_getHttpHeaderByID(JNIEnv *aEnv, jobject, jint aSourceID){
//    const int KHeaderLength = 1024;
//    char headerStr[KHeaderLength];
//    memset(headerStr, 0, KHeaderLength);
//    DK_GetHttpHeaderByID(aSourceID, headerStr, KHeaderLength);
//    return aEnv->NewStringUTF(headerStr);
//}
//
//
//JNIEXPORT jstring JNICALL Java_com_duokan_tv_client_DkJNIClient_getDownLoadM3U8Path(JNIEnv *, jobject){
//    return NULL;
//}
//
//
//JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DkJNIClient_getMaxBookMark(JNIEnv *, jobject){
//    return DK_GetMaxBookMark();
//}
//
//
//JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DkJNIClient_getMaxHistory(JNIEnv *, jobject){
//    return DK_GetMaxHistory();
//}
//
//
//JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DkJNIClient_getWeatherforcast(JNIEnv *aEnv, jobject, jint aDays, jstring aPostcode, jobject aWeatherForcastList){
//
//    WEATHERFORECASTLIST forcastList;
//    memset(&forcastList, 0, sizeof(forcastList));
//
//    int res = DK_GetWeatherforecast(1, NULL, &forcastList);
//    if (res > FAILED_ERROR){
//        jobjectArray args = NULL;
//        jclass weatherForcastListClass = getClassFromCache(aEnv, KClassWeatherForcastList);
//        jfieldID lpWeatherForcast = JniUtil::getFieldID(aEnv, KClassWeatherForcastList, "lpWeatherForcast", KSigWeatherForcastArray);
//
//        jclass weatherForcastClass = getClassFromCache(aEnv, KClassWeatherForcast);
//        jsize itemCnt = forcastList.nDataCount;
//        jobjectArray forcastArray =  aEnv->NewObjectArray(itemCnt, weatherForcastClass, 0);
//
//        jfieldID nTemp_h = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "nTemp_h", KSigInt);
//        jfieldID nTemp_l = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "nTemp_l", KSigInt);
//        jfieldID nTemp_c = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "nTemp_c", KSigInt);
//        jfieldID nHumidity = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "nHumidity", KSigInt);
//        jfieldID szProvince = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "szProvince", KSigStr);
//        jfieldID szCity = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "szCity", KSigStr);
//        jfieldID szWinddir_d = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "szWinddir_d", KSigStr);
//        jfieldID szWinddir_n = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "szWinddir_n", KSigStr);
//        jfieldID szSunset = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "szSunset", KSigStr);
//        jfieldID szPublish = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "szPublish", KSigStr);
//        jfieldID szWindlevel_d = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "szWindlevel_d", KSigStr);
//        jfieldID szDate = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "szDate", KSigStr);
//        jfieldID szCond_d = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "szCond_d", KSigStr);
//        jfieldID szWindlevel_n = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "szWindlevel_n", KSigStr);
//        jfieldID szSunrise = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "szSunrise", KSigStr);
//        jfieldID szCond_n = JniUtil::getFieldID(aEnv, KClassWeatherForcast, "szCond_n", KSigStr);
//
//        for(int i = 0; i < itemCnt; ++ i){
//            jobject weatherForcastObj = aEnv->AllocObject(weatherForcastClass);
//            aEnv->SetIntField(weatherForcastObj, nTemp_h, forcastList.lpWeatherforcecast[i].nTemp_h);
//            aEnv->SetIntField(weatherForcastObj, nTemp_l, forcastList.lpWeatherforcecast[i].nTemp_l);
//            aEnv->SetIntField(weatherForcastObj, nTemp_c, forcastList.lpWeatherforcecast[i].nTemp_c);
//            aEnv->SetIntField(weatherForcastObj, nHumidity, forcastList.lpWeatherforcecast[i].nHumidity);
//            aEnv->SetObjectField(weatherForcastObj, szProvince, aEnv->NewStringUTF(forcastList.lpWeatherforcecast[i].szProvince));
//            aEnv->SetObjectField(weatherForcastObj, szCity, aEnv->NewStringUTF(forcastList.lpWeatherforcecast[i].szCity));
//            aEnv->SetObjectField(weatherForcastObj, szWinddir_d, aEnv->NewStringUTF(forcastList.lpWeatherforcecast[i].szWinddir_d));
//            aEnv->SetObjectField(weatherForcastObj, szWinddir_n, aEnv->NewStringUTF(forcastList.lpWeatherforcecast[i].szWinddir_n));
//            aEnv->SetObjectField(weatherForcastObj, szSunset, aEnv->NewStringUTF(forcastList.lpWeatherforcecast[i].szSunset));
//            aEnv->SetObjectField(weatherForcastObj, szPublish, aEnv->NewStringUTF(forcastList.lpWeatherforcecast[i].szPublish));
//            aEnv->SetObjectField(weatherForcastObj, szWindlevel_d, aEnv->NewStringUTF(forcastList.lpWeatherforcecast[i].szWindlevel_d));
//            aEnv->SetObjectField(weatherForcastObj, szDate, aEnv->NewStringUTF(forcastList.lpWeatherforcecast[i].szDate));
//            aEnv->SetObjectField(weatherForcastObj, szCond_d, aEnv->NewStringUTF(forcastList.lpWeatherforcecast[i].szCond_d));
//            aEnv->SetObjectField(weatherForcastObj, szWindlevel_n, aEnv->NewStringUTF(forcastList.lpWeatherforcecast[i].szWindlevel_n));
//            aEnv->SetObjectField(weatherForcastObj, szSunrise, aEnv->NewStringUTF(forcastList.lpWeatherforcecast[i].szSunrise));
//            aEnv->SetObjectField(weatherForcastObj, szCond_n, aEnv->NewStringUTF(forcastList.lpWeatherforcecast[i].szCond_n));
//
//            aEnv->SetObjectArrayElement(forcastArray, i, weatherForcastObj);
//        }
//        aEnv->SetObjectField(aWeatherForcastList, lpWeatherForcast, forcastArray);
//    }
//    DK_DestroyWeatherforecast(&forcastList);
//    LOGV("%s return err %d", __FUNCTION__, res);
//    return res;
//}
//
//
//
//
//

//
//JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DkJNIClient_getMediaByCelebrity(JNIEnv *aEnv, jobject, jobject aMediaByCelebrityGetter, jobject aMediaInfoList){
//    MEDIAINFOLIST infoList;
//    memset(&infoList, 0, sizeof(infoList));
//    GETMEDIABYCELEBRITY requestInfo;
//    buildCMediaByCelebrityGetter(aEnv, &requestInfo, aMediaByCelebrityGetter);
//    int res = DK_GetMediaByCelebrity(&requestInfo, &infoList);
//    if (res > FAILED_ERROR){
//        buildJMediaInfoList(aEnv, &infoList, aMediaInfoList);
//    }
//    DK_DestroyMediaInfoList(&infoList);
//    LOGV("%s return err %d", __FUNCTION__, res);
//    return res;
//}
//
//
//JNIEXPORT jint JNICALL Java_com_duokan_tv_client_DkJNIClient_getPersonInfor(JNIEnv *aEnv, jobject, jint aCelebrityId, jobject aPersonInfo){
//
//    PERSONINFOR personInfo;
//    memset(&personInfo, 0, sizeof(personInfo));
//    int res =  DK_GetPersonInfor(aCelebrityId, &personInfo);
//    if (res > FAILED_ERROR){
//        buildJPersonInfo(aEnv, &personInfo, aPersonInfo);
//    }
//    LOGV("%s return err %d", __FUNCTION__, res);
//    return res;
//}

}
