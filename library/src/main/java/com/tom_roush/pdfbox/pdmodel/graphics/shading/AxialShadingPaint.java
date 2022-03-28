package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.Rect;
import android.util.Log;

import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.rendering.PaintContext;
import com.tom_roush.pdfbox.util.Matrix;

import java.io.IOException;

public class AxialShadingPaint extends ShadingPaint<PDShadingType2>{

    /**
     * Constructor.
     *
     * @param shadingType2 the shading resources
     * @param matrix the pattern matrix concatenated with that of the parent content stream
     */
    AxialShadingPaint(PDShadingType2 shadingType2, Matrix matrix)
    {
        super(shadingType2, matrix);
    }

    @Override
    public PaintContext createContext(Rect deviceBounds, AffineTransform xform) {
        try {
            return new AxialShadingContext(shading, xform, matrix, deviceBounds);
        }
        catch (IOException e){
            Log.e("Pdfbox-Android", "IOError while create paint context");
            return null;
        }
    }
}
