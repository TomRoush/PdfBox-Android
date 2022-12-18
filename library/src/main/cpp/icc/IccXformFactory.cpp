/** @file
    File:       IccXformFactory.cpp

    Contains:   Implementation of the CIccXform class and creation factories

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
// -Oct 30, 2005
// Added CICCXform Creation using factory support
//
//////////////////////////////////////////////////////////////////////

#include "IccXformFactory.h"

#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif

CIccXform* CIccBaseXformFactory::CreateXform(icXformType xformSig, CIccTag *pTag/*=NULL*/, CIccCreateXformHintManager *pHintManager/*=NULL*/)
{
  //We generally ignore pHint in the base creator (used by others to determine what form of xform to create)
  switch(xformSig) {
   case icXformTypeMatrixTRC:
     return new CIccXformMatrixTRC();

   case icXformType3DLut:
     return new CIccXform3DLut(pTag);

   case icXformType4DLut:
     return new CIccXform4DLut(pTag);

   case icXformTypeNDLut:
     return new CIccXformNDLut(pTag);

   case icXformTypeNamedColor:
     if (pHintManager) {
			 IIccCreateXformHint* pHint = pHintManager->GetHint("CIccCreateXformNamedColorHint");
			 if (pHint) {
				 CIccCreateNamedColorXformHint *pNCHint = (CIccCreateNamedColorXformHint*)pHint;
				 return new CIccXformNamedColor(pTag, pNCHint->csPcs, pNCHint->csDevice);
			 }
     }
		 return NULL;

   case icXformTypeMpe:
     return new CIccXformMpe(pTag);

	 case icXformTypeMonochrome:
		 return new CIccXformMonochrome();

    default:
      return NULL;
  }
}

std::auto_ptr<CIccXformCreator> CIccXformCreator::theXformCreator;

CIccXformCreator::~CIccXformCreator()
{
  IIccXformFactory *pFactory = DoPopFactory(true);

  while (pFactory) {
    delete pFactory;
    pFactory = DoPopFactory(true);
  }
}

CIccXformCreator* CIccXformCreator::GetInstance()
{
  if (!theXformCreator.get()) {
    theXformCreator = CIccXformCreatorPtr(new CIccXformCreator);

    theXformCreator->DoPushFactory(new CIccBaseXformFactory);
  }

  return theXformCreator.get();
}

CIccXform* CIccXformCreator::DoCreateXform(icXformType xformTypeSig, CIccTag *pTag/*=NULL*/, CIccCreateXformHintManager *pHintManager/*=NULL*/)
{
  CIccXformFactoryList::iterator i;
  CIccXform *rv = NULL;

  for (i=factoryStack.begin(); i!=factoryStack.end(); i++) {
    rv = (*i)->CreateXform(xformTypeSig, pTag, pHintManager);
    if (rv)
      break;
  }
  return rv;
}

void CIccXformCreator::DoPushFactory(IIccXformFactory *pFactory)
{
  factoryStack.push_front(pFactory);
}

IIccXformFactory* CIccXformCreator::DoPopFactory(bool bAll /*=false*/)
{
  //int nNum = (bAll ? 0 : 1);

  if (factoryStack.size()>0) {
    CIccXformFactoryList::iterator i=factoryStack.begin();
    IIccXformFactory* rv = (*i);
    factoryStack.pop_front();
    return rv;
  }
  return NULL;
}

#ifdef USESAMPLEICCNAMESPACE
} //namespace sampleICC
#endif
