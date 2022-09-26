/** @file
File:       IccMpeACS.h

Contains:   Header for implementation of CIccTagMultiProcessElement
ACS elements and supporting classes

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
//  Initial CIccMpeent prototype development
//
//////////////////////////////////////////////////////////////////////

#ifndef _ICCMPEACS_H
#define _ICCMPEACS_H

#include "IccTagMPE.h"


//CIccFloatTag support
#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif

/**
****************************************************************************
* Class: CIccMpeAcs
* 
* Purpose: The alternate connection space base class element
*****************************************************************************
*/
class CIccMpeAcs : public CIccMultiProcessElement
{
public:
  virtual ~CIccMpeAcs();
  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  virtual bool Begin(icElemInterp nInterp, CIccTagMultiProcessElement *pMPE);
  virtual void Apply(CIccApplyMpe *pApply, icFloatNumber *dstPixel, const icFloatNumber *srcPixel) const;

  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccTagMultiProcessElement* pMPE=NULL) const;

  virtual bool IsAcs() { return true; }

  bool AllocData(icUInt32Number size);
  icUInt8Number* GetData() { return m_pData; }
  icUInt32Number GetDataSize() { return m_nDataSize; }

  virtual icAcsSignature GetAcsSig() { return m_signature; }

protected:
  CIccMpeAcs();
  icAcsSignature m_signature;

  icUInt32Number m_nDataSize;
  icUInt8Number *m_pData;
};



/**
****************************************************************************
* Class: CIccMpeBAcs
* 
* Purpose: The bACS element
*****************************************************************************
*/
class CIccMpeBAcs : public CIccMpeAcs
{
public:
  CIccMpeBAcs(icUInt16Number nChannels=0, icAcsSignature sig = 0);
  CIccMpeBAcs(const CIccMpeBAcs &elemAcs);
  CIccMpeBAcs &operator=(const CIccMpeBAcs &elemAcs);
  virtual CIccMultiProcessElement *NewCopy() const { return new CIccMpeBAcs(*this);}
  virtual ~CIccMpeBAcs();

  virtual icElemTypeSignature GetType() const { return icSigBAcsElemType; }
  virtual const icChar *GetClassName() const { return "CIccMpeBAcs"; }

  virtual icAcsSignature GetBAcsSig() { return m_signature; }

};


/**
****************************************************************************
* Class: CIccMpeEndAcs
* 
* Purpose: The eAcs element
*****************************************************************************
*/
class CIccMpeEAcs : public CIccMpeAcs
{
public:
  CIccMpeEAcs(icUInt16Number nChannels=0, icAcsSignature sig = 0);
  CIccMpeEAcs(const CIccMpeEAcs &elemAcs);
  CIccMpeEAcs &operator=(const CIccMpeEAcs &elemAcs);
  virtual CIccMultiProcessElement *NewCopy() const { return new CIccMpeEAcs(*this);}
  virtual ~CIccMpeEAcs();

  virtual icElemTypeSignature GetType() const { return icSigEAcsElemType; }
  virtual const icChar *GetClassName() const { return "CIccMpeEAcs"; }

  virtual icAcsSignature GetEAcsSig() { return m_signature;}
};


//CIccMPElements support  
#ifdef USESAMPLEICCNAMESPACE
}
#endif

#endif //_ICCMPEACS_H
