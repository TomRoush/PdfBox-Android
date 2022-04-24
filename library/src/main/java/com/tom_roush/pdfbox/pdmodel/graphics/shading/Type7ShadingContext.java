package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.Rect;

import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.util.Matrix;

import java.io.IOException;

public class Type7ShadingContext extends PatchMeshesShadingContext {
    /**
     * Constructor creates an instance to be used for fill operations.
     *
     * @param shading       the shading type to be used
     * @param xform         transformation for user to device space
     * @param matrix        the pattern matrix concatenated with that of the parent content stream
     * @param deviceBounds  device bounds
     * @throws IOException if something went wrong
     */
    protected Type7ShadingContext(PDShadingType7 shading, AffineTransform xform, Matrix matrix, Rect deviceBounds) throws IOException {
        super(shading, xform, matrix, deviceBounds, 16);
    }
}
