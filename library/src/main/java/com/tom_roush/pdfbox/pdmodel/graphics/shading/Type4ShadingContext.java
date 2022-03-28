package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.Rect;
import android.util.Log;

import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.util.Matrix;

import java.io.IOException;

public class Type4ShadingContext extends GouraudShadingContext{
    private final int bitsPerFlag;

    /**
     * Constructor creates an instance to be used for fill operations.
     *
     * @param shading the shading type to be used
     * @param xform transformation for user to device space
     * @param matrix the pattern matrix concatenated with that of the parent content stream
     */
    Type4ShadingContext(PDShadingType4 shading, AffineTransform xform,
                        Matrix matrix, Rect deviceBounds) throws IOException
    {
        super(shading, xform, matrix);
        Log.d("Pdfbox-Android", "Type4ShadingContext");

        bitsPerFlag = shading.getBitsPerFlag();
        //TODO handle cases where bitperflag isn't 8
        Log.d("Pdfbox-Android", "bitsPerFlag: " + bitsPerFlag);
        setTriangleList(shading.collectTriangles(xform, matrix));
        createPixelTable(deviceBounds);
    }
}
