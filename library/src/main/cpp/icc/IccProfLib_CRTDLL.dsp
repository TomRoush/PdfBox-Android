# Microsoft Developer Studio Project File - Name="IccProfLib_CRTDLL" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Static Library" 0x0104

CFG=IccProfLib_CRTDLL - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "IccProfLib_CRTDLL.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "IccProfLib_CRTDLL.mak" CFG="IccProfLib_CRTDLL - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "IccProfLib_CRTDLL - Win32 Release" (based on "Win32 (x86) Static Library")
!MESSAGE "IccProfLib_CRTDLL - Win32 Debug" (based on "Win32 (x86) Static Library")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName "IccProfLib_CRTDLL"
# PROP Scc_LocalPath "."
CPP=cl.exe
RSC=rc.exe

!IF  "$(CFG)" == "IccProfLib_CRTDLL - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release_CRTDLL"
# PROP BASE Intermediate_Dir "Release_CRTDLL"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release_CRTDLL"
# PROP Intermediate_Dir "Release_CRTDLL"
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_MBCS" /D "_LIB" /Yu"stdafx.h" /FD /c
# ADD CPP /nologo /MD /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_MBCS" /D "_LIB" /FD /c
# SUBTRACT CPP /YX /Yc /Yu
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LIB32=link.exe -lib
# ADD BASE LIB32 /nologo
# ADD LIB32 /nologo

!ELSEIF  "$(CFG)" == "IccProfLib_CRTDLL - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug_CRTDLL"
# PROP BASE Intermediate_Dir "Debug_CRTDLL"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug_CRTDLL"
# PROP Intermediate_Dir "Debug_CRTDLL"
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_MBCS" /D "_LIB" /Yu"stdafx.h" /FD /GZ /c
# ADD CPP /nologo /MDd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_MBCS" /D "_LIB" /FD /GZ /c
# SUBTRACT CPP /YX /Yc /Yu
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LIB32=link.exe -lib
# ADD BASE LIB32 /nologo
# ADD LIB32 /nologo

!ENDIF 

# Begin Target

# Name "IccProfLib_CRTDLL - Win32 Release"
# Name "IccProfLib_CRTDLL - Win32 Debug"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Source File

SOURCE=.\IccApplyBPC.cpp
# End Source File
# Begin Source File

SOURCE=.\IccCmm.cpp
# End Source File
# Begin Source File

SOURCE=.\IccConvertUTF.cpp
# End Source File
# Begin Source File

SOURCE=.\IccEval.cpp
# End Source File
# Begin Source File

SOURCE=.\IccIO.cpp
# End Source File
# Begin Source File

SOURCE=.\IccMpeACS.cpp
# End Source File
# Begin Source File

SOURCE=.\IccMpeBasic.cpp
# End Source File
# Begin Source File

SOURCE=.\IccMpeFactory.cpp
# End Source File
# Begin Source File

SOURCE=.\IccPrmg.cpp
# End Source File
# Begin Source File

SOURCE=.\IccProfile.cpp
# End Source File
# Begin Source File

SOURCE=.\IccTagBasic.cpp
# End Source File
# Begin Source File

SOURCE=.\IccTagBasic.h
# End Source File
# Begin Source File

SOURCE=.\IccTagDict.cpp
# End Source File
# Begin Source File

SOURCE=.\IccTagFactory.cpp
# End Source File
# Begin Source File

SOURCE=.\IccTagLut.cpp
# End Source File
# Begin Source File

SOURCE=.\IccTagLut.h
# End Source File
# Begin Source File

SOURCE=.\IccTagMPE.cpp
# End Source File
# Begin Source File

SOURCE=.\IccTagProfSeqId.cpp
# End Source File
# Begin Source File

SOURCE=.\IccUtil.cpp
# End Source File
# Begin Source File

SOURCE=.\IccXformFactory.cpp
# End Source File
# Begin Source File

SOURCE=.\md5.cpp
# End Source File
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# Begin Source File

SOURCE=.\IccApplyBPC.h
# End Source File
# Begin Source File

SOURCE=.\IccCmm.h
# End Source File
# Begin Source File

SOURCE=.\IccConvertUTF.h
# End Source File
# Begin Source File

SOURCE=.\IccDefs.h
# End Source File
# Begin Source File

SOURCE=.\IccEval.h
# End Source File
# Begin Source File

SOURCE=.\IccIO.h
# End Source File
# Begin Source File

SOURCE=.\IccMpeACS.h
# End Source File
# Begin Source File

SOURCE=.\IccMpeBasic.h
# End Source File
# Begin Source File

SOURCE=.\IccMpeFactory.h
# End Source File
# Begin Source File

SOURCE=.\IccPrmg.h
# End Source File
# Begin Source File

SOURCE=.\IccProfile.h
# End Source File
# Begin Source File

SOURCE=.\IccProfLib_CRTDLLConf.h
# End Source File
# Begin Source File

SOURCE=.\IccTag.h
# End Source File
# Begin Source File

SOURCE=.\IccTagDict.h
# End Source File
# Begin Source File

SOURCE=.\IccTagProfSeqId.h
# End Source File
# Begin Source File

SOURCE=.\IccUtil.h
# End Source File
# Begin Source File

SOURCE=.\IccXformFactory.h
# End Source File
# Begin Source File

SOURCE=.\MainPage.h
# End Source File
# Begin Source File

SOURCE=.\md5.h
# End Source File
# End Group
# Begin Source File

SOURCE=.\Readme.txt
# End Source File
# End Target
# End Project
