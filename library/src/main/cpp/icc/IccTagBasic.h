/** @file
    File:       IccTagBasic.h

    Contains:   Header for implementation of the CIccTag class
                and inherited classes

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

#if !defined(_ICCTAGBASIC_H)
#define _ICCTAGBASIC_H

#include <list>
#include <string>
#include "IccDefs.h"
#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif

class CIccIO;

class ICCPROFLIB_API CIccProfile;

class IIccExtensionTag
{
public:
  virtual const char *GetExtClassName() const=0;
  virtual const char *GetExtDerivedClassName() const=0;
};

/**
 ***********************************************************************
 * Class: CIccTag
 *
 * Purpose:
 *  CIccTag is the base class that all Icc Tags are derived
 *  from.  It defines basic tag functionality, and provides
 *  a static function that acts as an object construction
 *  factory.
 ***********************************************************************
 */
class ICCPROFLIB_API CIccTag  
{
public:
  CIccTag();

  /**
  * Function: NewCopy(sDescription)
  *  Each derived tag will implement it's own NewCopy() function.
  *
  * Parameter(s):
  *  none
  *
  * Returns a new CIccTag object that is a copy of this object.
  */
  virtual CIccTag* NewCopy() const {return new CIccTag;}

  virtual ~CIccTag();

  /**
  * Function: GetType() 
  * 
  * Purpose: Get Tag Type.
  *  Each derived tag will implement it's own GetType() function.
  */
  virtual icTagTypeSignature GetType() const { return icMaxEnumType; }
  virtual bool IsArrayType() { return false; }
  virtual bool IsMBBType() { return false; }

  virtual const icChar *GetClassName() const { return "CIccTag"; }

  static CIccTag* Create(icTagTypeSignature sig);

  virtual IIccExtensionTag* GetExtension() {return NULL;}

  /**
  * Function: IsSupported(size, pIO) - Check if tag fully
  *  supported for apply purposes.  By Default inherited
  *  classes are supported.  Unknown tag types are not
  *  supported.
  *
  * Returns true if tag type is supported.
  */
  virtual bool IsSupported() { return true; }

  /**
  * Function: Read(size, pIO) - Read tag from file.
  *  Each derived tag will implement it's own Read() function.
  *
  * Parameter(s):
  * size - number of bytes in tag including the type signature.
  * pIO - IO object used to read in tag. The IO object should
  *       already be initialized to point to the begining of
  *       the tag.
  *
  * Returns true if Read is successful.
  */
  virtual bool Read(icUInt32Number size, CIccIO *pIO) { return false; }

  /**
  * Function: Write(pIO)
  *  Each derived tag will implement it's own Write() function.
  *
  * Parameter(s):
  * pIO - IO object used to write a tag. The IO object should
  *       already be initialized to point to the begining of
  *       the tag.
  *
  * Returns true if Write is successful.
  */
  virtual bool Write(CIccIO *pIO) { return false; }

  /**
  * Function: Describe(sDescription)
  *  Each derived tag will implement it's own Describe() function.
  *
  * Parameter(s):
  * sDescription - A string to put the tag's description into.
  */
  virtual void Describe(std::string &sDescription) { sDescription.empty(); }

  /**
   ******************************************************************************
   * Function: Validate
   *  Each derived tag will implement it's own IsValid() function
   * 
   * Parameter(s):
   * sig - signature of tag being validated,
   * sDescription - A string to put tag validation report.
   */
  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

  //All tags start with a reserved value.  Allocate a place to put it.
  icUInt32Number m_nReserved;
};


/**
****************************************************************************
* Class: IccTagUnknown
* 
* Purpose: The general purpose I don't know tag.
*****************************************************************************
*/
class ICCPROFLIB_API CIccTagUnknown : public CIccTag
{
public:
  CIccTagUnknown();
  CIccTagUnknown(const CIccTagUnknown &ITU);
  CIccTagUnknown &operator=(const CIccTagUnknown &UnknownTag);
  virtual CIccTag* NewCopy() const {return new CIccTagUnknown(*this);}
  virtual ~CIccTagUnknown();

  virtual bool IsSupported() { return false; }

  virtual icTagTypeSignature GetType() const { return m_nType; }
  virtual const icChar *GetClassName() const { return "CIccTagUnknown"; }

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  virtual void Describe(std::string &sDescription);


protected:
  icTagTypeSignature m_nType;
  icUInt8Number *m_pData;
  icUInt32Number m_nSize;
};


/**
****************************************************************************
* Class: CIccTagText()
* 
* Purpose: The textType ICC tag
*****************************************************************************
*/
class ICCPROFLIB_API CIccTagText : public CIccTag
{
public:
  CIccTagText();
  CIccTagText(const CIccTagText &ITT);
  CIccTagText &operator=(const CIccTagText &TextTag);
  virtual CIccTag* NewCopy() const {return new CIccTagText(*this);}
  virtual ~CIccTagText();

  virtual icTagTypeSignature GetType() const { return icSigTextType; }
  virtual const icChar *GetClassName() const { return "CIccTagText"; }

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  virtual void Describe(std::string &sDescription);

  const icChar *GetText() const { return m_szText; }
  void SetText(const icChar *szText);
  const icChar *operator=(const icChar *szText);

  icChar *GetBuffer(icUInt32Number nSize);
  void Release();
  icUInt32Number Capacity() const { return m_nBufSize; }
  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

protected:
  icChar *m_szText;
  icUInt32Number m_nBufSize;
};

/**
****************************************************************************
* Class: CIccTagTextDescription()
* 
* Purpose: The textType ICC tag
****************************************************************************
*/
class ICCPROFLIB_API CIccTagTextDescription : public CIccTag
{
public:
  CIccTagTextDescription();
  CIccTagTextDescription(const CIccTagTextDescription &ITTD);
  CIccTagTextDescription &operator=(const CIccTagTextDescription &TextDescTag);
  virtual CIccTag* NewCopy() const {return new CIccTagTextDescription(*this);}
  virtual ~CIccTagTextDescription();

  virtual icTagTypeSignature GetType() const { return icSigTextDescriptionType; }
  virtual const icChar *GetClassName() const { return "CIccTagTextDescription"; }

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  virtual void Describe(std::string &sDescription);

  const icChar *GetText() const { return m_szText; }
  void SetText(const icChar *szText);
  const icChar *operator=(const icChar *szText);

  icChar *GetBuffer(icUInt32Number nSize);
  void Release();
  icUInt32Number Capacity() const { return m_nASCIISize; }

  icUInt16Number *GetUnicodeBuffer(icUInt32Number nSize);
  void ReleaseUnicode();
  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;


protected:
  icChar *m_szText;
  icUInt32Number m_nASCIISize;

  icUInt16Number *m_uzUnicodeText;
  icUInt32Number m_nUnicodeSize;
  icUInt32Number m_nUnicodeLanguageCode;

  icUInt8Number m_szScriptText[67];
  icUInt8Number m_nScriptSize;
  icUInt16Number m_nScriptCode;

  bool m_bInvalidScript;
};


/**
****************************************************************************
* Class: CIccTagSignature
* 
* Purpose:  The signatureType tag
****************************************************************************
*/
class ICCPROFLIB_API CIccTagSignature : public CIccTag
{
public:
  CIccTagSignature();
  CIccTagSignature(const CIccTagSignature &ITS);
  CIccTagSignature &operator=(const CIccTagSignature &SignatureTag);
  virtual CIccTag* NewCopy() const {return new CIccTagSignature(*this);}
  virtual ~CIccTagSignature();

  virtual icTagTypeSignature GetType() const { return icSigSignatureType; }
  virtual const icChar *GetClassName() const { return "CIccTagSignature"; }

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  icUInt32Number GetValue() const { return m_nSig; }
  void SetValue(icUInt32Number sig) { m_nSig = sig; }
  icUInt32Number operator=(icUInt32Number sig) { SetValue(sig); return m_nSig; }
  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

protected:
  icUInt32Number m_nSig;
};

typedef struct
{
  icChar rootName[32];
  icFloatNumber pcsCoords[3];
  icFloatNumber deviceCoords[icAny];
} SIccNamedColorEntry;

typedef struct {
  icFloatNumber lab[3];
} SIccNamedLabEntry;

/**
****************************************************************************
* Class: CIccTagNamedColor2
* 
* Purpose: the NamedColor2 tag - an array of Named Colors
****************************************************************************
*/
class ICCPROFLIB_API CIccTagNamedColor2 : public CIccTag
{
public:
  CIccTagNamedColor2(int nSize=1, int nDeviceCoords=0);
  CIccTagNamedColor2(const CIccTagNamedColor2 &ITNC);
  CIccTagNamedColor2 &operator=(const CIccTagNamedColor2 &NamedColor2Tag);
  virtual CIccTag* NewCopy() const {return new CIccTagNamedColor2(*this);}
  virtual ~CIccTagNamedColor2();

  virtual icTagTypeSignature GetType() const { return icSigNamedColor2Type; }
  virtual const icChar *GetClassName() const { return "CIccTagNamedColor2"; }

  virtual bool UseLegacyPCS() const { return true; } //Treat Lab Encoding differently?

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  const icChar *GetPrefix() const { return m_szPrefix; }
  void SetPrefix(const icChar *szPrefix);

  const icChar *GetSufix() const { return m_szSufix; }
  void SetSufix(const icChar *szSufix);

  icUInt32Number GetVendorFlags() const { return m_nVendorFlags; }
  void SetVendorFlags(icUInt32Number nVendorFlags) {m_nVendorFlags = nVendorFlags;}

  //The following Find functions return the zero based index of the color
  //or -1 to indicate that the color was not found.
  icInt32Number FindColor(const icChar *szColor) const;
  icInt32Number FindRootColor(const icChar *szRootColor) const;
  icInt32Number FindDeviceColor(icFloatNumber *pDevColor) const;
  icInt32Number FindPCSColor(icFloatNumber *pPCS, icFloatNumber dMinDE=1000.0);

  bool InitFindCachedPCSColor();
  //FindPCSColor returns the zero based index of the color or -1 to indicate that the color was not found.
  //InitFindPCSColor must be called before FindPCSColor
  icInt32Number FindCachedPCSColor(icFloatNumber *pPCS, icFloatNumber dMinDE=1000.0) const;

  ///Call ResetPCSCache() if entry values change between calls to FindPCSColor()
  void ResetPCSCache();

  bool GetColorName(std::string &sColorName, icInt32Number index) const;
  SIccNamedColorEntry &operator[](icUInt32Number index) const {return *(SIccNamedColorEntry*)((icUInt8Number*)m_NamedColor + index * m_nColorEntrySize);}
  SIccNamedColorEntry *GetEntry(icUInt32Number index) const {return (SIccNamedColorEntry*)((icUInt8Number*)m_NamedColor + index * m_nColorEntrySize);}

  icUInt32Number GetSize() const { return m_nSize; }
  icUInt32Number GetDeviceCoords() const {return m_nDeviceCoords;}
  void SetSize(icUInt32Number nSize, icInt32Number nDeviceCoords=-1);

  virtual void SetColorSpaces(icColorSpaceSignature csPCS, icColorSpaceSignature csDevice);
  icColorSpaceSignature GetPCS() const {return m_csPCS;}
  icColorSpaceSignature GetDeviceSpace() const {return m_csDevice;}

  icFloatNumber NegClip(icFloatNumber v) const;
  icFloatNumber UnitClip(icFloatNumber v) const;

  void Lab2ToLab4(icFloatNumber *Dst, const icFloatNumber *Src) const;
  void Lab4ToLab2(icFloatNumber *Dst, const icFloatNumber *Src) const;

  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

protected:
  icChar m_szPrefix[32];
  icChar m_szSufix[32];
  
  SIccNamedColorEntry *m_NamedColor;
  SIccNamedLabEntry *m_NamedLab; ///For quick response of repeated FindPCSColor
  icUInt32Number m_nColorEntrySize;

  icUInt32Number m_nVendorFlags;
  icUInt32Number m_nDeviceCoords;
  icUInt32Number m_nSize;

  icColorSpaceSignature m_csPCS;
  icColorSpaceSignature m_csDevice;
};

/**
****************************************************************************
* Class: CIccTagXYZ
* 
* Purpose: the XYZType tag - an array of XYZ values
****************************************************************************
*/
class ICCPROFLIB_API CIccTagXYZ : public CIccTag
{
public:
  CIccTagXYZ(int nSize=1);
  CIccTagXYZ(const CIccTagXYZ &ITXYZ);
  CIccTagXYZ &operator=(const CIccTagXYZ &XYZTag);
  virtual CIccTag* NewCopy() const {return new CIccTagXYZ(*this);}
  virtual ~CIccTagXYZ();

  virtual bool IsArrayType() { return m_nSize > 1; }

  virtual icTagTypeSignature GetType() const { return icSigXYZType; }
  virtual const icChar *GetClassName() const { return "CIccTagXYZ"; }

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  icXYZNumber &operator[](icUInt32Number index) const {return m_XYZ[index];}
  icXYZNumber *GetXYZ(icUInt32Number index) {return &m_XYZ[index];}
  icUInt32Number GetSize() const { return m_nSize; }
  void SetSize(icUInt32Number nSize, bool bZeroNew=true);
  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

protected:
  icXYZNumber *m_XYZ;
  icUInt32Number m_nSize;
};

/**
****************************************************************************
* Class: CIccTagChromaticity
* 
* Purpose: the chromaticity tag - xy chromaticity values for each channel
****************************************************************************
*/
class ICCPROFLIB_API CIccTagChromaticity : public CIccTag
{
public:
  CIccTagChromaticity(int nSize=3);
  CIccTagChromaticity(const CIccTagChromaticity &ITCh);
  CIccTagChromaticity &operator=(const CIccTagChromaticity &ChromTag);
  virtual CIccTag* NewCopy() const {return new CIccTagChromaticity(*this);}
  virtual ~CIccTagChromaticity();

  virtual bool IsArrayType() { return m_nChannels > 1; }

  virtual icTagTypeSignature GetType() const { return icSigChromaticityType; }
  virtual const icChar *GetClassName() const { return "CIccTagChromaticity"; }

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  icChromaticityNumber &operator[](icUInt32Number index) {return m_xy[index];}
  icChromaticityNumber *Getxy(icUInt32Number index) {return &m_xy[index];}
  icUInt32Number GetSize() const { return m_nChannels; }
  void SetSize(icUInt16Number nSize, bool bZeroNew=true);

  icUInt16Number m_nColorantType;

  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

protected:
  icUInt16Number m_nChannels;
  icChromaticityNumber *m_xy;
};

/**
****************************************************************************
* Class: CIccTagFixedNum
* 
* Purpose: A template class for arrays of Fixed point numbers
*
* Derived Tags: CIccTagS15Fixed16 and CIccTagU16Fixed16
*****************************************************************************
*/
template <class T, icTagTypeSignature Tsig>
class ICCPROFLIB_API CIccTagFixedNum : public CIccTag
{
public:
  CIccTagFixedNum(int nSize=1);
  CIccTagFixedNum(const CIccTagFixedNum &ITFN);
  CIccTagFixedNum &operator=(const CIccTagFixedNum &FixedNumTag);
  virtual CIccTag* NewCopy() const { return new CIccTagFixedNum(*this); }
  virtual ~CIccTagFixedNum();

  virtual bool IsArrayType() { return m_nSize > 1; }

  virtual icTagTypeSignature GetType() const { return Tsig; }
  virtual const icChar *GetClassName() const;

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  T &operator[](icUInt32Number index) {return m_Num[index];}

  /// Returns the size of the data array
  icUInt32Number GetSize() const { return m_nSize; }
  void SetSize(icUInt32Number nSize, bool bZeroNew=true);

protected:
  T *m_Num;
  icUInt32Number m_nSize;
};

/**
****************************************************************************
* Class: CIccTagS15Fixed16
* 
* Purpose: s15Fixed16type tag derived from CIccTagFixedNum
*****************************************************************************
*/
typedef CIccTagFixedNum<icS15Fixed16Number, icSigS15Fixed16ArrayType> CIccTagS15Fixed16;

/**
****************************************************************************
* Classe: CIccTagU16Fixed16
* 
* Purpose: u16Fixed16type tag derived from CIccTagFixedNum
*****************************************************************************
*/
typedef CIccTagFixedNum<icU16Fixed16Number, icSigU16Fixed16ArrayType> CIccTagU16Fixed16;

/**
****************************************************************************
* Class: CIccTagNum
* 
* Purpose: A template class for arrays of integers
* 
* Derived Tags: CIccTagUInt8, CIccTagUInt16, CIccTagUInt32 and CIccTagUInt64
*****************************************************************************
*/
template <class T, icTagTypeSignature Tsig>
class ICCPROFLIB_API CIccTagNum : public CIccTag
{
public:
  CIccTagNum(int nSize=1);
  CIccTagNum(const CIccTagNum &ITNum);
  CIccTagNum &operator=(const CIccTagNum &NumTag);
  virtual CIccTag* NewCopy() const { return new CIccTagNum(*this); }
  virtual ~CIccTagNum();

  virtual bool IsArrayType() { return m_nSize > 1; }

  virtual icTagTypeSignature GetType() const { return Tsig; }
  virtual const icChar *GetClassName() const;

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  T &operator[](icUInt32Number index) {return m_Num[index];}

  /// Returns the size of the data array
  icUInt32Number GetSize() const { return m_nSize; }
  void SetSize(icUInt32Number nSize, bool bZeroNew=true);

protected:
  T *m_Num;
  icUInt32Number m_nSize;
};


/**
****************************************************************************
* Class: CIccTagUInt8
* 
* Purpose: icUInt8Number type tag derived from CIccTagNum
*****************************************************************************
*/
typedef CIccTagNum<icUInt8Number, icSigUInt8ArrayType> CIccTagUInt8;

/**
****************************************************************************
* Class: CIccTagUInt16
* 
* Purpose: icUInt16Number type tag derived from CIccTagNum
*****************************************************************************
*/
typedef CIccTagNum<icUInt16Number, icSigUInt16ArrayType> CIccTagUInt16;

/**
****************************************************************************
* Class: CIccTagUInt32
* 
* Purpose: icUInt32Number type tag derived from CIccTagNum
*****************************************************************************
*/
typedef CIccTagNum<icUInt32Number, icSigUInt32ArrayType> CIccTagUInt32;

/**
****************************************************************************
* Class: CIccTagUInt64
* 
* Purpose: icUInt64Number type tag derived from CIccTagNum
*****************************************************************************
*/
typedef CIccTagNum<icUInt64Number, icSigUInt64ArrayType> CIccTagUInt64;



/**
****************************************************************************
* Class: CIccTagMeasurement
* 
* Purpose: The measurmentType tag 
****************************************************************************
*/
class ICCPROFLIB_API CIccTagMeasurement : public CIccTag
{
public:
  CIccTagMeasurement();
  CIccTagMeasurement(const CIccTagMeasurement &ITM);
  CIccTagMeasurement &operator=(const CIccTagMeasurement &MeasTag);
  virtual CIccTag* NewCopy() const {return new CIccTagMeasurement(*this);}
  virtual ~CIccTagMeasurement();

  virtual icTagTypeSignature GetType() const { return icSigMeasurementType; }
  virtual const icChar *GetClassName() const { return "CIccTagMeasurement"; }

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);
  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;


  icMeasurement m_Data;
};

/**
****************************************************************************
* Data Class: CIccLocalizedUnicode
* 
* Purpose: Implementation of a unicode string with language and region
*  identifiers.
*****************************************************************************
*/
class ICCPROFLIB_API CIccLocalizedUnicode
{
public: //member functions
  CIccLocalizedUnicode();
  CIccLocalizedUnicode(const CIccLocalizedUnicode& ILU);
  CIccLocalizedUnicode &operator=(const CIccLocalizedUnicode &UnicodeText);
  virtual ~CIccLocalizedUnicode();

  icUInt32Number GetLength() const { return m_nLength; }
  icUInt16Number *GetBuf() const { return m_pBuf; }

  icUInt32Number GetAnsiSize();
  const icChar *GetAnsi(icChar *szBuf, icUInt32Number nBufSize);
  
  void SetSize(icUInt32Number);

  void SetText(const icChar *szText,
               icLanguageCode nLanguageCode = icLanguageCodeEnglish,
               icCountryCode nRegionCode = icCountryCodeUSA);
  void SetText(const icUInt16Number *sszUnicode16Text,
               icLanguageCode nLanguageCode = icLanguageCodeEnglish,
               icCountryCode nRegionCode = icCountryCodeUSA);
  void SetText(const icUInt32Number *sszUnicode32Text,
               icLanguageCode nLanguageCode = icLanguageCodeEnglish,
               icCountryCode nRegionCode = icCountryCodeUSA);

  const icChar *operator=(const icChar *szText) { SetText(szText); return szText; }
  const icUInt16Number *operator=(const icUInt16Number *sszText) { SetText(sszText); return sszText; }
  const icUInt32Number *operator=(const icUInt32Number *sszText) { SetText(sszText); return sszText; }

  //Data
  icLanguageCode m_nLanguageCode;
  icCountryCode m_nCountryCode;

protected:
  icUInt32Number m_nLength;

  icUInt16Number *m_pBuf;

};

/**
****************************************************************************
* List Class: CIccMultiLocalizedUnicode
* 
* Purpose: List of CIccLocalizedUnicode objects
*****************************************************************************
*/
typedef std::list<CIccLocalizedUnicode> CIccMultiLocalizedUnicode;


/**
****************************************************************************
* Class: CIccTagMultiLocalizedUnicode
* 
* Purpose: The MultiLocalizedUnicode tag
*****************************************************************************
*/
class ICCPROFLIB_API CIccTagMultiLocalizedUnicode : public CIccTag
{
public:
  CIccTagMultiLocalizedUnicode();
  CIccTagMultiLocalizedUnicode(const CIccTagMultiLocalizedUnicode& ITMLU);
  CIccTagMultiLocalizedUnicode &operator=(const CIccTagMultiLocalizedUnicode &MultiLocalizedTag);
  virtual CIccTag* NewCopy() const {return new CIccTagMultiLocalizedUnicode(*this);}
  virtual ~CIccTagMultiLocalizedUnicode();

  virtual icTagTypeSignature GetType() const { return icSigMultiLocalizedUnicodeType; }
  virtual const icChar *GetClassName() const { return "CIcciSigMultiLocalizedUnicode"; }

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);
  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

  CIccLocalizedUnicode *Find(icLanguageCode nLanguageCode = icLanguageCodeEnglish,
                             icCountryCode nRegionCode = icCountryCodeUSA);

  void SetText(const icChar *szText,
               icLanguageCode nLanguageCode = icLanguageCodeEnglish,
               icCountryCode nRegionCode = icCountryCodeUSA);
  void SetText(const icUInt16Number *sszUnicode16Text,
               icLanguageCode nLanguageCode = icLanguageCodeEnglish,
               icCountryCode nRegionCode = icCountryCodeUSA);
  void SetText(const icUInt32Number *sszUnicode32Text,
               icLanguageCode nLanguageCode = icLanguageCodeEnglish,
               icCountryCode nRegionCode = icCountryCodeUSA);


  CIccMultiLocalizedUnicode *m_Strings;
};


//
// MD: Moved Curve & LUT Tags to IccTagLut.h (4-30-05)
//

/**
****************************************************************************
* Class: CIccTagData
* 
* Purpose: The data type tag
*****************************************************************************
*/
class ICCPROFLIB_API CIccTagData : public CIccTag
{
public:
  CIccTagData(int nSize=1);
  CIccTagData(const CIccTagData &ITD);
  CIccTagData &operator=(const CIccTagData &DataTag);
  virtual CIccTag* NewCopy() const {return new CIccTagData(*this);}
  virtual ~CIccTagData();

  virtual icTagTypeSignature GetType() const { return icSigDataType; }
  virtual const icChar *GetClassName() const { return "CIccTagData"; }

  virtual bool IsArrayType() { return m_nSize > 1; }

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  icUInt32Number GetSize() const { return m_nSize; }
  void SetSize(icUInt32Number nSize, bool bZeroNew=true);
  icUInt8Number &operator[] (icUInt32Number index) { return m_pData[index]; }
  icUInt8Number *GetData(icUInt32Number index) { return &m_pData[index];}

  void SetTypeAscii(bool bIsAscii=true) { m_nDataFlag = bIsAscii ? icAsciiData : icBinaryData; }
  bool IsTypeAscii() { return m_nDataFlag == icAsciiData; }
  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

protected:
  icUInt32Number m_nDataFlag;
  icUInt8Number *m_pData;
  icUInt32Number m_nSize;
};

/**
****************************************************************************
* Class: CIccTagDateTime
* 
* Purpose: The date time tag type
*****************************************************************************
*/
class ICCPROFLIB_API CIccTagDateTime : public CIccTag
{
public:
  CIccTagDateTime();
  CIccTagDateTime(const CIccTagDateTime &ITDT);
  CIccTagDateTime &operator=(const CIccTagDateTime &DateTimeTag);
  virtual CIccTag* NewCopy() const {return new CIccTagDateTime(*this);}
  virtual ~CIccTagDateTime();

  virtual icTagTypeSignature GetType() const { return icSigDateTimeType; }
  virtual const icChar *GetClassName() const { return "CIccTagDateTime"; }

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  virtual void Describe(std::string &sDescription);

  void SetDateTime(icDateTimeNumber nDateTime) { m_DateTime = nDateTime;}
  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

protected:
  icDateTimeNumber m_DateTime;
};

/**
****************************************************************************
* Class: CIccTagColorantOrder
* 
* Purpose: Colorant Order Tag 
*****************************************************************************
*/
class ICCPROFLIB_API CIccTagColorantOrder : public CIccTag
{
public:
  CIccTagColorantOrder(int nsize=1);
  CIccTagColorantOrder(const CIccTagColorantOrder &ITCO);
  CIccTagColorantOrder &operator=(const CIccTagColorantOrder &ColorantOrderTag);
  virtual CIccTag* NewCopy() const {return new CIccTagColorantOrder(*this);}
  virtual ~CIccTagColorantOrder();

  virtual icTagTypeSignature GetType() const { return icSigColorantOrderType; }
  virtual const icChar *GetClassName() const { return "CIccTagColorantOrder"; }

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  virtual void Describe(std::string &sDescription);
  icUInt8Number& operator[](int index) { return m_pData[index]; }
  icUInt8Number *GetData(int index) { return &m_pData[index]; }
  void SetSize(icUInt16Number nsize, bool bZeronew=true);
  icUInt32Number GetSize() const {return m_nCount;}
  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

protected:
  icUInt32Number m_nCount;
  icUInt8Number *m_pData;
};

/**
****************************************************************************
* Class: CIccTagColorantTable
* 
* Purpose: Colorant Table Tag
*****************************************************************************
*/
class ICCPROFLIB_API CIccTagColorantTable : public CIccTag
{
public:
  CIccTagColorantTable(int nsize=1);
  CIccTagColorantTable(const CIccTagColorantTable &ITCT);
  CIccTagColorantTable &operator=(const CIccTagColorantTable &ColorantTableTag);
  virtual CIccTag* NewCopy() const {return new CIccTagColorantTable(*this);}
  virtual ~CIccTagColorantTable();

  virtual icTagTypeSignature GetType() const { return icSigColorantTableType; }
  virtual const icChar *GetClassName() const { return "CIccTagColorantTable"; }

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  icColorantTableEntry &operator[](icUInt32Number index) {return m_pData[index];}
  icColorantTableEntry *GetEntry(icUInt32Number index) {return &m_pData[index];}
  icUInt32Number GetSize() const { return m_nCount; }
  void SetSize(icUInt16Number nSize, bool bZeroNew=true);

  void SetPCS(icColorSpaceSignature sig) {m_PCS = sig;}
  icColorSpaceSignature GetPCS() const {return m_PCS;};
  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

protected:
  icUInt32Number m_nCount;
  icColorantTableEntry *m_pData;
  icColorSpaceSignature m_PCS;
};

/**
****************************************************************************
* Class: CIccTagViewingConditions
* 
* Purpose: Viewing conditions tag type
*****************************************************************************
*/
class ICCPROFLIB_API CIccTagViewingConditions : public CIccTag
{
public:
  CIccTagViewingConditions();
  CIccTagViewingConditions(const CIccTagViewingConditions &ITVC);
  CIccTagViewingConditions &operator=(const CIccTagViewingConditions &ViewCondTag);
  virtual CIccTag* NewCopy() const {return new CIccTagViewingConditions(*this);}
  virtual ~CIccTagViewingConditions();

  virtual icTagTypeSignature GetType() const { return icSigViewingConditionsType; }
  virtual const icChar *GetClassName() const { return "CIccTagViewingConditions"; }

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);
  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

  icXYZNumber m_XYZIllum;
  icXYZNumber m_XYZSurround;
  icIlluminant m_illumType;
};

/**
****************************************************************************
* Data Class: CIccProfileDescText
* 
* Purpose: Private text class for CIccProfileDescStruct.
*  Text can be either a CIccTagTextDescription or a CIccTagMultiLocalizedUnicode
*  so this class provides a single interface to both.
*****************************************************************************
*/
class ICCPROFLIB_API CIccProfileDescText
{
public:
  CIccProfileDescText();
  CIccProfileDescText(const CIccProfileDescText& IPDC);
  CIccProfileDescText &operator=(const CIccProfileDescText &ProfDescText);
  virtual ~CIccProfileDescText();

  bool SetType(icTagTypeSignature nType);
  virtual icTagTypeSignature GetType() const;

  CIccTag* GetTag() const { return m_pTag; }

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  bool m_bNeedsPading;

protected:
  CIccTag *m_pTag;  //either a CIccTagTextDescription or a CIccTagMultiLocalizedUnicode  
};



/**
****************************************************************************
* Data Class: CIccProfileDescStruct
* 
* Purpose: Storage class for profile description structure
*****************************************************************************
*/
class ICCPROFLIB_API CIccProfileDescStruct
{  
public:
  CIccProfileDescStruct();
  CIccProfileDescStruct(const CIccProfileDescStruct& IPDS);
  CIccProfileDescStruct &operator=(const CIccProfileDescStruct& ProfDescStruct);


  icSignature                 m_deviceMfg;      /* Device Manufacturer */
  icSignature                 m_deviceModel;    /* Device Model */
  icUInt64Number              m_attributes;     /* Device attributes */
  icTechnologySignature       m_technology;     /* Technology signature */
  CIccProfileDescText         m_deviceMfgDesc;
  CIccProfileDescText         m_deviceModelDesc;
};

/**
****************************************************************************
* List Class: CIccProfileSeqDesc
* 
* Purpose: List of profile description structures
*****************************************************************************
*/
typedef std::list<CIccProfileDescStruct> CIccProfileSeqDesc;


/**
****************************************************************************
* Class: CIccTagProfileSeqDesc
* 
* Purpose: Profile Sequence description tag  
*****************************************************************************
*/
class ICCPROFLIB_API CIccTagProfileSeqDesc : public CIccTag
{
public:
  CIccTagProfileSeqDesc();
  CIccTagProfileSeqDesc(const CIccTagProfileSeqDesc &ITPSD);
  CIccTagProfileSeqDesc &operator=(const CIccTagProfileSeqDesc &ProfSeqDescTag);
  virtual CIccTag* NewCopy() const {return new CIccTagProfileSeqDesc(*this);}
  virtual ~CIccTagProfileSeqDesc();

  virtual icTagTypeSignature GetType() const { return icSigProfileSequenceDescType; }
  virtual const icChar *GetClassName() const { return "CIccTagProfileSeqDesc"; }

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);
  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

  CIccProfileSeqDesc *m_Descriptions;
};


/**
****************************************************************************
* List Class: CIccResponse16List
* 
* Purpose: List of response16numbers 
*****************************************************************************
*/
typedef std::list<icResponse16Number> CIccResponse16List;

/**
****************************************************************************
* Data Class: CIccResponseCurveStruct
* 
* Purpose: The base class for response curve structure
*****************************************************************************
*/
class ICCPROFLIB_API CIccResponseCurveStruct
{
  friend class ICCPROFLIB_API CIccTagResponseCurveSet16;
public: //member functions
  CIccResponseCurveStruct(icMeasurementUnitSig sig, icUInt16Number nChannels=0);
  CIccResponseCurveStruct(icUInt16Number nChannels=0);

  CIccResponseCurveStruct(const CIccResponseCurveStruct& IRCS);
  CIccResponseCurveStruct &operator=(const CIccResponseCurveStruct& RespCurveStruct);
  virtual ~CIccResponseCurveStruct();

  bool Read(icUInt32Number size, CIccIO *pIO);
  bool Write(CIccIO *pIO);
  void Describe(std::string &sDescription);

  icMeasurementUnitSig GetMeasurementType() const {return m_measurementUnitSig;}
  icUInt16Number GetNumChannels() const {return m_nChannels;}

  icXYZNumber *GetXYZ(icUInt32Number index) {return &m_maxColorantXYZ[index];}
  CIccResponse16List *GetResponseList(icUInt16Number nChannel) {return &m_Response16ListArray[nChannel];}
  CIccResponseCurveStruct* GetThis() {return this;}
  icValidateStatus Validate(std::string &sReport) const;

protected:
  icUInt16Number m_nChannels;
  icMeasurementUnitSig m_measurementUnitSig;
  icXYZNumber *m_maxColorantXYZ;
  CIccResponse16List *m_Response16ListArray;
};


/**
****************************************************************************
* List Class: CIccResponseCurveSet
* 
* Purpose: List of response curve structures
*****************************************************************************
*/

typedef std::list<CIccResponseCurveStruct> CIccResponseCurveSet;

class CIccResponseCurveSetIter
{
public:
  bool inited;
  CIccResponseCurveSet::iterator item;
};


/**
****************************************************************************
* Class: CIccTagResponseCurveSet16
* 
* Purpose: The responseCurveSet16 Tag type
*****************************************************************************
*/
class ICCPROFLIB_API CIccTagResponseCurveSet16 : public CIccTag
{
public:
  CIccTagResponseCurveSet16();
  CIccTagResponseCurveSet16(const CIccTagResponseCurveSet16 &ITRCS);
  CIccTagResponseCurveSet16 &operator=(const CIccTagResponseCurveSet16 &RespCurveSet16Tag);
  virtual CIccTag* NewCopy() const {return new CIccTagResponseCurveSet16(*this);}
  virtual ~CIccTagResponseCurveSet16();

  virtual icTagTypeSignature GetType() const { return icSigResponseCurveSet16Type; }
  virtual const icChar *GetClassName() const { return "CIccTagResponseCurveSet16"; }

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);
  virtual void Describe(std::string &sDescription);

  void SetNumChannels(icUInt16Number nChannels);
  icUInt16Number GetNumChannels() const {return m_nChannels;}

  CIccResponseCurveStruct  *NewResponseCurves(icMeasurementUnitSig sig);
  CIccResponseCurveStruct *GetResponseCurves(icMeasurementUnitSig sig);

  CIccResponseCurveStruct *GetFirstCurves();
  CIccResponseCurveStruct *GetNextCurves();

  icUInt16Number GetNumResponseCurveTypes() const;
  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;
 
protected:
  CIccResponseCurveSet *m_ResponseCurves;
  icUInt16Number m_nChannels;
  CIccResponseCurveSetIter *m_Curve;
};


#ifdef USESAMPLEICCNAMESPACE
} //namespace sampleICC
#endif

#endif // !defined(_ICCTAGBASIC_H)
