/** @file
    File:       IccTagFactory.cpp

    Contains:   Implementation of the CIccTag class and creation factories

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
// -Oct 30, 2005
// Added CICCTag Creation using factory support
//
//////////////////////////////////////////////////////////////////////

#include "IccTag.h"
#include "IccTagFactory.h"
#include "IccUtil.h"
#include "IccProfile.h"

#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif

CIccTag* CIccSpecTagFactory::CreateTag(icTagTypeSignature tagSig)
{
  switch(tagSig) {
    case icSigSignatureType:
      return new CIccTagSignature;

    case icSigTextType:
      return new CIccTagText;

    case icSigXYZArrayType:
      return new CIccTagXYZ;

    case icSigUInt8ArrayType:
      return new CIccTagUInt8;

    case icSigUInt16ArrayType:
      return new CIccTagUInt16;

    case icSigUInt32ArrayType:
      return new CIccTagUInt32;

    case icSigUInt64ArrayType:
      return new CIccTagUInt64;

    case icSigS15Fixed16ArrayType:
      return new CIccTagS15Fixed16;

    case icSigU16Fixed16ArrayType:
      return new CIccTagU16Fixed16;

    case icSigCurveType:
      return new CIccTagCurve;

    case icSigMeasurementType:
      return new CIccTagMeasurement;

    case icSigMultiLocalizedUnicodeType:
      return new CIccTagMultiLocalizedUnicode;

    case icSigMultiProcessElementType:
      return new CIccTagMultiProcessElement();

    case icSigParametricCurveType:
      return new CIccTagParametricCurve;

    case icSigLutAtoBType:
      return new CIccTagLutAtoB;

    case icSigLutBtoAType:
      return new CIccTagLutBtoA;

    case icSigLut16Type:
      return new CIccTagLut16;

    case icSigLut8Type:
      return new CIccTagLut8;

    case icSigTextDescriptionType:
      return new CIccTagTextDescription;

    case icSigNamedColor2Type:
      return new CIccTagNamedColor2;

    case icSigChromaticityType:
      return new CIccTagChromaticity;

    case icSigDataType:
      return new CIccTagData;

    case icSigDateTimeType:
      return new CIccTagDateTime;

#ifndef ICC_UNSUPPORTED_TAG_DICT
    case icSigDictType:
      return new CIccTagDict;
#endif

    case icSigColorantOrderType:
      return new CIccTagColorantOrder;

    case icSigColorantTableType:
      return new CIccTagColorantTable;

    case icSigViewingConditionsType:
      return new CIccTagViewingConditions;

    case icSigProfileSequenceDescType:
      return new CIccTagProfileSeqDesc;

    case icSigResponseCurveSet16Type:
      return new CIccTagResponseCurveSet16;

    case icSigProfileSequceIdType:
      return new CIccTagProfileSequenceId;

    case icSigScreeningType:
    case icSigUcrBgType:
    case icSigCrdInfoType:

    default:
      return new CIccTagUnknown;
  }
}

const icChar* CIccSpecTagFactory::GetTagSigName(icTagSignature tagSig)
{
  switch (tagSig) {
  case icSigAToB0Tag:
    return "AToB0Tag";

  case icSigAToB1Tag:
    return "AToB1Tag";

  case icSigAToB2Tag:
    return "AToB2Tag";

  case icSigBlueColorantTag:
    return "blueColorantTag";

  case icSigBlueTRCTag:
    return "blueTRCTag";

  case icSigBToA0Tag:
    return "BToA0Tag";

  case icSigBToA1Tag:
    return "BToA1Tag";

  case icSigBToA2Tag:
    return "BToA2Tag";

  case icSigBToD0Tag:
    return "BToD0Tag";

  case icSigBToD1Tag:
    return "BToD1Tag";

  case icSigBToD2Tag:
    return "BToD2Tag";

  case icSigBToD3Tag:
    return "BToD3Tag";

  case icSigCalibrationDateTimeTag:
    return "calibrationDateTimeTag";

  case icSigCharTargetTag:
    return "charTargetTag";

  case icSigChromaticityTag:
    return "chromaticityTag";

  case icSigCopyrightTag:
    return "copyrightTag";

  case icSigCrdInfoTag:
    return "crdInfoTag";

  case icSigDataTag:
    return "dataTag";

  case icSigDateTimeTag:
    return "dateTimeTag";

  case icSigDeviceMfgDescTag:
    return "deviceMfgDescTag";

  case icSigDeviceModelDescTag:
    return "deviceModelDescTag";

  case icSigMetaDataTag:
    return "metaDataTag";

  case icSigDToB0Tag:
    return "DToB0Tag";

  case icSigDToB1Tag:
    return "DToB1Tag";

  case icSigDToB2Tag:
    return "DToB2Tag";

  case icSigDToB3Tag:
    return "DToB3Tag";

  case icSigGamutTag:
    return "gamutTag";

  case icSigGrayTRCTag:
    return "grayTRCTag";

  case icSigGreenColorantTag:
    return "greenColorantTag";

  case icSigGreenTRCTag:
    return "greenTRCTag";

  case icSigLuminanceTag:
    return "luminanceTag";

  case icSigMeasurementTag:
    return "measurementTag";

  case icSigMediaBlackPointTag:
    return "mediaBlackPointTag";

  case icSigMediaWhitePointTag:
    return "mediaWhitePointTag";

  case icSigNamedColor2Tag:
    return "namedColor2Tag";

  case icSigPreview0Tag:
    return "preview0Tag";

  case icSigPreview1Tag:
    return "preview1Tag";

  case icSigPreview2Tag:
    return "preview2Tag";

  case icSigPrintConditionTag:
    return "printConditionTag";

  case icSigProfileDescriptionTag:
    return "profileDescriptionTag";

  case icSigProfileSequenceDescTag:
    return "profileSequenceDescTag";

  case icSigProfileSequceIdTag:
    return "profileSequenceIdentifierTag";

  case icSigPs2CRD0Tag:
    return "ps2CRD0Tag";

  case icSigPs2CRD1Tag:
    return "ps2CRD1Tag";

  case icSigPs2CRD2Tag:
    return "ps2CRD2Tag";

  case icSigPs2CRD3Tag:
    return "ps2CRD3Tag";

  case icSigPs2CSATag:
    return "ps2CSATag";

  case icSigPs2RenderingIntentTag:
    return "ps2RenderingIntentTag";

  case icSigRedColorantTag:
    return "redColorantTag";

  case icSigRedTRCTag:
    return "redTRCTag";

  case icSigScreeningDescTag:
    return "screeningDescTag";

  case icSigScreeningTag:
    return "screeningTag";

  case icSigTechnologyTag:
    return "technologyTag";

  case icSigUcrBgTag:
    return "ucrBgTag";

  case icSigViewingCondDescTag:
    return "viewingCondDescTag";

  case icSigViewingConditionsTag:
    return "viewingConditionsTag";

  case icSigColorantOrderTag:
    return "colorantOrderTag";

  case icSigColorantTableTag:
    return "colorantTableTag";

  case icSigChromaticAdaptationTag:
    return "chromaticAdaptationTag";

  case icSigColorantTableOutTag:
    return "colorantTableOutTag";

  case icSigOutputResponseTag:
    return "outputResponseTag";

  case icSigPerceptualRenderingIntentGamutTag:
    return "perceptualRenderingIntentGamutTag";

  case icSigSaturationRenderingIntentGamutTag:
    return "saturationRenderingIntentGamutTag";

  default:
    return NULL;
  }
  return NULL;
}

const icChar* CIccSpecTagFactory::GetTagTypeSigName(icTagTypeSignature tagSig)
{
  switch (tagSig) {
  case icSigChromaticityType:
    return "chromaticityType";

  case icSigColorantOrderType:
    return "colorantOrderType";

  case icSigColorantTableType:
    return "colorantTableType";

  case icSigCrdInfoType:
    return "crdInfoType";

  case icSigCurveType:
    return "curveType";

  case icSigDataType:
    return "dataType";

  case icSigDateTimeType:
    return "dateTimeType";

  case icSigDeviceSettingsType:
    return "deviceSettingsType";

  case icSigDictType:
    return "dictType";

  case icSigLut16Type:
    return "lut16Type";

  case icSigLut8Type:
    return "lut8Type";

  case icSigLutAtoBType:
    return "lutAtoBType";

  case icSigLutBtoAType:
    return "lutBtoAType";

  case icSigMeasurementType:
    return "measurementType";

  case icSigMultiLocalizedUnicodeType:
    return "multiLocalizedUnicodeType";

  case icSigMultiProcessElementType:
    return "multiProcessElementType";

  case icSigNamedColor2Type:
    return "namedColor2Type";

  case icSigParametricCurveType:
    return "parametricCurveType";

  case icSigResponseCurveSet16Type:
    return "responseCurveSet16Type";

  case icSigProfileSequenceDescType:
    return "profileSequenceDescType";

  case icSigS15Fixed16ArrayType:
    return "s15Fixed16 ArrayType";

  case icSigScreeningType:
    return "screeningType";

  case icSigSignatureType:
    return "signatureType";

  case icSigTextType:
    return "textType";

  case icSigTextDescriptionType:
    return "textDescriptionType";

  case icSigU16Fixed16ArrayType:
    return "u16Fixed16 Type";

  case icSigUcrBgType:
    return "ucrBgType";

  case icSigUInt16ArrayType:
    return "uInt16 Type";

  case icSigUInt32ArrayType:
    return "uInt32 Type";

  case icSigUInt64ArrayType:
    return "uInt64 Type";

  case icSigUInt8ArrayType:
    return "uInt8 Type";

  case icSigViewingConditionsType:
    return "viewingConditionsType";

  case icSigXYZArrayType:
    return "XYZ Type";

  case icSigProfileSequceIdType:
    return "profileSequenceIdentifierType";

  default:
    return NULL;
  }

  return NULL;
}

std::auto_ptr<CIccTagCreator> CIccTagCreator::theTagCreator;

CIccTagCreator::~CIccTagCreator()
{
  IIccTagFactory *pFactory = DoPopFactory(true);

  while (pFactory) {
    delete pFactory;
    pFactory = DoPopFactory(true);
  }
}

CIccTagCreator* CIccTagCreator::GetInstance()
{
  if (!theTagCreator.get()) {
    theTagCreator = CIccTagCreatorPtr(new CIccTagCreator);

    theTagCreator->DoPushFactory(new CIccSpecTagFactory);
  }

  return theTagCreator.get();
}

CIccTag* CIccTagCreator::DoCreateTag(icTagTypeSignature tagTypeSig)
{
  CIccTagFactoryList::iterator i;
  CIccTag *rv = NULL;

  for (i=factoryStack.begin(); i!=factoryStack.end(); i++) {
    rv = (*i)->CreateTag(tagTypeSig);
    if (rv)
      break;
  }
  return rv;
}

const icChar* CIccTagCreator::DoGetTagSigName(icTagSignature tagSig)
{
  CIccTagFactoryList::iterator i;
  const icChar* rv;

  for (i=factoryStack.begin(); i!=factoryStack.end(); i++) {
    rv = (*i)->GetTagSigName(tagSig);
    if (rv)
      return rv;
  }

  return NULL;
}

const icChar* CIccTagCreator::DoGetTagTypeSigName(icTagTypeSignature tagTypeSig)
{
  CIccTagFactoryList::iterator i;
  const icChar* rv;

  for (i=factoryStack.begin(); i!=factoryStack.end(); i++) {
    rv = (*i)->GetTagTypeSigName(tagTypeSig);
    if (rv)
      return rv;
  }

  return NULL;
}

void CIccTagCreator::DoPushFactory(IIccTagFactory *pFactory)
{
  factoryStack.push_front(pFactory);
}

IIccTagFactory* CIccTagCreator::DoPopFactory(bool bAll /*=false*/)
{
//  int nNum = (bAll ? 0 : 1);

  if (factoryStack.size()>0) {
    CIccTagFactoryList::iterator i=factoryStack.begin();
    IIccTagFactory* rv = (*i);
    factoryStack.pop_front();
    return rv;
  }
  return NULL;
}

#ifdef USESAMPLEICCNAMESPACE
} //namespace sampleICC
#endif
