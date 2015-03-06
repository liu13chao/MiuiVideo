#ifndef JNI_UTIL_H
#define JNI_UTIL_H

#include <jni.h>
#include <string>
#include <map>

using std::string;
using std::map;

class JniUtil {

public:
	static jclass findClass(JNIEnv* env, const string& className);
	static jmethodID getMethodID(JNIEnv* env, const string& className,
			const string& methodName, const string& sig);
	static jfieldID getFieldID(JNIEnv* env, const string& className,
				const string& fieldName, const string& sig);
	static void getCStrFromJField(JNIEnv *env, string& str, jobject jObj, jfieldID jField);
	static void getCStrFromJStr(JNIEnv *env, string& str, jstring jstr);

private:
	typedef map<string, jfieldID> FieldMap;
	typedef map<string, jmethodID> MethodMap;
	typedef map<string, jclass> ClassMap;

	typedef FieldMap::iterator FieldIterator;
	typedef MethodMap::iterator MethodIterator;
	typedef ClassMap::iterator ClassIterator;

	typedef FieldMap::value_type FieldItem;
	typedef MethodMap::value_type MethodItem;
	typedef ClassMap::value_type ClassItem;

	static MethodMap methodCache;
	static FieldMap fieldCache;
	static ClassMap classCache;
};

















#endif
