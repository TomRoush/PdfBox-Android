/** @file
File:       IccTagDictTag.h

Contains:   Header for implementation of CIccTagDict
and supporting classes

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
// -Jun 26, 2009 
//  Initial CIccDictTag prototype development
//
//////////////////////////////////////////////////////////////////////

#ifndef _ICCTAGSUBTAG_H
#define _ICCTAGSUBTAG_H

#include "IccProfile.h"
#include "IccTag.h"
#include "IccTagFactory.h"
#include "IccUtil.h"
#include <memory>
#include <list>
#include <string>

#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif


/**
****************************************************************************
* Data Class: CIccDictEntry
* 
* Purpose: Implementation of a dictionary entry with optional localization of
* name and value
*****************************************************************************
*/
class ICCPROFLIB_API CIccDictEntry
{
public: //member functions
  CIccDictEntry();
  CIccDictEntry(const CIccDictEntry& IDE);
  CIccDictEntry &operator=(const CIccDictEntry &IDE);
  virtual ~CIccDictEntry();

  void Describe(std::string &sDescription);

  icUInt32Number PosRecSize();

  //Data
  CIccUTF16String m_sName;

  const CIccUTF16String &GetValue() { return m_sValue; }
  bool IsValueSet() { return m_bValueSet; }

  //GetNameLocalized and GetValueLocalized both give direct access to objects owned by the CIccDirEntry object
  CIccTagMultiLocalizedUnicode* GetNameLocalized() { return m_pNameLocalized; }
  CIccTagMultiLocalizedUnicode* GetValueLocalized() { return m_pValueLocalized; }

  void UnsetValue() { m_sValue.Clear(); m_bValueSet = false; }
  bool SetValue(const CIccUTF16String &sValue);

  //SetNameLocalized and SetValueLocalized both transfer ownership of the argument to the CIccDirEntry object
  //deleting access to previous object
  bool SetNameLocalized(CIccTagMultiLocalizedUnicode *pNameLocalized);
  bool SetValueLocalized(CIccTagMultiLocalizedUnicode *pValueLocalized);

protected:
  CIccUTF16String m_sValue; 
  bool m_bValueSet;

  CIccTagMultiLocalizedUnicode *m_pNameLocalized;
  CIccTagMultiLocalizedUnicode *m_pValueLocalized;
};

class CIccDictEntryPtr
{
public:
  CIccDictEntry *ptr;
};

/**
****************************************************************************
* List Class: CIccDictEntry
* 
* Purpose: Dictionary is stored as a List of CIccDictEntry objects
*****************************************************************************
*/
typedef std::list<CIccDictEntryPtr> CIccNameValueDict;

/**
****************************************************************************
* Class: CIccTagDict
* 
* Purpose: A name-value dictionary tag with optional localization 
*****************************************************************************
*/
class ICCPROFLIB_API CIccTagDict : public CIccTag
{
public:
  CIccTagDict();
  CIccTagDict(const CIccTagDict &dict);
  CIccTagDict &operator=(const CIccTagDict &dict);
  virtual CIccTag *NewCopy() const { return new CIccTagDict(*this);}
  virtual ~CIccTagDict();

  virtual icTagTypeSignature GetType() const { return icSigDictType; }
  virtual const icChar *GetClassName() const { return "CIccTagDict"; }

  virtual void Describe(std::string &sDescription);

  virtual bool Read(icUInt32Number size, CIccIO *pIO);
  virtual bool Write(CIccIO *pIO);

  virtual icValidateStatus Validate(icTagSignature sig, std::string &sReport, const CIccProfile* pProfile=NULL) const;

  bool AreNamesUnique() const;
  bool AreNamesNonzero() const;

  CIccDictEntry *Get(const char *szName) const;
  CIccDictEntry *Get(const icUInt16Number *szName) const;
  CIccDictEntry *Get(const CIccUTF16String &sName) const;

  CIccUTF16String GetValue(const char *szName, bool *bIsSet=NULL) const;
  CIccUTF16String GetValue(const icUnicodeChar *szName, bool *bIsSet=NULL) const;
  CIccUTF16String GetValue(const CIccUTF16String &sName, bool *bIsSet=NULL) const;

  CIccTagMultiLocalizedUnicode* GetNameLocalized(const CIccUTF16String &sName) const;
  CIccTagMultiLocalizedUnicode* GetNameLocalized(const icUnicodeChar *szName) const;
  CIccTagMultiLocalizedUnicode* GetNameLocalized(const char *szName) const;

  CIccTagMultiLocalizedUnicode* GetValueLocalized(const CIccUTF16String &sName) const;
  CIccTagMultiLocalizedUnicode* GetValueLocalized(const icUnicodeChar *szName) const;
  CIccTagMultiLocalizedUnicode* GetValueLocalized(const char *szName) const;

  bool Remove(const CIccUTF16String &sName);
  bool Remove(const icUnicodeChar *szName);
  bool Remove(const char *szName);

  bool Set(const char *szName, const char *szValue=NULL);
  bool Set(const icUnicodeChar *szName, const icUnicodeChar *szValue=NULL);
  bool Set(const CIccUTF16String &sName, const CIccUTF16String &sValue, bool bUnSet=false);

  bool SetNameLocalized(const char *szName, CIccTagMultiLocalizedUnicode *pTag);
  bool SetNameLocalized(const icUnicodeChar *szName, CIccTagMultiLocalizedUnicode *pTag);
  bool SetNameLocalized(const CIccUTF16String &sName, CIccTagMultiLocalizedUnicode *pTag);

  bool SetValueLocalized(const char *szName, CIccTagMultiLocalizedUnicode *pTag);
  bool SetValueLocalized(const icUnicodeChar *szName, CIccTagMultiLocalizedUnicode *pTag);
  bool SetValueLocalized(const CIccUTF16String &sName, CIccTagMultiLocalizedUnicode *pTag);

  CIccNameValueDict *m_Dict;

protected:
  bool m_bBadAlignment;
  void Cleanup();
  icUInt32Number MaxPosRecSize();

  icUInt32Number m_tagSize;
  icUInt32Number m_tagStart;
};


//CIccFloatTag support
#ifdef USESAMPLEICCNAMESPACE
}
#endif

#endif //_ICCTAGDICTTAG_H

