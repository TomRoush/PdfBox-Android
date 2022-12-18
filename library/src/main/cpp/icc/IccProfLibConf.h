/** @file
    File:       IccProfLibConf.h

    Contains:   Platform Specific Configuration

    Version:    V1

    Copyright:  � see ICC Software License
*/

/*
 * The ICC Software License, Version 0.2
 *
 *
 * Copyright (c) 2003-2015 The International Color Consortium. All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. In the absence of prior written permission, the names "ICC" and "The
 *    International Color Consortium" must not be used to imply that the
 *    ICC organization endorses or promotes products derived from this
 *    software.
 *
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE INTERNATIONAL COLOR CONSORTIUM OR
 * ITS CONTRIBUTING MEMBERS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the The International Color Consortium. 
 *
 *
 * Membership in the ICC is encouraged when this software is used for
 * commercial purposes. 
 *
 *  
 * For more information on The International Color Consortium, please
 * see <http://www.color.org/>.
 *  
 * 
 */

/* Header file guard bands */
#ifndef ICCCONFIG_h
#define ICCCONFIG_h

//Define the following to use namespace
//#define USESAMPLEICCNAMESPACE

#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif

//PC, visual C++
#if defined(_MSC_VER) && !defined(__MWERKS__) && (defined(_M_IX86) || defined(_M_X64) || defined(__amd64__))

  //Define how 64 bit integers are represented
  #define ICCUINT64 unsigned __int64
  #define ICCINT64 __int64
  #define ICUINT64TYPE unsigned __int64
  #define ICINT64TYPE __int64

  #define ICCUINT32 unsigned long
  #define ICCINT32  long
  #define ICUINT32TYPE unsigned long
  #define ICINT32TYPE  long

  #define USE_WINDOWS_MB_SUPPORT
  #define WIN32_LEAN_AND_MEAN    // Exclude rarely-used stuff from Windows headers
  //#include <windows.h> //For Multibyte Translation Support

  #define ICC_BYTE_ORDER_LITTLE_ENDIAN

  #if defined(ICCPROFLIBDLL_EXPORTS)
    #define ICCPROFLIB_API _declspec(dllexport)
    #define ICCPROFLIB_EXTERN
  #elif defined(ICCPROFLIBDLL_IMPORTS)
    #define ICCPROFLIB_API _declspec(dllimport)
    #define ICCPROFLIB_EXTERN extern
  #else //just a regular lib
    #define ICCPROFLIB_API
    #define ICCPROFLIB_EXTERN
  #endif

  //Since msvc doesn't support cbrtf use pow instead
  #define ICC_CBRTF(v) pow((double)(v), 1.0/3.0)

  #if (_MSC_VER < 1300)
    #define ICC_UNSUPPORTED_TAG_DICT
  #endif

#else // non-PC, perhaps Mac, Linux, or Solaris

  #define ICCUINT64 unsigned long long
  #define ICCINT64  long long
  #define ICUINT64TYPE unsigned long long
  #define ICINT64TYPE long long

  #include <stdint.h>

  //Make sure that 32 bit values are set correctly
  #define ICCUINT32 uint32_t
  #define ICCINT32  int32_t
  #define ICUINT32TYPE uint32_t
  #define ICINT32TYPE  int32_t

  #if defined(__APPLE__)
    #if  defined(__LITTLE_ENDIAN__)
      #define ICC_BYTE_ORDER_LITTLE_ENDIAN
    #else
      #define ICC_BYTE_ORDER_BIG_ENDIAN
    #endif

  #else // Sun Solaris or Linux
    #if defined(__sun__)
      #define ICC_BYTE_ORDER_BIG_ENDIAN
    #else
      #define ICC_BYTE_ORDER_LITTLE_ENDIAN
    #endif
  #endif

  #define ICCPROFLIB_API
  #define ICCPROFLIB_EXTERN
  #define stricmp strcasecmp

  //Define ICC_CBRTF as a call to cbrtf (replace with pow if system doesn't support cbrtf)
  #define ICC_CBRTF(v) cbrtf(v)

//  #define ICC_WCHAR_32BIT

  #define ICC_ENUM_CONVENIENCE

#endif

// remove comment below if you want LAB to XYZ conversions to not clip negative XYZ values
#define SAMPLEICC_NOCLIPLABTOXYZ

#ifdef SAMPLEICCCMM_EXPORTS
#define MAKE_A_DLL
#endif

#ifdef MAKE_A_DLL
#define SAMPLEICCEXPORT __declspec( dllexport)
#else
#define SAMPLEICCEXPORT __declspec( dllimport)
#endif

#ifdef USESAMPLEICCNAMESPACE
}
#endif

#endif //ICCCOFIG_h
