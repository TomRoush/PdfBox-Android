//
// Created by 钟元杰 on 2022/10/28.
//

#include <jni.h>
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <cerrno>
#include <zlib.h>
#include <sys/stat.h>

#include "jbig2.h"
#include "jbig2_priv.h"
#include "jbig2_image.h"
//#include "jbig2_image_rw.h"

#include <android/log.h>
#define TAG "JBIG_TEST"
#define pri_debug(format, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, "[%s:%d]" format, "XSOOY", __LINE__, ##args)


#define ALIGNMENT 16
#define KBYTE 1024
#define MBYTE (1024 * KBYTE)

//typedef struct {
//    jbig2dec_mode mode;
//    int verbose, hash, embedded;
//    SHA1_CTX *hash_ctx;
//    char *output_filename;
//    jbig2dec_format output_format;
//    size_t memory_limit;
//} jbig2dec_params_t;
extern "C" {

typedef struct {
    int verbose;
    char *last_message;
    Jbig2Severity severity;
    char *type;
    long repeats;
} jbig2dec_error_callback_state_t;

typedef struct {
    Jbig2Allocator super;
    Jbig2Ctx *ctx;
    size_t memory_limit;
    size_t memory_used;
    size_t memory_peak;
} jbig2dec_allocator_t;

unsigned char* ConvertJByteaArrayToChars(JNIEnv *env, jbyteArray bytearray)
{
    unsigned char *chars = nullptr;
    jbyte *bytes;
    bytes = env->GetByteArrayElements(bytearray, 0);
    int chars_len = env->GetArrayLength(bytearray);
    chars = (unsigned char *)malloc(chars_len+1);
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

static void *jbig2dec_reached_limit(jbig2dec_allocator_t *allocator, size_t oldsize, size_t size)
{
    size_t limit_mb = allocator->memory_limit / MBYTE;
    size_t used_mb = allocator->memory_used / MBYTE;
    size_t oldsize_mb = oldsize / MBYTE;
    size_t size_mb = size / MBYTE;

    if (oldsize == 0)
        jbig2_error(allocator->ctx, JBIG2_SEVERITY_FATAL, -1, "memory: limit reached: limit: %zu (%zu Mbyte) used: %zu (%zu Mbyte) allocation: %zu (%zu Mbyte)",
                    allocator->memory_limit, limit_mb,
                    allocator->memory_used, used_mb,
                    size, size_mb);
    else
        jbig2_error(allocator->ctx, JBIG2_SEVERITY_FATAL, -1, "memory: limit reached: limit: %zu (%zu Mbyte) used: %zu (%zu Mbyte) reallocation: %zu (%zu Mbyte) -> %zu (%zu Mbyte)",
                    allocator->memory_limit, limit_mb,
                    allocator->memory_used, used_mb,
                    oldsize, oldsize_mb,
                    size, size_mb);

    return NULL;
}

static void jbig2dec_peak(jbig2dec_allocator_t *allocator)
{
    size_t limit_mb = allocator->memory_limit / MBYTE;
    size_t peak_mb = allocator->memory_peak / MBYTE;
    size_t used_mb = allocator->memory_used / MBYTE;

    if (allocator->ctx == NULL)
        return;
    if (used_mb <= peak_mb)
        return;

    allocator->memory_peak = allocator->memory_used;

    jbig2_error(allocator->ctx, JBIG2_SEVERITY_DEBUG, JBIG2_UNKNOWN_SEGMENT_NUMBER, "memory: limit: %lu %sbyte used: %lu %sbyte, peak: %lu %sbyte",
                limit_mb > 0 ? limit_mb : allocator->memory_limit, limit_mb > 0 ? "M" : "",
                used_mb > 0 ? used_mb : allocator->memory_used, used_mb > 0 ? "M" : "",
                peak_mb > 0 ? peak_mb : allocator->memory_peak, peak_mb > 0 ? "M" : "");
}

static void *jbig2dec_alloc(Jbig2Allocator *allocator_, size_t size)
{
    jbig2dec_allocator_t *allocator = (jbig2dec_allocator_t *) allocator_;
    void *ptr;

    if (size == 0)
        return NULL;
    if (size > SIZE_MAX - ALIGNMENT)
        return NULL;

    if (size + ALIGNMENT > allocator->memory_limit - allocator->memory_used)
        return jbig2dec_reached_limit(allocator, 0, size + ALIGNMENT);

    ptr = malloc(size + ALIGNMENT);
    if (ptr == NULL)
        return NULL;
    memcpy(ptr, &size, sizeof(size));
    allocator->memory_used += size + ALIGNMENT;

    jbig2dec_peak(allocator);

    return (unsigned char *) ptr + ALIGNMENT;
}

static void jbig2dec_free(Jbig2Allocator *allocator_, void *p)
{
    jbig2dec_allocator_t *allocator = (jbig2dec_allocator_t *) allocator_;
    size_t size;

    if (p == NULL)
        return;

    memcpy(&size, (unsigned char *) p - ALIGNMENT, sizeof(size));
    allocator->memory_used -= size + ALIGNMENT;
    free((unsigned char *) p - ALIGNMENT);
}


static void *jbig2dec_realloc(Jbig2Allocator *allocator_, void *p, size_t size)
{
    jbig2dec_allocator_t *allocator = (jbig2dec_allocator_t *) allocator_;
    unsigned char *oldp;
    size_t oldsize;

    if (p == NULL)
        return jbig2dec_alloc(allocator_, size);
    if (p < (void *) ALIGNMENT)
        return NULL;

    if (size == 0) {
        jbig2dec_free(allocator_, p);
        return NULL;
    }
    if (size > SIZE_MAX - ALIGNMENT)
        return NULL;

    oldp = (unsigned char *) p - ALIGNMENT;
    memcpy(&oldsize, oldp, sizeof(oldsize));

    if (size + ALIGNMENT > allocator->memory_limit - allocator->memory_used + oldsize + ALIGNMENT)
        return jbig2dec_reached_limit(allocator, oldsize + ALIGNMENT, size + ALIGNMENT);

    p = realloc(oldp, size + ALIGNMENT);
    if (p == NULL)
        return NULL;

    allocator->memory_used -= oldsize + ALIGNMENT;
    memcpy(p, &size, sizeof(size));
    allocator->memory_used += size + ALIGNMENT;

    jbig2dec_peak(allocator);

    return (unsigned char *) p + ALIGNMENT;
}

//    static
    void error_callback(void *error_callback_data, const char *message, Jbig2Severity severity, uint32_t seg_idx)
    {
        jbig2dec_error_callback_state_t *state = (jbig2dec_error_callback_state_t *) error_callback_data;
        char *type;
        int ret;

        switch (severity) {
            case JBIG2_SEVERITY_DEBUG:
                if (state->verbose < 3)
                    return;
                type = "DEBUG";
                break;
            case JBIG2_SEVERITY_INFO:
                if (state->verbose < 2)
                    return;
                type = "info";
                break;
            case JBIG2_SEVERITY_WARNING:
                if (state->verbose < 1)
                    return;
                type = "WARNING";
                break;
            case JBIG2_SEVERITY_FATAL:
                type = "FATAL ERROR";
                break;
            default:
                type = "unknown message";
                break;
        }

        if (state->last_message != NULL && !strcmp(message, state->last_message) && state->severity == severity && state->type == type) {
            state->repeats++;
            if (state->repeats % 1000000 == 0) {
                if (state->type!= nullptr)
                    pri_debug("jbig2dec %s last message repeated %ld times so far\n", state->type, state->repeats);
                else
                    pri_debug("jbig2dec last message repeated %ld times so far\n", state->repeats);
//                ret = fprintf(stderr, "jbig2dec %s last message repeated %ld times so far\n", state->type, state->repeats);
//                if (ret < 0)
//                    goto printerror;
            }
        } else {
            if (state->repeats > 1) {
//                pri_debug("error_callback222 %s",state->type);
//                pri_debug("jbig2dec %s last message repeated %ld times\n", state->type, state->repeats);
                pri_debug("jbig2dec last message repeated %ld times\n", state->repeats);
//                ret = fprintf(stderr, "jbig2dec %s last message repeated %ld times\n", state->type, state->repeats);
//                if (ret < 0)
//                    goto printerror;
            }

            if (seg_idx == JBIG2_UNKNOWN_SEGMENT_NUMBER){
                pri_debug("jbig2dec %s %s\n", type, message);
            }
//                ret = fprintf(stderr, "jbig2dec %s %s\n", type, message);
            else {
                pri_debug("jbig2dec %s %s (segment 0x%08x)\n", type, message, seg_idx);
            }

//                ret = fprintf(stderr, "jbig2dec %s %s (segment 0x%08x)\n", type, message, seg_idx);
//            if (ret < 0)
//                goto printerror;

            state->repeats = 0;
            state->severity = severity;
            state->type = type;
//            free(state->last_message);
            state->last_message = NULL;

            if (message) {
                state->last_message = strdup(message);
                if (state->last_message == NULL) {
                    pri_debug("error_callback555");
                    pri_debug("jbig2dec WARNING could not duplicate message\n");
//                    ret = fprintf(stderr, "jbig2dec WARNING could not duplicate message\n");
//                    if (ret < 0)
//                        goto printerror;
                }
            }
        }

//        return;
//        printerror:
//        pri_debug("error_callback666");
//        pri_debug("jbig2dec WARNING could not print message\n");
//        fprintf(stderr, "jbig2dec WARNING could not print message\n");
//        state->repeats = 0;
//        free(state->last_message);
//        state->last_message = NULL;
    }

    JNIEXPORT jbyteArray JNICALL Java_com_xsooy_jbig2_Jbig2Utils_converData(JNIEnv *env, jobject thiz, jbyteArray data) {

    unsigned char *pmsg = ConvertJByteaArrayToChars(env,data);
    int chars_len = env->GetArrayLength(data);

//    jbig2dec_params_t params;
    jbig2dec_error_callback_state_t error_callback_state;
    Jbig2Ctx *ctx = NULL;
    jbig2dec_allocator_t allocator_;
    jbig2dec_allocator_t *allocator = &allocator_;

    Jbig2Image *image;

    allocator->super.alloc = jbig2dec_alloc;
    allocator->super.free = jbig2dec_free;
    allocator->super.realloc = jbig2dec_realloc;
    allocator->ctx = NULL;
//    allocator->memory_limit = params.memory_limit;
    allocator->memory_used = 0;
    allocator->memory_peak = 0;

    ctx = jbig2_ctx_new((Jbig2Allocator *) allocator, (Jbig2Options) JBIG2_OPTIONS_EMBEDDED, NULL, error_callback, &error_callback_state);
    jbig2_data_in(ctx, pmsg, chars_len);

    int code = jbig2_complete_page(ctx);

    if (code>=0 && ctx->max_page_index>0) {
        image = jbig2_page_out(ctx);
        if (image!=NULL) {
//            pri_debug("4444_111 %d,%d",image->width,image->height);
//            pri_debug("4444_111 %d",image->stride);
            jbyteArray result = ConvertCharsToJByteaArray(env,image->data,(int)(image->stride*image->height));
            jbig2_release_page(ctx, image);
            return result;
        } else {
            pri_debug("没有图片？？？");
        }
    }
    return nullptr;
//    while ((image = jbig2_page_out(ctx)) != NULL) {
//        result = ConvertCharsToJByteaArray(env,image->data,image->width*image->height);
//        jbig2_release_page(ctx, image);
//    }

//    jbyteArray result = ConvertCharsToJByteaArray(env,raw_image,width*height*depth);
//    return result;
}

}

