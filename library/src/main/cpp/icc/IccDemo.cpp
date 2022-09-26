//
// Created by 钟元杰 on 2022/9/19.
//

//#include "include/IccDemo.h"
#include "IccCmm.h"
#include "IccProfile.h"
#include <jni.h>

CIccCmm *cmm = nullptr;
icFloatNumber Pixels[16];

icUInt8Number* ConvertJByteaArrayToChars(JNIEnv *env, jbyteArray bytearray)
{
    icUInt8Number *chars = NULL;
    jbyte *bytes;
    bytes = env->GetByteArrayElements(bytearray, 0);
    int chars_len = env->GetArrayLength(bytearray);
    chars = new icUInt8Number[chars_len + 1];
    memset(chars,0,chars_len + 1);
    memcpy(chars, bytes, chars_len);
    chars[chars_len] = 0;

    env->ReleaseByteArrayElements(bytearray, bytes, 0);
    return chars;
}

extern "C"
JNIEXPORT jint JNICALL Java_com_xsooy_icc_IccUtils_loadProfile(JNIEnv *env, jobject thiz, jstring path) {
    delete cmm;
    cmm = new CIccCmm;
    if (cmm->GetNumXforms()!=0) {
        return 1;
    }
    const char *nativeString = env->GetStringUTFChars(path, 0);
    if (cmm->AddXform(nativeString, (icRenderingIntent)0)) {
//            printf("Invalid Profile:  %s\n", szSrcProfile);
        return -1;
    }
    if (cmm->Begin() != icCmmStatOk) {
        return false;
    }
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL Java_com_xsooy_icc_IccUtils_loadProfileByData(JNIEnv *env, jobject thiz, jbyteArray data) {
    delete cmm;
    icUInt8Number *pmsg = ConvertJByteaArrayToChars(env,data);
    int chars_len = env->GetArrayLength(data);
    CIccProfile* cIccProfile = OpenIccProfile(pmsg, chars_len);
    if (cmm->AddXform(cIccProfile, (icRenderingIntent)0)) {
        return -1;
    }
    if (cmm->Begin() != icCmmStatOk) {
        return false;
    }
    return cmm->GetSourceSpace();
}

extern "C"
JNIEXPORT jfloat JNICALL Java_com_xsooy_icc_IccUtils_apply(JNIEnv *env, jobject thiz, jfloat pixel) {
    Pixels[0] = (float) pixel;
    cmm->Apply(Pixels, Pixels);
    return Pixels[0];
}

extern "C"
JNIEXPORT void JNICALL Java_com_xsooy_icc_IccUtils_applyGray(JNIEnv *env, jobject thiz, jfloatArray array,jfloatArray outArray) {
    jboolean isCopy = JNI_FALSE;
    jfloat *parray = env->GetFloatArrayElements(array, &isCopy);
    Pixels[0] = float (parray[0]);

    cmm->Apply(Pixels, Pixels);

    env->SetFloatArrayRegion(outArray,0,3,Pixels);
}

extern "C"
JNIEXPORT void JNICALL Java_com_xsooy_icc_IccUtils_applyCmyk(JNIEnv *env, jobject thiz, jfloatArray array,jfloatArray outArray) {
    jboolean isCopy = JNI_FALSE;
    jfloat *parray = env->GetFloatArrayElements(array, &isCopy);
    Pixels[0] = float (parray[0]);
    Pixels[1] = float (parray[1]);
    Pixels[2] = float (parray[2]);
    Pixels[3] = float (parray[3]);

    //change data to 'lab'
    cmm->Apply(Pixels, Pixels);
    env->SetFloatArrayRegion(outArray,0,3,Pixels);
}

