package com.tom_roush.pdfbox.filter;

import com.tom_roush.pdfbox.cos.COSDictionary;

/**
 * The result of a filter decode operation. Allows information such as color space to be
 * extracted from image streams, and for stream parameters to be repaired during reading.
 *
 * @author John Hewson
 */
public final class DecodeResult
{
    /** Default decode result. */
    public static final DecodeResult DEFAULT = new DecodeResult(new COSDictionary());

    private final COSDictionary parameters;
//    private PDJPXColorSpace colorSpace;TODO: PdfBox-Android

    DecodeResult(COSDictionary parameters)
    {
        this.parameters = parameters;
    }

//    DecodeResult(COSDictionary parameters, PDJPXColorSpace colorSpace)
//    {
//        this.parameters = parameters;
//        this.colorSpace = colorSpace;
//    }TODO: PdfBox-Android

    /**
     * Returns the stream parameters, repaired using the embedded stream data.
     * @return the repaired stream parameters, or an empty dictionary
     */
    public COSDictionary getParameters()
    {
        return parameters;
    }

    /**
     * Returns the embedded JPX color space, if any.
     * @return the the embedded JPX color space, or null if there is none.
     */
//    public PDJPXColorSpace getJPXColorSpace()
//    {
//        return colorSpace;
//    }TODO: PdfBox-Android

    // Sets the JPX color space
//    void setColorSpace(PDJPXColorSpace colorSpace)
//    {
//        this.colorSpace = colorSpace;
//    }TODO: PdfBox-Android
}
