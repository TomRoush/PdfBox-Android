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

import android.graphics.Point;
import android.graphics.PointF;

import java.util.HashSet;
import java.util.Set;

/**
 * This is an assistant class for accomplishing type 4, 5, 6 and 7 shading. It
 * describes a triangle actually, which is used to compose a patch. It contains
 * the degenerated cases, a triangle degenerates to a line or to a point. This
 * was done as part of GSoC2014, Tilman Hausherr is the mentor.
 *
 * @author Shaola Ren
 */
class ShadedTriangle
{
    protected final PointF[] corner; // vertices coordinates of a triangle
    protected final float[][] color;
    private final double area; // area of the triangle

    /*
     degree = 3 describes a normal triangle,
     degree = 2 when a triangle degenerates to a line,
     degree = 1 when a triangle degenerates to a point
     */
    private final int degree;

    // describes a rasterized line when a triangle degerates to a line, otherwise null
    private final Line line;

    // corner's edge (the opposite edge of a corner) equation value
    private final double v0;
    private final double v1;
    private final double v2;

    /**
     * Constructor.
     *
     * @param p an array of the 3 vertices of a triangle; the PointF objects should not be modified
     * by the caller.
     * @param c an array of color corresponding the vertex array p
     */
    ShadedTriangle(PointF[] p, float[][] c)
    {
        corner = p.clone(); // yes this is a shallow clone
        color = c.clone();
        area = getArea(p[0], p[1], p[2]);
        degree = calcDeg(p);

        if (degree == 2)
        {
            if (overlaps(corner[1], corner[2]) && !overlaps(corner[0], corner[2]))
            {
                Point p0 = new Point((int) Math.round(corner[0].x),
                        (int) Math.round(corner[0].y));
                Point p1 = new Point((int) Math.round(corner[2].x),
                        (int) Math.round(corner[2].y));
                line = new Line(p0, p1, color[0], color[2]);
            }
            else
            {
                Point p0 = new Point((int) Math.round(corner[1].x),
                        (int) Math.round(corner[1].y));
                Point p1 = new Point((int) Math.round(corner[2].x),
                        (int) Math.round(corner[2].y));
                line = new Line(p0, p1, color[1], color[2]);
            }
        }
        else
        {
            line = null;
        }

        v0 = edgeEquationValue(p[0], p[1], p[2]);
        v1 = edgeEquationValue(p[1], p[2], p[0]);
        v2 = edgeEquationValue(p[2], p[0], p[1]);
    }

    /**
     * Calculate the degree value of a triangle.
     *
     * @param p 3 vertices coordinates
     * @return number of unique points in the 3 vertices of a triangle, 3, 2 or
     * 1
     */
    private int calcDeg(PointF[] p)
    {
        Set<Point> set = new HashSet<Point>();
        for (PointF itp : p)
        {
            Point np = new Point((int) Math.round(itp.x * 1000), (int) Math.round(itp.y * 1000));
            set.add(np);
        }
        return set.size();
    }

    public int getDeg()
    {
        return degree;
    }

    /**
     * get the boundary of a triangle.
     *
     * @return {xmin, xmax, ymin, ymax}
     */
    public int[] getBoundary()
    {
        int[] boundary = new int[4];
        int x0 = (int) Math.round(corner[0].x);
        int x1 = (int) Math.round(corner[1].x);
        int x2 = (int) Math.round(corner[2].x);
        int y0 = (int) Math.round(corner[0].y);
        int y1 = (int) Math.round(corner[1].y);
        int y2 = (int) Math.round(corner[2].y);

        boundary[0] = Math.min(Math.min(x0, x1), x2);
        boundary[1] = Math.max(Math.max(x0, x1), x2);
        boundary[2] = Math.min(Math.min(y0, y1), y2);
        boundary[3] = Math.max(Math.max(y0, y1), y2);

        return boundary;
    }

    /**
     * Get the line of a triangle.
     *
     * @return points of the line, or null if this triangle isn't a line
     */
    public Line getLine()
    {
        return line;
    }

    /**
     * Whether a point is contained in this ShadedTriangle.
     *
     * @param p the target point
     * @return false if p is outside of this triangle, otherwise true
     */
    public boolean contains(PointF p)
    {
        if (degree == 1)
        {
            return overlaps(corner[0], p) || overlaps(corner[1], p) || overlaps(corner[2], p);
        }
        else if (degree == 2)
        {
            Point tp = new Point((int) Math.round(p.x), (int) Math.round(p.y));
            return line.linePoints.contains(tp);
        }

        /*
         the following code judges whether a point is contained in a normal triangle,
         taking the on edge case as contained
         */
        double pv0 = edgeEquationValue(p, corner[1], corner[2]);
        /*
         if corner[0] and point p are on different sides of line from corner[1] to corner[2],
         p is outside of the triangle
         */
        if (pv0 * v0 < 0)
        {
            return false;
        }
        double pv1 = edgeEquationValue(p, corner[2], corner[0]);
        /*
         if vertex corner[1] and point p are on different sides of line from corner[2] to corner[0],
         p is outside of the triangle
         */
        if (pv1 * v1 < 0)
        {
            return false;
        }
        double pv2 = edgeEquationValue(p, corner[0], corner[1]);
        /*
         only left one case:
         if corner[1] and point p are on different sides of line from corner[2] to corner[0],
         p is outside of the triangle, otherwise p is contained in the triangle
         */
        return pv2 * v2 >= 0; // !(pv2 * v2 < 0)
    }

    /*
     check whether two points overlaps each other, as points' coordinates are
     of type double, the coordinates' accuracy used here is 0.001
     */
    private boolean overlaps(PointF p0, PointF p1)
    {
        return Math.abs(p0.x - p1.x) < 0.001 && Math.abs(p0.y - p1.y) < 0.001;
    }

    /*
     two points can define a line equation, adjust the form of the equation to
     let the rhs equals 0, calculate the lhs value by plugging the coordinate
     of p in the lhs expression
     */
    private double edgeEquationValue(PointF p, PointF p1, PointF p2)
    {
        return (p2.y - p1.y) * (p.x - p1.x)
                - (p2.x - p1.x) * (p.y - p1.y);
    }

    // calculate the area of a triangle
    private double getArea(PointF a, PointF b, PointF c)
    {
        return Math.abs((c.x - b.x) * (c.y - a.y)
                - (c.x - a.x) * (c.y - b.y)) / 2.0;
    }

    /**
     * Calculate the color of a point.
     *
     * @param p the target point
     * @return an array denotes the point's color
     */
    public float[] calcColor(PointF p)
    {
        int numberOfColorComponents = color[0].length;
        float[] pCol = new float[numberOfColorComponents];

        switch (degree)
        {
            case 1:
                for (int i = 0; i < numberOfColorComponents; i++)
                {
                    // average
                    pCol[i] = (color[0][i] + color[1][i] + color[2][i]) / 3.0f;
                }
                break;
            case 2:
                // linear interpolation
                Point tp = new Point((int) Math.round(p.x), (int) Math.round(p.y));
                return line.calcColor(tp);
            default:
                float aw = (float) (getArea(p, corner[1], corner[2]) / area);
                float bw = (float) (getArea(p, corner[2], corner[0]) / area);
                float cw = (float) (getArea(p, corner[0], corner[1]) / area);
                for (int i = 0; i < numberOfColorComponents; i++)
                {
                    // barycentric interpolation
                    pCol[i] = color[0][i] * aw + color[1][i] * bw + color[2][i] * cw;
                }
                break;
        }
        return pCol;
    }

    @Override
    public String toString()
    {
        return corner[0] + " " + corner[1] + " " + corner[2];
    }
}
