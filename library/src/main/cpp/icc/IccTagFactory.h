/** @file
    File:       IccTagFactory.h

    Contains:   Header for implementation of CIccTagFactory class and
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
// -Oct 30, 2005 
//  A CIccTagCreator singleton class has been added to provide general
//  support for dynamically creating tag classes using a tag signature.
//  Prototype and private tag type support can be added to the system
//  by pushing additional IIccTagFactory based objects to the 
//  singleton CIccTagCreator object.
//
//////////////////////////////////////////////////////////////////////

#ifndef _ICCTAGFACTORY_H
#define _ICCTAGFACTORY_H

#include "IccDefs.h"
#include <memory>
#include <list>
#include <string>

//CIccTag factory support
#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif

class CIccTag;

/**
 ***********************************************************************
 * Class: IIccTagFactory
 *
 * Purpose:
 * IIccTagFactory is a factory pattern interface for CIccTag creation.
 * This class is pure virtual.
 ***********************************************************************
 */
class IIccTagFactory 
{
public:
  virtual ~IIccTagFactory() {}

  /**
  * Function: CreateTag(tagTypeSig)
  *  Create a tag of type tagTypeSig.
  *
  * Parameter(s):
  *  tagTypeSig = signature of the ICC tag type for the tag to be created
  *
  * Returns a new CIccTag object of the given signature type.  If the tag
  * factory doesn't support creation of tags of type tagTypeSig then it
  * should return NULL.
  */
  virtual CIccTag* CreateTag(icTagTypeSignature tagTypeSig)=0;

  /**
  * Function: GetTagSigName(tagSig)
  *  Get display name of tagSig.
  *
  * Parameter(s):
  *  tagSig = signature of the ICC tag to get a name for
  *
  * Returns pointer to string containing name of tag if tag is recognized
  * by the factory, NULL if the factory doesn't create tagSig tags.
  */
  virtual const icChar* GetTagSigName(icTagSignature tagSig)=0;

  /**
  * Function: GetTagTypeSigName(tagTypeSig)
  *  Get display name of tagTypeSig.
  *
  * Parameter(s):
  *  tagTypeSig = signature of the ICC tag type to get a name for
  *
  * Returns pointer to string containing name of tag type if tag is recognized
  * by the factory, NULL if the factory doesn't create tagTypeSig tags.
  */
  virtual const icChar* GetTagTypeSigName(icTagTypeSignature tagTypeSig)=0;
};


//A CIccTagFactoryList is used by CIccTagCreator to keep track of tag
//creation factories
typedef std::list<IIccTagFactory*> CIccTagFactoryList;


/**
 ***********************************************************************
 * Class: CIccSpecTagFactory
 *
 * Purpose:
 * CIccSpecTagFactory provides creation of CIccTag's defined by the ICC profile
 * specification.  The CIccTagCreator always creates a CIccSpecTagFactory.
 ***********************************************************************
 */
class CIccSpecTagFactory : public IIccTagFactory
{
public:
  /**
  * Function: CreateTag(tagTypeSig)
  *  Create a tag of type tagTypeSig.
  *
  * Parameter(s):
  *  tagTypeSig = signature of the ICC tag type for the tag to be created
  *
  * Returns a new CIccTag object of the given signature type.
  * Unrecognized tagTypeSig's will be created as a CIccTagUnknown object.
  */
  virtual CIccTag* CreateTag(icTagTypeSignature tagSig);

  /**
  * Function: GetTagSigName(tagSig)
  *  Get display name of tagSig.
  *
  * Parameter(s):
  *  tagName = string to put tag name into, 
  *  tagSig = signature of the ICC tag type to get a name for
  *
  * Returns pointer to string containing name of tag if tag is recognized
  * by the factory, NULL if the factory doesn't create tagSig tags.
  */
  virtual const icChar* GetTagSigName(icTagSignature tagSig);

  /**
  * Function: GetTagTypeSigName(tagTypeSig)
  *  Get display name of tagTypeSig.
  *
  * Parameter(s):
  *  tagName = string to put tag name into, 
  *  tagTypeSig = signature of the ICC tag type to get a name for
  *
  * Returns pointer to string containing name of tag type if tag is recognized
  * by the factory, NULL if the factory doesn't create tagTypeSig tags.
  */
  virtual const icChar* GetTagTypeSigName(icTagTypeSignature tagTypeSig);
};

class CIccTagCreator;

typedef std::auto_ptr<CIccTagCreator> CIccTagCreatorPtr;

/**
 ***********************************************************************
 * Class: CIccTagCreator
 *
 * Purpose:
 * CIccTagCreator uses a singleton pattern to provide dynamically
 * upgradeable CIccTag derived object creation based on tag signature.
 ***********************************************************************
 */
class CIccTagCreator 
{
public:
  ~CIccTagCreator();

  /**
  * Function: CreateTag(tagTypeSig)
  *  Create a tag of type tagTypeSig.
  *
  * Parameter(s):
  *  tagTypeSig = signature of the ICC tag type for the tag to be created
  *
  * Returns a new CIccTag object of the given signature type.
  * Each factory in the factoryStack is used until a factory supports the
  * signature type.
  */
  static CIccTag* CreateTag(icTagTypeSignature tagTypeSig)
      { return CIccTagCreator::GetInstance()->DoCreateTag(tagTypeSig); }

  /**
  * Function: GetTagSigName(tagSig)
  *  Get display name of tagSig.
  *
  * Parameter(s):
  *  tagSig = signature of the ICC tag to get a name for
  *
  * Returns ptr to string containing name of tag type if it is recognized
  * by any factory, NULL if all factories do not create tagTypeSig tags.
  */
  static const icChar* GetTagSigName(icTagSignature tagTypeSig)
      { return CIccTagCreator::GetInstance()->DoGetTagSigName(tagTypeSig); }


  /**
  * Function: GetTagTypeSigName(tagTypeSig)
  *  Get display name of tagTypeSig.
  *
  * Parameter(s):
  *  tagTypeSig = signature of the ICC tag type to get a name for
  *
  * Returns ptr to string containing name of tag type if it is recognized by
  * any factory, NULL if all factories do not create tagTypeSig tags. 
  */
  static const icChar* GetTagTypeSigName(icTagTypeSignature tagTypeSig)
      { return CIccTagCreator::GetInstance()->DoGetTagTypeSigName(tagTypeSig); }

  /**
  * Function: PushFactory(pFactory)
  *  Add an IIccTagFactory to the stack of tag factories tracked by the system.
  *
  * Parameter(s):
  *  pFactory = pointer to an IIccTagFactory object to add to the system.
  *    The pFactory must be created with new, and will be owned CIccTagCreator
  *    until popped off the stack using PopFactory().  Any factories not
  *    popped off will be taken care of properly on application shutdown.
  *
  */
  static void PushFactory(IIccTagFactory *pFactory)
      { CIccTagCreator::GetInstance()->CIccTagCreator::DoPushFactory(pFactory); }

  /**
  * Function: PopFactory()
  *  Remove the top IIccTagFactory from the stack of tag factories tracked by the system.
  *
  * Parameter(s):
  *  None
  *
  *  Returns the top IIccTagFactory from the stack of tag factories tracked by the system.
  *  The returned tag factory is no longer owned by the system and needs to be deleted
  *  to avoid memory leaks.
  *
  *  Note: The initial CIccSpecTagFactory cannot be popped off the stack.
  */
  static IIccTagFactory* PopFactory()
      { return CIccTagCreator::GetInstance()->DoPopFactory(); }

private:
  /**Only GetInstance() can create the signleton*/
  CIccTagCreator() { }

  /**
  * Function: GetInstance()
  *  Private static function to access singleton CiccTagCreator Object.
  *
  * Parameter(s):
  *  None
  *
  * Returns the singleton CIccTagCreator object.  It will allocate
  * a new one and push a single CIccSpecTag Factory object onto the factory
  * stack if the singleton has not been intialized.
  */
  static CIccTagCreator* GetInstance();

  CIccTag* DoCreateTag(icTagTypeSignature tagTypeSig);
  const icChar *DoGetTagSigName(icTagSignature tagSig);
  const icChar *DoGetTagTypeSigName(icTagTypeSignature tagTypeSig);
  void DoPushFactory(IIccTagFactory *pFactory);
  IIccTagFactory* DoPopFactory(bool bAll=false);

  static CIccTagCreatorPtr theTagCreator; 

  CIccTagFactoryList factoryStack;
};

#ifdef USESAMPLEICCNAMESPACE
} //namespace sampleICC
#endif

#endif //_ICCTAGFACTORY_H
