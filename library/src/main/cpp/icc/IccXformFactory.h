/** @file
    File:       IccXformFactory.h

    Contains:   Header for implementation of CIccXformFactory class and
                creation factories

    Version:    V1

    Copyright:  © see ICC Software License
*/

/*
 * The ICC Software License, Version 0.2
 *
 *
 * Copyright (c) 2007-2015 The International Color Consortium. All rights 
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
// -Nov 21, 2007 
//  A CIccXformCreator singleton class has been added to provide general
//  support for dynamically creating xform classes using a xform type.
//  Prototype and private xform type support can be added to the system
//  by pushing additional IIccXformFactory based objects to the 
//  singleton CIccXformCreator object.
//
//////////////////////////////////////////////////////////////////////

#ifndef _ICCXFORMFACTORY_H
#define _ICCXFORMFACTORY_H

#include "IccCmm.h"
#include <memory>
#include <list>
#include <string>

//CIccXform factory support
#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif

/**
 ***********************************************************************
 * Class: IIccXformFactory
 *
 * Purpose:
 * IIccXformFactory is a factory pattern interface for CIccXform creation.
 * This class is pure virtual.
 ***********************************************************************
 */
class IIccXformFactory 
{
public:
  virtual ~IIccXformFactory() {}

  /**
  * Function: CreateXform(xformTypeSig)
  *  Create a xform of type xformTypeSig.
  *
  * Parameter(s):
  *  xformTypeSig = signature of the ICC xform type for the xform to be created
  *  pTag = tag information for created xform
  *  pHintManager = contains additional information used to create xform
  *
  * Returns a new CIccXform object of the given signature type.  If the xform
  * factory doesn't support creation of xforms of type xformTypeSig then it
  * should return NULL.
  */
  virtual CIccXform* CreateXform(icXformType xformType, CIccTag *pTag=NULL, CIccCreateXformHintManager* pHintManager=0)=0;
};


//A CIccXformFactoryList is used by CIccXformCreator to keep track of xform
//creation factories
typedef std::list<IIccXformFactory*> CIccXformFactoryList;


/**
 ***********************************************************************
 * Class: CIccBaseXformFactory
 *
 * Purpose:
 * CIccSpecXformFactory provides creation of Base CIccXform's. The
 * CIccXformCreator always creates a CIccSpecXformFactory.
 ***********************************************************************
 */
class CIccBaseXformFactory : public IIccXformFactory
{
public:
  /**
  * Function: CreateXform(xformTypeSig)
  *  Create a xform of type xformTypeSig.
  *
  * Parameter(s):
  *  xformTypeSig = signature of the ICC xform type for the xform to be created
  *  pTag = tag information for created xform
  *  pHintManager = contains additional information used to create xform
  *
  * Returns a new CIccXform object of the given xform type.
  * Unrecognized xformTypeSig's will be created as a CIccXformUnknown object.
  */
  virtual CIccXform* CreateXform(icXformType xformType, CIccTag *pTag=NULL, CIccCreateXformHintManager *pHintManager=NULL);

};

class CIccXformCreator;

typedef std::auto_ptr<CIccXformCreator> CIccXformCreatorPtr;

/**
 ***********************************************************************
 * Class: CIccXformCreator
 *
 * Purpose:
 * CIccXformCreator uses a singleton pattern to provide dynamically
 * upgradeable CIccXform derived object creation based on xform type.
 ***********************************************************************
 */
class CIccXformCreator 
{
public:
  ~CIccXformCreator();

  /**
  * Function: CreateXform(xformTypeSig)
  *  Create a xform of type xformTypeSig.
  *
  * Parameter(s):
  *  xformType = signature of the ICC xform type for the xform to be created
  *  pTag = tag information for created xform
  *  pHintManager = contains additional information used to create xform
  *
  * Returns a new CIccXform object of the given xform type.
  * Each factory in the factoryStack is used until a factory supports the
  * signature type.
  */
  static CIccXform* CreateXform(icXformType xformType, CIccTag *pTag=NULL, CIccCreateXformHintManager *pHintManager=NULL)
      { return CIccXformCreator::GetInstance()->DoCreateXform(xformType, pTag, pHintManager); }

  /**
  * Function: PushFactory(pFactory)
  *  Add an IIccXformFactory to the stack of xform factories tracked by the system.
  *
  * Parameter(s):
  *  pFactory = pointer to an IIccXformFactory object to add to the system.
  *    The pFactory must be created with new, and will be owned CIccXformCreator
  *    until popped off the stack using PopFactory().  Any factories not
  *    popped off will be taken care of properly on application shutdown.
  *
  */
  static void PushFactory(IIccXformFactory *pFactory)
      { CIccXformCreator::GetInstance()->CIccXformCreator::DoPushFactory(pFactory); }

  /**
  * Function: PopFactory()
  *  Remove the top IIccXformFactory from the stack of xform factories tracked by the system.
  *
  * Parameter(s):
  *  None
  *
  *  Returns the top IIccXformFactory from the stack of xform factories tracked by the system.
  *  The returned xform factory is no longer owned by the system and needs to be deleted
  *  to avoid memory leaks.
  *
  *  Note: The initial CIccSpecXformFactory cannot be popped off the stack.
  */
  static IIccXformFactory* PopFactory()
      { return CIccXformCreator::GetInstance()->DoPopFactory(); }

private:
  /**Only GetInstance() can create the signleton*/
  CIccXformCreator() { }

  /**
  * Function: GetInstance()
  *  Private static function to access singleton CiccXformCreator Object.
  *
  * Parameter(s):
  *  None
  *
  * Returns the singleton CIccXformCreator object.  It will allocate
  * a new one and push a single CIccSpecXform Factory object onto the factory
  * stack if the singleton has not been intialized.
  */
  static CIccXformCreator* GetInstance();

  CIccXform* DoCreateXform(icXformType xformType, CIccTag *pTag=NULL, CIccCreateXformHintManager *pHintManager=NULL);
  void DoPushFactory(IIccXformFactory *pFactory);
  IIccXformFactory* DoPopFactory(bool bAll=false);

  static CIccXformCreatorPtr theXformCreator; 

  CIccXformFactoryList factoryStack;
};

#ifdef USESAMPLEICCNAMESPACE
} //namespace sampleICC
#endif

#endif //_ICCXFORMFACTORY_H
