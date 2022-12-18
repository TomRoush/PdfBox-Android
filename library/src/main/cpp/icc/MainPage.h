/** @file
    File:       MainPage.h

  Note: This file was added to provide documentation in doxygen.  Nothing in IccProfLib actually uses it.
*/

/** \mainpage 
 *
 * The IccProfLib is an open source cross platform C++ library for reading, writing, manipulating,
 * and applying ICC profiles.  It is an attempt at a strict interpretation of the ICC profile
 * specification.
 * The structure of the library very closely follows the structure of the specification.
 * A working knowledge of the ICC specification and color management workflows will aid in 
 * understanding the library and it's proper usage. For the latest ICC profile 
 * specification please visit http://www.color.org. Several useful white papers and resources 
 * are also available on the website.  
 *
 * Note: More documentation on SampleICC's Color Management Modules (CMM's) can be found in the white
 * paper titled <i>"Implementation Notes for the IccLib CMM in SampleICC"</i>. 
 * (see http://www.color.org/ICC_white_paper_18_IccLib_Notes.pdf)
 * 
 * Here are some of the things that the IccProfLib supports:
 *  - ICC profile file I/O in CIccProfile class
 *    - Version 4.2 profiles (read & write)
 *    - Version 2.x profiles (read)
 *  - C++ classes for all specified tag types (based on CIccTag). Default behavior for
 *    unrecognized private tag types is implemented using a generic Tag class.
 *  - Two basic Color Management Module (CMM) implementations
 *    - Basic pixel level transforms in CIccCmm class
 *    - Additional named color profile support in CIccNamedColorCmm class
 *  - File I/O can be re-directed
 *  - All operations performed using floating point.  Pixel precision not limited to integers.
 *  - Transforms are done one pixel at a time.
 *  - Flexible number of profile transforms in a series (as long as the colorspaces match)
 *  - Multidimensional lookup table interpolation
 *    - Three dimensional interpolation uses either linear or tetrahedral interpolation
 *      (selectable at time profile is attached to the CMM).
 *    - Greater than three dimensional interpolation uses linear interpolation
 *  - Matrix/TRC support
 *  - Calculation of Profile ID using the MD5 fingerprinting method (see md5.h)
 *  - Dynamic creation and seemless use of private CIccTag derived objects that are implemented
 *    outside of IccProfLib (IE inside a private library or application that links with
 *    IccProfLib).
 *
 * <b>USAGE COMMENTS</b>
 *  -# The IccProfLib implements very basic CMMs.  These may not
 *   provide the optimum speed in all situations. Profile transforms are done one pixel
 *   at a time for each profile in a profile transformation chain.  Various techniques
 *   can possibly be used to improve performance. An initial thought would be to create a
 *   CMM that uses the basic CIccCmm to generate a single link transform (concatenating
 *   the profiles).  Such a transform could employ integer math if needed.
 *  -# The IccProfLib can be used to open, generate, manipulate (by adding, removing, or
 *   modifying tags), and/or save profiles without needing to use the pixel transformations
 *   provided by the CMM classes.
 *  -# Several applications have been written (in SampleICC) that make use of the IccProfLib.
 *   It is advisable to examine these applications for additional guidance in making
 *   the best use of the IccProfLib.
 *  -# Before compiling on non-Windows and non Mac OSX platforms it will be necessary to edit
 *   the configuration parameters in IccProfLibConf.h. 
 *
 *  <b>VERSION HISTORY</b>
 * - December 2015 - 1.6.10 release
 *  - 1.6.11 release
 *  - Fixed bug in validation of GamutTags
 *
 * - December 2015 - 1.6.10 release based on submission by Vitaly Bondar
 *  - 1.6.10 release
 *  - Fixed bug in copy data of copy constructors and copy operations of CIccTagUnkown, 
 *    CIccTagNamedColor2, CIccTagChromaticity, CIccTagFixedNum, CIccTagNum, CIccTagData,
 *    and CIccTagColorantOrder
 *
 * - Sept 2015 - 1.6.9 release
 *  - 1.6.9 release
 *  - Added check to black point compensation to check for negative values going into square
 *    root calculation
 *  - Fixed bug in copying data in constructor and copy operator of CIccTagFixedNum
 *  - Updated copyright dates
 *  - Added use of WXVER environment variable to select wxWidgets library version for build 
 *    of wxProfileDump
 *
 * - April 2014 - 1.6.8 release
 *   - 1.6.8 release
 *   - Modified CIccTagParametricCurve to use icFloatNumber rather than icS15Fixed16Number
 *     for internal storage purposes.  Fixes crashing bug with profile verification.
 *   - Added check for named color profile class when icSigNamedColor2Tag is filed
 *   - Changed #ifdef WIN32 to #if defined(WIN32) || defined(WIN64)
 *   - Added zeros to end of PRMG gamut for easy detection of end of gamut
 *   - Removed 4 byte alignment check for profile length for versions before v4.2
 *   - Fixed bug with copy of data in CIccTagColorantTable objects
 *
 * - August 2012 - 1.6.7 release
 *   - 1.6.7 release
 *   - Made const functions more consistent
 *   - Moved CIccUTF16String class from IccXML
 *   - Replace use of std::wstring in CIccTagDict with CIccUTF16String
 *   - Added ICC_ENUM_CONVENIENCE define that makes convenience enums part of the enum type
 *
 * - August 2011 - 1.6.6 release
 *  - Added iccGetBPCInfo command line tool to retrieve information about BPC connection
 *  - Changed CIccApplyBPC private members to protected members to allow object overridden
 *    and made CIccApplyBPC:calcBlackPoint virtual to support override in iccGetBPCInfo
 *
 * - April  2011 - 1.6.5 release
 *  - Modified .sln and .vcproj files to work with Visual Studio 2008
 *  - Added _v8.sln and _v8.vcproj files to work with Visual Studio 2005
 *  - Fixed bugs in CIccInfo::GetProfileID() and CIccInfo::IsProfileIDCalculated()
 *
 * - January 2011 - 1.6.4 release
 *  - Added CIccNullIO class that can be used by a caller to "write" the profile thus updating
 *    tag directory entries
 *  - Fixed various bugs related to setting text in CIccTagDict tags
 *  - Added CIccTagLut8::GetPrecision() function
 *  - Fixed bug in validation of CIccTextDescription class
 *  - Initialize m_nVendorflags in CIccTagNamedColor2 constructor
 *  - Fixed bug in CIccTagNamedColor2::SetSize() that was copying wrong thing
 *  - Fixed bug in CIccTagMultiLocalizedUnicode::Read() that was using wrong seek value at end
 *  - Defined initial values in CIccTagViewingCondions constructor
 *  - Modified CLUT interpolation in IccTagLut.cpp to perform clipping on input and no clipping
 *    on output (as opposed to the other way around).  Fixes crashing bug found with absolute
 *    intent processing of colors that are whiter than media point.  Also supports floating point range
 *    of output for MPE CLUT elements.
 *  - Renaming of MD5 calculation functions to avoid conflicts with other libraries
 * 
 * - November 2010 - 1.6.3 release
 *  - Modification of type for CIccCLUT::m_nOutput to icUInt16Nubmer to better support MPE
 *    CLUT elements
 *  - Fixed typo in CIccTagUnknown::IsSupported()
 *
 * - August 2010 - 1.6.1 release
 *  - Fix bugs with reading and displaying metaDataTags using the dictTagType
 *
 * - August 2010 - 1.6.1 release
 *  - Modifications to CIccTagLut16 and CIccTagLut8 to correctly track curve mapping when table
 *    is used as an output table and PCS is XYZ.  In this case M and B curves are swapped since
 *    the legacy Lut16 and Lut8 tags do not have M curves.
 *  - Removed check in CIccXform::Create() for BtoD0/DtoB0 tags if BtoDx/DtoBx tag for rendering
 *    intent not found (as this never made it into the approved specification).
 *  - Further changes from Joseph Goldstone that eliminate various compiler warnings
 *
 * - July 2010 - 1.6.0 release
 *  - Moved main Build for Windows systems to Build\MSVC folder with intent to add builds for
 *    other systems to Build folder
 *  - Incorporated changes from Joseph Goldstone that eliminate various compiler warnings
 *  - Modified CIccProfile::Write() to allow for options in how ProfileIDs are created 
 *    (Example: ByProfileVersion/Always/Never)
 *
 * - May 2010
 *  - Modifications for better support for compiling with 64 bit compilers
 *  - Added IccProfLibVer.h to provide a macro for defining the library version
 *  - Fixed crashing bug with gamut tags with XYZ PCS
 *  - Modified CLUT::dump to use reflect legacy encoding of when lut16 tags are used
 *
 * - April 2010
 *  - Modified IccProfLibTest to allow modification of ProfileDescription and Copyright tags
 *
 * - March 2010
 *  - Added support for PrintConditions tag implemented using Dictionary tag type
 *
 * - January 2009
 *  - Added CIccCreateXformHintManager to allow for a list of hint object to be passed
 *    in at the time of CIccXform creation.
 *  - Modified PCS adjustment to use a scale and offset in new function CIccXform::AdjustPCS().
 *    CIccXform::CheckSrcAbs and CIccXform::CheckDestAbs now use CIccXform::AdjustPCS()/
 *  - Hint mechanism can now be used to set up scale and offset values.
 *  - Added CIccApplyBPCHint and CIccApplyBPC classes in IccApplyBPC.cpp and IccApplyBPC.h
 *    to provide optional support for Adobe Black Point Compensation
 *   - Since BPC is outside the scope of the ICC specifiction, users of CIccXform::Create
 *     must define and use a CIccApplyBPCHint object to enable BPC processing.
 *   - CIccApplyBPC temporarily instantiates a CIccCmm for the purpose of finding the black point
 *     of a profile.
 *   - Black point processing between two profiles is performed in two steps.  The first profile's
 *     black point is mapped to the V4 perceptual black point.  The second profile maps from the
 *     V4 perceptual black point to the second profile's black point.  This allows BPC processing
 *     to be performed on a single profile.
 *  - iccApplyNamedCMM.cpp modified to support BPC using CIccApplyBPC
 *  - Added CIccProfile::ReadTags() to force all tags to be loaded into memory. (Used by CIccApplyBPC)
 *  - CIccTagMultiLocalizedUnicode::Read() now seeks to the end of the last record at end of the function
 *  - Added ICC_CBRTF macro that can allow for substitution of cbrtf() function if it is available
 *  - Commented several additional functions in IccCmm.cpp
 *  - Modified WinNT\ApplyProfiles to allow for applying an output profile to a Lab image file
 *  - Fixed header in cmyk8bit.txt and cmyk16bit.txt files
 *
 * - December 2008
 *  - Added support for Monochrome ICC profile apply
 *
 * - November 2008
 *  - Cleanup of build files for Linux
 *  - Revised License to version 0.2
 *
 * - October 2008
 *  - Added support for External extension of CIccTags and CIccMPE objects
 *  - Make CIccMultiProcessElement::m_nReserved public
 *  - Added CIccMpeUnknown:SetType() and CIccMpeUnknown::SetChannels() to allow unknown elements to be externally created
 *  - CIccCLUT::Init() now returns bool to indicate allocation failure
 *  - Added icDeltaE() funciton to IccUtil.cpp
 *  - Modified MPE Sampled Curve to conform to specification.  First point is NOT stored in file.
 *  - Fixed MPE processing to not Clip PCS or apply Absolute Rendering Intent adjustments.
 *  - Added support for selective use of MPE tags in iccApplyNamedCmm.cpp
 *  - Modified IccV4ToMPE to correctly create SampledCurve segments by not saving first point.
 *  - wxProfileDump now supports option to perform round trip performance analysis.
 *  - Added fix to make CIccCmmMRU work after chages to add CIccApplyCmm architecture.
*
 * - November 2007
 *  - Addition of CIccXformCreator singleton factory and IIccXformFactory interface for dynamic
 *    creation of CIccXform objects based upon xform type.   With a IIccXformFactory  derived
 *    object properly registered using CIccXformCreator::PushFactory() overlaoded CIccXform objects
 *    seemleessly get created and applied.
 *
 * - October 2007
 *  - Fixed Memory leak in CIccProfile Copy constructor and operator=.
 *
 * - September 2007
 *  - Fixed bug with Tetrahedral Interpolation
 *
 * - August 2007
 *  - MPE Formula Curve bug fixes.
 *  - Registered CMM signatures recognized and displayed correctly.
 *  - Unknown platform signature type added (00000000h). 
 *
 * - June 2007
 *  - Added support for optional ProfileSequenceId tags.  These tags provide contents of 
 *    profile description tags and id's for profiles used to create an device link profile.  The
 *    CIccTagProfileSequeceId class implements these objects.
 *  - Added support for optional colorimetric Intent Image State tags.  This tag provides information
 *    about the image state implied by the use of a profile containg this tag.
 *
 * - Febrary 2007
 *  - Added a CIccMruCmm class that keeps track of the last 5 pixels that were applied.  Used by the
 *    SampleIccCmm Windows CMM DLL project.
 *
 * - November 2006
 *  - Added support for optional multiProcessingElementType tags.  These tags provide
 *    an arbitrary order of curves, matricies, and N-D luts encoded using floating
 *    point.  The CIccTagMultiProcessElement class implements these objects.  MPE based tags
 *    can have 1 or more CIccMultiProcessElement based objects attached to them.
 *    See CIccMpeCurveSet, CIccMpeMatrix, CIccMpeCLUT for more details.  Additional future
 *    placeholder elements CIccMpeBAcs and CIccMpeEAcs objects are defined, but provide no
 *    processing capabilities. See additions to Icc Specification for more details
 *    releated to optional MPE based tags.
 *  - Modified icProfileHeader.h to include newly approved Technololgy signatures for the
 *    digital motion picture industry.
 *
 * - October 2006
 *  - Added direct accessors CIccTagMultiLocalizedUnicode::Find() and 
 *    CIccTagMultiLocalizedUnicode::SetText() for easier creation of tags based on 
 *    CIccTagMultiLocalizedUnicode
 *  - Added CIccTagCurve::SetGamma() function
 *  - Added validation check for single entry (gamma) curves to CIccTagLut8 and CIccTagLut16
 *  - Added IsIdentity() function to the CIccCurve and CIccMatrix classes which returns true
 *    if they are identity
 *  - Modified the Xform objects in the CMM to use the IsIdentity() function. 
 *    Now CIccXform::Apply() will not apply the curves or the matrix if 
 *    they are identity, to improve the CMM performance
 *
 * - July 2006
 *  - Fixed bug with displaying the icSigPerceptualRenderingIntentGamutTag tag's name correctly
 *  - Added icVectorApplyMatrix3x3() to IccUtil
 *  - Fixed bug in CIccTagChromaticity::Validate() to use fixed floating point encoding in
 *    comparisons rather than IEEE encoding
 *
 * - June 2006
 *  - Added concept of device Lab and XYZ separate from PCS Lab and XYZ.  The encoding for
 *    device Lab and XYZ can be different than that used by the PCS.
 *    Both CIccXform::GetSrcSpace() and CIccXform::GetDstSpace() now return icSigDevLabData
 *    (rather than icSigLabData) or icSigDevXYZData (rather than icSigXYZData) if the connection
 *    is to a device (rather than PCS).  
 *  - The macros icSigDevLabData and icSigDevXYZData were added to IccDefs.h.
 *  - icGetSpaceSamples() and CIccInfo::GetColorSpaceSigName() were modified to recognize
 *    icSigDevLabData and icSigDevXYZData.
 *
 * - May 2006
 *  - Added icSigSampleICC to IccDefs.h and CIccProfile.cpp now uses this to initialize
 *    default values for creator and cmm in header fields.
 *  - Renamed icMatrix3x3Invert() to icInvertMatrix3x3() in IccUtil
 *  - Added icMatrixMultipily3x3() to IccUtil
 *
 * - April 2006
 *  - The CIccXform derived objects now have a virual GetType function to allow for easy
 *    identification and casting to an appropriate class type.
 *  - Modified CIccCmm to Allocate and use a single CIccPCS object rather than instantiating
 *    a new object on each call to Apply.  The CIccPCS object creation is performed using
 *    a virtual member function.
 *  - Minor type casting for beter compilation on Linux
 *  - Added SAMPLEICC_NOCLIPLABTOXYZ macro to IccProfLibConf.h to remove clipping when
 *    converting from Lab to XYZ.  This makes things round trip better but possibly results
 *    in imaginary (not well defined) XYZ values.
 *  - Added clipping to CIccTagCurve::Apply(v) to handle when v is out of range.
 *  - CIccLocalizedUnicode constructor now allocates enough data for a single 16 bit character.
 *  - CIccFileIO::Open() now appends a 'b' to szAttr if missing in WIN32.
 *  - Added check in profile validation for existance of colorantTableTag if output profile is xCLR.
 *
 * - March 2006
 *  - Modified icProfileHeader.h with reduced ICC copyright notice and changed icRegionCode
 *    to icCountryCode to agree with ISO 3166 naming convention. 
 *
 * - February 2006
 *  - Modified CIccCLUT Interp interfaces to take separate src and dst pixel values.
 *  - Modified CIccCLUT interface with selectable clipping function.
 *  - Added IsSupported() function to CIccTag and CIccTagUnknown classes.  This function
 *    is used to find out if tag is supported (for apply purposes).
 *  - Modified ToInternalEncoding and FromInternalEncoding to add icEncodeValue support
 *    for XYZ data.  The icEncodePercent was also modified to take Y=100.0 into and out of
 *    XYZ internal PCS encoding.
 *  - TagFactory interface for GetSigName() didn't function properly.  It was modified to 
 *    provide better support for GetSigName() and GetSigTypeName().
 *  - Additional cleanup of icProfileHeader.h.  Noticable Difference icProfileID.ID was
 *    chaged to icProfileID.ID8
 *
 * - December 2005
 *  - Moved most of the contents of IccDefs.h to icProfileHeader.h which corresponds to the "C" 
 *    header file published on ICC Web site.  The file icProfileHeader.h has been updated to reduce
 *    complications with compilers, missing version 4 items were added, and basic cleanup was
 *    performed.
 *  - A cross platform GUI based ICC Profile Viewer tool named wxProfileDump was added that
 *    makes use of the wxWidgets (http://www.wxWidgets.org) version 2.6.2 cross platform development
 *    framework.
 *  - Addition of CIccTagCreator singleton factory and IIccTagFactory interface for dynamic
 *    creation of CIccTag objects based upon tag signature.  The CIccTag::Create() funciton now uses
 *    a CIccTagCreator singleton object to create all CIccTag derived objects.  With a IIccTagFactory
 *    derived object properly registered using CIccTagCreator::PushFactory() private CIccTag objecs
 *    seemleessly load, save and validate.
 *  - CIccProfile::Write() modified to check for version 4 before calculating ProfileID value.
 *
 * - October 2005
 *  - Fixed bugs in copy constructors for CIccProfile, CIccTagCurve, and CIccTagText.
 *  - Added comments to IccDefs.h to indicate convenience enums.
 *  - Changed icMaxFlare to icMaxEnumFlare and icMaxGeometry to icMaxEnumGeometry to improve
 *    consistancy.
 *  - Corrected spelling of icMaxEnumIluminant to icMaxEnumIlluminant.
 *
 * - September 2005
 *  - Moved InvertMatrix to ICCUtils
 *
 * - August 2005
 *  - Cleaned up more warnings.
 *  - Added additional CIccCmm::AddXform() method for easily attaching memory based profiles.
 *  - Added CIccCmm::ToInternalEncoding() and CIccCmm::FromInternalEncoding() methods that
 *    make use of Cmm's tracking of source and destination color spaces.
 *
 * - July 2005
 *  - Renamed IccLib to IccProfLib to avoid confusion with Graeme Gill's "ICCLIB" project.
 *
 * - June 2005
 *  - Cleaned up warnings.
 *  - Added support for applying Preview and Gamut Tags in CIccCmm and CIccNamedColorCmm. This
 *    is accomplished through the new nLutType argument to the CIccCmm::AddXform() methods.
 *
 * - May 2005
 *  - Fixed bug in ParametricCurve type introduced with enhanced profile validation support.
 *
 * - April 2005
 *  - Greatly enhanced Profile Validation support. (Note: Validation is a separate step from
 *    reading profiles for speed purposes).
 *    - The CIccProfile class's ValidateProfile() function provides a Validation report
 *      within a string in addition to returning a validation status.
 *    - Additional functions were added to the profile class for Validation purposes.
 *    - Tags now have a Validate() member function to check out the validity of the data
 *      in the tags. (No check is made for color accuracy).
 *  - Tags now store reserved data to provide better validation reporting.
 *  - Added support for perceptualRenderingIntentGamutTag and saturationRederingIntentGamutTag.
 *  - Split Tag implementation into two files IccTagBasic and IccTagLut.
 *  - Fixed bug with reading testDescriptionTagType.
 *
 * - March 2005
 *  - Fixed bugs with N-Dimensional interpolation.
 *  - Fixed bugs with Lut8 Writing.
 *  - Added new CIccCLUT::Iterate() function to allow for manipulating data in a CLUT without having
 *    to mess with the details of dimension and granularity.
 *
 * - February 2005
 *  - Added ability for IccProfLib to be compiled as a DLL.
 *  - Fixed bugs in CIccCmm::ToInternalEncoding() and CIccCmm::FromInternalEncoding()
 *
 * - January 2005
 *  - <b>Complete support for version 4.2 profiles as defined in ICC specification ICC.1:2004-10.</b>
 *  - Added support for all tag types
 *  - N-dimensional interpolation function added (NOT TESTED)
 *  - Added support for calculation of profile ID using MD5 fingerprinting method
 *  - Profile validation function added
 *  - Added support for named color tags
 *  - Additional CMM class was added which supports named color profiles
 *  - Added copy constructors and copy operators for all Tag classes and Profile class.
 *  - Comments in the code were modified to allow the use of <b>doxygen</b>.  Additional comments 
 *    were added, and HTML documentation pages were generated.
 *  - Modified IccProfLib classes so that the library can be compiled as a DLL and gain access to
 *    IccProfLib objects from this separate DLL. 
 *
 * - February 2004 
 *  - Merged in changes to get Mac OS X compatibility with the gnu compiler.
 *  - Added boiler plate disclaimers to all the source files.
 *
 * - November 2003 \n
 *  - There has been some limited testing by members of the ICC, and changes have 
 *    been made as appropriate. Development was done on a WINTEL platform using Microsoft Visual C++ 6.0.  
 *    It should work for this environment.  Modifications have been made so that the 
 *    projects can be converted and work with Visual Studio .NET.\n
 *    The IccProfLib was written to be platform independent.  Peter McCloud of Adobe 
 *    was able to get IccProfLib to compile and run on Mac OS X. 
 *
 * <b>TODO List</b>
 *
 * - Create OS specific loadable library CMM wrappers to IccProfLib CMM objects.
 * - Naming Convention Cleanup of conversion functions in IccUtil.
 * - Restructure profile validation to use Tag Factory mechanism.
 *
 * <b>The ICC Software License, Version 0.2</b>
 *
 * Copyright � 2003-2015 The International Color Consortium. All rights 
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
 * ====================================================================\n
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED\n
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\n
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE\n
 * DISCLAIMED.  IN NO EVENT SHALL THE INTERNATIONAL COLOR CONSORTIUM OR\n
 * ITS CONTRIBUTING MEMBERS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,\n
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT\n
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF\n
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND\n
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,\n
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT\n
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF\n
 * SUCH DAMAGE.\n
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the The International Color Consortium. 
 *
 * Membership in the ICC is encouraged when this software is used for
 * commercial purposes. 
 *
 *
 * <b>CONTACT</b>
 *
 * Please send your questions, comments, and or suggestions to forums
 * on the SampleICC project site. (http://sourceforge.net/projects/sampleicc/).\n
 *
 */

