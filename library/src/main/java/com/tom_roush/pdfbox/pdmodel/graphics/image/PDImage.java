package com.tom_roush.pdfbox.pdmodel.graphics.image;

import android.graphics.Bitmap;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;
import com.tom_roush.pdfbox.pdmodel.common.PDStream;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * An image in a PDF document.
 *
 * @author John Hewson
 */
public interface PDImage extends COSObjectable
{
    /**
     * Returns the content of this image as a Bitmap with ARGB_888.
     * The size of the returned image is the larger of the size of the image itself or its mask.
     * @return content of this image as a buffered image.
     * @throws IOException
     */
    Bitmap getImage() throws IOException;

    /**
     * Returns an ARGB image filled with the given paint and using this image as a mask.
     * @param paint the paint to fill the visible portions of the image with
     * @return a masked image filled with the given paint
     * @throws IOException if the image cannot be read
     * @throws IllegalStateException if the image is not a stencil.
     */
//    BufferedImage getStencilImage(Paint paint) throws IOException;TODO: PdfBox-Android

    /**
     * Returns a stream containing this image's data. Null for inline images.
     * @throws IOException if the stream could not be read.
     */
    PDStream getStream() throws IOException;

    /**
     * Returns an InputStream containing the image data, irrespective of whether this is an
     * inline image or an image XObject.
     *
     * @return Decoded stream\
     * @throws IOException if the data could not be read.
     */
    InputStream createInputStream() throws IOException;

    /**
     * Returns an InputStream containing the image data, irrespective of whether this is an
     * inline image or an image XObject. The given filters will not be decoded.
     *
     * @return Decoded stream
     * @throws IOException if the data could not be read.
     */
    InputStream createInputStream(List<String> stopFilters) throws IOException;

    /**
     * Returns true if the image has no data.
     */
    boolean isEmpty();

    /**
     * Returns true if the image is a stencil mask.
     */
    boolean isStencil();

    /**
     * Sets whether or not the image is a stencil.
     * This corresponds to the {@code ImageMask} entry in the image stream's dictionary.
     * @param isStencil True to make the image a stencil.
     */
    void setStencil(boolean isStencil);

    /**
     * Returns bits per component of this image, or -1 if one has not been set.
     */
    int getBitsPerComponent();

    /**
     * Set the number of bits per component.
     * @param bitsPerComponent The number of bits per component.
     */
    void setBitsPerComponent(int bitsPerComponent);

    /**
     * Returns the image's color space.
     * @throws IOException If there is an error getting the color space.
     */
    PDColorSpace getColorSpace() throws IOException;

    /**
     * Sets the color space for this image.
     * @param colorSpace The color space for this image.
     */
    void setColorSpace(PDColorSpace colorSpace);

    /**
     * Returns height of this image, or -1 if one has not been set.
     */
    int getHeight();

    /**
     * Sets the height of the image.
     * @param height The height of the image.
     */
    void setHeight(int height);

    /**
     * Returns the width of this image, or -1 if one has not been set.
     */
    int getWidth();

    /**
     * Sets the width of the image.
     * @param width The width of the image.
     */
    void setWidth(int width);

    /**
     * Sets the decode array.
     * @param decode  the new decode array.
     */
    void setDecode(COSArray decode);

    /**
     * Returns the decode array.
     */
    COSArray getDecode();

    /**
     * Returns true if the image should be interpolated when rendered.
     */
    boolean getInterpolate();


    /**
     * Sets the Interpolate flag, true for high-quality image scaling.
     */
    void setInterpolate(boolean value);

    /**
     * Returns the suffix for this image type, e.g. "jpg"
     */
    String getSuffix();
}
