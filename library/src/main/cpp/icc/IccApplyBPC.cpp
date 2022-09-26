/** @file
File:       IccApplyBPC.cpp

Contains:   Implementation of Black Point Compensation calculations.

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
// -Initial implementation by Rohit Patil 12-10-2008
//
//////////////////////////////////////////////////////////////////////

#include "IccApplyBPC.h"
#include <math.h>

#define IsSpacePCS(x) ((x)==icSigXYZData || (x)==icSigLabData)

/**
**************************************************************************
* Name: CIccApplyBPCHint::GetNewAdjustPCSXform
* 
* Purpose:
*  Returns a new CIccApplyBPC object. Returned object should be deleted 
*  by the caller.
* 
**************************************************************************
*/
IIccAdjustPCSXform* CIccApplyBPCHint::GetNewAdjustPCSXform() const
{
	return new CIccApplyBPC();
}

//////////////////////////////////////////////////////////////////////
// CIccApplyBPC utility functions
//////////////////////////////////////////////////////////////////////

// converts lab to pcs
void CIccApplyBPC::lab2pcs(icFloatNumber* pixel, const CIccProfile* pProfile) const
{
	switch (pProfile->m_Header.pcs)
	{
	case icSigLabData:
		icLabToPcs(pixel);
		break;

	case icSigXYZData:
		icLabtoXYZ(pixel);
		icXyzToPcs(pixel);
		break;

  default:
    break;
	}
}

// converts pcs to lab
void CIccApplyBPC::pcs2lab(icFloatNumber* pixel, const CIccProfile* pProfile) const
{
	switch (pProfile->m_Header.pcs)
	{
	case icSigLabData:
		icLabFromPcs(pixel);
		break;

	case icSigXYZData:
		icXyzFromPcs(pixel);
		icXYZtoLab(pixel);
		break;

  default:
    break;
	}
}

// calculates sum of product of x^j and y^k polynomials
icFloatNumber CIccApplyBPC::calcsum(icFloatNumber* x, icFloatNumber* y, int n, int j, int k) const
{
	icFloatNumber dSum = 0.0;

	int i;
	if (j && k) {
		for (i=0; i<n; i++) {
			dSum += pow(x[i], j)*pow(y[i], k);
		}
	}
	else if (j) {
		for (i=0; i<n; i++) {
			dSum += pow(x[i], j);
		}
	}
	else if (k) {
		for (i=0; i<n; i++) {
			dSum += pow(y[i], k);
		}
	}
	else {
		dSum = icFloatNumber(n);
	}

	return dSum;
}

// fits a quadratic curve through x,y points and returns the vertex of the parabola
icFloatNumber CIccApplyBPC::calcQuadraticVertex(icFloatNumber* x, icFloatNumber* y, int n) const
{
	icFloatNumber vert = 0.0;
	
	if (n>2) { // need at least three points to solve three linear equations
		icFloatNumber s00, s10, s20, s30, s40, s01, s11, s21, denom;
		s00 = calcsum(x, y, n, 0, 0);
		s10 = calcsum(x, y, n, 1, 0);
		s20 = calcsum(x, y, n, 2, 0);
		s30 = calcsum(x, y, n, 3, 0);
		s40 = calcsum(x, y, n, 4, 0);
		s01 = calcsum(x, y, n, 0, 1);
		s11 = calcsum(x, y, n, 1, 1);
		s21 = calcsum(x, y, n, 2, 1);
		denom = (icFloatNumber)(s00*s20*s40 - s10*s10*s40 - s00*s30*s30 + 2.0*s10*s20*s30 - s20*s20*s20);
		if (fabs(denom)>0.0) {
			// t and u are the coefficients of the quadratic equation y = tx^2 + ux + c
			// the three equations with 3 unknowns can be written as
			// [s40 s30 s20][t]   [s21]
			// [s30 s20 s10][u] = [s11]
			// [s20 s10 s00][c]   [s01]
			icFloatNumber t = (s01*s10*s30 - s11*s00*s30 - s01*s20*s20 + s11*s10*s20 + s21*s00*s20 - s21*s10*s10)/denom;

			icFloatNumber u = (s11*s00*s40 - s01*s10*s40 + s01*s20*s30 - s21*s00*s30 - s11*s20*s20 + s21*s10*s20)/denom;

			icFloatNumber c = (s01*s20*s40 - s11*s10*s40 - s01*s30*s30 + s11*s20*s30 + s21*s10*s30 - s21*s20*s20)/denom;

			// vertex is (-u + sqrt(u^2 - 4tc))/2t
			vert = (icFloatNumber)((-1.0 * u + sqrt(u*u - 4*t*c)) / (2.0 * t));
		}
	}

	return vert;
}

/**
**************************************************************************
* Name: CIccApplyBPC::CalculateFactors
* 
* Purpose:
*  This function does the suitable calculations to setup black point
*  compensation.
* 
* Args: 
*  pXform = pointer to the Xform object that calls this function
* 
* Return: 
*  true = all calculations done
*  false = an error occurred
**************************************************************************
*/
bool CIccApplyBPC::CalcFactors(const CIccProfile* pProfile, const CIccXform* pXform, icFloatNumber* Scale, icFloatNumber* Offset) const
{
	if (!pProfile || !pXform)
		return false;

	if (pXform->GetIntent()==icAbsoluteColorimetric) { // black point compensation not supported
		return false;
	}

	switch (pProfile->m_Header.deviceClass)
	{ // These profile classes not supported
		case icSigLinkClass:
		case icSigAbstractClass:
		//case icSigColorSpaceClass:
		case icSigNamedColorClass:
			return false;
    default:
      break;
	}

	icFloatNumber XYZbp[3]; // storage for black point XYZ

	// calculate the black point
	if (!calcBlackPoint(pProfile, pXform, XYZbp)) {
		return false;
	}

	// calculate the scale and offset
	if (pXform->IsInput()) { // use PRM black as destination black
		Scale[0] = (icFloatNumber)((1.0 - icPerceptualRefBlackY)/(1.0 - XYZbp[1]));
	}
	else { // use PRM black as source black
		Scale[0] = (icFloatNumber)((1.0 - XYZbp[1])/(1.0 - icPerceptualRefBlackY));
	}

	Scale[1] = Scale[0];
	Scale[2] = Scale[0];

	Offset[0] = (icFloatNumber)((1.0 - Scale[0]) * icPerceptualRefWhiteX);
	Offset[1] = (icFloatNumber)((1.0 - Scale[1]) * icPerceptualRefWhiteY);
	Offset[2] = (icFloatNumber)((1.0 - Scale[2]) * icPerceptualRefWhiteZ);

	icXyzToPcs(Offset);

	return true;
}

/**
**************************************************************************
* Name: CIccApplyBPC::calcBlackPoint
* 
* Purpose:
*  Calculates the black point of a profile
* 
**************************************************************************
*/
bool CIccApplyBPC::calcBlackPoint(const CIccProfile* pProfile, const CIccXform* pXform, icFloatNumber* XYZb) const
{
	if (pXform->IsInput()) { // profile used as input/source profile
		return calcSrcBlackPoint(pProfile, pXform, XYZb);
	}
	else { // profile used as output profile
		return calcDstBlackPoint(pProfile, pXform, XYZb);
	}

	return true;
}

/**
**************************************************************************
* Name: CIccApplyBPC::calcSrcBlackPoint
* 
* Purpose:
*  Calculates the black point of a source profile
* 
**************************************************************************
*/
bool CIccApplyBPC::calcSrcBlackPoint(const CIccProfile* pProfile, const CIccXform* pXform, icFloatNumber* XYZb) const
{
	icFloatNumber Pixel[16];
	if ((pProfile->m_Header.colorSpace == icSigCmykData) && (pProfile->m_Header.deviceClass == icSigOutputClass)) {

		// calculate intermediate CMYK
		XYZb[0] = XYZb[1] = XYZb[2] = 0.0;

		// convert the Lab of 0,0,0 to relevant PCS
		lab2pcs(XYZb, pProfile);

		//convert the PCS value to CMYK
		if (!pixelXfm(Pixel, XYZb, pProfile->m_Header.pcs, icPerceptual, pProfile)) {
			return false;
		}
	}
	else {
		switch (pProfile->m_Header.colorSpace) {
				case icSigRgbData:
					Pixel[0] = 0.0;
					Pixel[1] = 0.0;
					Pixel[2] = 0.0;
					break;

				case icSigGrayData:
					Pixel[0] = 0.0;
					break;

				case icSigCmykData:
				case icSigCmyData:
				case icSig2colorData:
				case icSig3colorData:
				case icSig4colorData:
				case icSig5colorData:
				case icSig6colorData:
				case icSig7colorData:
				case icSig8colorData:
				case icSig9colorData:
				case icSig10colorData:
				case icSig11colorData:
				case icSig12colorData:
				case icSig13colorData:
				case icSig14colorData:
				case icSig15colorData:
					{
						icUInt32Number nSamples = icGetSpaceSamples(pProfile->m_Header.colorSpace);
						for (icUInt32Number i=0; i<nSamples; i++) {
							Pixel[i] = 1.0;
						}
					}
					break;

				default:
					return false;
		}
	}

	// convert the device value to PCS
	if (!pixelXfm(XYZb, Pixel, pProfile->m_Header.colorSpace, pXform->GetIntent(), pProfile)) {
		return false;
	}

	// convert PCS to Lab
	pcs2lab(XYZb, pProfile);

	// set a* b* to zero for cmyk profiles
	if (pProfile->m_Header.colorSpace == icSigCmykData) {
		XYZb[1] = XYZb[2] = 0.0;
	}

	// clip L* to 50
	if (XYZb[0]>50.0) {
		XYZb[0] = 50.0;
	}

	// convert Lab to XYZ
	icLabtoXYZ(XYZb);
	return true;
}

/**
**************************************************************************
* Name: CIccApplyBPC::calcDstBlackPoint
* 
* Purpose:
*  Calculates the black point of a destination profile
* 
**************************************************************************
*/
bool CIccApplyBPC::calcDstBlackPoint(const CIccProfile* pProfile, const CIccXform* pXform, icFloatNumber* XYZb) const
{
	icRenderingIntent nIntent = pXform->GetIntent();
	icFloatNumber Pixel[3];
	icFloatNumber pcsPixel[3];

	// check if the profile is lut based gray, rgb or cmyk
	if (pProfile->IsTagPresent(icSigBToA0Tag) && 
			(pProfile->m_Header.colorSpace==icSigGrayData || pProfile->m_Header.colorSpace==icSigRgbData || pProfile->m_Header.colorSpace==icSigCmykData))
	{ // do the complicated and lengthy black point estimation

		// get the black transform
		CIccCmm* pCmm = getBlackXfm(nIntent, pProfile);
		if (!pCmm) {
			return false;
		}

		// set the initial Lab
		icFloatNumber iniLab[3] = {0.0, 0.0, 0.0};

		// calculate minL
		pcsPixel[0] = 0.0;
		pcsPixel[1] = iniLab[1];
		pcsPixel[2] = iniLab[2];
		lab2pcs(pcsPixel, pProfile);
		if (pCmm->Apply(Pixel, pcsPixel)!=icCmmStatOk) {
			delete pCmm;
			return false;
		}
		pcs2lab(Pixel, pProfile);
		icFloatNumber MinL = Pixel[0];

		// calculate MaxL
		pcsPixel[0] = 100.0;
		pcsPixel[1] = iniLab[1];
		pcsPixel[2] = iniLab[2];
		lab2pcs(pcsPixel, pProfile);
		if (pCmm->Apply(Pixel, pcsPixel)!=icCmmStatOk) {
			delete pCmm;
			return false;
		}
		pcs2lab(Pixel, pProfile);
		icFloatNumber MaxL = Pixel[0];

		// check if quadratic estimation needs to be done
		bool bStraightMidRange = false;

		// if the intent is relative
		if (nIntent==icRelativeColorimetric)
		{ 
			// calculate initial Lab as source black point
			if (!calcSrcBlackPoint(pProfile, pXform, iniLab)) {
				delete pCmm;
				return false;
			}

			// convert the XYZ to lab
			icXYZtoLab(iniLab);

			// check mid range L* values
			icFloatNumber lcnt=0.0, roundtripL;
			bStraightMidRange = true;
			while (lcnt<100.1)
			{
				pcsPixel[0] = icFloatNumber(lcnt);
				pcsPixel[1] = iniLab[1];
				pcsPixel[2] = iniLab[2];
				lab2pcs(pcsPixel, pProfile);
				if (pCmm->Apply(Pixel, pcsPixel)!=icCmmStatOk) {
					delete pCmm;
					return false;
				}
				pcs2lab(Pixel, pProfile);
				roundtripL = Pixel[0];

				if (roundtripL>(MinL + 0.2 * (MaxL - MinL))) {
					if (fabs(roundtripL - lcnt)>4.0) {
						bStraightMidRange = false;
						break;
					}
				}

				lcnt += 1.0;
			}
		}

		// quadratic estimation is not needed
		if (bStraightMidRange) { // initial Lab is the destination black point
			XYZb[0] = iniLab[0];
			XYZb[1] = iniLab[1];
			XYZb[2] = iniLab[2];
			icLabtoXYZ(XYZb);
			delete pCmm;
			return true;
		}

		// find the black point using the least squares error quadratic curve fitting

		// calculate y values
		icFloatNumber x[101], y[101];
		icFloatNumber lo=0.03f, hi=0.25f;
		int i, n;
		if (nIntent==icRelativeColorimetric) {
			lo = 0.1f;
			hi = 0.5f;
		}

		for (i=0; i<101; i++) {
			x[i] = icFloatNumber(i);
			pcsPixel[0] = x[i];
			pcsPixel[1] = iniLab[1];
			pcsPixel[2] = iniLab[2];
			lab2pcs(pcsPixel, pProfile);
			if (pCmm->Apply(Pixel, pcsPixel)!=icCmmStatOk) {
				delete pCmm;
				return false;
			}
			pcs2lab(Pixel, pProfile);
			y[i] = (Pixel[0] - MinL)/(MaxL - MinL);
		}

		// check for y values in the range and rearrange
		n = 0;
		for (i=0; i<101; i++) {
			if (y[i]>=lo && y[i]<hi) {
				x[n] = x[i];
				y[n] = y[i];
				n++;
			}
		}

		if (!n) {
			delete pCmm;
			return false;
		}

		// fit and get the vertex of quadratic curve
		XYZb[0] = calcQuadraticVertex(x, y, n);
		if (XYZb[0]<0.0) { // clip to zero L* if the vertex is negative
			XYZb[0] = 0.0;
		}
		XYZb[1] = iniLab[1];
		XYZb[2] = iniLab[2];
		icLabtoXYZ(XYZb);

		delete pCmm;
	}
	else { // use the procedure for source black point
		return calcSrcBlackPoint(pProfile, pXform, XYZb);
	}

	return true;
}

/**
**************************************************************************
* Name: CIccApplyBPC::pixelXfm
* 
* Purpose:
*  Applies the specified transform to the source pixel
* 
**************************************************************************
*/
bool CIccApplyBPC::pixelXfm(icFloatNumber *DstPixel, icFloatNumber *SrcPixel, icColorSpaceSignature SrcSpace, 
														icRenderingIntent nIntent, const CIccProfile *pProfile) const
{
	// create the cmm object
	CIccCmm cmm(SrcSpace, icSigUnknownData, !IsSpacePCS(SrcSpace));

	// first create a copy of the profile because the copy will be owned by the cmm
	CIccProfile* pICC = new CIccProfile(*pProfile);
	if (!pICC) return false;

	// add the xform
	if (cmm.AddXform(pICC, nIntent, icInterpTetrahedral)!=icCmmStatOk) {
		delete pICC;
		return false;
	}

	// get the cmm ready to do transforms
	if (cmm.Begin()!=icCmmStatOk) {
		return false;
	}

	// Apply the pixel
	if (cmm.Apply(DstPixel, SrcPixel)!=icCmmStatOk) {
		return false;
	}

	return true;
}

/**
**************************************************************************
* Name: CIccApplyBPC::blackXfm
* 
* Purpose:
*  PCS -> PCS round trip transform, always uses relative intent on the device -> pcs transform
* 
**************************************************************************
*/
CIccCmm* CIccApplyBPC::getBlackXfm(icRenderingIntent nIntent, const CIccProfile *pProfile) const
{
	// create the cmm object
	CIccCmm* pCmm = new CIccCmm(pProfile->m_Header.pcs, icSigUnknownData, false);
	if (!pCmm) return NULL;

	// first create a copy of the profile because the copy will be owned by the cmm
	CIccProfile* pICC1 = new CIccProfile(*pProfile);
	if (!pICC1) {
		delete pCmm;
		return NULL;
	}

	// add the xform
	if (pCmm->AddXform(pICC1, nIntent, icInterpTetrahedral)!=icCmmStatOk) {
		delete pICC1;
		delete pCmm;
		return NULL;
	}

	// create another copy of the profile because the copy will be owned by the cmm
	CIccProfile* pICC2 = new CIccProfile(*pProfile);
	if (!pICC2) {
		delete pCmm;
		return NULL;
	}

	// add the xform
	if (pCmm->AddXform(pICC2, icRelativeColorimetric, icInterpTetrahedral)!=icCmmStatOk) { // uses the relative intent on the device to Lab side
		delete pICC2;
		delete pCmm;
		return NULL;
	}

	// get the cmm ready to do transforms
	if (pCmm->Begin()!=icCmmStatOk) {
		delete pCmm;
		return NULL;
	}

	return pCmm;
}
