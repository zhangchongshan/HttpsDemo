#include <jni.h>
#include <string>

#include <inttypes.h>
#include <pthread.h>
#include <android/log.h>
#include <assert.h>

// processing callback to handler class
typedef struct tick_context {
    JavaVM  *javaVM;
    jclass   jniHelperClz;
    jobject  jniHelperObj;
    jclass   mainActivityClz;
    jobject  mainActivityObj;
    pthread_mutex_t  lock;
    int      done;
} TickContext;
TickContext g_ctx;


extern "C" JNIEXPORT jstring JNICALL
Java_com_sonoptek_httpscheckdemo_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

/**
 *这个key就是要用来加密传输内容的rsa的公钥
 */
const char* AUTH_KEY = "1111";
/**
* 发布的app 签名,只有和本签名一致的app 才会返回 AUTH_KEY
* 这个RELEASE_SIGN的值是上一步用java代码获取的值
*/
const char* RELEASE_SIGN = "xxxxxx30102020440625908300d06092a864886f70d01010b0500305a310b3009060355040613023836310e300c060355040813056875626569310e300c06035504071305777568616e310d300b060355040a130468656865310d300b060355040b130468656865310d300b0603550403130463616c6c301e170d3136303932393130303234355a170d3436303932323130303234355a305a310b3009060355040613023836310e300c060355040813056875626569310e300c06035504071305777568616e310d300b060355040a130468656865310d300b060355040b130468656865310d300b0603550403130463616c6c30820122300d06092a864886f70d01010105000382010f003082010a0282010100b508259dd7e36da221a2b5de5158e6e1f310f2b11073b359b4a3e49d80f0b8c741c167e1364e0d3054af4a084d70a7a793cc51c47818c6b862ccb11d8316cc29c9f26ae5d543288b3392d36ad7556673621d25c6ad0dc469b8355d75ead3799d7806878c1f925dad789173c8e09d196b1197a300d73ecee78228c5def17c483138db50376c5d7c1ce0aaea3e7e90b37fa8d94f3418056f25aa12522356005678065b1f559b164758dfa470c0a63f6678400abba1983db0621422eac20d2f5406d4667f6d9175084641dd12180a1a1b048836864bb0336b9ad439d5ee059562352037473460e6885ac85362a5258d9438266a07085ae8044303049b2df6a0340f0203010001a321301f301d0603551d0e04160414fcc824f06f53f2a8c8efa1b97c8fcd43f5bcfff3300d06092a864886f70d01010b050003820101004f09129e656dc9ba39082615a112ce68a08383e518dbe9fe6c12d2b67fcf4287ee7d89faadbd189f31a374be641167ec366d2ae16b82a215fef9a33f468877a1d7edc395f5224fb0a4237fdfa4e960b42a99b082f66fbc37c991b7ee0306fdfd565e432ec6e11807e6c541aad33bd221fc793484519e932b82d963694df6605e2af3d66996188cc78d9e76a2e9b5d2ab60ea481384d327f3b62efef7eab79eb6df447cfadfc6a5c0717b9b3a22592080eec1822c22380f1fa37bc0119d30878f3b8a78d93da2d3d06fd6b45f4eac4afed8fac66393b04666e6436c86f0a68e31e3013634c1a6c93ed70256f3a3bf47506baab07bfb578d48922eaeea881bacd7";

extern "C"
JNIEXPORT jstring JNICALL
Java_com_sonoptek_httpscheckdemo_MainActivity_getCourseKeyFromC(JNIEnv *env, jobject thiz) {
    // TODO: implement getCourseKeyFromC()
    jclass native_class = env->GetObjectClass(thiz);
    jmethodID pm_id = env->GetMethodID(native_class, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    jobject pm_obj = env->CallObjectMethod(thiz, pm_id);
    jclass pm_clazz = env->GetObjectClass(pm_obj);
// 得到 getPackageInfo 方法的 ID
    jmethodID package_info_id = env->GetMethodID(pm_clazz, "getPackageInfo","(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jclass native_classs = env->GetObjectClass(thiz);
    jmethodID mId = env->GetMethodID(native_classs, "getPackageName", "()Ljava/lang/String;");
    jstring pkg_str = static_cast<jstring>(env->CallObjectMethod(thiz, mId));
// 获得应用包的信息
    jobject pi_obj = env->CallObjectMethod(pm_obj, package_info_id, pkg_str, 64);
// 获得 PackageInfo 类
    jclass pi_clazz = env->GetObjectClass(pi_obj);
// 获得签名数组属性的 ID
    jfieldID signatures_fieldId = env->GetFieldID(pi_clazz, "signatures", "[Landroid/content/pm/Signature;");
    jobject signatures_obj = env->GetObjectField(pi_obj, signatures_fieldId);
    jobjectArray signaturesArray = (jobjectArray)signatures_obj;
    jsize size = env->GetArrayLength(signaturesArray);
    jobject signature_obj = env->GetObjectArrayElement(signaturesArray, 0);
    jclass signature_clazz = env->GetObjectClass(signature_obj);
    jmethodID string_id = env->GetMethodID(signature_clazz, "toCharsString", "()Ljava/lang/String;");
    jstring str = static_cast<jstring>(env->CallObjectMethod(signature_obj, string_id));
    char *c_msg = (char*)env->GetStringUTFChars(str,0);
//    return str;
    if(strcmp(c_msg,RELEASE_SIGN)==0)//签名一致  返回合法的 api key，否则返回错误
    {
        return (env)->NewStringUTF(AUTH_KEY);
    }else
    {
        return (env)->NewStringUTF("error");
    }
}

//签名信息
const char *app_sha1="549E10BB7ADFACDD3DBA242BB4D3AAC14CD4CFFE";
const char *key_sha1="E87D3CFBA3F500048930D8062D418E2B76A74AC2";
const char hexcode[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

char* getSha1(JNIEnv *env, jobject context_object){
    //上下文对象
    jclass context_class = env->GetObjectClass(context_object);

    //反射获取PackageManager
    jmethodID methodId = env->GetMethodID(context_class, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    jobject package_manager = env->CallObjectMethod(context_object, methodId);
    if (package_manager == NULL) {
//        __android_log_print(ANDROID_LOG_ERROR, "CPP-log", "package_manager is NULL!!!");
        return NULL;
    }

    //反射获取包名
    methodId = env->GetMethodID(context_class, "getPackageName", "()Ljava/lang/String;");
    jstring package_name = (jstring)env->CallObjectMethod(context_object, methodId);
    if (package_name == NULL) {
//        __android_log_print(ANDROID_LOG_ERROR, "CPP-log", "package_manager is NULL!!!");
        return NULL;
    }
    env->DeleteLocalRef(context_class);

    //获取PackageInfo对象
    jclass pack_manager_class = env->GetObjectClass(package_manager);
    methodId = env->GetMethodID(pack_manager_class, "getPackageInfo", "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    env->DeleteLocalRef(pack_manager_class);
    jobject package_info = env->CallObjectMethod(package_manager, methodId, package_name, 0x40);
    if (package_info == NULL) {
//        __android_log_print(ANDROID_LOG_ERROR, "CPP-log", "getPackageInfo() is NULL!!!");
        return NULL;
    }
    env->DeleteLocalRef(package_manager);

    //获取签名信息
    jclass package_info_class = env->GetObjectClass(package_info);
    jfieldID fieldId = env->GetFieldID(package_info_class, "signatures", "[Landroid/content/pm/Signature;");
    env->DeleteLocalRef(package_info_class);
    jobjectArray signature_object_array = (jobjectArray)env->GetObjectField(package_info, fieldId);
    if (signature_object_array == NULL) {
//        __android_log_print(ANDROID_LOG_ERROR, "CPP-log", "signature is NULL!!!");
        return NULL;
    }
    jobject signature_object = env->GetObjectArrayElement(signature_object_array, 0);
    env->DeleteLocalRef(package_info);

    //签名信息转换成sha1值
    jclass signature_class = env->GetObjectClass(signature_object);
    methodId = env->GetMethodID(signature_class, "toByteArray", "()[B");
    env->DeleteLocalRef(signature_class);
    jbyteArray signature_byte = (jbyteArray) env->CallObjectMethod(signature_object, methodId);
    jclass byte_array_input_class=env->FindClass("java/io/ByteArrayInputStream");
    methodId=env->GetMethodID(byte_array_input_class,"<init>","([B)V");
    jobject byte_array_input=env->NewObject(byte_array_input_class,methodId,signature_byte);
    jclass certificate_factory_class=env->FindClass("java/security/cert/CertificateFactory");
    methodId=env->GetStaticMethodID(certificate_factory_class,"getInstance","(Ljava/lang/String;)Ljava/security/cert/CertificateFactory;");
    jstring x_509_jstring=env->NewStringUTF("X.509");
    jobject cert_factory=env->CallStaticObjectMethod(certificate_factory_class,methodId,x_509_jstring);
    methodId=env->GetMethodID(certificate_factory_class,"generateCertificate",("(Ljava/io/InputStream;)Ljava/security/cert/Certificate;"));
    jobject x509_cert=env->CallObjectMethod(cert_factory,methodId,byte_array_input);
    env->DeleteLocalRef(certificate_factory_class);
    jclass x509_cert_class=env->GetObjectClass(x509_cert);
    methodId=env->GetMethodID(x509_cert_class,"getEncoded","()[B");
    jbyteArray cert_byte=(jbyteArray)env->CallObjectMethod(x509_cert,methodId);
    env->DeleteLocalRef(x509_cert_class);
    jclass message_digest_class=env->FindClass("java/security/MessageDigest");
    methodId=env->GetStaticMethodID(message_digest_class,"getInstance","(Ljava/lang/String;)Ljava/security/MessageDigest;");
    jstring sha1_jstring=env->NewStringUTF("SHA1");
    jobject sha1_digest=env->CallStaticObjectMethod(message_digest_class,methodId,sha1_jstring);
    methodId=env->GetMethodID(message_digest_class,"digest","([B)[B");
    jbyteArray sha1_byte=(jbyteArray)env->CallObjectMethod(sha1_digest,methodId,cert_byte);
    env->DeleteLocalRef(message_digest_class);

    //转换成char
    jsize array_size=env->GetArrayLength(sha1_byte);
    jbyte* sha1 =env->GetByteArrayElements(sha1_byte,NULL);
    char *hex_sha=new char[array_size*2+1];
//    __android_log_print(ANDROID_LOG_ERROR,"CPP-log","hex-sha1 %s",hex_sha);
    for (int i = 0; i <array_size ; ++i) {
        hex_sha[2*i]=hexcode[((unsigned char)sha1[i])/16];
        hex_sha[2*i+1]=hexcode[((unsigned char)sha1[i])%16];
    }
    hex_sha[array_size*2]='\0';


//    __android_log_print(ANDROID_LOG_ERROR, "CPP-log", "hex-sha %s",hex_sha);
    return hex_sha;
}
jboolean checkValidity(JNIEnv *env,char *sha1){
    //比较签名
    if (strcmp(sha1,key_sha1)==0)
    {
//        __android_log_print(ANDROID_LOG_ERROR,"CPP-log","验证成功");
        return true;
    }
//    __android_log_print(ANDROID_LOG_ERROR,"CPP-log","验证失败");
    return false;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_sonoptek_httpscheckdemo_MainActivity_getSha(JNIEnv *env, jobject thiz) {
    // TODO: implement getSha()

    return (env)->NewStringUTF(getSha1(env,thiz));
}

jstring getPackageName(JNIEnv *env,jobject context_object){

    //上下文对象
    jclass context_class = env->GetObjectClass(context_object);

    //反射获取PackageManager
    jmethodID methodId = env->GetMethodID(context_class, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    jobject package_manager = env->CallObjectMethod(context_object, methodId);
    if (package_manager == NULL) {
//        __android_log_print(ANDROID_LOG_ERROR, "CPP-log", "package_manager is NULL!!!");
        return NULL;
    }

    //反射获取包名
    methodId = env->GetMethodID(context_class, "getPackageName", "()Ljava/lang/String;");
    jstring package_name = (jstring)env->CallObjectMethod(context_object, methodId);

    if (package_name == NULL) {
//        __android_log_print(ANDROID_LOG_ERROR, "CPP-log", "package_manager is NULL!!!");
        return NULL;
    }
    env->DeleteLocalRef(context_class);
    return package_name;
}

jstring getVersionName(JNIEnv *env,jobject context_object){

    //上下文对象
    jclass context_class = env->GetObjectClass(context_object);

    //反射获取PackageManager
    jmethodID methodId = env->GetMethodID(context_class, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    jobject package_manager = env->CallObjectMethod(context_object, methodId);
    if (package_manager == NULL) {
//        __android_log_print(ANDROID_LOG_ERROR, "CPP-log", "package_manager is NULL!!!");
        return NULL;
    }

    //反射获取version
    methodId = env->GetMethodID(context_class, "getVersionName", "()Ljava/lang/String;");
    jstring version_name = (jstring)env->CallObjectMethod(context_object, methodId);
    if (version_name == NULL) {
//        __android_log_print(ANDROID_LOG_ERROR, "CPP-log", "package_manager is NULL!!!");
        return NULL;
    }
    env->DeleteLocalRef(context_class);
    return version_name;
}

const jbyte imageType[]={120,-55,-123,3,-48,-58,27,80,-67,56,-35,20,118,95,105,-1};
extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sonoptek_httpscheckdemo_MainActivity_toBytes(JNIEnv *env, jobject thiz) {
    jbyteArray kvArray = env->NewByteArray(sizeof(imageType));
    jbyte *bytes = env->GetByteArrayElements(kvArray,0);
    int i;
    for (i = 0; i < sizeof(imageType);i++){

        bytes[i] = (jbyte)imageType[i];
    }
    env->SetByteArrayRegion(kvArray, 0, sizeof(imageType),bytes);
    env->ReleaseByteArrayElements(kvArray,bytes,0);

    return kvArray;
}

/*
 *  A helper function to show how to call
 *     java static functions JniHelper::getBuildVersion()
 *     java non-static function JniHelper::getRuntimeMemorySize()
 *  The trivial implementation for these functions are inside file
 *     JniHelper.java
 */
void queryRuntimeInfo(JNIEnv *env, jobject instance) {
    // Find out which OS we are running on. It does not matter for this app
    // just to demo how to call static functions.
    // Our java JniHelper class id and instance are initialized when this
    // shared lib got loaded, we just directly use them
    //    static function does not need instance, so we just need to feed
    //    class and method id to JNI
    jmethodID versionFunc = env->GetStaticMethodID(
            g_ctx.jniHelperClz,
            "bb", "()Ljava/lang/String;");
    if (!versionFunc) {
//        LOGE("Failed to retrieve getBuildVersion() methodID @ line %d",
//             __LINE__);
        return;
    }
    jstring buildVersion = (jstring)env->CallStaticObjectMethod(g_ctx.jniHelperClz, versionFunc);
    const char *version = env->GetStringUTFChars(buildVersion, NULL);
    if (!version) {
//        LOGE("Unable to get version string @ line %d", __LINE__);
        return;
    }
//    LOGI("Android Version - %s", version);
    env->ReleaseStringUTFChars(buildVersion, version);

    // we are called from JNI_OnLoad, so got to release LocalRef to avoid leaking
    env->DeleteLocalRef( buildVersion);

    // Query available memory size from a non-static public function
    // we need use an instance of JniHelper class to call JNI
    jmethodID memFunc = env->GetMethodID(g_ctx.jniHelperClz,
                                         "bbb", "()J");
    if (!memFunc) {
//        LOGE("Failed to retrieve getRuntimeMemorySize() methodID @ line %d",
//             __LINE__);
        return;
    }
    jlong result = env->CallLongMethod( instance, memFunc);
//    LOGI("Runtime free memory size: %" PRId64, result);
    (void)result;  // silence the compiler warning
}
/*
 * processing one time initialization:
 *     Cache the javaVM into our context
 *     Find class ID for JniHelper
 *     Create an instance of JniHelper
 *     Make global reference since we are using them from a native thread
 * Note:
 *     All resources allocated here are never released by application
 *     we rely on system to free all global refs when it goes away;
 *     the pairing function JNI_OnUnload() never gets called at all.
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    memset(&g_ctx, 0, sizeof(g_ctx));

    g_ctx.javaVM = vm;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR; // JNI version not supported.
    }

    jclass  clz = env->FindClass("com/sonoptek/httpscheckdemo/bb");
    g_ctx.jniHelperClz = (jclass)env->NewGlobalRef(clz);

    jmethodID  jniHelperCtor = env->GetMethodID(g_ctx.jniHelperClz,
                                                "<init>", "()V");
    jobject    handler = env->NewObject(g_ctx.jniHelperClz,
                                        jniHelperCtor);
    g_ctx.jniHelperObj = env->NewGlobalRef( handler);
    queryRuntimeInfo(env, g_ctx.jniHelperObj);

    g_ctx.done = 0;
    g_ctx.mainActivityObj = NULL;
    return  JNI_VERSION_1_6;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_sonoptek_httpscheckdemo_MainActivity_init(JNIEnv *env, jobject thiz) {
    // TODO: implement init()

    pthread_t       threadInfo_;
    pthread_attr_t  threadAttr_;

    pthread_attr_init(&threadAttr_);
    pthread_attr_setdetachstate(&threadAttr_, PTHREAD_CREATE_DETACHED);

    pthread_mutex_init(&g_ctx.lock, NULL);

    jclass clz = env->GetObjectClass( thiz);
    g_ctx.mainActivityClz = (jclass)env->NewGlobalRef( clz);
    g_ctx.mainActivityObj = (jobject)env->NewGlobalRef(thiz);

    pthread_attr_destroy(&threadAttr_);
}
//key
const jbyte imageType0[]={120,-55,-123,3,-48,-58,27,80,-67,56,-35,20,118,95,105,-1};
extern "C"
JNIEXPORT jstring JNICALL
Java_com_sonoptek_httpscheckdemo_MainActivity_getImageData(JNIEnv *env, jobject thiz, jint id) {
    // TODO: implement getImageData()


    jbyteArray kvArray = env->NewByteArray(sizeof(imageType0));
    jbyte *bytes = env->GetByteArrayElements(kvArray,0);
    int i;
    for (i = 0; i < sizeof(imageType0);i++){
        bytes[i] = (jbyte)imageType0[i];
    }
    env->SetByteArrayRegion(kvArray, 0, sizeof(imageType0),bytes);
    env->ReleaseByteArrayElements(kvArray,bytes,0);
    jmethodID byte2String = env->GetMethodID( g_ctx.jniHelperClz,
                                       "aa","([B)Ljava/lang/String;");
    jstring str=(jstring)env->CallStaticObjectMethod(g_ctx.jniHelperClz,byte2String,kvArray);

    jmethodID func = env->GetStaticMethodID( g_ctx.jniHelperClz,
                                             "a","(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
    jstring ret=(jstring)env->CallStaticObjectMethod(g_ctx.jniHelperClz, func, env->NewStringUTF("SonopTek_2012"), str);
    return ret;
}

jstring charTojstring(JNIEnv* env, const char* pat) {
    //定义java String类 strClass
    jclass strClass = (env)->FindClass("java/lang/String");
    //获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    //建立byte数组
    jbyteArray bytes = (env)->NewByteArray(strlen(pat));
    //将char* 转换为byte数组
    (env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*) pat);
    // 设置String, 保存语言类型,用于byte数组转换至String时的参数
    jstring encoding = (env)->NewStringUTF("GB2312");
    //将byte数组转换为java String,并输出
    return (jstring) (env)->NewObject(strClass, ctorID, bytes, encoding);
}

//C字符串转java字符串
jstring strToJstring(JNIEnv* env, const char* pStr)
{
    int        strLen    = strlen(pStr);
    jclass     jstrObj   = env->FindClass( "java/lang/String");
    jmethodID  methodId  = env->GetMethodID( jstrObj, "<init>", "([BLjava/lang/String;)V");
    jbyteArray byteArray = env->NewByteArray( strLen);
    jstring    encode    = env->NewStringUTF( "utf-8");

    env->SetByteArrayRegion( byteArray, 0, strLen, (jbyte*)pStr);

    return (jstring) env->NewObject( jstrObj, methodId, byteArray, encode);
}
const char * probes[]={"UL-1C","US-1C","UX-1C"};

const char*  sa[] = { "Hello", "world!", "JNI", "很", "好玩" };

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_sonoptek_httpscheckdemo_MainActivity_getSupportProbeList(JNIEnv *env, jobject thiz,
                                                                  jobject context) {
    // TODO: implement getSupportProbeList()

    jclass objClass = env->FindClass("java/lang/String");
    jobjectArray texts= env->NewObjectArray(sizeof(sa), objClass, nullptr);
    jstring jstr;
    int i=0;
    for(;i< sizeof(sa);i++)
    {
        const char *jstring1="hhhh";
        char char1[]="gggg";
        strcpy(char1,jstring1);
//        jstr = env->NewStringUTF(char1);
//        jstr=charTojstring(env,char1);

        jstr=strToJstring(env,char1);
        env->SetObjectArrayElement(texts, i, jstr);//必须放入jstring
    }
    return texts;
}
/*
extern "C"
JNIEXPORT jobject JNICALL
Java_com_sonoptek_httpscheckdemo_MainActivity_getUser(JNIEnv *env, jobject thiz, jstring name) {
    // TODO: implement getUser()


}extern "C"
JNIEXPORT jobject JNICALL
Java_com_sonoptek_httpscheckdemo_User_getUser(JNIEnv *env, jobject thiz, jstring name) {
    // TODO: implement getUser()
}*/
