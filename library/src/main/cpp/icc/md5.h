
/** @file
 
md5.H - header file for md5.cpp

Copyright (C) 1991-2, RSA Data Security, Inc. Created 1991. All
rights reserved.

License to copy and use this software is granted provided that it
is identified as the "RSA Data Security, Inc. MD5 Message-Digest
Algorithm" in all material mentioning or referencing this software
or this function.

License is also granted to make and use derivative works provided
that such works are identified as "derived from the RSA Data
Security, Inc. MD5 Message-Digest Algorithm" in all material
mentioning or referencing the derived work.

RSA Data Security, Inc. makes no representations concerning either
the merchantability of this software or the suitability of this
software for any particular purpose. It is provided "as is"
without express or implied warranty of any kind.

These notices must be retained in any copies of any part of this
documentation and/or software.
 */

/* ---------------------------------------------------------------------
January 2011 
- Modified names to avoid possible conflicts - Max Derhak
- Added IccProfLibConf.h include to use ICCPROFLIB_API with functions
- Changed typedef of UINT4 to use ICCUINT64

August 2012
- Change typedef of UINT4 to use ICCUINT32 (oops!)
------------------------------------------------------------------------ */

#include "IccProfLibConf.h"


/** POINTER defines a generic pointer type */
typedef unsigned char *POINTER;

/** UINT2 defines a two byte word */
typedef unsigned short int UINT2;

/** UINT4 defines a four byte word */
typedef ICCUINT32 UINT4;


/** MD5 context. */
typedef struct {
  UINT4 state[4];                                   /* state (ABCD) */
  UINT4 count[2];        /* number of bits, modulo 2^64 (lsb first) */
  unsigned char buffer[64];                         /* input buffer */
} MD5_CTX;

void ICCPROFLIB_API icMD5Init  (MD5_CTX *);
void ICCPROFLIB_API icMD5Update  (MD5_CTX *, unsigned char *, unsigned int);
void ICCPROFLIB_API icMD5Final  (unsigned char* , MD5_CTX *);

