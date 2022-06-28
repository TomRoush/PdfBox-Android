/*
 * Copyright 2014 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.tom_roush.harmony.awt.PaintContext;
import com.tom_roush.harmony.awt.geom.AffineTransform;
import com.tom_roush.pdfbox.util.Matrix;

/**
 * Intermediate class extended by the shading types 4,5,6 and 7 that contains the common methods
 * used by these classes.
 *
 * @author Shaola Ren
 * @author Tilman Hausherr
 */
abstract class TriangleBasedShadingContext extends ShadingContext implements PaintContext
{
    // map of pixels within triangles to their RGB color
    private Map<Point, Integer> pixelTable;

    /**
     * Constructor.
     *
     * @param shading the shading type to be used
     * @param xform transformation for user to device space
     * @param matrix the pattern matrix concatenated with that of the parent content stream
     * @throws IOException if there is an error getting the color space or doing background color conversion.
     */
    TriangleBasedShadingContext(PDShading shading, AffineTransform xform, Matrix matrix) throws IOException
    {
        super(shading, xform, matrix);
    }

    /**
     * Creates the pixel table.
     */
    protected final void createPixelTable(Rect deviceBounds) throws IOException
    {
        pixelTable = calcPixelTable(deviceBounds);
    }

    /**
     * Calculate every point and its color and store them in a Hash table.
     *
     * @return a Hash table which contains all the points' positions and colors of one image
     */
    abstract Map<Point, Integer> calcPixelTable(Rect deviceBounds) throws IOException;

    /**
     * Get the points from the triangles, calculate their color and add point-color mappings.
     */
    protected void calcPixelTable(List<ShadedTriangle> triangleList, Map<Point, Integer> map,
        Rect deviceBounds) throws IOException
    {
        for (ShadedTriangle tri : triangleList)
        {
            int degree = tri.getDeg();
            if (degree == 2)
            {
                Line line = tri.getLine();
                for (Point p : line.linePoints)
                {
                    map.put(p, evalFunctionAndConvertToRGB(line.calcColor(p)));
                }
            }
            else
            {
                int[] boundary = tri.getBoundary();
                boundary[0] = Math.max(boundary[0], deviceBounds.left);
                boundary[1] = Math.min(boundary[1], deviceBounds.right);
                boundary[2] = Math.max(boundary[2], deviceBounds.top);
                boundary[3] = Math.min(boundary[3], deviceBounds.bottom);
                for (int x = boundary[0]; x <= boundary[1]; x++)
                {
                    for (int y = boundary[2]; y <= boundary[3]; y++)
                    {
                        Point p = new Point(x, y);
                        PointF pf = new PointF(x, y);
                        if (tri.contains(pf))
                        {
                            map.put(p, evalFunctionAndConvertToRGB(tri.calcColor(pf)));
                        }
                    }
                }

                // "fatten" triangle by drawing the borders with Bresenham's line algorithm
                // Inspiration: Raph Levien in http://bugs.ghostscript.com/show_bug.cgi?id=219588
                Point p0 = new Point((int) Math.round(tri.corner[0].x),
                    (int) Math.round(tri.corner[0].y));
                Point p1 = new Point((int) Math.round(tri.corner[1].x),
                    (int) Math.round(tri.corner[1].y));
                Point p2 = new Point((int) Math.round(tri.corner[2].x),
                    (int) Math.round(tri.corner[2].y));
                Line l1 = new Line(p0, p1, tri.color[0], tri.color[1]);
                Line l2 = new Line(p1, p2, tri.color[1], tri.color[2]);
                Line l3 = new Line(p2, p0, tri.color[2], tri.color[0]);
                for (Point p : l1.linePoints)
                {
                    map.put(p, evalFunctionAndConvertToRGB(l1.calcColor(p)));
                }
                for (Point p : l2.linePoints)
                {
                    map.put(p, evalFunctionAndConvertToRGB(l2.calcColor(p)));
                }
                for (Point p : l3.linePoints)
                {
                    map.put(p, evalFunctionAndConvertToRGB(l3.calcColor(p)));
                }
            }
        }
    }

    /**
     * Convert color to RGB color value, using function if required, then convert from the shading
     * color space to an RGB value, which is encoded into an integer.
     */
    private int evalFunctionAndConvertToRGB(float[] values) throws IOException
    {
        if (getShading().getFunction() != null)
        {
            values = getShading().evalFunction(values);
        }
        return convertToRGB(values);
    }

    /**
     * Returns true if the shading has an empty data stream.
     */
    abstract boolean isDataEmpty();

    @Override
    public final Bitmap.Config getColorModel()
    {
        return super.getColorModel();
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

    @Override
    public final Bitmap getRaster(int x, int y, int w, int h)
    {
        Bitmap raster = Bitmap.createBitmap(w, h, getColorModel());
        int[] data = new int[w * h];
        if (!isDataEmpty() || getBackground() != null)
        {
            for (int row = 0; row < h; row++)
            {
                for (int col = 0; col < w; col++)
                {
                    Point p = new Point(x + col, y + row);
                    int value;
                    Integer v = pixelTable.get(p);
                    if (v != null)
                    {
                        value = v;
                    }
                    else
                    {
                        if (getBackground() == null)
                        {
                            continue;
                        }
                        value = getRgbBackground();
                    }
                    int index = row * w + col;
                    int r = value & 255;
                    value >>= 8;
                    int g = value & 255;
                    value >>= 8;
                    int b = value & 255;
                    data[index] = Color.argb(255, r, g, b);
                }
            }
        }
        raster.setPixels(data, 0, w, 0, 0, w, h);
        return raster;
    }
}
