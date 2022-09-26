/** @file
File:       IccApplyBPC.h

Contains:   Header file for implementation of Black Point Compensation calculations.

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
// -Initial implementation by Rohit Patil 12-8-2008
//
//////////////////////////////////////////////////////////////////////

#if !defined(_ICCAPPLYBPC_H)
#define _ICCAPPLYBPC_H

#include "IccCmm.h"

#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif

/**
**************************************************************************
* Type: Class
* 
* Purpose: 
*		Interface and hint for creating a BPC xform object
**************************************************************************
*/
class ICCPROFLIB_API CIccApplyBPCHint : public CIccCreateAdjustPCSXformHint
{
public:
	virtual const char *GetAdjustPCSType() const { return "CIccApplyBPCHint"; }
	virtual IIccAdjustPCSXform* GetNewAdjustPCSXform() const;
};

/**
 **************************************************************************
 * Type: Class
 * 
 * Purpose: This is the hint for applying black point compensation.
 *					Also does the calculations to setup actual application of BPC.
 * 
 **************************************************************************
*/
class ICCPROFLIB_API CIccApplyBPC : public IIccAdjustPCSXform
{
public: 
	// virtual IIccAdjustPCSXform functions
	// does all the calculations for BPC and returns the scale and offset in the arguments passed
	virtual bool CalcFactors(const CIccProfile* pProfile, const CIccXform* pXfm, icFloatNumber* Scale, icFloatNumber* Offset) const;

private:
	// utility functions
	void lab2pcs(icFloatNumber* pixel, const CIccProfile* pProfile) const;
	void pcs2lab(icFloatNumber* pixel, const CIccProfile* pProfile) const;
	icFloatNumber calcsum(icFloatNumber* x, icFloatNumber* y, int n, int j, int k) const;
	icFloatNumber calcQuadraticVertex(icFloatNumber* x, icFloatNumber* y, int n) const;

	// worker functions
	bool calcBlackPoint(const CIccProfile* pProfile, const CIccXform* pXform, icFloatNumber* XYZb) const;
	bool calcSrcBlackPoint(const CIccProfile* pProfile, const CIccXform* pXform, icFloatNumber* XYZb) const;
	bool calcDstBlackPoint(const CIccProfile* pProfile, const CIccXform* pXform, icFloatNumber* XYZb) const;

	bool pixelXfm(icFloatNumber *DstPixel, icFloatNumber *SrcPixel, icColorSpaceSignature SrcSpace, 
								icRenderingIntent nIntent, const CIccProfile *pProfile) const;

	// PCS -> PCS round trip transform, always uses relative intent on the device -> pcs transform
	CIccCmm* getBlackXfm(icRenderingIntent nIntent, const CIccProfile *pProfile) const;
};

#ifdef USESAMPLEICCNAMESPACE
}; //namespace sampleICC
#endif

#endif // _ICCAPPLYBPC_H

