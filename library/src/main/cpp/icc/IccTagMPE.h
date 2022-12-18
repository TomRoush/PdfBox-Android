/** @file
File:       IccTagMPE.h

Contains:   Header for implementation of CIccTagMultiProcessElement
and supporting classes

Version:    V1

Copyright:  © see ICC Software License
*/

/*
* The ICC Software License, Version 0.2
*
*
* Copyright (c) 2005-2015 The International Color Consortium. All rights 
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
// -Jan 30, 2005 
//  Initial CIccFloatTag prototype development
//
// -Nov 6, 2006
//  Prototype Merged into release
//
//////////////////////////////////////////////////////////////////////

#ifndef _ICCTAGMPE_H
#define _ICCTAGMPE_H

#include "IccTag.h"
#include "IccTagFactory.h"
#include "icProfileHeader.h"
#include <memory>
#include <list>


//CIccFloatTag support
#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif

typedef enum {
  icElemInterpLinear,
  icElemInterpTetra,
} icElemInterp;

class CIccTagMultiProcessElement;
class CIccMultiProcessElement;

class CIccApplyTagMpe;
class CIccApplyMpe;

/**
****************************************************************************
* Class: CIccProcessElementPtr
* 
* Purpose: Get std list class to work with pointers to elements rather than
*  element objects so they can be shared.
*****************************************************************************
*/
class CIccMultiProcessElementPtr
{
public:
  CIccMultiProcessElement *ptr;
};

typedef std::list<CIccMultiProcessElementPtr> CIccMultiProcessElementList;
typedef CIccMultiProcessElementList::iterator CIccMultiProcessElementIter;

#define icSigMpeLevel0 ((icSignature)0x6D706530)  /* 'mpe0' */

class CIccApplyMpePtr
{
public:
  CIccApplyMpe *ptr;
};

typedef std::list<CIccApplyMpePtr> CIccApplyMpeList;
typedef CIccApplyMpeList::iterator CIccApplyMpeIter;

class IIccExtensionMpe
{
public:
  virtual const char *GetExtClassName() const=0;
};

/**
****************************************************************************
* Class: CIccMultiProcessElement
* 
* Purpose: Base Class for Multi Process Elements
*****************************************************************************
*/
class CIccMultiProcessElement
{
public:
  CIccMultiProcessElement() {}

  virtual ~CIccMultiProcessElement() {}
  
  static CIccMultiProcessElement* Create(icElemTypeSignature sig);

  virtual CIccMultiProcessElement *NewCopy() const = 0;

  virtual icElemTypeSignature GetType() const = 0;
  virtual const icChar *GetClassName() const = 0;

  virtual icUInt16Number NumInputChannels() const { return m_nInputChannels; }
  virtual icUInt16Number NumOutputChannels() const { return m_nOutputChannels; }

  virtual bool IsSupported() { return true; }

  virtual void Describe(std::string &sDescription) = 0;

  virtual bool Read(icUInt32Number size, CIccIO *pIO) = 0;
  virtual bool Write(CIccIO *pIO) = 0;

  virtual bool Begin(icElemInterp nIterp=icElemInterpLinear, CIccTagMultiProcessElement *pMPE=NULL) = 0;

  virtual CIccApplyMpe* GetNewApply(CIccApplyTagMpe *pApplyTag);
  virtual void Apply(CIccApplyMpe *pApply, icFloatNumber *pDestPixel, const icFloatNumber *pSrcPixel) const = 0;

  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccTagMultiProcessElement* pMPE=NULL) const = 0;

  //Future Acs Expansion Element Accessors
  virtual bool IsAcs() { return false; }
  virtual icAcsSignature GetBAcsSig() { return icSigAcsZero; }
  virtual icAcsSignature GetEAcsSig() { return icSigAcsZero; }

  // Allow MPE objects to be extended and get extended object type.
  virtual IIccExtensionMpe *GetExtension() { return NULL; }

  //All elements start with a reserved value.  Allocate a place to put it.
  icUInt32Number m_nReserved;

protected:
  icUInt16Number m_nInputChannels;
  icUInt16Number m_nOutputChannels;
};

/**
****************************************************************************
* Class: CIccApplyMpe
* 
* Purpose: Base Class for Apply storage for Multi Process Elements
*****************************************************************************
*/
class CIccApplyMpe
{
public:
  CIccApplyMpe(CIccMultiProcessElement *pElem);
  virtual ~CIccApplyMpe();

  virtual icElemTypeSignature GetType() const { return icSigUnknownElemType; }
  virtual const icChar *GetClassName() const { return "CIccApplyMpe"; }

  CIccMultiProcessElement *GetElem() const { return m_pElem; }

  void Apply(icFloatNumber *pDestPixel, const icFloatNumber *pSrcPixel) { m_pElem->Apply(this, pDestPixel, pSrcPixel); }

protected:
  CIccApplyTagMpe *m_pApplyTag;

  CIccMultiProcessElement *m_pElem;
};


/**
****************************************************************************
* Class: CIccMpeUnknown
* 
* Purpose: Base Class for Process Elements
*****************************************************************************
*/
class CIccMpeUnknown : public CIccMultiProcessElement
{
public:
  CIccMpeUnknown();
  CIccMpeUnknown(const CIccMpeUnknown &elem);
  CIccMpeUnknown &operator=(const CIccMpeUnknown &elem);
  virtual CIccMultiProcessElement *NewCopy() const { return new CIccMpeUnknown(*this);}
  virtual ~CIccMpeUnknown();

  virtual icElemTypeSignature GetType() const { return m_sig; }
  virtual const icChar *GetClassName() const { return "CIccMpeUnknown"; }

  virtual bool IsSupported() { return false; }

  virtual void Describe(std::string &sDescription);

  void SetType(icElemTypeSignature sig);
  void SetChannels(icUInt16Number nInputChannels, icUInt16Number nOutputChannels);

  bool SetDataSize(icUInt32Number nSize, bool bZeroData=true);
  icUInt8Number *GetData() { return m_pData; }

  virtual bool Read(icUInt32Number nSize, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  virtual bool Begin(icElemInterp nIterp=icElemInterpLinear, CIccTagMultiProcessElement *pMPE=NULL) { return false; }
  virtual CIccApplyMpe *GetNewApply() { return NULL; }
  virtual void Apply(CIccApplyMpe *pApply, icFloatNumber *pDestPixel, const icFloatNumber *pSrcPixel) const {}

  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccTagMultiProcessElement* pMPE=NULL) const;

protected:
  icElemTypeSignature m_sig;
  icUInt32Number m_nReserved;
  icUInt16Number m_nInputChannels;
  icUInt16Number m_nOutputChannels;
  icUInt32Number m_nSize;
  icUInt8Number *m_pData;
};


/**
****************************************************************************
* Class: CIccDblPixelBuffer
* 
* Purpose: The general purpose pixel storage buffer for pixel apply
*****************************************************************************
*/
class CIccDblPixelBuffer
{
public:
  CIccDblPixelBuffer();
  CIccDblPixelBuffer(const CIccDblPixelBuffer &buf);
  CIccDblPixelBuffer &operator=(const CIccDblPixelBuffer &buf);
  virtual ~CIccDblPixelBuffer();

  void Clean();
  void Reset() { m_nLastNumChannels = 0; }
  
  void UpdateChannels(icUInt16Number nNumChannels) { 
    m_nLastNumChannels = nNumChannels;
    if (nNumChannels>m_nMaxChannels) 
      m_nMaxChannels=nNumChannels;
  }

  bool Begin();

  icUInt16Number GetMaxChannels() { return m_nMaxChannels; }
  icFloatNumber *GetSrcBuf() { return m_pixelBuf1; }
  icFloatNumber *GetDstBuf() { return m_pixelBuf2; }

  void Switch() { icFloatNumber *tmp; tmp=m_pixelBuf2; m_pixelBuf2=m_pixelBuf1; m_pixelBuf1=tmp; }

  icUInt16Number GetAvailChannels() { return m_nLastNumChannels & 0x7fff; }

protected:
  //For application
  icUInt16Number m_nMaxChannels;
  icUInt16Number m_nLastNumChannels;
  icFloatNumber *m_pixelBuf1;
  icFloatNumber *m_pixelBuf2;
};


/**
****************************************************************************
* Class: CIccTagMultiProcessElement
* 
* Purpose: Apply storage for MPE general purpose processing tags
*****************************************************************************
*/
class CIccApplyTagMpe
{
public:
  CIccApplyTagMpe(CIccTagMultiProcessElement *pTag);
  virtual ~CIccApplyTagMpe();

  CIccTagMultiProcessElement *GetTag() { return m_pTag; }

  virtual bool AppendElem(CIccMultiProcessElement *pElem);

  CIccDblPixelBuffer *GetBuf() { return &m_applyBuf; }
  CIccApplyMpeList *GetList() { return m_list; }

  CIccApplyMpeIter begin() { return m_list->begin(); }
  CIccApplyMpeIter end() { return m_list->end(); }

protected:
  CIccTagMultiProcessElement *m_pTag;

  //List of processing elements
  CIccApplyMpeList *m_list;

  //Pixel data for Apply 
  CIccDblPixelBuffer m_applyBuf;
};

/**
****************************************************************************
* Class: CIccTagMultiProcessElement
* 
* Purpose: A general purpose processing tag 
*****************************************************************************
*/
class CIccTagMultiProcessElement : public CIccTag
{
public:
  CIccTagMultiProcessElement(icUInt16Number nInputChannels=0, icUInt16Number nOutputChannels=0);
  CIccTagMultiProcessElement(const CIccTagMultiProcessElement &lut);
  CIccTagMultiProcessElement &operator=(const CIccTagMultiProcessElement &lut);
  virtual CIccTag *NewCopy() const { return new CIccTagMultiProcessElement(*this);}
  virtual ~CIccTagMultiProcessElement();

  virtual bool IsSupported();

  virtual icTagTypeSignature GetType() const { return icSigMultiProcessElementType; }
  virtual const icChar *GetClassName() const { return "CIccTagMultiProcessElement"; }

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  virtual void Attach(CIccMultiProcessElement *pElement);

  CIccMultiProcessElement *GetElement(int nIndex);
  void DeleteElement(int nIndex);

  virtual bool Begin(icElemInterp nInterp=icElemInterpLinear);
  virtual CIccApplyTagMpe *GetNewApply();

  virtual void Apply(CIccApplyTagMpe *pApply, icFloatNumber *pDestPixel, const icFloatNumber *pSrcPixel) const;

  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

  icUInt16Number NumInputChannels() const { return m_nInputChannels; }
  icUInt16Number NumOutputChannels() const { return m_nOutputChannels; }
 
protected:
  virtual void Clean();
  virtual void GetNextElemIterator(CIccMultiProcessElementList::iterator &itr);
  virtual icInt32Number ElementIndex(CIccMultiProcessElement *pElem);

  virtual CIccMultiProcessElementList::iterator GetFirstElem();
  virtual CIccMultiProcessElementList::iterator GetLastElem();

  icUInt16Number m_nInputChannels;
  icUInt16Number m_nOutputChannels;

  //List of processing elements
  CIccMultiProcessElementList *m_list;

  //Offsets of loaded elements
  icUInt32Number m_nProcElements;
  icPositionNumber *m_position;

  //Number of Buffer Channels needed
  icUInt16Number m_nBufChannels;
};


//CIccMpeTag support
#ifdef USESAMPLEICCNAMESPACE
}
#endif

#endif //_ICCTAGMPE_H
