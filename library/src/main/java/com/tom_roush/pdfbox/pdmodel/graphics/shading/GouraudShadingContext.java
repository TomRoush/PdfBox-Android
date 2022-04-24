package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.Point;
import android.graphics.Rect;

import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.util.Matrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GouraudShadingContext extends TriangleBasedShadingContext{

    /**
     * triangle list.
     */
    private List<ShadedTriangle> triangleList = new ArrayList<ShadedTriangle>();

    /**
     * Constructor creates an instance to be used for fill operations.
     *
     * @param shading the shading type to be used
     * @param xform transformation for user to device space
     * @param matrix the pattern matrix concatenated with that of the parent content stream
     * @throws IOException if something went wrong
     */
    protected GouraudShadingContext(PDShading shading, AffineTransform xform,
                                    Matrix matrix) throws IOException
    {
        super(shading, xform, matrix);
    }

    final void setTriangleList(List<ShadedTriangle> triangleList)
    {
        this.triangleList = triangleList;
    }

    @Override
    protected Map<Point, Integer> calcPixelTable(Rect deviceBounds) throws IOException
    {
        Map<Point, Integer> map = new HashMap<Point, Integer>();
        super.calcPixelTable(triangleList, map, deviceBounds);
        return map;
    }

    @Override
    public void dispose()
    {
        triangleList = null;
        super.dispose();
    }

    @Override
    protected boolean isDataEmpty()
    {
        return triangleList.isEmpty();
    }
}
