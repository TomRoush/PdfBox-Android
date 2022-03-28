package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.Bitmap;
import android.graphics.ColorSpace;

import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace;
import com.tom_roush.pdfbox.util.Matrix;

import java.io.IOException;

public abstract class ShadingContext {
    private float[] background;
    private int rgbBackground;
    private final PDShading shading;
    private Bitmap.Config outputColorModel;
    private PDColorSpace shadingColorSpace;

    /**
     * Constructor.
     *
     * @param shading the shading type to be used
     * @param xform transformation for user to device space
     * @param matrix the pattern matrix concatenated with that of the parent content stream
     * @throws java.io.IOException if there is an error getting the color space
     * or doing background color conversion.
     */
    public ShadingContext(PDShading shading, AffineTransform xform, Matrix matrix) throws IOException
    {
        this.shading = shading;
        shadingColorSpace = shading.getColorSpace();

        // create the output color model using RGB+alpha as color space
        outputColorModel = Bitmap.Config.ARGB_8888;

        // get background values if available
        COSArray bg = shading.getBackground();
        if (bg != null)
        {
            background = bg.toFloatArray();
            rgbBackground = convertToRGB(background);
        }
    }

    PDColorSpace getShadingColorSpace()
    {
        return shadingColorSpace;
    }

    PDShading getShading()
    {
        return shading;
    }

    float[] getBackground()
    {
        return background;
    }

    int getRgbBackground()
    {
        return rgbBackground;
    }

    /**
     * Convert color values from shading colorspace to RGB color values encoded
     * into an integer.
     *
     * @param values color values in shading colorspace.
     * @return RGB values encoded in an integer.
     * @throws java.io.IOException if the color conversion fails.
     */
    final int convertToRGB(float[] values) throws IOException
    {
        int normRGBValues;

        float[] rgbValues = shadingColorSpace.toRGB(values);
        normRGBValues = (int) (rgbValues[0] * 255);
        normRGBValues |= (int) (rgbValues[1] * 255) << 8;
        normRGBValues |= (int) (rgbValues[2] * 255) << 16;

        return normRGBValues;
    }

    Bitmap.Config getColorModel()
    {
        return outputColorModel;
    }

    void dispose()
    {
        outputColorModel = null;
        shadingColorSpace = null;
    }
}
