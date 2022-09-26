/** @file
    File:       IccMpeFactory.cpp

    Contains:   Implementation of the CIccProcessElement class and creation factories

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
// -Feb 4, 2006
// Added CIccProcessElement Creation using factory support
//
//////////////////////////////////////////////////////////////////////

#include "IccTagMPE.h"
#include "IccMpeBasic.h"
#include "IccMpeACS.h"
#include "IccMpeFactory.h"
#include "IccUtil.h"
#include "IccProfile.h"

#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif

CIccMultiProcessElement* CIccBasicMpeFactory::CreateElement(icElemTypeSignature elemTypeSig)
{
  switch(elemTypeSig) {
    case icSigCurveSetElemType:
      return new CIccMpeCurveSet();

    case icSigMatrixElemType:
      return new CIccMpeMatrix();

    case icSigCLutElemType:
      return new CIccMpeCLUT();

    case icSigBAcsElemType:
      return new CIccMpeBAcs();

    case icSigEAcsElemType:
      return new CIccMpeEAcs();

    default:
      return new CIccMpeUnknown();
  }
}

bool CIccBasicMpeFactory::GetElementSigName(std::string &elemName, icElemTypeSignature elemTypeSig)
{
  switch(elemTypeSig) {
    case icSigCurveSetElemType:
      elemName = "Curve Set Element";
      break;

    case icSigMatrixElemType:
      elemName = "Matrix Element";
      break;

    case icSigCLutElemType:
      elemName = "CLUT Element";
      break;

    default:
      elemName = "Unknown Element Type";
      break;
  }

  return true;
}

std::auto_ptr<CIccMpeCreator> CIccMpeCreator::theElementCreator;

CIccMpeCreator::~CIccMpeCreator()
{
  IIccMpeFactory *pFactory = DoPopFactory(true);

  while (pFactory) {
    delete pFactory;
    pFactory = DoPopFactory(true);
  }
}

CIccMpeCreator* CIccMpeCreator::GetInstance()
{
  if (!theElementCreator.get()) {
    theElementCreator = CIccMpeCreatorPtr(new CIccMpeCreator);

    theElementCreator->DoPushFactory(new CIccBasicMpeFactory);
  }

  return theElementCreator.get();
}

CIccMultiProcessElement* CIccMpeCreator::DoCreateElement(icElemTypeSignature elemTypeSig)
{
  CIccMpeFactoryList::iterator i;
  CIccMultiProcessElement *rv = NULL;

  for (i=factoryStack.begin(); i!=factoryStack.end(); i++) {
    rv = (*i)->CreateElement(elemTypeSig);
    if (rv)
      break;
  }
  return rv;
}

bool CIccMpeCreator::DoGetElementSigName(std::string &elemName, icElemTypeSignature elemTypeSig)
{
  CIccMpeFactoryList::iterator i;

  for (i=factoryStack.begin(); i!=factoryStack.end(); i++) {
    if ((*i)->GetElementSigName(elemName, elemTypeSig))
      return true;
  }

  return false;
}

void CIccMpeCreator::DoPushFactory(IIccMpeFactory *pFactory)
{
  factoryStack.push_front(pFactory);
}

IIccMpeFactory* CIccMpeCreator::DoPopFactory(bool bAll /*=false*/)
{
  if (factoryStack.size()>0) {
    CIccMpeFactoryList::iterator i=factoryStack.begin();
    IIccMpeFactory* rv = (*i);
    factoryStack.pop_front();
    return rv;
  }
  return NULL;
}

#ifdef USESAMPLEICCNAMESPACE
} //namespace sampleICC
#endif
