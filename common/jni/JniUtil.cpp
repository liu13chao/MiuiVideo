#include "JniUtil.h"

JniUtil::MethodMap JniUtil::methodCache;
JniUtil::FieldMap JniUtil::fieldCache;
JniUtil::ClassMap JniUtil::classCache;

jclass JniUtil::findClass(JNIEnv* env, const string& className) {
	ClassIterator itr = classCache.find(className);
	if (itr != classCache.end()) {
		return itr->second;
	} else {
		jclass classID = (jclass)env->NewGlobalRef(env->FindClass(className.c_str()));
		classCache.insert(ClassItem(className, classID));
		return classID;
	}
}

jmethodID JniUtil::getMethodID(JNIEnv* env, const string& className,
		const string& methodName, const string& sig) {
	string fullName = className + "::" + methodName;
	MethodIterator itr = methodCache.find(fullName);
	if (itr != methodCache.end()) {
		return itr->second;
	} else {
		jclass classID = findClass(env, className);
		jmethodID methodID = (env->GetMethodID(classID, methodName.c_str() , sig.c_str()));
		methodCache.insert(MethodItem(fullName, methodID));
		return methodID;
	}
}

jfieldID JniUtil::getFieldID(JNIEnv* env, const string& className,
		const string& fieldName, const string& sig) {
	string fullName = className + "::" + fieldName;
	FieldIterator itr = fieldCache.find(fullName);
	if (itr != fieldCache.end()) {
		return itr->second;
	} else {
		jclass classID = findClass(env, className);
		jfieldID fieldID = (env->GetFieldID(classID, fieldName.c_str() , sig.c_str()));
		fieldCache.insert(FieldItem(fullName, fieldID));
		return fieldID;
	}
}

void JniUtil::getCStrFromJField(JNIEnv *env, string& str, jobject jObj, jfieldID jField){
    jstring JStr = (jstring)env->GetObjectField(jObj, jField);
    const char* CStr = env->GetStringUTFChars(JStr, 0);
    str = env->GetStringUTFChars(JStr, 0);
    env->ReleaseStringUTFChars(JStr, CStr);
}

void JniUtil::getCStrFromJStr(JNIEnv *env, string& str, jstring jstr){
	if(jstr != NULL){
		const char* cstr = env->GetStringUTFChars(jstr, 0);
		str = cstr;
		env->ReleaseStringUTFChars(jstr, cstr);
	}
}
