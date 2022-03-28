package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.Rect;
import android.util.Log;

import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.rendering.PaintContext;
import com.tom_roush.pdfbox.util.Matrix;

import java.io.IOException;

public class Type6ShadingPaint extends ShadingPaint<PDShadingType6>{

    /**
     * Constructor.
     *
     * @param shading the shading resources
     * @param matrix the pattern matrix concatenated with that of the parent content stream
     */
    Type6ShadingPaint(PDShadingType6 shading, Matrix matrix)
    {
        super(shading, matrix);
    }


    @Override
    public PaintContext createContext(Rect deviceBounds, AffineTransform xform) {
        try {
            return new Type6ShadingContext(shading, xform, matrix, deviceBounds);
        }
        catch (IOException e){
            Log.e("Pdfbox-Android", "IOError while create paint context");
            return null;
        }
    }
}
