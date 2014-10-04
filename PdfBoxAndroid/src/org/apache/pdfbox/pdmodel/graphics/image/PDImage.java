package org.apache.pdfbox.pdmodel.graphics.image;

import java.io.IOException;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDStream;

/**
 * An image in a PDF document.
 *
 * @author John Hewson
 */
public interface PDImage extends COSObjectable
{
    /**
     * Returns the content of this image as an AWT buffered image with an (A)RGB color space.
     * @return content of this image as a buffered image.
     * @throws IOException
     */
//    public BufferedImage getImage() throws IOException;TODO

    /**
     * Returns an ARGB image filled with the given paint and using this image as a mask.
     * @param paint the paint to fill the visible portions of the image with
     * @return a masked image filled with the given paint
     * @throws IOException if the image cannot be read
     * @throws IllegalStateException if the image is not a stencil.
     */
//    public BufferedImage getStencilImage(Paint paint) throws IOException;TODO

    /**
     * Returns a stream containing this image's data.
     * @throws IOException if the
     */
    public PDStream getStream() throws IOException;

    /**
     * Returns true if the image is a stencil mask.
     */
    public boolean isStencil();

    /**
     * Sets whether or not the image is a stencil.
     * This corresponds to the {@code ImageMask} entry in the image stream's dictionary.
     * @param isStencil True to make the image a stencil.
     */
    public void setStencil(boolean isStencil);

    /**
     * Returns bits per component of this image, or -1 if one has not been set.
     */
    public int getBitsPerComponent();

    /**
     * Set the number of bits per component.
     * @param bitsPerComponent The number of bits per component.
     */
    public void setBitsPerComponent(int bitsPerComponent);

    /**
     * Returns the image's color space.
     * @throws IOException If there is an error getting the color space.
     */
//    public PDColorSpace getColorSpace() throws IOException;TODO

    /**
     * Sets the color space for this image.
     * @param colorSpace The color space for this image.
     */
//    public void setColorSpace(PDColorSpace colorSpace);TODO

    /**
     * Returns height of this image, or -1 if one has not been set.
     */
    public int getHeight();

    /**
     * Sets the height of the image.
     * @param height The height of the image.
     */
    public void setHeight(int height);

    /**
     * Returns the width of this image, or -1 if one has not been set.
     */
    public int getWidth();

    /**
     * Sets the width of the image.
     * @param width The width of the image.
     */
    public void setWidth(int width);

    /**
     * Sets the decode array.
     * @param decode  the new decode array.
     */
    public void setDecode(COSArray decode);

    /**
     * Returns the decode array.
     */
    public COSArray getDecode();

    /**
     * Returns true if the image should be interpolated when rendered.
     */
    public boolean getInterpolate();


    /**
     * Sets the Interpolate flag, true for high-quality image scaling.
     */
    public void setInterpolate(boolean value);

    /**
     * Returns the suffix for this image type, e.g. "jpg"
     */
    public String getSuffix();
}
