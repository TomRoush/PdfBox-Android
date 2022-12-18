/** @file
    File:       IccTagMpe.cpp

    Contains:   Implementation of MultiProcessElementType Tag

    Version:    V1

    Copyright:  © see ICC Software License
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
// -Initial implementation by Max Derhak 1-30-2006
//
//////////////////////////////////////////////////////////////////////

#if defined(WIN32) || defined(WIN64)
#pragma warning( disable: 4786) //disable warning in <list.h>
#endif

#include <stdio.h>
#include <math.h>
#include <string.h>
#include <stdlib.h>
#include "IccTagMPE.h"
#include "IccIO.h"
#include "IccMpeFactory.h"
#include <map>
#include "IccUtil.h"

#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif

/**
 ******************************************************************************
 * Name: CIccApplyMpe::CIccApplyMpe
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccApplyMpe::CIccApplyMpe(CIccMultiProcessElement *pElem)
{
  m_pElem = pElem;
}


/**
 ******************************************************************************
 * Name: CIccApplyMpe::~CIccApplyMpe
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccApplyMpe::~CIccApplyMpe()
{
}


/**
 ******************************************************************************
 * Name: CIccMultiProcessElement::Create
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccMultiProcessElement* CIccMultiProcessElement::Create(icElemTypeSignature sig)
{
  return CIccMpeCreator::CreateElement(sig);
}

/**
 ******************************************************************************
 * Name: CIccMultiProcessElement::GetNewApply()
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
******************************************************************************/
CIccApplyMpe* CIccMultiProcessElement::GetNewApply( CIccApplyTagMpe *pApplyTag )
{
  return new CIccApplyMpe(this);
}


/**
 ******************************************************************************
 * Name: CIccMpeUnknown::CIccMpeUnknown
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccMpeUnknown::CIccMpeUnknown()
{
   m_sig = icSigUnknownElemType;
   m_nReserved = 0;
   m_nInputChannels = 0;
   m_nOutputChannels = 0;
   m_nSize = 0;
   m_pData = 0;
}

/**
 ******************************************************************************
 * Name: CIccMpeUnknown::CIccMpeUnknown
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccMpeUnknown::CIccMpeUnknown(const CIccMpeUnknown &elem)
{
   m_sig = elem.m_sig;
   m_nReserved = elem.m_nReserved;
   m_nInputChannels = elem.m_nInputChannels;
   m_nOutputChannels = elem.m_nOutputChannels;
   m_nSize = elem.m_nSize;
   if (m_nSize) {
     m_pData = (icUInt8Number*)malloc(m_nSize);
     memcpy(m_pData, elem.m_pData, m_nSize);
   }
   else
     m_pData = NULL;
}

/**
 ******************************************************************************
 * Name: CIccMpeUnknown::operator=
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccMpeUnknown &CIccMpeUnknown::operator=(const CIccMpeUnknown &elem)
{
  if (m_pData)
    free(m_pData);

   m_sig = elem.m_sig;
   m_nReserved = elem.m_nReserved;
   m_nInputChannels = elem.m_nInputChannels;
   m_nOutputChannels = elem.m_nOutputChannels;
   m_nSize = elem.m_nSize;
   if (m_nSize) {
     m_pData = (icUInt8Number*)malloc(m_nSize);
     memcpy(m_pData, elem.m_pData, m_nSize);
   }
   else
     m_pData = NULL;

   return (*this);
}

/**
 ******************************************************************************
 * Name: CIccMpeUnknown::~CIccMpeUnknown
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccMpeUnknown::~CIccMpeUnknown()
{
  if (m_pData)
    free(m_pData);
}

/**
******************************************************************************
* Name: CIccMpeUnknown::SetType
* 
* Purpose: 
* 
* Args: 
* 
* Return: 
******************************************************************************/
void CIccMpeUnknown::SetType(icElemTypeSignature sig)
{
  m_sig = sig;
}

/**
******************************************************************************
* Name: CIccMpeUnknown::Describe
* 
* Purpose: 
* 
* Args: 
* 
* Return: 
******************************************************************************/
void CIccMpeUnknown::SetChannels(icUInt16Number nInputChannels, icUInt16Number nOutputChannels)
{
  m_nInputChannels = nInputChannels;
  m_nOutputChannels = nOutputChannels;
}

/**
 ******************************************************************************
 * Name: CIccMpeUnknown::Describe
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
void CIccMpeUnknown::Describe(std::string &sDescription)
{
  icChar buf[128], sigbuf[40];

  sprintf(buf, "Unknown Element(%s) Type of %u Bytes.", 
          icGetSig(sigbuf, m_sig), m_nSize);
  sDescription += buf;

  sDescription += "\r\n\r\nData Follows:\r\n";

  icMemDump(sDescription, m_pData, m_nSize);

}

/**
 ******************************************************************************
 * Name: CIccMpeUnknown::SetDataSize
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
bool CIccMpeUnknown::SetDataSize(icUInt32Number nSize, bool bZeroData/*=true*/)
{
  bool rv = true;
  if (m_pData)
    free(m_pData);

  m_nSize = nSize;
  if (m_nSize) {
    m_pData = (icUInt8Number*)malloc(m_nSize);
    if (!m_pData) {
      rv = false;
      m_nSize = 0;
    }
  }
  else
    m_pData = NULL;

  return rv;
}

/**
 ******************************************************************************
 * Name: CIccMpeUnknown::Read
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
bool CIccMpeUnknown::Read(icUInt32Number nSize, CIccIO *pIO)
{
  icUInt32Number nHeaderSize = sizeof(icTagTypeSignature) + 
    sizeof(icUInt32Number) + 
    sizeof(icUInt16Number) + 
    sizeof(icUInt16Number);

  if (nHeaderSize > nSize)
    return false;

  if (!pIO) {
    return false;
  }

  if (!pIO->Read32(&m_sig))
    return false;

  if (!pIO->Read32(&m_nReserved))
    return false;

  if (!pIO->Read16(&m_nInputChannels))
    return false;

  if (!pIO->Read16(&m_nOutputChannels))
    return false;

  icUInt32Number nDataSize = nSize - nHeaderSize;

  if (nDataSize) {
    if (!SetDataSize(nDataSize, false))
      return false;

    if (pIO->Read8(m_pData, nDataSize)!=(icInt32Number)nDataSize)
      return false;
  }

  return true;
}

/**
 ******************************************************************************
 * Name: CIccMpeUnknown::Write
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
bool CIccMpeUnknown::Write(CIccIO *pIO)
{
  if (!pIO)
    return false;

  //icUInt32Number elemStart = pIO->Tell();

  if (!pIO->Write32(&m_sig))
    return false;

  if (!pIO->Write32(&m_nReserved))
    return false;

  if (!pIO->Write16(&m_nInputChannels))
    return false;

  if (!pIO->Write16(&m_nInputChannels))
    return false;

  if (m_nSize) {
    if (pIO->Write8(m_pData, m_nSize)!=(icInt32Number)m_nSize)
      return false;
  }

  return true;
}

/**
 ******************************************************************************
 * Name: CIccMpeUnknown::Validate
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
icValidateStatus CIccMpeUnknown::Validate(icTagSignature sig, std::string &sReport, const CIccTagMultiProcessElement* pMPE/*=NULL*/) const
{
  CIccInfo Info;
  icChar buf[40];
  std::string sSigName = Info.GetSigName(sig);

  sReport += icValidateCriticalErrorMsg;
  sReport += sSigName;
  sReport += " - Element ";
  sSigName = Info.GetSigName(GetType());
  sReport += sSigName;
  sReport += " - Contains unknown processing element type (";
  icGetSig(buf, m_sig, true);
  sReport += buf;
  sReport += ").\r\n";

  return icValidateCriticalError;
}


/**
 ******************************************************************************
 * Name: CIccProcessElement::Validate
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
icValidateStatus CIccMultiProcessElement::Validate(icTagSignature sig, std::string &sReport, const CIccTagMultiProcessElement* pMPE/*=NULL*/) const
{
  icValidateStatus rv = icValidateOK;

  CIccInfo Info;
  std::string sSigName = Info.GetSigName(sig);
  if (m_nReserved!=0) {
    sReport += icValidateNonCompliantMsg;
    sReport += sSigName;
    sReport += " - Element ";
    sSigName = Info.GetSigName(GetType());
    sReport += sSigName;
    sReport += " - Reserved Value must be zero.\r\n";

    rv = icValidateNonCompliant;
  }

  return rv;
}


/**
 ******************************************************************************
 * Name: CIccDblPixelBuffer::CIccDblPixelBuffer
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccDblPixelBuffer::CIccDblPixelBuffer()
{
  m_nMaxChannels = 0;
  m_nLastNumChannels = 0;
  m_pixelBuf1 = NULL;
  m_pixelBuf2 = NULL;
}


/**
 ******************************************************************************
 * Name: CIccDblPixelBuffer::CIccDblPixelBuffer
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccDblPixelBuffer::CIccDblPixelBuffer(const CIccDblPixelBuffer &buf)
{
  m_nMaxChannels = buf.m_nMaxChannels;
  if (m_nMaxChannels) {
    m_pixelBuf1 = (icFloatNumber*)malloc(m_nMaxChannels*sizeof(icFloatNumber));
    if (m_pixelBuf1)
      memcpy(m_pixelBuf1, buf.m_pixelBuf1, m_nMaxChannels*sizeof(icFloatNumber));

    m_pixelBuf2 = (icFloatNumber*)malloc(m_nMaxChannels*sizeof(icFloatNumber));
    if (m_pixelBuf2)
      memcpy(m_pixelBuf2, buf.m_pixelBuf2, m_nMaxChannels*sizeof(icFloatNumber));
  }
  else {
    m_pixelBuf1 = NULL;;
    m_pixelBuf2 = NULL;
  }
}


/**
 ******************************************************************************
 * Name: CIccDblPixelBuffer::CIccDblPixelBuffer
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccDblPixelBuffer& CIccDblPixelBuffer::operator=(const CIccDblPixelBuffer &buf)
{
  Clean();

  m_nMaxChannels = buf.m_nMaxChannels;
  if (m_nMaxChannels) {
    m_pixelBuf1 = (icFloatNumber*)malloc(m_nMaxChannels*sizeof(icFloatNumber));
    if (m_pixelBuf1)
      memcpy(m_pixelBuf1, buf.m_pixelBuf1, m_nMaxChannels*sizeof(icFloatNumber));

    m_pixelBuf2 = (icFloatNumber*)malloc(m_nMaxChannels*sizeof(icFloatNumber));
    if (m_pixelBuf2)
      memcpy(m_pixelBuf2, buf.m_pixelBuf2, m_nMaxChannels*sizeof(icFloatNumber));
  }
  else {
    m_pixelBuf1 = NULL;;
    m_pixelBuf2 = NULL;
  }

  return *this;
}

/**
 ******************************************************************************
 * Name: CIccDblPixelBuffer::~CIccDblPixelBuffer
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccDblPixelBuffer::~CIccDblPixelBuffer()
{
  Clean();
}


/**
 ******************************************************************************
 * Name: CIccDblPixelBuffer::CIccDblPixelBuffer
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
void CIccDblPixelBuffer::Clean()
{
  if (m_pixelBuf1) {
    free(m_pixelBuf1);
    m_pixelBuf1 = NULL;
  }
  if (m_pixelBuf2) {
    free(m_pixelBuf2);
    m_pixelBuf2 = NULL;
  }
  m_nMaxChannels = 0;
  m_nLastNumChannels = 0;
}

/**
 ******************************************************************************
 * Name: CIccDblPixelBuffer::CIccDblPixelBuffer
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
bool CIccDblPixelBuffer::Begin()
{
  m_pixelBuf1 = (icFloatNumber*)calloc(m_nMaxChannels, sizeof(icFloatNumber));
  m_pixelBuf2 = (icFloatNumber*)calloc(m_nMaxChannels, sizeof(icFloatNumber));

  return (!m_nMaxChannels || (m_pixelBuf1!=NULL && m_pixelBuf2!=NULL));
}


/**
******************************************************************************
* Name: CIccApplyTagMpe::CIccApplyTagMpe
* 
* Purpose: 
* 
* Args: 
* 
* Return: 
******************************************************************************/
CIccApplyTagMpe::CIccApplyTagMpe(CIccTagMultiProcessElement *pTag)
{
  m_pTag = pTag;
  m_list = NULL;
}


/**
******************************************************************************
* Name: CIccApplyTagMpe::~CIccApplyTagMpe
* 
* Purpose: 
* 
* Args: 
* 
* Return: 
******************************************************************************/
CIccApplyTagMpe::~CIccApplyTagMpe()
{
  if (m_list) {
    CIccApplyMpeList::iterator i;

    for (i=m_list->begin(); i!=m_list->end(); i++) {
      delete i->ptr;
    }

    delete m_list;
  }
}


/**
******************************************************************************
* Name: CIccApplyTagMpe::CIccApplyTagMpe
* 
* Purpose: 
* 
* Args: 
* 
* Return: 
******************************************************************************/
bool CIccApplyTagMpe::AppendElem(CIccMultiProcessElement *pElem)
{
  if (!m_list)
    m_list = new CIccApplyMpeList();

  if (!m_list)
    return false;

  CIccApplyMpe *pApply = pElem->GetNewApply(this);

  if (!pApply)
    return false;

  CIccApplyMpePtr ptr;

  ptr.ptr = pApply;
  m_list->push_back(ptr);

  return true;
}


/**
 ******************************************************************************
 * Name: CIccTagMultiProcessElement::CIccTagMultiProcessElement
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccTagMultiProcessElement::CIccTagMultiProcessElement(icUInt16Number nInputChannels/* =0 */, icUInt16Number nOutputChannels/* =0 */)
{
  m_nReserved = 0;
  m_list = NULL;
  m_nProcElements = 0;
  m_position = NULL;

  m_nInputChannels = nInputChannels;
  m_nOutputChannels = nOutputChannels;
}

/**
 ******************************************************************************
 * Name: CIccTagMultiProcessElement::CIccTagMultiProcessElement
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccTagMultiProcessElement::CIccTagMultiProcessElement(const CIccTagMultiProcessElement &lut)
{
  m_nReserved = lut.m_nReserved;

  if (lut.m_list) {
    m_list = new CIccMultiProcessElementList();

    CIccMultiProcessElementList::iterator i;
    CIccMultiProcessElementPtr ptr;

    for (i=lut.m_list->begin(); i!= lut.m_list->end(); i++) {
      ptr.ptr = (CIccMultiProcessElement*)i->ptr->NewCopy();
      m_list->push_back(ptr);
    }
  }
  m_nInputChannels = lut.m_nInputChannels;
  m_nOutputChannels = lut.m_nOutputChannels;

  if (lut.m_nProcElements && lut.m_position) {
    m_position = (icPositionNumber*)malloc(lut.m_nProcElements*sizeof(icPositionNumber));
    if (m_position) {
      memcpy(m_position, lut.m_position, lut.m_nProcElements*sizeof(icPositionNumber));
    }
    m_nProcElements = lut.m_nProcElements;
  }

}

/**
 ******************************************************************************
 * Name: &operator=
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccTagMultiProcessElement &CIccTagMultiProcessElement::operator=(const CIccTagMultiProcessElement &lut)
{
  Clean();

  m_nReserved = lut.m_nReserved;

  if (lut.m_list) {
    m_list = new CIccMultiProcessElementList();

    CIccMultiProcessElementList::iterator i;
    CIccMultiProcessElementPtr ptr;

    for (i=lut.m_list->begin(); i!= lut.m_list->end(); i++) {
      ptr.ptr = (CIccMultiProcessElement*)i->ptr->NewCopy();
      m_list->push_back(ptr);
    }
  }
  m_nInputChannels = lut.m_nInputChannels;
  m_nOutputChannels = lut.m_nOutputChannels;

  if (lut.m_nProcElements && lut.m_position) {
    m_position = (icPositionNumber*)malloc(lut.m_nProcElements*sizeof(icPositionNumber));
    if (m_position) {
      memcpy(m_position, lut.m_position, lut.m_nProcElements*sizeof(icPositionNumber));
    }
    m_nProcElements = lut.m_nProcElements;
  }

  return *this;
}

/**
 ******************************************************************************
 * Name: CIccTagMultiProcessElement::~CIccTagMultiProcessElement
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
CIccTagMultiProcessElement::~CIccTagMultiProcessElement()
{
  Clean();
}

typedef std::map<CIccMultiProcessElement*, icPositionNumber> CIccLutPtrMap;
typedef std::map<icUInt32Number, CIccMultiProcessElement*> CIccLutOffsetMap;
/**
 ******************************************************************************
 * Name: CIccTagMultiProcessElement::Clean
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
void CIccTagMultiProcessElement::Clean()
{
  if (m_list) {
    CIccLutPtrMap map;
    CIccMultiProcessElementList::iterator i;

    for (i=m_list->begin(); i!=m_list->end(); i++) {
      if (!map[i->ptr].offset) {
        map[i->ptr].offset = 1;
        delete i->ptr;
      }
    }

    delete m_list;
    m_list = NULL;
  }

  if (m_position) {
    free(m_position);
    m_position = NULL;
  }

  m_nProcElements = 0;
}

/**
 ******************************************************************************
 * Name: CIccTagMultiProcessElement::IsSupported
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
bool CIccTagMultiProcessElement::IsSupported()
{
  if (m_list) {
    CIccMultiProcessElementList::iterator i;

    for (i=m_list->begin(); i!=m_list->end(); i++) {
      if (!i->ptr->IsSupported())
        return false;
    }
  }

  return true;
}



/**
 ******************************************************************************
 * Name: CIccTagMultiProcessElement::Describe
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
void CIccTagMultiProcessElement::Describe(std::string &sDescription)
{
  icChar buf[128];

  sprintf(buf, "BEGIN MULTI_PROCESS_ELEMENT_TAG %d %d\r\n", m_nInputChannels, m_nOutputChannels);
  sDescription += buf;
  sDescription += "\r\n";

  CIccMultiProcessElementList::iterator i;
  int j;

  for (j=0, i=m_list->begin(); i!=m_list->end(); j++, i++) {
    sprintf(buf, "PROCESS_ELEMENT #%d\r\n", j+1);
    sDescription += buf;
    i->ptr->Describe(sDescription);
    sDescription += "\r\n";
  }
}

/**
 ******************************************************************************
 * Name: CIccTagMultiProcessElement::Attach
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
void CIccTagMultiProcessElement::Attach(CIccMultiProcessElement *pElement)
{
  if (!m_list) {
    m_list = new CIccMultiProcessElementList();
  }

  CIccMultiProcessElementPtr ptr;

  ptr.ptr = pElement;

  m_list->push_back(ptr);
}



/**
 ******************************************************************************
 * Name: CIccTagMultiProcessElement::Read
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
bool CIccTagMultiProcessElement::Read(icUInt32Number size, CIccIO *pIO)
{
  icTagTypeSignature sig;
  
  icUInt32Number headerSize = sizeof(icTagTypeSignature) + 
    sizeof(icUInt32Number) + 
    sizeof(icUInt8Number) + 
    sizeof(icUInt8Number) + 
    sizeof(icUInt16Number);

  if (headerSize > size)
    return false;

  if (!pIO) {
    return false;
  }

  Clean();

  icUInt32Number tagStart = pIO->Tell();

  if (!pIO->Read32(&sig))
    return false;

  if (!pIO->Read32(&m_nReserved))
    return false;

  if (!pIO->Read16(&m_nInputChannels))
    return false;

  if (!pIO->Read16(&m_nOutputChannels))
    return false;

  if (!pIO->Read32(&m_nProcElements))
    return false;

  if (headerSize + m_nProcElements*sizeof(icUInt32Number) > size)
    return false;

  m_list = new CIccMultiProcessElementList();

  if (!m_list)
    return false;

  icUInt32Number i;

  m_position = (icPositionNumber*)calloc(m_nProcElements, sizeof(icPositionNumber));

  if (!m_position)
    return false;

  CIccLutOffsetMap loadedElements;

  for (i=0; i<m_nProcElements; i++) {
    if (!pIO->Read32(&m_position[i].offset))
      return false;
    if (!pIO->Read32(&m_position[i].size))
      return false;
  }

  CIccMultiProcessElementPtr ptr;
  icElemTypeSignature sigElem;

  for (i=0; i<m_nProcElements; i++) {
    if (m_position[i].offset+m_position[i].size > size) {
      return false;
    }

    //Use hash to cache offset duplication
    CIccMultiProcessElement *element = loadedElements[m_position[i].offset];
    if (!element) {
      icUInt32Number pos = tagStart + m_position[i].offset;

      if (pIO->Seek(pos, icSeekSet)!=(icInt32Number)pos) {
        return false;
      }

      if (!pIO->Read32(&sigElem)) {
        return false;
      }
      
      if (pIO->Seek(pos, icSeekSet)!=(icInt32Number)pos) {
        return false;
      }

      element = CIccMultiProcessElement::Create(sigElem);
      if (!element) {
        return false;
      }

      if (!element->Read(m_position[i].size, pIO)) {
        delete element;
        return false;
      }

      loadedElements[m_position[i].offset] = element;
    }
    ptr.ptr = element;

    m_list->push_back(ptr);
  }

  return true;
}

/**
 ******************************************************************************
 * Name: CIccTagMultiProcessElement::Write
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
bool CIccTagMultiProcessElement::Write(CIccIO *pIO)
{
  icTagTypeSignature sig = GetType();

  if (!pIO)
    return false;

  icUInt32Number tagStart = pIO->Tell();

  if (!pIO->Write32(&sig))
    return false;

  if (!pIO->Write32(&m_nReserved))
    return false;

  if (!pIO->Write16(&m_nInputChannels))
    return false;

  if (!pIO->Write16(&m_nOutputChannels))
    return false;

  if (m_list) {
    m_nProcElements = (icUInt32Number)m_list->size();
  }
  else {
    m_nProcElements = 0;
  }
  
  if (!pIO->Write32(&m_nProcElements))
    return false;

  if (m_nProcElements) {
    icUInt32Number offsetPos = pIO->Tell();

    if (m_position) {
      delete [] m_position;
    }

    m_position = (icPositionNumber*)calloc(m_nProcElements, sizeof(icPositionNumber));

    if (!m_position)
      return false;

    //Write an empty position table
    icUInt32Number j, zeros[2] = { 0, 0 };
    for (j=0; j<m_nProcElements; j++) {
      if (pIO->Write32(zeros, 2)!=2)
        return false;
    }

    CIccLutPtrMap map;
    CIccMultiProcessElementList::iterator i;
    icUInt32Number start, end;
    icPositionNumber position;

    //Write out each process element
    for (j=0, i=m_list->begin(); i!=m_list->end(); i++, j++) {
      if (map.find(i->ptr)==map.end()) {
        start = pIO->Tell();

        if (!i->ptr->Write(pIO))
          return false;

        end = pIO->Tell();

        if (!pIO->Sync32())
          return false;

        position.offset = start - tagStart;
        position.size = end - start;

        map[i->ptr] = position;
      }
      m_position[j] = map[i->ptr];
    }

    icUInt32Number endPos = pIO->Tell();

    if (pIO->Seek(offsetPos, icSeekSet)<0)
      return false;

    for (j=0; j<m_nProcElements; j++) {
      if (!pIO->Write32(&m_position[j].offset))
        return false;
      if (!pIO->Write32(&m_position[j].size))
        return false;
    }

    if (pIO->Seek(endPos, icSeekSet)<0)
      return false;
  }

  return true;
}

/**
******************************************************************************
* Name: CIccTagMultiProcessElement::GetElement
* 
* Purpose: 
* 
* Args: 
* 
* Return: 
******************************************************************************/
CIccMultiProcessElement *CIccTagMultiProcessElement::GetElement(int nIndex)
{
  if (!m_list)
    return NULL;

  CIccMultiProcessElementList::iterator i;
  int j;

  for(i=m_list->begin(), j=0; j<nIndex && i!=m_list->end(); i++, j++);

  if (i!=m_list->end())
    return i->ptr;

  return NULL;
}

/**
******************************************************************************
* Name: CIccTagMultiProcessElement::GetNextElemIterator
* 
* Purpose: 
* 
* Args: 
* 
* Return: 
******************************************************************************/
void CIccTagMultiProcessElement::GetNextElemIterator(CIccMultiProcessElementList::iterator &itr)
{
  itr++;
}


/**
 ******************************************************************************
 * Name: CIccTagMultiProcessElement::Begin
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
bool CIccTagMultiProcessElement::Begin(icElemInterp nInterp/* =icElemInterpLinear */)
{
  if (!m_list || !m_list->size()) {
    if (m_nInputChannels != m_nOutputChannels)
      return false;
    else
      return true;
  }

  CIccMultiProcessElementList::iterator i;

  m_nBufChannels=0;

  //Now we initialize each processing element checking channel matching as we go
  CIccMultiProcessElement *last=NULL;
  i = m_list->begin();
  if (i->ptr->NumInputChannels() != m_nInputChannels)
    return false;

  for (; i!= m_list->end(); i++) {
    if (last) {
      if (i->ptr->NumInputChannels() != last->NumOutputChannels())
        return false;
    }
    last = i->ptr;

    if (m_nBufChannels<last->NumOutputChannels())
      m_nBufChannels = last->NumOutputChannels();

    if (!last->Begin(nInterp, this))
      return false;
  }

  //The output channels must match
  if (last && last->NumOutputChannels() != m_nOutputChannels)
    return false;

  return true;
}




/**
******************************************************************************
* Name: CIccTagMultiProcessElement::ElementIndex
* 
* Purpose: 
* 
* Args: 
* 
* Return: 
******************************************************************************/
icInt32Number CIccTagMultiProcessElement::ElementIndex(CIccMultiProcessElement *pElem)
{
  CIccMultiProcessElementList::iterator i;
  icInt32Number n;

  for (n=0, i=m_list->begin(); i!= m_list->end(); i++, n++) {
    if (i->ptr == pElem)
      break;
  }
  if (i==m_list->end())
    n=-1;

  return n;
}

CIccMultiProcessElementList::iterator CIccTagMultiProcessElement::GetFirstElem()
{
  return m_list->begin();
}

CIccMultiProcessElementList::iterator CIccTagMultiProcessElement::GetLastElem()
{
  return m_list->end();
}


/**
******************************************************************************
* Name: CIccTagMultiProcessElement::GetNewApply
* 
* Purpose: 
* 
* Args: 
* 
* Return: 
******************************************************************************/
CIccApplyTagMpe *CIccTagMultiProcessElement::GetNewApply()
{
  CIccApplyTagMpe *pApply = new CIccApplyTagMpe(this);

  if (!pApply)
    return NULL;

  CIccDblPixelBuffer *pApplyBuf = pApply->GetBuf();
  pApplyBuf->UpdateChannels(m_nBufChannels);
  if (!pApplyBuf->Begin()) {
    delete pApply;
    return NULL;
  }

  if (!m_list || !m_list->size())
    return pApply;

  CIccMultiProcessElementList::iterator i, last;
  last = GetLastElem();
  for (i=GetFirstElem(); i!=last;) {
    pApply->AppendElem(i->ptr);

    GetNextElemIterator(i);
  }

  return pApply;
}


/**
 ******************************************************************************
 * Name: CIccTagMultiProcessElement::Apply
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
void CIccTagMultiProcessElement::Apply(CIccApplyTagMpe *pApply, icFloatNumber *pDestPixel, const icFloatNumber *pSrcPixel) const
{
  if (!pApply || !pApply->GetList() || !pApply->GetList()->size()) {
    memcpy(pDestPixel, pSrcPixel, m_nInputChannels*sizeof(icFloatNumber));
    return;
  }

  CIccDblPixelBuffer *pApplyBuf = pApply->GetBuf();
  CIccApplyMpeIter i = pApply->begin();
  CIccApplyMpeIter next;

  next = i;
  next++;

  if (next==pApply->end()) {
    //Elements rely on pDestPixel != pSrcPixel
    if (pSrcPixel==pDestPixel) {
      i->ptr->Apply(pApplyBuf->GetDstBuf(), pSrcPixel);
      memcpy(pDestPixel, pApplyBuf->GetDstBuf(), m_nOutputChannels*sizeof(icFloatNumber));
    }
    else {
      i->ptr->Apply(pDestPixel, pSrcPixel);
    }
  }
  else {
    i->ptr->Apply(pApplyBuf->GetDstBuf(), pSrcPixel);
    i++;
    next++;
    pApplyBuf->Switch();

    while (next != pApply->end()) {
      CIccMultiProcessElement *pElem = i->ptr->GetElem();

      if (!pElem->IsAcs()) {
        i->ptr->Apply(pApplyBuf->GetDstBuf(), pApplyBuf->GetSrcBuf());
        pApplyBuf->Switch();
      }

      i++;
      next++;
    }

    i->ptr->Apply(pDestPixel, pApplyBuf->GetSrcBuf());
  }
}


/**
 ******************************************************************************
 * Name: CIccTagMultiProcessElement::Validate
 * 
 * Purpose: 
 * 
 * Args: 
 * 
 * Return: 
 ******************************************************************************/
icValidateStatus CIccTagMultiProcessElement::Validate(icTagSignature sig, std::string &sReport,
                                           const CIccProfile* pProfile /*=NULL*/) const
{ 
  icValidateStatus rv = icValidateOK;

  CIccInfo Info;
  std::string sSigName = Info.GetSigName(sig);

  if (!m_list || !m_list->size()) {
    if (m_nInputChannels != m_nOutputChannels) {
      sReport += icValidateCriticalErrorMsg;
      sReport += sSigName;
      sReport += " No processing elements and input and output channels do not match!\r\n";
      return icValidateCriticalError;
    }
    else {
      sReport += icValidateWarningMsg;
      sReport += sSigName;
      sReport += " No processing elements.\r\n";
      return icValidateWarning;
    }
  }

  CIccMultiProcessElementList::iterator i = m_list->begin();
  CIccMultiProcessElement *last=NULL;

  if (i->ptr->NumInputChannels() != m_nInputChannels) {
    sReport += icValidateCriticalErrorMsg;
    sReport += sSigName;
    sReport += " Mis-matching number of input channels!\r\n";
    return icValidateCriticalError;
  }

  for (; i!= m_list->end(); i++) {
    if (last) {
      if (i->ptr->NumInputChannels() != last->NumOutputChannels()) {
        sReport += icValidateCriticalErrorMsg;
        sReport += sSigName;

        sReport += "(";
        sReport += last->GetClassName();
        sReport += "->";
        sReport += i->ptr->GetClassName();

        sReport += " Mis-matching number of channels!\r\n";
        return icValidateCriticalError;
      }
    }
    last = i->ptr;

    rv = icMaxStatus(rv, last->Validate(sig, sReport, this));
  }

  if (last && last->NumOutputChannels() != m_nOutputChannels) {
    sReport += icValidateCriticalErrorMsg;
    sReport += sSigName;
    sReport += " Mis-matching number of output channels!\r\n";
    return icValidateCriticalError;
  }

  return rv;
}

#ifdef USESAMPLEICCNAMESPACE
} //namespace sampleICC
#endif
