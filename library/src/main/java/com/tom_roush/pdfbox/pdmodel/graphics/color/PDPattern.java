package com.tom_roush.pdfbox.pdmodel.graphics.color;

import android.graphics.Bitmap;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;

import java.io.IOException;

public final class PDPattern extends PDSpecialColorSpace {
    /** A pattern which leaves no marks on the page. */
    private static PDColor EMPTY_PATTERN = new PDColor(new float[] { }, null);

    private final PDResources resources;
    private PDColorSpace underlyingColorSpace;

    /**
     * Creates a new pattern color space.
     *
     * @param resources The current resources.
     */
    public PDPattern(PDResources resources)
    {
        this.resources = resources;
        array = new COSArray();
        array.add(COSName.PATTERN);
    }

    /**
     * Creates a new uncolored tiling pattern color space.
     *
     * @param resources The current resources.
     * @param colorSpace The underlying color space.
     */
    public PDPattern(PDResources resources, PDColorSpace colorSpace)
    {
        this.resources = resources;
        this.underlyingColorSpace = colorSpace;
        array = new COSArray();
        array.add(COSName.PATTERN);
        array.add(colorSpace);
    }

    @Override
    public String getName()
    {
        return COSName.PATTERN.getName();
    }

    @Override
    public int getNumberOfComponents()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDColor getInitialColor()
    {
        return EMPTY_PATTERN;
    }

    @Override
    public float[] toRGB(float[] value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bitmap toRGBImage(Bitmap raster) throws IOException
    {
        throw new UnsupportedOperationException();
    }

//    @Override
//    public Bitmap toRawImage(Bitmap raster) throws IOException
//    {
//        throw new UnsupportedOperationException();
//    } TODO: PdfBox-Android

    /**
     * Returns the pattern for the given color.
     *
     * @param color color containing a pattern name
     * @return pattern for the given color
     * @throws java.io.IOException if the pattern name was not found.
     */
    public PDAbstractPattern getPattern(PDColor color) throws IOException
    {
        PDAbstractPattern pattern = resources.getPattern(color.getPatternName());
        if (pattern == null)
        {
            throw new IOException("pattern " + color.getPatternName() + " was not found");
        }
        else
        {
            return pattern;
        }
    }

    /**
     * Returns the underlying color space, if this is an uncolored tiling pattern, otherwise null.
     */
    public PDColorSpace getUnderlyingColorSpace()
    {
        return underlyingColorSpace;
    }

    @Override
    public String toString()
    {
        return "Pattern";
    }
}
