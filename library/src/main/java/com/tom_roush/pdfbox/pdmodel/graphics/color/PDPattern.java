package com.tom_roush.pdfbox.pdmodel.graphics.color;

import android.graphics.Bitmap;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;

import java.io.IOException;

public class PDPattern extends PDSpecialColorSpace{

    private static final PDColor EMPTY_PATTERN = new PDColor(new float[] { }, null);

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


    @Override
    public String getName() {
        return COSName.PATTERN.getName();
    }

    @Override
    public int getNumberOfComponents() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDColor getInitialColor() {
        return EMPTY_PATTERN;
    }

    @Override
    public float[] toRGB(float[] value) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bitmap toRGBImage(Bitmap raster) throws IOException {
        throw new UnsupportedOperationException();
    }

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

    @Override
    public String toString()
    {
        return "Pattern";
    }
}
