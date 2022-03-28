package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.Point;
import android.graphics.Rect;

import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.util.Matrix;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatchMeshesShadingContext extends TriangleBasedShadingContext{

    /**
     * patch list
     */
    private List<Patch> patchList;

    /**
     * Constructor creates an instance to be used for fill operations.
     *
     * @param shading the shading type to be used
     * @param xform transformation for user to device space
     * @param matrix the pattern matrix concatenated with that of the parent content stream
     * @param deviceBounds device bounds
     * @param controlPoints number of control points, 12 for type 6 shading and 16 for type 7 shading
     * @throws IOException if something went wrong
     */
    protected PatchMeshesShadingContext(PDMeshBasedShadingType shading,
                                        AffineTransform xform, Matrix matrix, Rect deviceBounds,
                                        int controlPoints) throws IOException
    {
        super(shading, xform, matrix);
        patchList = shading.collectPatches(xform, matrix, controlPoints);
        createPixelTable(deviceBounds);
    }

    @Override
    protected Map<Point, Integer> calcPixelTable(Rect deviceBounds)  throws IOException
    {
        Map<Point, Integer> map = new HashMap<Point, Integer>();
        for (Patch it : patchList)
        {
            super.calcPixelTable(it.listOfTriangles, map, deviceBounds);
        }
        return map;
    }

    @Override
    public void dispose()
    {
        patchList = null;
        super.dispose();
    }

    @Override
    protected boolean isDataEmpty()
    {
        return patchList.isEmpty();
    }
}
