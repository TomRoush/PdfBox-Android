/** @file
    File:       IccMpeFactory.h

    Contains:   Header for implementation of CIccMpeFactory class and
                creation factories

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
// -Feb 4, 2006 
//  A CIccMpeCreator singleton class has been added to provide general
//  support for dynamically creating element classes using a element signature.
//  Prototype and private element type support can be added to the system
//  by pushing additional IIccMpeFactory based objects to the 
//  singleton CIccMpeCreator object.
//
// -Nov 6, 2006
//  Merged into release
//
//////////////////////////////////////////////////////////////////////

#ifndef _ICCMPEFACTORY_H
#define _ICCMPEFACTORY_H

#include "IccDefs.h"
#include <memory>
#include <list>

//CIccProcessElement factory support
#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif

class CIccMultiProcessElement;

/**
 ***********************************************************************
 * Class: IIccMpeFactory
 *
 * Purpose:
 * IIccMpeFactory is a factory pattern interface for CIccProcessElement
 * creation.
 * This class is pure virtual.
 ***********************************************************************
 */
class ICCPROFLIB_API IIccMpeFactory 
{
public:
  virtual ~IIccMpeFactory() {}

  /**
  * Function: CreateElement(elemTypeSig)
  *  Create a element of type elemTypeSig.
  *
  * Parameter(s):
  *  elemTypeSig = signature of the ICC element type for the element to
  *  be created
  *
  * Returns a new CIccProcessElement object of the given signature type.
  * If the element factory doesn't support creation of elements of type
  * elemTypeSig then it should return NULL.
  */
  virtual CIccMultiProcessElement* CreateElement(icElemTypeSignature elemTypeSig)=0;

  /**
  * Function: GetElementSigName(elemTypeSig)
  *  Get display name of elemTypeSig.
  *
  * Parameter(s):
  *  elemName = string to put element name into, 
  *  elemTypeSig = signature of the ICC element type to get a name for
  *
  * Returns true if element type is recognized by the factory, false if
  * the factory doesn't create elemTypeSig elements.
  */
  virtual bool GetElementSigName(std::string &elemName, icElemTypeSignature elemTypeSig)=0;
};


//A CIccMpeFactoryList is used by CIccMpeCreator to keep track of element
//creation factories
typedef std::list<IIccMpeFactory*> CIccMpeFactoryList;


/**
 ***********************************************************************
 * Class: CIccBasicMpeFactory
 *
 * Purpose:
 * CIccBasicMpeFactory provides creation of CIccProcessElement's 
 * defined by the ICC profile specification.  The CIccMpeCreator always
 * creates a CIccBasicElemFactory.
 ***********************************************************************
 */
class CIccBasicMpeFactory : public IIccMpeFactory
{
public:
  /**
  * Function: CreateElement(elemTypeSig)
  *  Create a element of type elemTypeSig.
  *
  * Parameter(s):
  *  elemTypeSig = signature of the ICC element type for the element to be created
  *
  * Returns a new CIccProcessElement object of the given signature type.
  * Unrecognized elemTypeSig's will be created as a CIccProcessElementUnknown object.
  */
  virtual CIccMultiProcessElement* CreateElement(icElemTypeSignature elementSig);

  /**
  * Function: GetElementSigName(elemTypeSig)
  *  Get display name of elemTypeSig.
  *
  * Parameter(s):
  *  elemName = string to put element name into, 
  *  elemTypeSig = signature of the ICC element type to get a name for
  *
  * Returns true if element type is recognized by the factory, false if the
  * factory doesn't create elemTypeSig elements.
  */
  virtual bool GetElementSigName(std::string &elemName, icElemTypeSignature elemTypeSig);
};

class CIccMpeCreator;

typedef std::auto_ptr<CIccMpeCreator> CIccMpeCreatorPtr;

/**
 ***********************************************************************
 * Class: CIccMpeCreator
 *
 * Purpose:
 * CIccMpeCreator uses a singleton pattern to provide dynamically
 * upgradeable CIccProcessElement derived object creation based on
 * element signature.
 ***********************************************************************
 */
class CIccMpeCreator 
{
public:
  ~CIccMpeCreator();

  /**
  * Function: CreateElement(elemTypeSig)
  *  Create a element of type elemTypeSig.
  *
  * Parameter(s):
  *  elemTypeSig = signature of the ICC element type for the element to
  *  be created
  *
  * Returns a new CIccProcessElement object of the given signature type.
  * Each factory in the factoryStack is used until a factory supports the
  * signature type.
  */
  static CIccMultiProcessElement* CreateElement(icElemTypeSignature elemTypeSig)
      { return CIccMpeCreator::GetInstance()->DoCreateElement(elemTypeSig); }

  /**
  * Function: GetElementSigName(elemTypeSig)
  *  Get display name of elemTypeSig.
  *
  * Parameter(s):
  *  elemName = string to put element name into
  *  elemTypeSig = signature of the ICC element type to get a name for
  *
  * Returns true if element type is recognized by any factory, false if all
  * factories do not create elemTypeSig elements. If element type is not
  * recognized by any factories a suitable display name will be placed in
  * elemName.
  */
  static bool GetElementSigName(std::string &elemName, icElemTypeSignature elemTypeSig)
      { return CIccMpeCreator::GetInstance()->DoGetElementSigName(elemName, elemTypeSig); }

  /**
  * Function: PushFactory(pFactory)
  *  Add an IIccMpeFactory to the stack of element factories tracked by
  *  the system.
  *
  * Parameter(s):
  *  pFactory = pointer to an IIccMpeFactory object to add to the
  *    system.  The pFactory must be created with new, and will be owned
  *    CIccMpeCreator until popped off the stack using PopFactory().
  *    Any factories not popped off will be taken care of properly on
  *    application shutdown.
  *
  */
  static void PushFactory(IIccMpeFactory *pFactory)
      { CIccMpeCreator::GetInstance()->CIccMpeCreator::DoPushFactory(pFactory); }

  /**
  * Function: PopFactory()
  *  Remove the top IIccMpeFactory from the stack of element factories
  *  tracked by the system.
  *
  * Parameter(s):
  *  None
  *
  *  Returns the top IIccMpeFactory from the stack of element factories
  *  tracked by the system.  The returned element factory is no longer 
  *  owned by the system and needs to be deleted to avoid memory leaks.
  *
  *  Note: The initial CIccBasicElemFactory cannot be popped off the stack.
  */
  static IIccMpeFactory* PopFactory()
      { return CIccMpeCreator::GetInstance()->DoPopFactory(); }

private:
  /**Only GetInstance() can create the singleton*/
  CIccMpeCreator() { }

  /**
  * Function: GetInstance()
  *  Private static function to access singleton CiccElementCreator Object.
  *
  * Parameter(s):
  *  None
  *
  * Returns the singleton CIccMpeCreator object.  It will allocate
  * a new one and push a single CIccSpecElement Factory object onto the
  * factory stack if the singleton has not been intialized.
  */
  static CIccMpeCreator* GetInstance();

  CIccMultiProcessElement* DoCreateElement(icElemTypeSignature elemTypeSig);
  bool DoGetElementSigName(std::string &elemName, icElemTypeSignature elemTypeSig);
  void DoPushFactory(IIccMpeFactory *pFactory);
  IIccMpeFactory* DoPopFactory(bool bAll=false);

  static CIccMpeCreatorPtr theElementCreator; 

  CIccMpeFactoryList factoryStack;
};

#ifdef USESAMPLEICCNAMESPACE
} //namespace sampleICC
#endif

#endif //_ICCMPEFACTORY_H
