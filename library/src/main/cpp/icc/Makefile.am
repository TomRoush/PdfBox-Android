## Process this file with automake to produce Makefile.in

lib_LTLIBRARIES = libSampleICC.la

libSampleICC_la_SOURCES = \
	IccApplyBPC.cpp \
	IccCmm.cpp \
	IccConvertUTF.cpp \
	IccEval.cpp \
	IccXformFactory.cpp \
	IccIO.cpp \
	IccMpeACS.cpp \
	IccMpeBasic.cpp \
	IccMpeFactory.cpp \
	IccPrmg.cpp \
	IccProfile.cpp \
	IccTagBasic.cpp \
	IccTagDict.cpp \
	IccTagFactory.cpp \
	IccTagLut.cpp \
	IccTagMPE.cpp \
	IccTagProfSeqId.cpp \
	IccUtil.cpp \
	md5.cpp

libSampleICC_la_LDFLAGS = -version-info @LIBTOOL_VERSION@

libSampleICCincludedir = $(includedir)/SampleICC

libSampleICCinclude_HEADERS = \
	IccApplyBPC.h \
	IccCmm.h \
	IccConvertUTF.h \
	IccEval.h \
	IccXformFactory.h \
	IccDefs.h \
	IccIO.h \
	IccMpeACS.h \
	IccMpeBasic.h \
	IccMpeFactory.h \
	IccPrmg.h \
	IccProfLibConf.h \
	IccProfLibVer.h \
	IccProfile.h \
	IccTag.h \
	IccTagBasic.h \
	IccTagDict.h \
	IccTagFactory.h \
	IccTagLut.h \
	IccTagMPE.h \
	IccTagProfSeqId.h \
	IccUtil.h \
	icProfileHeader.h \
	md5.h

INCLUDES = -I$(top_builddir) -I$(top_srcdir)/SampleICC

EXTRA_DIST = MainPage.h
