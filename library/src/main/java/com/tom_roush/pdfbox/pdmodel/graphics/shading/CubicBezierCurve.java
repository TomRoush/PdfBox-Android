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

import android.graphics.PointF;

/**
 * This class is used to describe the edge of each patch for type 6 shading.
 * This was done as part of GSoC2014, Tilman Hausherr is the mentor.
 *
 * @author Shaola Ren
 */
class CubicBezierCurve
{
    private final PointF[] controlPoints;
    private final int level;
    private final PointF[] curve;

    /**
     * Constructor of CubicBezierCurve
     *
     * @param ctrlPnts, 4 control points [p0, p1, p2, p3]
     * @param l, dividing level, if l = 0, one cubic Bezier curve is divided
     * into 2^0 = 1 segments, if l = n, one cubic Bezier curve is divided into
     * 2^n segments
     */
    CubicBezierCurve(PointF[] ctrlPnts, int l)
    {
        controlPoints = ctrlPnts.clone();
        level = l;
        curve = getPoints(level);
    }

    /**
     * Get level parameter
     *
     * @return level
     */
    int getLevel()
    {
        return level;
    }

    // calculate sampled points on the cubic Bezier curve defined by the 4 given control points
    private PointF[] getPoints(int l)
    {
        if (l < 0)
        {
            l = 0;
        }
        int sz = (1 << l) + 1;
        PointF[] res = new PointF[sz];
        double step = (double) 1 / (sz - 1);
        double t = -step;
        for (int i = 0; i < sz; i++)
        {
            t += step;
            double tmpX = (1 - t) * (1 - t) * (1 - t) * controlPoints[0].x
                + 3 * t * (1 - t) * (1 - t) * controlPoints[1].x
                + 3 * t * t * (1 - t) * controlPoints[2].x
                + t * t * t * controlPoints[3].x;
            double tmpY = (1 - t) * (1 - t) * (1 - t) * controlPoints[0].y
                + 3 * t * (1 - t) * (1 - t) * controlPoints[1].y
                + 3 * t * t * (1 - t) * controlPoints[2].y
                + t * t * t * controlPoints[3].y;
            res[i] = new PointF((float)tmpX, (float)tmpY);
        }
        return res;
    }

    /**
     * Get sampled points of this cubic Bezier curve.
     *
     * @return sampled points
     */
    PointF[] getCubicBezierCurve()
    {
        return curve;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (PointF p : controlPoints)
        {
            if (sb.length() > 0)
            {
                sb.append(' ');
            }
            sb.append(p);
        }
        return "Cubic Bezier curve{control points p0, p1, p2, p3: " + sb + "}";
    }
}
