#include <unistd.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <sys/wait.h>
#include <arpa/inet.h>
#include <android/log.h>
#include <signal.h>
#include <jni.h>
#include <dlfcn.h>
#include <stdbool.h>

#define LOG_TAG "SMTShell"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#define ERR_CHECK(env) if ((*env)->ExceptionCheck(env)) { goto fail; }

bool find_self(Dl_info* pInfo) {
    if (dladdr(find_self, pInfo)) {
        LOGI("Loaded from path: %s", (*pInfo).dli_fname);
        return true;
    }
    return false;
}

// TODO we could do this by getting a foreign package context via JNI,
//  which is probably more reliable
void get_apk_path_from_so(const char* soPath, char** pApkPath) {
    char *suffixStart = strstr(soPath, "/lib/");
    if (suffixStart == NULL) {
        // no suffix found
        *pApkPath = NULL;
    } else {
        // suffix found, allocate new string and copy prefix and "/base.apk"
        size_t prefixLen = suffixStart - soPath;
        size_t outputLen = prefixLen + strlen("/base.apk");
        char *newStr = (char*) malloc(outputLen + 1);
        strncpy(newStr, soPath, prefixLen);
        strcpy(newStr + prefixLen, "/base.apk");
        *pApkPath = newStr;
    }
}

JNIEXPORT jint JNICALL Java_com_samsung_SMT_engine_SmtTTS_initialize(JNIEnv* env, jobject thiz) {
    LOGI("SmtTTS_initialize() called, my uid is %d", getuid());

    Dl_info info;
    if (!find_self(&info)) {
        goto fail;
    }

    char* apk_path;
    get_apk_path_from_so(info.dli_fname, &apk_path);
    if (NULL == apk_path) {
        goto fail;
    }

    LOGI("got apk path: %s", apk_path);

    (*env)->PushLocalFrame(env, 16);

    jclass ClassLoader = (*env)->FindClass(env, "java/lang/ClassLoader");
    ERR_CHECK(env);

    jmethodID getSystemClassLoader = (*env)->GetStaticMethodID(env, ClassLoader, "getSystemClassLoader", "()Ljava/lang/ClassLoader;");
    ERR_CHECK(env);

    jobject classLoader = (*env)->CallStaticObjectMethod(env, ClassLoader, getSystemClassLoader);
    ERR_CHECK(env);

    jclass PathClassLoader = (*env)->FindClass(env, "dalvik/system/PathClassLoader");
    ERR_CHECK(env);

    jmethodID pclCtor = (*env)->GetMethodID(env, PathClassLoader, "<init>", "(Ljava/lang/String;Ljava/lang/ClassLoader;)V");
    ERR_CHECK(env);

    jstring jApkPath = (*env)->NewStringUTF(env, apk_path);
    ERR_CHECK(env);

    jobject apkLoader = (*env)->NewObject(env, PathClassLoader, pclCtor, jApkPath, classLoader);
    ERR_CHECK(env);

    jmethodID loadClass = (*env)->GetMethodID(env, PathClassLoader, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
    ERR_CHECK(env);

    jstring jEntrypointName = (*env)->NewStringUTF(env, "SystemEntrypoint");
    ERR_CHECK(env);

    jclass SystemEntrypoint = (*env)->CallObjectMethod(env, apkLoader, loadClass, jEntrypointName);
    ERR_CHECK(env);

    jmethodID entryMethod = (*env)->GetStaticMethodID(env, SystemEntrypoint, "main", "([Ljava/lang/String;)V");
    ERR_CHECK(env);

    LOGI("Calling entrypoint: %p", entryMethod);
    (*env)->CallStaticVoidMethod(env, SystemEntrypoint, entryMethod, NULL);
    ERR_CHECK(env);

fail:
    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        LOGE("Java payload failed!");
    }

    (*env)->PopLocalFrame(env, NULL);

    return 0;
}

/**
 * return success for all JNI exports that the TTS service expects so we don't get killed/restarted
 */
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wunused-parameter"
JNIEXPORT jstring JNICALL Java_com_samsung_SMT_engine_SmtTTS_getDefaultVoice(JNIEnv *env, jobject obj, jstring str1, jstring str2) {
    return NULL;
}

JNIEXPORT jint JNICALL Java_com_samsung_SMT_engine_SmtTTS_getIsLanguageAvailable(JNIEnv *env, jobject obj, jstring str1, jstring str2, jstring str3, jstring str4, jint i, jint i2) {
    return 0;
}

JNIEXPORT jstring JNICALL Java_com_samsung_SMT_engine_SmtTTS_getProperty(JNIEnv *env, jobject obj, jstring str) {
    return NULL;
}

JNIEXPORT jint JNICALL Java_com_samsung_SMT_engine_SmtTTS_getSamplingRate(JNIEnv *env, jobject obj) {
    return 0;
}

JNIEXPORT jstring JNICALL Java_com_samsung_SMT_engine_SmtTTS_getSupportedLocales(JNIEnv *env, jobject obj) {
    return NULL;
}

JNIEXPORT jint JNICALL Java_com_samsung_SMT_engine_SmtTTS_getSynthesizedData(JNIEnv *env, jobject obj, jbyteArray arr) {
    return 0;
}

JNIEXPORT jint JNICALL Java_com_samsung_SMT_engine_SmtTTS_getVersion(JNIEnv *env, jobject obj) {
    return 0;
}

JNIEXPORT jint JNICALL Java_com_samsung_SMT_engine_SmtTTS_loadProperty(JNIEnv *env, jobject obj, jobjectArray arr, jstring str1, jstring str2, jstring str3, jstring str4, jstring str5, jint i) {
    return 0;
}

JNIEXPORT jint JNICALL Java_com_samsung_SMT_engine_SmtTTS_setLanguage(JNIEnv *env, jobject obj, jstring str1, jstring str2, jstring str3, jstring str4, jint i, jint i2) {
    return 0;
}

JNIEXPORT jint JNICALL Java_com_samsung_SMT_engine_SmtTTS_setParameters(JNIEnv *env, jobject obj, jint i, jint i2, jfloat f, jint i3) {
    return 0;
}

JNIEXPORT jint JNICALL Java_com_samsung_SMT_engine_SmtTTS_speak(JNIEnv *env, jobject obj, jstring str) {
    return 0;
}

JNIEXPORT jint JNICALL Java_com_samsung_SMT_engine_SmtTTS_terminate(JNIEnv *env, jobject obj) {
    return 0;
}

JNIEXPORT jboolean JNICALL Java_com_samsung_SMT_engine_SmtTTS_supervisor_1GetUtterance(JNIEnv *env, jobject obj, jobjectArray arr, jboolean z) {
    return JNI_FALSE;
}

JNIEXPORT void JNICALL Java_com_samsung_SMT_engine_SmtTTS_supervisor_1InputText(JNIEnv *env, jobject obj, jstring str1, jstring str2, jstring str3) {

}
#pragma GCC diagnostic pop

__attribute__((constructor)) static void on_load() {
    LOGI("on_load() called, my uid is %d", getuid());
}
