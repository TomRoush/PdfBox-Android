/** @file
    File:       IccUtil.h

    Contains:   Implementation of utility classes/functions

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

////////////////////////////////////////////////////////////////////// 
// HISTORY:
//
// -Initial implementation by Max Derhak 5-15-2003
//
//////////////////////////////////////////////////////////////////////

#ifndef _ICCUTIL_H
#define _ICCUTIL_H

#include "IccDefs.h"
#include "IccProfLibConf.h"
#include <string>

#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif

double ICCPROFLIB_API icRoundOffset(double v);

icValidateStatus ICCPROFLIB_API icMaxStatus(icValidateStatus s1, icValidateStatus s2);
bool ICCPROFLIB_API icIsSpaceCLR(icColorSpaceSignature sig);

void ICCPROFLIB_API icColorIndexName(icChar *szName, icColorSpaceSignature csSig,
                      int nIndex, int nColors, const icChar *szUnknown);
void ICCPROFLIB_API icColorValue(icChar *szValue, icFloatNumber nValue,
                  icColorSpaceSignature csSig, int nIndex, bool bUseLegacy=false);

bool ICCPROFLIB_API icMatrixInvert3x3(icFloatNumber *matrix);
void ICCPROFLIB_API icMatrixMultiply3x3(icFloatNumber *result,
                                        const icFloatNumber *l,
                                        const icFloatNumber *r);
void ICCPROFLIB_API icVectorApplyMatrix3x3(icFloatNumber *result,
                                           const icFloatNumber *m,
                                           const icFloatNumber *v);

icS15Fixed16Number ICCPROFLIB_API icDtoF(icFloatNumber num);
icFloatNumber ICCPROFLIB_API icFtoD(icS15Fixed16Number num);

icU16Fixed16Number ICCPROFLIB_API icDtoUF(icFloatNumber num);
icFloatNumber ICCPROFLIB_API icUFtoD(icU16Fixed16Number num);

icU1Fixed15Number ICCPROFLIB_API icDtoUSF(icFloatNumber num);
icFloatNumber ICCPROFLIB_API icUSFtoD(icU1Fixed15Number num);

icU8Fixed8Number ICCPROFLIB_API icDtoUCF(icFloatNumber num);
icFloatNumber ICCPROFLIB_API icUCFtoD(icU8Fixed8Number num);

/*0 to 255 <-> 0.0 to 1.0*/
icUInt8Number ICCPROFLIB_API icFtoU8(icFloatNumber num);
icFloatNumber ICCPROFLIB_API icU8toF(icUInt8Number num);

/*0 to 65535 <-> 0.0 to 1.0*/
icUInt16Number ICCPROFLIB_API icFtoU16(icFloatNumber num);
icFloatNumber ICCPROFLIB_API icU16toF(icUInt16Number num);

/*0 to 255 <-> -128.0 to 127.0*/
icUInt8Number ICCPROFLIB_API icABtoU8(icFloatNumber num);
icFloatNumber ICCPROFLIB_API icU8toAB(icUInt8Number num);

extern ICCPROFLIB_API icFloatNumber icD50XYZ[3];
extern ICCPROFLIB_API icFloatNumber icD50XYZxx[3];

void ICCPROFLIB_API icNormXYZ(icFloatNumber *XYZ, icFloatNumber *WhiteXYZ=NULL);
void ICCPROFLIB_API icDeNormXYZ(icFloatNumber *XYZ, icFloatNumber *WhiteXYZ=NULL);

void ICCPROFLIB_API icXYZtoLab(icFloatNumber *Lab, icFloatNumber *XYZ=NULL, icFloatNumber *WhiteXYZ=NULL);
void ICCPROFLIB_API icLabtoXYZ(icFloatNumber *XYZ, icFloatNumber *Lab=NULL, icFloatNumber *WhiteXYZ=NULL);

void ICCPROFLIB_API icLab2Lch(icFloatNumber *Lch, icFloatNumber *Lab=NULL);
void ICCPROFLIB_API icLch2Lab(icFloatNumber *Lab, icFloatNumber *Lch=NULL);

icFloatNumber ICCPROFLIB_API icMin(icFloatNumber v1, icFloatNumber v2);
icFloatNumber ICCPROFLIB_API icMax(icFloatNumber v1, icFloatNumber v2);

icUInt32Number ICCPROFLIB_API icIntMin(icUInt32Number v1, icUInt32Number v2);
icUInt32Number ICCPROFLIB_API icIntMax(icUInt32Number v1, icUInt32Number v2);

icFloatNumber ICCPROFLIB_API icDeltaE(icFloatNumber *Lab1, icFloatNumber *Lab2);

/**Floating point encoding of Lab in PCS is in range 0.0 to 1.0 */
///Here are some conversion routines to convert to regular Lab encoding
void ICCPROFLIB_API icLabFromPcs(icFloatNumber *Lab);
void ICCPROFLIB_API icLabToPcs(icFloatNumber *Lab);

/** Floating point encoding of XYZ in PCS is in range 0.0 to 1.0
 (Note: X=1.0 is encoded as about 0.5)*/
///Here are some conversion routines to convert to regular XYZ encoding
void ICCPROFLIB_API icXyzFromPcs(icFloatNumber *XYZ);
void ICCPROFLIB_API icXyzToPcs(icFloatNumber *XYZ);


void ICCPROFLIB_API icMemDump(std::string &sDump, void *pBuf, icUInt32Number nNum);
void ICCPROFLIB_API icMatrixDump(std::string &sDump, icS15Fixed16Number *pMatrix);
ICCPROFLIB_API const icChar* icGetSig(icChar *pBuf, icUInt32Number sig, bool bGetHexVal=true);
ICCPROFLIB_API const icChar* icGetSigStr(icChar *pBuf, icUInt32Number nSig);

icUInt32Number ICCPROFLIB_API icGetSigVal(const icChar *pBuf);
icUInt32Number ICCPROFLIB_API icGetSpaceSamples(icColorSpaceSignature sig);

ICCPROFLIB_API extern const char *icValidateWarningMsg;
ICCPROFLIB_API extern const char *icValidateNonCompliantMsg;
ICCPROFLIB_API extern const char *icValidateCriticalErrorMsg;

#ifdef ICC_BYTE_ORDER_LITTLE_ENDIAN
inline void icSwab16Ptr(void *pVoid)
{
  icUInt8Number *ptr = (icUInt8Number*)pVoid;
  icUInt8Number tmp;

  tmp = ptr[0]; ptr[0] = ptr[1]; ptr[1] = tmp;
}

inline void icSwab16Array(void *pVoid, int num)
{
  icUInt8Number *ptr = (icUInt8Number*)pVoid;
  icUInt8Number tmp;

  while (num>0) {
    tmp = ptr[0]; ptr[0] = ptr[1]; ptr[1] = tmp;
    ptr += 2;
    num--;
  }
}

inline void icSwab32Ptr(void *pVoid)
{
  icUInt8Number *ptr = (icUInt8Number*)pVoid;
  icUInt8Number tmp;

  tmp = ptr[0]; ptr[0] = ptr[3]; ptr[3] = tmp;
  tmp = ptr[1]; ptr[1] = ptr[2]; ptr[2] = tmp;
}

inline void icSwab32Array(void *pVoid, int num)
{
  icUInt8Number *ptr = (icUInt8Number*)pVoid;
  icUInt8Number tmp;

  while (num>0) {
    tmp = ptr[0]; ptr[0] = ptr[3]; ptr[3] = tmp;
    tmp = ptr[1]; ptr[1] = ptr[2]; ptr[2] = tmp;
    ptr += 4;
    num--;
  }

}

inline void icSwab64Ptr(void *pVoid)
{
  icUInt8Number *ptr = (icUInt8Number*)pVoid;
  icUInt8Number tmp;

  tmp = ptr[0]; ptr[0] = ptr[7]; ptr[7] = tmp;
  tmp = ptr[1]; ptr[1] = ptr[6]; ptr[6] = tmp;
  tmp = ptr[2]; ptr[2] = ptr[5]; ptr[5] = tmp;
  tmp = ptr[3]; ptr[3] = ptr[4]; ptr[4] = tmp;
}

inline void icSwab64Array(void *pVoid, int num)
{
  icUInt8Number *ptr = (icUInt8Number*)pVoid;
  icUInt8Number tmp;

  while (num>0) {
    tmp = ptr[0]; ptr[0] = ptr[7]; ptr[7] = tmp;
    tmp = ptr[1]; ptr[1] = ptr[6]; ptr[6] = tmp;
    tmp = ptr[2]; ptr[2] = ptr[5]; ptr[5] = tmp;
    tmp = ptr[3]; ptr[3] = ptr[4]; ptr[4] = tmp;
    ptr += 8;
    num--;
  }

}
#else //!ICC_BYTE_ORDER_LITTLE_ENDIAN
#define icSwab16Ptr(x)
#define icSwab16Array(x, n)
#define icSwab32Ptr(x)
#define icSwab32Array(x, n)
#define icSwab64Ptr(x)
#define icSwab64Array(x, n)
#endif

#define icSwab16(x) icSwab16Ptr(&x)
#define icSwab32(x) icSwab32Ptr(&x)
#define icSwab64(x) icSwab64Ptr(&x)


/**
 **************************************************************************
 * Type: Class
 * 
 * Purpose: 
 *  This is a utility class which can be used to get profile info 
 *  for printing. The member functions are used to convert signatures
 *  and other enum values to character strings for printing.
 **************************************************************************
 */
class ICCPROFLIB_API  CIccInfo {
public:
  //Signature values
  const icChar *GetVersionName(icUInt32Number val);
  const icChar *GetDeviceAttrName(icUInt64Number val);
  const icChar *GetProfileFlagsName(icUInt32Number val);

  const icChar *GetTagSigName(icTagSignature sig);
  const icChar *GetTechnologySigName(icTechnologySignature sig);
  const icChar *GetTagTypeSigName(icTagTypeSignature sig);
  const icChar *GetColorSpaceSigName(icColorSpaceSignature sig);
  const icChar *GetProfileClassSigName(icProfileClassSignature sig);
  const icChar *GetPlatformSigName(icPlatformSignature sig);
  const icChar *GetCmmSigName(icCmmSignature sig);
  const icChar *GetReferenceMediumGamutSigNameName(icReferenceMediumGamutSignature sig);
  const icChar *GetColorimetricIntentImageStateName(icColorimetricIntentImageStateSignature sig);

  const icChar *GetSigName(icUInt32Number val);

  //Other values
  const icChar *GetMeasurementFlareName(icMeasurementFlare val);
  const icChar *GetMeasurementGeometryName(icMeasurementGeometry val);
  const icChar *GetRenderingIntentName(icRenderingIntent val);
  const icChar *GetSpotShapeName(icSpotShape val);
  const icChar *GetStandardObserverName(icStandardObserver val);
  const icChar *GetIlluminantName(icIlluminant val);

  const icChar *GetUnknownName(icUInt32Number val);
  const icChar *GetMeasurementUnit(icSignature sig);
  const icChar *GetProfileID(icProfileID *profileID);
  const icChar *GetColorantEncoding(icColorantEncoding colorant);
  
  bool IsProfileIDCalculated(icProfileID *profileID);
  icValidateStatus CheckData(std::string &sReport, const icDateTimeNumber &dateTime);
  icValidateStatus CheckData(std::string &sReport, const icXYZNumber &XYZ);

  bool IsValidSpace(icColorSpaceSignature sig);

protected:
  icChar m_szStr[128];
  icChar m_szSigStr[128];
};

extern ICCPROFLIB_API CIccInfo icInfo;


/**
 **************************************************************************
 * Type: Class
 * 
 * Purpose: 
 *  This is a UTF16 string class that provides conversions 
 **************************************************************************
 */
class ICCPROFLIB_API CIccUTF16String
{
public:
  CIccUTF16String();
  CIccUTF16String(const icUInt16Number *uzStr);
  CIccUTF16String(const char *szStr);
  CIccUTF16String(const CIccUTF16String &str);
  virtual ~CIccUTF16String();

  void Clear();
  bool Empty() const { return m_len==0; }
  size_t Size() const { return m_len; }
  void Resize(size_t len);

  CIccUTF16String& operator=(const CIccUTF16String &wstr);
  CIccUTF16String& operator=(const char *szStr);
  CIccUTF16String& operator=(const icUInt16Number *uzStr);

  bool operator==(const CIccUTF16String &str) const;

  icUInt16Number operator[](size_t nIndex) const { return m_str[nIndex]; }

  const icUInt16Number *c_str() const { return m_str; }

  void FromUtf8(const char *szStr, size_t sizeSrc=0);
  const char *ToUtf8(std::string &buf) const;
  void FromWString(const std::wstring &buf);
  const wchar_t *ToWString(std::wstring &buf) const;

  static size_t WStrlen(const icUInt16Number *uzStr);

protected:
  static size_t AllocSize(size_t n) { return (((n+64)/64)*64); }
  size_t m_alloc;
  size_t m_len;
  icUInt16Number *m_str;
};

const char * ICCPROFLIB_API icUtf16ToUtf8(std::string &buf, const icUInt16Number *szSrc, int sizeSrc=0);
const unsigned short * ICCPROFLIB_API icUtf8ToUtf16(CIccUTF16String &buf, const char *szSrc, int sizeSrc=0);



#ifdef USESAMPLEICCNAMESPACE
} //namespace sampleICC
#endif

#endif
