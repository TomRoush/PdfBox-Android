package com.tom_roush.pdfbox.util;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class GraphicsUtil {
    public static void addPoint2Rect(RectF rectF, PointF pointF){
        float newx = pointF.x;
        float newy = pointF.y;
        float x1 = Math.min(rectF.left, newx);
        float x2 = Math.max(rectF.right, newx);
        float y1 = Math.min(rectF.top, newy);
        float y2 = Math.max(rectF.bottom, newy);
        rectF.set(x1, y1, x2, y2);
    }

    public static Region getPathRegion(Path path){
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        Region outRegion = new Region();
        Rect boundsRounded = new Rect();
        bounds.round(boundsRounded);
        outRegion.setPath(path, new Region(boundsRounded));
        return outRegion;
    }
}
