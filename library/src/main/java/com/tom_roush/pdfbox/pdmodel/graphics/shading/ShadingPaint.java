package com.tom_roush.pdfbox.pdmodel.graphics.shading;


import android.graphics.Paint;

import com.tom_roush.pdfbox.rendering.WrapPaint;
import com.tom_roush.pdfbox.util.Matrix;

public abstract class ShadingPaint<T extends PDShading> implements WrapPaint {

    protected T shading;
    protected final Matrix matrix;

    ShadingPaint(T shading, Matrix matrix)
    {
        this.shading = shading;
        this.matrix = matrix;
    }

    /**
     * @return the PDShading of this paint
     */
    public T getShading()
    {
        return shading;
    }

    /**
     * @return the active Matrix of this paint
     */
    public Matrix getMatrix()
    {
        return matrix;
    }
}
