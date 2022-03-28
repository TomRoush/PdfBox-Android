package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.Rect;
import android.util.Log;

import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.rendering.PaintContext;
import com.tom_roush.pdfbox.util.Matrix;

import java.io.IOException;

public class Type1ShadingPaint extends ShadingPaint<PDShadingType1> {

    /**
     * Constructor.
     *
     * @param shading the shading resources
     * @param matrix  the pattern matrix concatenated with that of the parent content stream
     */
    Type1ShadingPaint(PDShadingType1 shading, Matrix matrix) {
        super(shading, matrix);
    }

    public int getTransparency() {
        return 0;
    }

    @Override
    public PaintContext createContext(Rect deviceBounds, AffineTransform xform) {
        try {
            return new Type1ShadingContext(shading, xform, matrix);
        } catch (IOException e) {
            Log.e("Pdfbox-Android", "An error occurred while painting", e);
            return null;
        }
    }
}
