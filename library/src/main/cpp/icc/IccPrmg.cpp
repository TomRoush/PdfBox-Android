/** @file
File:       IccPRMG.cpp

Contains:   Implementation of CIccPRMG class

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

#include "IccPrmg.h"
#include "IccUtil.h"

#ifdef USESAMPLEICCNAMESPACE
namespace sampleICC {
#endif

/**********************************************************************
 * The following table is from the PRMG specification.
 *
 * The first dimension corresponds to hue values going from 0 to 360
 * degrees in ten degree intervals (with 360 duplicating 0 for
 * interpolation purposes.  
 * 
 * The second dimension corresponds to increasing lightness values. The
 * first entry is for 3.5 L*, succeeding entries are for L* values 
 * increasing by 5 L* from 5 to 100.  L* values below 3.5 or above 100
 * are considered to be out of gamut.
 */
static icFloatNumber icPRMG_Chroma[37][21] = {
  {0, 11, 26, 39, 52, 64, 74, 83, 91, 92, 91, 87, 82, 75, 67, 57, 47, 37, 25, 13, 0},
  {0, 10, 24, 38, 50, 62, 73, 82, 90, 92, 91, 87, 82, 75, 67, 58, 48, 37, 26, 13, 0},
  {0, 10, 23, 37, 50, 62, 73, 84, 93, 94, 94, 90, 85, 78, 70, 60, 50, 39, 27, 14, 0},
  {0, 9, 22, 35, 48, 61, 74, 86, 98, 100, 101, 96, 90, 83, 75, 65, 54, 42, 30, 15, 0},
  {0, 8, 21, 34, 47, 60, 73, 83, 93, 97, 101, 99, 97, 90, 83, 73, 61, 47, 34, 17, 0},
  {0, 8, 20, 32, 43, 55, 66, 77, 88, 95, 99, 101, 100, 98, 92, 85, 72, 56, 40, 20, 0},
  {0, 7, 17, 27, 37, 47, 57, 67, 76, 84, 91, 96, 100, 102, 103, 98, 90, 72, 51, 26, 0},
  {0, 6, 16, 25, 34, 43, 52, 60, 68, 76, 83, 90, 96, 100, 104, 107, 109, 100, 74, 37, 0},
  {0, 6, 15, 23, 32, 40, 48, 57, 64, 71, 78, 85, 91, 97, 103, 107, 110, 113, 110, 70, 0},
  {0, 6, 14, 22, 30, 39, 47, 55, 62, 68, 75, 82, 88, 95, 101, 106, 112, 117, 120, 123, 0},
  {0, 6, 14, 22, 30, 38, 46, 54, 61, 68, 74, 81, 88, 94, 100, 106, 109, 112, 112, 92, 0},
  {0, 6, 14, 22, 31, 39, 47, 55, 63, 69, 76, 83, 89, 96, 100, 103, 106, 107, 102, 75, 0},
  {0, 6, 15, 24, 32, 41, 49, 58, 66, 73, 80, 87, 93, 98, 101, 102, 99, 91, 73, 50, 0},
  {0, 6, 16, 25, 35, 44, 54, 63, 72, 80, 87, 93, 97, 101, 99, 94, 86, 73, 56, 34, 0},
  {0, 7, 18, 28, 38, 48, 57, 67, 77, 86, 95, 98, 101, 97, 93, 85, 75, 61, 44, 26, 0},
  {0, 7, 19, 30, 40, 51, 62, 72, 83, 92, 97, 99, 96, 91, 85, 76, 66, 52, 37, 22, 0},
  {0, 7, 20, 32, 44, 56, 68, 80, 92, 96, 99, 97, 92, 87, 79, 70, 59, 46, 33, 19, 0},
  {0, 8, 20, 32, 43, 53, 64, 75, 85, 91, 96, 93, 89, 82, 75, 65, 55, 42, 30, 17, 0},
  {0, 8, 20, 31, 41, 52, 62, 72, 81, 87, 92, 90, 86, 79, 71, 61, 52, 40, 28, 15, 0},
  {0, 8, 20, 30, 40, 50, 60, 68, 76, 82, 87, 85, 82, 76, 69, 60, 50, 39, 27, 14, 0},
  {0, 8, 20, 30, 38, 47, 56, 63, 70, 76, 82, 81, 77, 72, 66, 58, 49, 38, 27, 14, 0},
  {0, 8, 20, 29, 37, 46, 53, 60, 66, 73, 79, 80, 75, 70, 64, 57, 49, 38, 27, 14, 0},
  {0, 8, 20, 29, 37, 45, 52, 59, 65, 71, 76, 75, 72, 68, 63, 56, 48, 38, 27, 14, 0},
  {0, 9, 20, 29, 38, 46, 53, 59, 65, 70, 75, 73, 71, 66, 61, 54, 46, 36, 26, 13, 0},
  {0, 10, 22, 31, 40, 48, 55, 61, 67, 71, 74, 70, 66, 61, 56, 49, 41, 32, 23, 12, 0},
  {0, 11, 24, 34, 43, 51, 59, 65, 70, 73, 71, 68, 63, 58, 52, 45, 38, 30, 21, 11, 0},
  {0, 14, 27, 38, 48, 57, 64, 69, 73, 73, 70, 66, 61, 56, 50, 43, 35, 28, 20, 10, 0},
  {0, 17, 32, 45, 55, 65, 70, 75, 75, 73, 70, 66, 61, 55, 49, 42, 34, 27, 19, 10, 0},
  {0, 21, 42, 55, 68, 75, 81, 80, 79, 76, 72, 67, 61, 55, 49, 41, 34, 26, 18, 9, 0},
  {0, 26, 52, 68, 83, 86, 89, 87, 84, 80, 75, 69, 63, 57, 50, 42, 35, 27, 18, 10, 0},
  {0, 25, 69, 82, 95, 94, 93, 91, 88, 85, 79, 73, 66, 59, 52, 44, 36, 28, 19, 10, 0},
  {0, 21, 51, 74, 91, 97, 100, 98, 95, 90, 84, 77, 70, 63, 55, 47, 39, 30, 20, 10, 0},
  {0, 18, 41, 62, 79, 91, 102, 101, 98, 95, 89, 83, 76, 68, 60, 51, 42, 32, 22, 11, 0},
  {0, 16, 35, 53, 71, 82, 91, 100, 104, 102, 98, 91, 84, 76, 67, 57, 47, 36, 24, 12, 0},
  {0, 14, 31, 46, 61, 73, 83, 92, 101, 103, 99, 95, 89, 80, 71, 61, 50, 38, 26, 13, 0},
  {0, 12, 28, 42, 55, 68, 77, 86, 94, 96, 93, 90, 85, 77, 68, 58, 48, 37, 25, 13, 0},
  {0, 11, 26, 39, 52, 64, 74, 83, 91, 92, 91, 87, 82, 75, 67, 57, 47, 37, 25, 13, 0},
};

CIccPRMG::CIccPRMG()
{
  m_nTotal = m_nDE1 = m_nDE2 = m_nDE3 = m_nDE5 = m_nDE10 = 0;

  m_bPrmgImplied = false;
}

icFloatNumber CIccPRMG::GetChroma(icFloatNumber L, icFloatNumber h)
{
  if (L<3.5 || L>100.0)
    return -1;

  int nHIndex, nLIndex;
  icFloatNumber dHFraction, dLFraction;

  while (h<0.0)
    h+=360.0;

  while (h>=360.0)
    h-=360.0;

  nHIndex = (int)(h/10.0);
  dHFraction = (icFloatNumber)((h - nHIndex*10.0)/10.0);

  if (L<5) {
    nLIndex = 0;
    dLFraction = (icFloatNumber)((L-3.5) / (5.0-3.5));
  }
  else if (L==100.0) {
    nLIndex = 19;
    dLFraction = 1.0;
  }
  else {
    nLIndex = (int)((L-5.0)/5.0) + 1;
    dLFraction = (icFloatNumber)((L-nLIndex*5.0)/5.0);
  }

  icFloatNumber dInvLFraction = (icFloatNumber)(1.0 - dLFraction);

  icFloatNumber ch1 = icPRMG_Chroma[nHIndex][nLIndex]*dInvLFraction + icPRMG_Chroma[nHIndex][nLIndex+1]*dLFraction;
  icFloatNumber ch2 = icPRMG_Chroma[nHIndex+1][nLIndex]*dInvLFraction + icPRMG_Chroma[nHIndex+1][nLIndex+1]*dLFraction;

  return (icFloatNumber)(ch1*(1.0-dHFraction) + ch2 * 1.0*dHFraction);
}

bool CIccPRMG::InGamut(icFloatNumber L, icFloatNumber c, icFloatNumber h)
{
  icFloatNumber dChroma = GetChroma(L, h);

  if (dChroma<0.0 || c>dChroma)
    return false;
  
  return true;
}

bool CIccPRMG::InGamut(icFloatNumber *Lab)
{
  icFloatNumber Lch[3];

  icLab2Lch(Lch, Lab);
  return InGamut(Lch[0], Lch[1], Lch[2]);
}

icStatusCMM CIccPRMG::EvaluateProfile(CIccProfile *pProfile, icRenderingIntent nIntent/* =icUnknownIntent */,
                                      icXformInterp nInterp/* =icInterpLinear */, bool buseMpeTags/* =true */)
{
  if (!pProfile)
  {
    return icCmmStatCantOpenProfile;
  }

  if (pProfile->m_Header.deviceClass!=icSigInputClass &&
    pProfile->m_Header.deviceClass!=icSigDisplayClass &&
    pProfile->m_Header.deviceClass!=icSigOutputClass &&
    pProfile->m_Header.deviceClass!=icSigColorSpaceClass)
  {
    return icCmmStatInvalidProfile;
  }

  m_bPrmgImplied = false;
  if (nIntent==icPerceptual || nIntent==icSaturation) { 
    icTagSignature rigSig = (icTagSignature)(icSigPerceptualRenderingIntentGamutTag + ((icUInt32Number)nIntent)%4);
    CIccTag *pSigTag = pProfile->FindTag(rigSig);

    if (pSigTag && pSigTag->GetType()==icSigSignatureType) {
      CIccTagSignature *pSig = (CIccTagSignature*)pSigTag;

      if (pSig->GetValue()==icSigPerceptualReferenceMediumGamut)
        m_bPrmgImplied = true;
    }
  }

  CIccCmm Lab2Dev2Lab(icSigLabData, icSigLabData, false);

  icStatusCMM result = Lab2Dev2Lab.AddXform(*pProfile, nIntent, nInterp, icXformLutColor, buseMpeTags);
  if (result != icCmmStatOk) {
    return result;
  }

  result = Lab2Dev2Lab.AddXform(*pProfile, nIntent, nInterp, icXformLutColor, buseMpeTags);
  if (result != icCmmStatOk) {
    return result;
  }

  result = Lab2Dev2Lab.Begin();
  if (result != icCmmStatOk) {
    return result;
  }
  icFloatNumber pcs[3], Lab1[3], Lab2[3], dE;

  m_nTotal = m_nDE1 = m_nDE2 = m_nDE3 = m_nDE5 = m_nDE10 = 0;

  for (pcs[0]=0.0; pcs[0]<=1.0; pcs[0] += (icFloatNumber)0.01) {
    for (pcs[1]=0.0; pcs[1]<=1.0; pcs[1] += (icFloatNumber)0.01) {
      for (pcs[2]=0.0; pcs[2]<=1.0; pcs[2] += (icFloatNumber)0.01) {
        memcpy(Lab1, pcs, 3*sizeof(icFloatNumber));
        icLabFromPcs(Lab1);
        if (InGamut(Lab1)) {
          Lab2Dev2Lab.Apply(Lab2, pcs);
          icLabFromPcs(Lab2);

          dE = icDeltaE(Lab1, Lab2);
          m_nTotal++;

          if (dE<=1.0) {
            m_nDE1++;
            m_nDE2++;
            m_nDE3++;
            m_nDE5++;
            m_nDE10++;
          }
          else if (dE<=2.0) {
            m_nDE2++;
            m_nDE3++;
            m_nDE5++;
            m_nDE10++;
          }
          else if (dE<=3.0) {
            m_nDE3++;
            m_nDE5++;
            m_nDE10++;
          }
          else if (dE<=5.0) {
            m_nDE5++;
            m_nDE10++;
          }
          else if (dE<=10.0) {
            m_nDE10++;
          }
        }
      }
    }
  }

  return icCmmStatOk;
}

icStatusCMM CIccPRMG::EvaluateProfile(const icChar *szProfilePath, icRenderingIntent nIntent/* =icUnknownIntent */, 
                                             icXformInterp nInterp/* =icInterpLinear */, bool buseMpeTags/* =true */)
{
  CIccProfile *pProfile = ReadIccProfile(szProfilePath);

  if (!pProfile) 
    return icCmmStatCantOpenProfile;

  icStatusCMM result = EvaluateProfile(pProfile, nIntent, nInterp, buseMpeTags);

  delete pProfile;

  return result;
}


#ifdef USESAMPLEICCNAMESPACE
}
#endif
