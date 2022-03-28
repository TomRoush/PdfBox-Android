package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.PointF;

public class CoordinateColorPair {

    final PointF coordinate;
    final float[] color;

    /**
     * Constructor.
     *
     * @param p point
     * @param c color
     */
    CoordinateColorPair(PointF p, float[] c)
    {
        coordinate = p;
        color = c.clone();
    }
}
