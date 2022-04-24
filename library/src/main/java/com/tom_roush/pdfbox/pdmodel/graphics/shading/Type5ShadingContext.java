package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.Rect;
import android.util.Log;

import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.util.Matrix;

import java.io.IOException;

public class Type5ShadingContext extends GouraudShadingContext{
    /**
     * Constructor creates an instance to be used for fill operations.
     *
     * @param shading the shading type to be used
     * @param xform   transformation for user to device space
     * @param matrix  the pattern matrix concatenated with that of the parent content stream
     * @throws IOException if something went wrong
     */
    protected Type5ShadingContext(PDShadingType5 shading, AffineTransform xform, Matrix matrix, Rect deviceBounds) throws IOException {
        super(shading, xform, matrix);

        Log.d("Pdfbox-Android", "Type5ShadingContext");

        setTriangleList(shading.collectTriangles(xform, matrix));
        createPixelTable(deviceBounds);
    }
}
