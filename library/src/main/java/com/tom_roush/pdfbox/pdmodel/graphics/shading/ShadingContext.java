package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.util.Log;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace;

import java.io.IOException;

public class ShadingContext {

    private float[] background;
    private int rgbBackground;
    private final PDShading shading;
//    private ColorModel outputColorModel;
    private PDColorSpace shadingColorSpace;

    public ShadingContext(PDShading shading
//            ,
//                          ColorModel cm, AffineTransform xform,
//                          Matrix matrix
    ) throws IOException
    {
        this.shading = shading;
        shadingColorSpace = shading.getColorSpace();
        Log.w("ceshi","shadingColorSpace==="+shadingColorSpace.getName());
        // create the output color model using RGB+alpha as color space
//        shadingColorSpace = PDDeviceCMYK.INSTANCE;
//        ColorSpace outputCS = ColorSpace.getInstance(ColorSpace.CS_sRGB);
//        outputColorModel = new ComponentColorModel(outputCS, true, false, Transparency.TRANSLUCENT,
//                DataBuffer.TYPE_BYTE);

        // get background values if available
        COSArray bg = shading.getBackground();
        if (bg != null)
        {
            background = bg.toFloatArray();
            rgbBackground = convertToRGB(background);
        }
    }

    final int convertToRGB(float[] values) throws IOException
    {
        int normRGBValues;

        float[] rgbValues = shadingColorSpace.toRGB(values);
//        Log.w("ceshi","hadingColorSpace.toRGB::"+rgbValues[2]);
        normRGBValues = (int) (rgbValues[2] * 255);
        normRGBValues |= (int) (rgbValues[1] * 255) << 8;
        normRGBValues |= (int) (rgbValues[0] * 255) << 16;
//        StringBuilder builder = new StringBuilder();
//        for (float jj:rgbValues)
//            builder.append(jj+",");
//        Log.w("ceshi","转换结果:"+builder.toString());

        return normRGBValues;
    }

    float[] getBackground()
    {
        return background;
    }

    int getRgbBackground()
    {
        return rgbBackground;
    }

}
