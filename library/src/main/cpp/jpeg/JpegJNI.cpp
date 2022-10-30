//
// Created by 钟元杰 on 2022/9/19.
//

//#include "include/IccDemo.h"

#include <jni.h>
//#include <android/log.h>
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <cerrno>
#include <zlib.h>
#include <sys/stat.h>

#include "jpeglib.h"
#include "jpegint.h"

//#define TAG "Jpeg_ceshi"
//#define pri_debug(format, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, "[%s:%d]" format, "XSOOY", __LINE__, ##args)


extern "C"{
unsigned char* ConvertJByteaArrayToChars(JNIEnv *env, jbyteArray bytearray)
{

    unsigned char *chars = nullptr;
    jbyte *bytes;
    bytes = env->GetByteArrayElements(bytearray, 0);
    int chars_len = env->GetArrayLength(bytearray);
    chars = new unsigned char[chars_len + 1];
    memset(chars,0,chars_len + 1);
    memcpy(chars, bytes, chars_len);
    chars[chars_len] = 0;

    env->ReleaseByteArrayElements(bytearray, bytes, 0);
    return chars;
}

jbyteArray ConvertCharsToJByteaArray(JNIEnv *env, unsigned char *buff,int size)
{
    jbyteArray arr = env->NewByteArray(size);
    env->SetByteArrayRegion(arr, 0, size, (jbyte*)buff);
    return arr;
}

JNIEXPORT jbyteArray JNICALL Java_com_xsooy_jpeg_JpegUtils_converData(JNIEnv *env, jobject thiz, jbyteArray data) {
    unsigned char *pmsg = ConvertJByteaArrayToChars(env,data);
    int chars_len = env->GetArrayLength(data);

    struct jpeg_decompress_struct cinfo;

    jpeg_create_decompress(&cinfo);

    struct jpeg_error_mgr mjerr;
    cinfo.err = jpeg_std_error(&mjerr);

    jpeg_mem_src(&cinfo, pmsg, chars_len);

    jpeg_read_header(&cinfo,TRUE);

    jpeg_start_decompress(&cinfo);

    unsigned int width = cinfo.output_width;
    unsigned int height = cinfo.output_height;
    unsigned short depth = cinfo.output_components; //get from libjpeg. 1 for gray, 3 for color.


//    pri_debug("cinfo.jpeg_color_space:%d",cinfo.quantize_colors);
//    pri_debug("cinfo.out_color_space:%d",cinfo.out_color_space);
//    pri_debug("cinfo.out_color_space:%s",cinfo.colormap[0]);

    JSAMPROW row_pointer[1];
    unsigned long location = 0;
    unsigned char * raw_image = (unsigned char*)malloc( cinfo.output_width*cinfo.output_height*cinfo.num_components );
    row_pointer[0] = (unsigned char *)malloc( cinfo.output_width*cinfo.num_components );


    for (int y=0;y<height;y++){
//        pri_debug("jpeg_read_scanlines222====,%d",y);
        jpeg_read_scanlines( &cinfo, row_pointer, 1);
        for(int i=0; i<cinfo.image_width*cinfo.num_components;i++)
            raw_image[location++] = row_pointer[0][i];
    }



    jbyteArray result = ConvertCharsToJByteaArray(env,raw_image,width*height*depth);

//    jpeg_finish_decompress( &cinfo );
//    jpeg_destroy_decompress( &cinfo );
    free( row_pointer[0] );

//    pri_debug("result::%d",env->GetArrayLength(result));
    return result;
}

JNIEXPORT void JNICALL Java_com_xsooy_jpeg_JpegUtils_converDataToArray(JNIEnv *env, jobject thiz, jbyteArray data,jbyteArray output) {
    unsigned char *pmsg = ConvertJByteaArrayToChars(env,data);
    int chars_len = env->GetArrayLength(data);

    struct jpeg_decompress_struct cinfo;

    jpeg_create_decompress(&cinfo);

    struct jpeg_error_mgr mjerr;
    cinfo.err = jpeg_std_error(&mjerr);

    jpeg_mem_src(&cinfo, pmsg, chars_len);

    jpeg_read_header(&cinfo,TRUE);

    jpeg_start_decompress(&cinfo);

    unsigned int width = cinfo.output_width;
    unsigned int height = cinfo.output_height;
    unsigned short depth = cinfo.output_components; //get from libjpeg. 1 for gray, 3 for color.

//    pri_debug("cinfo.jpeg_color_space:%d",cinfo.quantize_colors);
//    pri_debug("cinfo.out_color_space:%d",cinfo.out_color_space);
//    pri_debug("cinfo.out_color_space:%s",cinfo.colormap[0]);

    JSAMPROW row_pointer[1];
    unsigned long location = 0;
    unsigned char * raw_image = (unsigned char*)malloc( cinfo.output_width*cinfo.output_height*cinfo.num_components );
    row_pointer[0] = (unsigned char *)malloc( cinfo.output_width*cinfo.num_components );

    for (int y=0;y<height;y++){
//        pri_debug("jpeg_read_scanlines222====,%d",y);
        jpeg_read_scanlines( &cinfo, row_pointer, 1);
        for(int i=0; i<cinfo.image_width*cinfo.num_components;i++)
            raw_image[location++] = row_pointer[0][i];
    }

    env->SetByteArrayRegion(output, 0, width*height*depth, (jbyte*)raw_image);
//    jbyteArray result = ConvertCharsToJByteaArray(env,raw_image,width*height*depth);
//    jpeg_finish_decompress( &cinfo );
//    jpeg_destroy_decompress( &cinfo );
    free( row_pointer[0]);

}

}